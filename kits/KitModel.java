package kits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.ability.KitSleep;
import maindatas.KitData;
import net.md_5.bungee.api.ChatColor;
import tools.ItemTools;

public abstract class KitModel implements Listener{

	protected Plugin plugin;
	protected Player player;
	KitPvPGame kpg;
	//キットナンバー
	private int kitnumber;
	//言語ナンバー
	private int lanumber;
	//クールダウン
	private boolean[] cooldown = new boolean[4];
	private int[] cooldownCount = new int[4];
	////ステータス
	//スピード
	protected int speed = 0;
	//ジャンプ
	protected int jump = 0;
	//サイレンス
	private boolean silence = false;
	//スタン
	private boolean stan = false;
	//表示
	private boolean display = false;
	////隠しステータス
	//無敵
	private boolean muteki = false;
	//動き停止
	private boolean stopmove = false;
	
	//状態異常系
	//眠り
	private KitSleep sleep = null;
	
	//安全キー(falseの間、技が発動しない)
	private boolean key = false;
	//攻撃キー(falseの間、攻撃が出来ない)
	private boolean attackkey = false;
	//パッシブが常時発動系かを確認する
	private boolean forpassive = false;
	//アイテムの詳細を見るかどうか
	private boolean detail = false;
	//生存
	protected boolean live = true;
	//TODO ????
	protected boolean sdd = true;
	
	//オプション
	//攻撃補助
	public boolean attackSupport = false;
	//簡単スペル
	boolean easySpell = false;
	

	public KitModel(Plugin plugin,Player player,KitPvPGame kpg,int kitnumber) {
		//初期関数
		this.kitnumber = kitnumber;
		this.lanumber = 0;
		this.plugin = plugin;
		this.player = player;
		this.kpg = kpg;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		//ステータス初期化
		player.setGameMode(GameMode.SURVIVAL);
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(KitData.kitmaxhp[kitnumber]);
		player.setHealth(KitData.kitmaxhp[kitnumber]);
		player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(KitData.kitdefence[kitnumber]);
		player.setFoodLevel(20);
		player.setSaturation(10);
		player.setAllowFlight(false);
		for(PotionEffect pe:player.getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}
		player.setLevel(0);
		player.setExp(0);
		
		//クールダウンの初期化
		for(int i = 0 ; i < cooldown.length ; i++) {
			cooldown[i] = true;
			cooldownCount[i] = 0;
		}
		if(KitData.kitweaponcool[kitnumber][3] == 0) {
			forpassive = true;
		}
		
		reScoreboard();
		displayBuff();
		sdd = false;
		addSpeed(3);
		addJump(2);
		
		//データベース
		Object[] options = kpg.db.getPlayerOptions(player);
		attackSupport = (boolean)options[1];
		easySpell = (boolean)options[2];
	}
	
	/**
	 * イベントのキャンセル
	 */
	public void cancelEvents() {
		PlayerInteractEntityEvent.getHandlerList().unregister(this);
		PlayerMoveEvent.getHandlerList().unregister(this);
		InventoryClickEvent.getHandlerList().unregister(this);
		BlockCanBuildEvent.getHandlerList().unregister(this);
		EntityDamageEvent.getHandlerList().unregister(this);
		EntityDamageByEntityEvent.getHandlerList().unregister(this);
	}
	
	public void cancelAll() {
		HandlerList.unregisterAll(this);
		live = false;
	}
	
	public void displayBuff() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(!display) {
					if(stan) {
						player.sendTitle(ChatColor.YELLOW + "スタン", "", 3, 0, 0);
					}else if(silence) {
						player.sendTitle(ChatColor.WHITE + "サイレンス","", 3, 0, 0);
					}else{
						player.resetTitle();
					}
				}	
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	/**
	 * 指定された値だけ食料ゲージを回復させる。
	 * また、最大ゲージを超えての回復の場合、隠し満腹度が癒える。
	 * @param heal
	 */
	public void healFood(double heal) {
		Bukkit.getLogger().info("" + heal);
		int foodlevel = player.getFoodLevel();
		float foodhide = player.getExhaustion();
		double h = heal;
		if(foodlevel < 20) {
			if(foodlevel + h >= 20) {
				h -= 20 - foodlevel;
				foodlevel = 20;
			}else {
				foodlevel += h;
				h = 0;
			}
		}
		if(h != 0) {
			if(foodhide + h >= 10) {
				foodhide = 10;
			}else {
				foodhide += h;
			}
		}
		player.setFoodLevel(foodlevel);
		player.setExhaustion(foodhide);
	}
	
