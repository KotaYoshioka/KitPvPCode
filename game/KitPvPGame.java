package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import db.DataBase;
import kitpvp.KitPvPPrepare;
import kits.KitAbility;
import kits.KitModel;
import kits.ability.KitAbilityProjectile;
import maindatas.KitData;
import maindatas.StageData;
import tools.ItemTools;

public class KitPvPGame implements Listener{
	public Plugin plugin;
	World world;
	//データベース
	public DataBase db;
	public KitAbility ka;
	public KitPvPBlocks blocks;
	
	//準備データ
	private HashMap<Player,KitPvPPrepare> prepares = new HashMap<Player,KitPvPPrepare>();
	//参加プレイヤー一覧
	private List<Player> players = new ArrayList<Player>();
	//生存プレイヤー一覧
	private List<Player> livings = new ArrayList<Player>();
	//死亡プレイヤー一覧
	private List<Player> deads = new ArrayList<Player>();
	//観戦プレイヤー
	private List<Player> watchers = new ArrayList<Player>();
	//各プレイヤーのデータ
	private HashMap<Player,KitModel> playerdata = new HashMap<Player,KitModel>();
	//各プレイヤーのキット番号
	private HashMap<Player,Integer> kitnumber = new HashMap<Player,Integer>();
	//ゲーム中に存在する飛び道具
	private HashMap<Entity,KitAbilityProjectile> projectiles = new HashMap<Entity,KitAbilityProjectile>();
	
	//プロデューサー達
	private List<GameProducer> producers = new ArrayList<GameProducer>();
	
	int stageID = 0;
	String battleUUID = "";
	public boolean nowgame = false;

	

	public KitPvPGame(Plugin plugin) {
		this.plugin = plugin;
		ka = new KitAbility(this,plugin);
		battleUUID = UUID.randomUUID().toString();
		world = Bukkit.getWorld("kitpvp");
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		db = new DataBase();
		blocks = new KitPvPBlocks(plugin);
		stageID = new Random().nextInt(5);
	}
	
	/**
	 * イベントをキャンセルする
	 */
	public void cancelEvents() {
		HandlerList.unregisterAll(this);
	}

	/**
	 * 準備画面を全プレイヤーに表示させる。
	 * 全員が準備を完了したのを確認次第、戦闘に移る。
	 */
	public void prepare() {
		for(Player p:players) {
			prepares.put(p, new KitPvPPrepare(plugin,this,p));
			p.sendMessage(ChatColor.WHITE + "ステージは、" + ChatColor.YELLOW + "「" + StageData.stages[stageID] + "」");
		}
		new BukkitRunnable() {
			public void run() {
				for(Player p:players) {
					if(!kitnumber.containsKey(p) || !prepares.get(p).getReady()) {
						return;
					}
				}
				for(Player p:players) {
					prepares.get(p).cancel();
					p.sendTitle("5秒後、開始します","",10,80,10);
				}
				this.cancel();
				new BukkitRunnable() {
					public void run() {
						start();
					}
				}.runTaskLater(plugin, 100);
			}
		}.runTaskTimer(plugin, 0, 10);
	}

	/**
	 * ゲームを開始させる処理
	 * 各プレイヤーの初期設定をした後、戦場にテレポートさせ、ワールドボーダーを始動させる。
	 */
	public void start() {
		nowgame = true;
		
		//各プレイヤーの設定
		livings.addAll(players);
		for(Player p: players) {
			for(PotionEffect pe:p.getActivePotionEffects()) {
				p.removePotionEffect(pe.getType());
			}
			if(!kitnumber.containsKey(p)) {
				kitnumber.put(p, (int)db.getPlayerOptions(p)[0]);
			}
			setPlayerData(p);
			getPlayerData(p).setKey(true);
			KitData.SetAllWeapon(p,kitnumber.get(p),0,false);
		}
		
		//各プレイヤーをバラバラの位置にテレポートさせる
		List<Player> shufflePlayers = new ArrayList<Player>();
		shufflePlayers.addAll(players);
		Collections.shuffle(shufflePlayers);
		for(int i = 0 ; i < shufflePlayers.size() ; i++) {
			Player p = shufflePlayers.get(i);
			p.teleport(StageData.stageLocation[stageID][i].toLocation(world));
		}
		
		//観戦者たちの移動
		for(Player p:watchers) {
			p.setGameMode(GameMode.SPECTATOR);
			p.teleport(players.get(0));
		}
		
		//プロデューサーたちの設定
		producers.add(new BorderProducer(plugin,this,world,stageID));
		producers.add(new DamageProducer(plugin,this));
	}