	/**
	 * 指定された値だけ食料ゲージを減らす。
	 * @param food
	 */
	public void reduceFood(int food) {
		int foodlevel = player.getFoodLevel();
		if(foodlevel - food < 0) {
			foodlevel = 0;
		}else {
			foodlevel = foodlevel - food;
		}
		player.setSaturation(0);
		player.setFoodLevel(foodlevel);
	}

	/**
	 * 指定したダメージを与える。
	 * @param damage
	 * @param causer
	 * @param cause
	 * @param canHeal
	 */
	public void damage(double damage,Player causer,String cause,boolean canHeal) {
		//無敵の場合、無効にする。
		if(muteki) {
			return;
		}
		//ダメージがマイナスの場合、ヒールに転じる。
		if(damage < 0) {
			heal(-damage,causer,cause);
			return;
		}
		//実行するかどうかの確認
		boolean start = true;
		Object[] obj1 = {player,damage,causer,cause,canHeal,true};
		if(causer != null) {
			//各キットの効力
			//技の発動者の都合
			obj1 = kpg.getPlayerData(causer).exDamage(player,damage, causer, cause, canHeal, true);
		}
		//技を受ける自分の都合
		Object[] obj = exDamage((Player)obj1[0],(double)obj1[1],(Player)obj1[2],(String)obj1[3],(boolean)obj1[4],(boolean)obj1[5]);
		damage = (double)obj[1];
		causer = (Player)obj[2];
		cause = (String)obj[3];
		canHeal = (boolean)obj[4];
		start = (boolean)obj[5];
		//攻撃処理
		if(start) {
			//寝ていた場合
			if(isSleep()) {
				awake();
			}
			//攻撃者の体力回復
			if(player != causer && canHeal && causer != null) {
				kpg.getPlayerData(causer).healFood(damage);
			}
			reScoreboard();
			//体力以上のダメージの場合、デス処理をする。
			if(player.getHealth() - damage <= 0) {
				death();
			}else {
				player.damage(damage);
				damageAnimation((int)damage);
			}
		}
	}
	
	/**
	 * 指定した値のダメージが落ちる演出
	 * @param damage
	 */
	void damageAnimation(int damage) {
		//出てくる場所の選定
		Location l = player.getLocation().clone();
		Random rnd = new Random();
		l.add(rnd.nextDouble() * (rnd.nextBoolean()?1:-1),rnd.nextDouble(),rnd.nextDouble() * (rnd.nextBoolean()?1:-1));
		
		//ダメージの出現
		ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(),EntityType.ARMOR_STAND);
		as.setCustomName(ChatColor.RED + "" + damage);
		as.setCustomNameVisible(true);
		as.setVisible(false);
		