	/**
	 * プレイヤーが敗北したときの処理
	 * 残り人数に及んでゲーム終了まで行う
	 * @param player
	 */
	public void death(Player player) {
		if(livings.contains(player)) {
			deads.add(player);
			livings.remove(player);
			getPlayerData(player).spectator(true);
			if(livings.size() == 1) {
				gameSet();
			}else {
				announce(player.getDisplayName()+"が敗退！","残り" + livings.size() + "人");
				world.dropItem(player.getLocation(), ItemTools.createItem("コア",Material.HEART_OF_THE_SEA));
			}
		}
	}
	
	/**
	 * ゲーム終了時の処理
	 */
	public void gameSet() {
		//ゲーム終了関係の処理
		nowgame = false;
		world.getWorldBorder().setSize(999999);
		announce(livings.get(0).getDisplayName() + "の勝利！","");
		getPlayerData(livings.get(0)).cancelEvents();
		getPlayerData(livings.get(0)).spectator(true);
		for(GameProducer producer: producers) {
			producer.endGame();
		}
		
		//存在する全生物を削除する
		for(Entity ent:world.getEntities()) {
			if(ent instanceof Player || ent instanceof ArmorStand) {
				continue;
			}
			ent.remove();
		}
		
		//10秒後、ロビーに返す。
		new BukkitRunnable() {
			public void run() {
				cancelEvents();
				for(Player p:players) {
					getPlayerData(p).cancelAll();
					getPlayerData(p).spectator(false);
					db.setBattle(p, battleUUID, getPlayerData(p).getKitNumber(), stageID, livings.get(0)==p,KitData.version);
				}
				for(Player p:watchers) {
					p.teleport(players.get(0));
				}
			}
		}.runTaskLater(plugin, 200);
	}
	
	@EventHandler
	public void onSpawnEntity(CreatureSpawnEvent e) {
		if(e.getSpawnReason() == SpawnReason.EGG) {
			e.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void onChangeFoodlevel(FoodLevelChangeEvent e) {
		if(deads.contains(e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e) {
		if(e.getEntity().getItemStack().getType() == Material.WHITE_WOOL) {
			e.setCancelled(true);
		}else if(e.getEntity().getItemStack().getType() == Material.POINTED_DRIPSTONE) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityExplodeBlockBreak(EntityExplodeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		if(e.getBlock().getType() == Material.POINTED_DRIPSTONE) {
			return;
		}else if(e.getBlock().getType() == Material.AIR) {
			return;
		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		e.setCancelled(true);
	}
	
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		if(players.contains(e.getPlayer())) {
			if(e.getItemDrop().getItemStack().getType() != Material.SHIELD) {
				e.setCancelled(true);
			}
		}
	}
	
	public void announce(String m,String s) {
		for(Player p:players) {
			playerdata.get(p).sendTitle(m, s, 20, 100, 20);
		}
		for(Player p:watchers) {
			p.sendTitle(m, s,20,100,20);
		}
	}

	/**
	 * プレイヤーを戦闘に参加させる。
	 * @param player
	 */
	public void joinGame(Player player) {
		if(!containsPlayers(player) && !watchers.contains(player)) {
			players.add(player);
			player.sendMessage("KitPvPに参加しました！");
		}
	}
	
	/**
	 * プレイヤーを観戦として参加させる
	 * @param player
	 */
	public void joinWatcher(Player player) {
		if(!containsPlayers(player) && !watchers.contains(player)) {
			watchers.add(player);
		}
	}
	//Getter、Setter等

	public boolean containsPlayers(Entity player) {
		return players.contains(player);
	}

	public boolean containsLivings(Entity player) {
		return livings.contains(player);
	}

	public void setLivings(Player player) {
		livings.add(player);
	}
	
	public void setKitNumver(Player player,int number) {
		kitnumber.put(player, number);
	}
	
	public KitModel getPlayerData(Player player) {
		return playerdata.get(player);
	}
	
	public void setPlayerData(Player player) {
		setPlayerData(player,kitnumber.get(player));
	}
	public void setPlayerData(Player player,int kitnumber) {
		playerdata.put(player, new KitDataMaker(plugin,this,player,kitnumber).getData());
	}
	
	public List<Player> getLivings(){
		return livings;
	}
	
	public List<Player> getPlayers(){
		return players;
	}
	
	public List<Player> getDeads(){
		return deads;
	}
	
	public boolean containProjectile(Entity ent) {
		return projectiles.containsKey(ent);
	}
	public KitAbilityProjectile getProjectileData(Entity ent) {
		return projectiles.get(ent);
	}
	public void addProjectile(Entity ent,KitAbilityProjectile data) {
		projectiles.put(ent, data);
	}
	public void removeProjectile(Entity ent) {
		if(projectiles.containsKey(ent)) {
			projectiles.remove(ent);
		}
	}
}