		//落下処理
		Location ll = as.getLocation().clone();
		new BukkitRunnable() {
			int counter = 0;
			public void run() {
				Location lo = ll.clone();
				lo.add(0, -1 * counter * 0.05,0);
				as.teleport(lo);
				counter++;
				if(counter > 30) {
					as.remove();
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	/**
	 * ダメージを食らった時の処理
	 * @param p
	 * @param damage
	 * @param causer
	 * @param cause
	 * @param canHeal
	 * @param damagable
	 * @return
	 */
	public Object[] exDamage(Player p,double damage,Player causer,String cause,boolean canHeal,boolean damagable) {
		Object[] a = {p,damage,causer,cause,canHeal,damagable};
		return a;
	}

	/**
	 * 普通に体力が回復する
	 * @param heal
	 * @param causer
	 * @param cause
	 */
	public void heal(double heal,Player causer,String cause) {
		if(heal < 0) {
			damage(-heal,causer,cause,false);
			return;
		}
		double hp = player.getHealth() + heal;
		if(hp >= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) {
			hp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		}
		player.setHealth(hp);
		reScoreboard();

	}

	/**
	 * 敗退したときの処理
	 */
	public void death() {
		live = false;
		kpg.death(player);
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
		player.setFoodLevel(20);
		player.setSaturation(19f);
		cancelEvents();
	}
	
	/**
	 * 敗退し、観戦にうつる処理
	 * @param hide
	 */
	public void spectator(boolean hide) {
		if(hide) {
			live = false;
			for(Player p:kpg.getPlayers()) {
				if(p != player) {
					p.hidePlayer(plugin, player);
				}
			}
			player.setAllowFlight(true);
			player.setFlying(true);
			player.getInventory().clear();
			player.getInventory().addItem(ItemTools.createItem("プレイヤーテレポート", Material.CLOCK));
		}else {
			for(Player p:kpg.getPlayers()) {
				if(p != player) {
					p.showPlayer(plugin, player);
				}
			}
			player.setAllowFlight(false);
		}
	}
	
	/**
	 * クールダウン処理
	 * @param index
	 * @param time
	 */
	public void cooldown(int index,int time) {
		cooldown[index] = false;
		cooldownCount[index] = time;
		reScoreboard();
		new BukkitRunnable() {
			@Override
			public void run() {
				if(cooldownCount[index] == 1) {
					cooldownCount[index] = 0;
					cooldown[index] = true;
					this.cancel();
					reScoreboard();
					return;
				}
				cooldownCount[index] = cooldownCount[index] - 1;
				reScoreboard();
			}
		}.runTaskTimer(plugin, 20, 20);
	}

	/**
	 * キット専用のスコアボード部分
	 * @param o
	 */
	public abstract void kitScoreboard(Objective o);

	/**
	 * スコアボードの設定
	 */
	public void reScoreboard() {
		if(!live) {
			return;
		}
		ScoreboardManager sm = Bukkit.getScoreboardManager();
		Scoreboard sb = sm.getNewScoreboard();
		Objective o = sb.registerNewObjective("KitPvPScoreboard", "dummy", "KitPvP");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		kitScoreboard(o);
		statusScoreboard(o);
		Score effect = o.getScore(ChatColor.WHITE + "<効果>");
		effect.setScore(30);
		Score s = o.getScore(ChatColor.WHITE + "<クールダウン>");
		s.setScore(5);
		for(int i = 0 ; i < 4 ; i++) {
			Score abi;
			if(i != 3) {
				abi = o.getScore((cooldown[i]?ChatColor.YELLOW:ChatColor.AQUA) + KitData.kitweapons[kitnumber][i][lanumber][0] + "：" + (cooldown[i]?"使用可能":cooldownCount[i]));
			}else {
				if(forpassive) {
					abi = o.getScore(ChatColor.YELLOW + KitData.kitweapons[kitnumber][i][lanumber][0] + "：常時");
				}else {
					abi = o.getScore((cooldown[i]?ChatColor.YELLOW:ChatColor.AQUA) + KitData.kitweapons[kitnumber][i][lanumber][0] + "：" + (cooldown[i]?"使用可能":cooldownCount[i]));
				}
			}
			abi.setScore(4 - i);
		}
		Objective ob = sb.registerNewObjective("Health" + player.getDisplayName(), "dummy",ChatColor.RED + "♥");
		ob.setDisplaySlot(DisplaySlot.BELOW_NAME);
		ob.getScore(player.getName()).setScore((int)player.getHealth());
		player.setScoreboard(sb);
	}
	
	/**
	 * 特殊異常状態のスコアボード表示
	 * @param o
	 */
	void statusScoreboard(Objective o) {
		if(silence) {
			Score silences = o.getScore(ChatColor.WHITE + "[サイレンス]");
			silences.setScore(29);
		}
		if(stan) {
			Score stans = o.getScore(ChatColor.YELLOW + "[スタン]");
			stans.setScore(28);
		}
		
	}


	

	//TODO プレイヤーテレポート辺りを観戦と合成して、観戦と重ね合わせたい。
	//TODO attacksupport別の場所に移動しておいて。
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(player == e.getPlayer()) {
			if(stopmove) {
				e.setCancelled(true);
				return;
			}
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(ItemTools.hasItemNotNull(player)) {
					if(player.getInventory().getItemInMainHand().getType() == Material.HEART_OF_THE_SEA) {
						this.heal(10, player, "コア");
						this.healFood(20);
						player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
						return;
					}
					if(key && !silence && live && !easySpell) {
						continueStop();
						int index = player.getInventory().getHeldItemSlot();
						if(index < 4) {
							if(player.getInventory().getItemInMainHand().getType() != Material.BOW) {
								e.setCancelled(true);
							}else if(player.isSneaking()) {
								e.setCancelled(true);
							}
						}
						kpg.ka.doAbility(kitnumber, index, player);
					}else if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("プレイヤーテレポート")) {
						List<Player> players = new ArrayList<Player>();
						players.addAll(kpg.getLivings());
						Collections.shuffle(players);
						Player target = (Player)players.get(0);
						player.teleport(target);
						player.sendMessage(ChatColor.GREEN + target.getDisplayName() + "のもとにテレポートしました。");
					}
				}
			}else if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
				if(key && !silence && live) {
					onLeftClick();
					if(player.getAttackCooldown() >= 1) {
						continueStop();
						int index = player.getInventory().getHeldItemSlot();
						kpg.ka.LeftClickDo(kitnumber, index, player);
						if(attackSupport) {
							ItemStack sword = player.getInventory().getItemInMainHand();
							ItemMeta swordm = sword.getItemMeta();
							swordm.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
							swordm.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
							sword.setItemMeta(swordm);	
						}
					}else if (attackSupport){
						e.setCancelled(true);
						ItemStack sword = player.getInventory().getItemInMainHand();
						ItemMeta swordm = sword.getItemMeta();
						double speed;
						double damage;
						switch(sword.getType()) {
						case WOODEN_SWORD:
							speed=1.6;
							damage=4;
							break;
						case STONE_SWORD:
							speed = 1.6;
							damage = 5;
							break;
						case IRON_SWORD:
							speed = 1.6;
							damage = 6;
							break;
						case GOLDEN_SWORD:
							speed = 1.6;
							damage =4;
							break;
						case DIAMOND_SWORD:
							speed = 1.6;
							damage = 7;
							break;
						case NETHERITE_SWORD:
							speed = 1.6;
							damage = 8;
							break;
						case GOLDEN_AXE:
							speed = 1;
							damage = 7;
							break;
						case STONE_AXE:
							speed = 0.8;
							damage = 9;
						default:
							speed = 1;
							damage = 1;
							break;
						}
						double a = (1 / speed) * 20;
						double f = a * player.getAttackCooldown();
						double x = 20 / f;
						double sus = speed - x;
						swordm.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
						swordm.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
						AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(),"generic.attackspeed",-sus-0.1,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
						swordm.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier);
						AttributeModifier attackm = new AttributeModifier(UUID.randomUUID(),"generic.attackdamage",damage-1,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HAND);
						swordm.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,attackm);
						sword.setItemMeta(swordm);
					}
				}
			}

		}
	}
	
	public void onLeftClick() {}
	
	@EventHandler
	public void onItemChange(PlayerItemHeldEvent e) {
		if(e.getPlayer() == player) {
			if(easySpell) {
				if(e.getNewSlot() < 4 && e.getNewSlot() != 0) {
					if(key && !silence && live) {
						continueStop();
						kpg.ka.doAbility(kitnumber, e.getNewSlot() - 1, player);
					}
				}
				e.setCancelled(true);
				player.getInventory().setHeldItemSlot(0);
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		if(player == e.getPlayer()) {
			if(stopmove) {
				e.setCancelled(true);
				return;
			}
			if(e.getRightClicked() instanceof LivingEntity) {
				if(key && !silence && live && !stan) {
					continueStop();
					int index = player.getInventory().getHeldItemSlot();
					kpg.ka.doAbility(kitnumber, index, player,e.getRightClicked());
				}
			}
		}
	}
	
	//TODO ゲーム中にアイテムが切り替わる系が対応していない。
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getWhoClicked() == player) {
			e.setCancelled(true);
			if(e.getClick() == ClickType.SHIFT_LEFT) {
				setDetail(!getDetail());
				KitData.SetAllWeapon(player, kitnumber, lanumber, getDetail());
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(e.getPlayer() == player) {
			if(getStop() || stan) {
				e.setCancelled(true);
			}
			if(e.getFrom().getX() != e.getTo().getX() ||e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ() ) {
				moven();	
			}
			if(player.getLocation().getY() < 0) {
				Location plo = player.getLocation().clone();
				plo.add(0,10,0);
				player.teleport(plo);
				death();
			}
		}
	}
	
	void moven() {}
	
	@EventHandler
	public void onPut(BlockPlaceEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e) {
		if(e.getDamager() == player) {
			if(attackkey) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDrop(EntityPickupItemEvent e) {
		if(!live && e.getEntity() == player) {
			e.setCancelled(true);
		}
	}
	
	/**
	 * 連続使用の防止
	 */
	public void continueStop() {
		key = false;
		new BukkitRunnable() {
			public void run() {
				key = true;
			}
		}.runTaskLater(plugin, 5);
	}
	
	
	//汎用
	public void setVelocity(Vector v) {
		player.setVelocity(v);
	}
	
	public void sendTitle(String main,String sub,int in,int delay,int out) {
		display = true;
		player.sendTitle(main, sub,in,delay,out);
		new BukkitRunnable() {
			public void run() {
				display = false;
			}
		}.runTaskLater(plugin, in + delay + out);
	}
	
	//GetterとSetter
	public int getKitNumber() {
		return kitnumber;
	}
	public boolean getCooldown(int index) {
		return cooldown[index];
	}
	public void setCooldowntime(int index,int time) {
		cooldownCount[index] = time;
	}
	public int getCooldowntime(int index) {
		return cooldownCount[index];
	}
	public boolean getMuteki() {
		return muteki;
	}
	public void tempMuteki(int timefortick) {
		muteki = true;
		new BukkitRunnable() {
			public void run() {
				muteki = false;
			}
		}.runTaskLater(plugin, timefortick);
	}
	public void setMuteki(boolean muteki) {
		this.muteki = muteki;
	}
	public boolean getDetail() {
		return detail;
	}
	public void setDetail(boolean b) {
		detail = b;
	}
	public boolean getStop() {
		return stopmove;
	}
	public void setStop(boolean b) {
		stopmove = b;
	}
	public boolean getLive() {
		return live;
	}
	public void setLive(boolean live) {
		this.live = live;
	}
	
	public void setSleep(int delayForTicks) {
		this.sleep = new KitSleep(this,plugin);
		int oneTick = delayForTicks/10;
		new BukkitRunnable() {
			int counter = 0;
			public void run() {
				if(sleep == null) {
					this.cancel();
					return;
				}
				if(counter >= 10) {
					awake();
					this.cancel();
					return;
				}
				counter++;
			}
		}.runTaskTimer(plugin, 0, oneTick);
	}
	public boolean isSleep() {
		return sleep != null;
	}
	public void awake() {
		this.sleep.endDream();
		sleep = null;
	}
	
	public boolean getSilence() {
		return silence;
	}
	public void setSilence(boolean s) {
		silence = s;
		reScoreboard();
	}
	public void setSilence(int tick) {
		if(!getSilence() && checkSilence()) {
			setSilence(true);
			new BukkitRunnable() {
				public void run() {
					setSilence(false);
				}
			}.runTaskLater(plugin, tick);
		}
	}
	boolean checkSilence() {
		return true;
	}
	public boolean getStan() {
		return stan;
	}
	public void setStan(boolean stan) {
		this.stan = stan;
		reScoreboard();
	}
	public void tempStan(int timeForTicks) {
		if(!getStan() && checkStan()) {
			setStan(true);
			new BukkitRunnable() {
				public void run() {
					setStan(false);
				}
			}.runTaskLater(plugin,timeForTicks);
		}
	}
	boolean checkStan() {
		return true;
	}
	public void setKey(boolean key) {
		this.key = key;
	}
	
	public boolean getKey() {
		return key;
	}
	
	public void tempSetKey(boolean newkey,int delayForTicks) {
		this.key = newkey;
		new BukkitRunnable() {
			public void run() {
				key = !newkey;
			}
		}.runTaskLater(plugin, delayForTicks);
	}
	
	public void setAttackKey(boolean key) {
		this.attackkey = key;
	}
	public boolean getAttackKey() {
		return attackkey;
	}
	
	public int getSpeed() {
		return speed;
	}
	public void addSpeed(int add) {
		speed += add;
		alterSpeed();
	}
	public void tempAddSpeed(int add,int delayForTick) {
		speed += add;
		alterSpeed();
		new BukkitRunnable() {
			public void run() {
				speed -= add;
				alterSpeed();
			}
		}.runTaskLater(plugin, delayForTick);
	}
	void alterSpeed() {
		if(player.hasPotionEffect(PotionEffectType.SPEED)) {
			player.removePotionEffect(PotionEffectType.SPEED);
		}
		if(player.hasPotionEffect(PotionEffectType.SLOW)) {
			player.removePotionEffect(PotionEffectType.SLOW);
		}
		if(speed > 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,999999,speed-1));
		}else if(speed < 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,99999,-speed-1));
		}
	}
	
	public int getJump() {
		return jump;
	}
	public void addJump(int add) {
		jump += add;
		alterJump();
	}
	public void tempAddJump(int add,int delayForTick) {
		jump += add;
		alterJump();
		new BukkitRunnable() {
			public void run() {
				jump -= add;
				alterJump();
			}
		}.runTaskLater(plugin, delayForTick);
	}
	void alterJump() {
		if(player.hasPotionEffect(PotionEffectType.JUMP)) {
			player.removePotionEffect(PotionEffectType.JUMP);
		}
		if(jump > 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,999999,jump-1));
		}
	}
	
	public Player getPlayer() {
		return player;
	}
}
