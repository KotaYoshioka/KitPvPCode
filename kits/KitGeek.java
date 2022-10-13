package kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.ability.geek.GeekCraftView;
import maindatas.KitData;

public class KitGeek extends KitModel{

	
	/*
	 * 0：通常
	 * 1：フレイム
	 * 2：スロー
	 * 3：フラッシュ
	 * 4：盲目
	 * 5：ケルベロス(3方向に分かれる)
	 * 6：ヘッジホッグ(爆散する)
	 * 7：レイン(そこから矢の雨が降る)
	 * 8：リコイル(反動がつく)
	 */
	List<Integer> gadgetMode = new ArrayList<Integer>();
	int parts = 0;
	/*
	 * 1:ウィンド
	 * 2:フレイム
	 * 3:スモーク
	 * 4:フラッシュ
	 * 5:サイレンス
	 * 6:パーティ
	 * 7:バウンド
	 */
	List<Integer> portableMode = new ArrayList<Integer>();
	List<Integer> portableModeShift = new ArrayList<Integer>();
	public int[] portableCost = {0,3,6,3,4,8,8,3};
	
	List<Egg> masterEgg = new ArrayList<Egg>();
	HashMap<Egg,Integer> boundTime = new HashMap<Egg,Integer>();
	HashMap<Egg,Boolean> eggShift = new HashMap<Egg,Boolean>();
	//収集家
	int partsCounter = 0;
	int arrowCounter = 0;
	
	GeekCraftView gcv;
	
	
	public KitGeek(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 1);
		reScoreboard();
	}

	@Override
	public void kitScoreboard(Objective o) {
		Score po = o.getScore("部品：" + parts);
		po.setScore(31);
	}
	
	@EventHandler
	public void onEntityShootBowEvent(EntityShootBowEvent e) {
		if(e.getEntity() == player) {
			if(e.getProjectile() instanceof Arrow) {
				float exp = player.getExp() - 0.333f;
				if(exp <= 0.01) {
					exp = 0;
					player.getInventory().setItem(0, KitData.getWeapon(1, 0, 0, false));
				}
				player.setExp(exp);
				Arrow arrow = (Arrow)e.getProjectile();
				arrow.setPickupStatus(PickupStatus.DISALLOWED);
				List<Integer> gadget = new ArrayList<Integer>();
				gadget.addAll(gadgetMode);
				arrowEffect(arrow);
				if(gadget.contains(5)) {
					kerberos(arrow);
				}else if(gadget.contains(6)) {
					hedgehog(arrow);
				}else if(gadget.contains(7)) {
					rain(arrow);
				}
				if(gadgetMode.contains(8)) {
					player.setVelocity(player.getLocation().getDirection().multiply(-1.5));
				}
			}
		}
	}
	
	@Override
	void moven() {
		partsCounter++;
		arrowCounter++;
		if(player.isSprinting()) {
			partsCounter += 2;
			arrowCounter += 2;
		}
		if(partsCounter > 250) {
			partsCounter = 0;
			if(getParts() >= 100) {
				return;
			}
			setParts(getParts() + 1);
		}
		if(arrowCounter > 400) {
			arrowCounter = 0;
			if(player.getInventory().contains(Material.ARROW)) {
				if(player.getInventory().getItem(player.getInventory().first(Material.ARROW)).getAmount() >= 64) {
					return;
				}
			}
			player.getInventory().addItem(new ItemStack(Material.ARROW));
		}
	}
	
	
	void arrowEffect(Arrow arrow) {
		List<Integer> gadget = new ArrayList<Integer>();
		gadget.addAll(gadgetMode);
		if(gadget.contains(1)) {
			arrow.setFireTicks(100);
		}
		if(gadget.contains(2)) {
			arrow.addCustomEffect(new PotionEffect(PotionEffectType.SLOW,100,2),true);
		}
		if(gadget.contains(3)) {
			arrow.addCustomEffect(new PotionEffect(PotionEffectType.GLOWING,100,1), true);
		}
		if(gadget.contains(4)) {
			arrow.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS,60,1), true);
		}
	}
	
	void kerberos(Arrow formerArrow) {
		List<Integer> gadget = new ArrayList<Integer>();
		gadget.addAll(gadgetMode);
		for(int i = 0; i < 2 ; i++) {
			Vector v = formerArrow.getVelocity().clone();
			v.rotateAroundY((i==0?1:-1) * 0.4);
			Arrow newarrow = player.getWorld().spawnArrow(player.getEyeLocation(),v, 0.6f, 12f);
			newarrow.setDamage(formerArrow.getDamage());
			newarrow.setVelocity(v);
			arrowEffect(newarrow);
			if(gadget.contains(6)) {
				hedgehog(newarrow);
			}else if(gadget.contains(7)) {
				rain(newarrow);
			}
		}
		//元の矢の処理
		if(gadget.contains(6)) {
			hedgehog(formerArrow);
		}else if(gadget.contains(7)) {
			rain(formerArrow);
		}
	}
	
	void hedgehog(Arrow formerArrow) {
		List<Integer> gadget = new ArrayList<Integer>();
		gadget.addAll(gadgetMode);
		new BukkitRunnable() {
			public void run() {
				for(int i = 1 ; i < 8; i++) {
					Vector v = formerArrow.getVelocity().clone();
					v.rotateAroundY(i * 0.775f);
					Arrow newarrow = player.getWorld().spawnArrow(formerArrow.getLocation(), v, 0.6f, 12f);
					newarrow.setDamage(formerArrow.getDamage());
					newarrow.setVelocity(v);
					arrowEffect(newarrow);
					if(gadget.contains(7)) {
						rain(newarrow);
					}
				}
				//元の矢の処理
				if(gadget.contains(7)) {
					rain(formerArrow);
				}
			}
		}.runTaskLater(plugin, 10);
	}
	
	void rain(Arrow formerArrow) {
		new BukkitRunnable() {
			public void run() {
				Random rnd = new Random();
				Location l = formerArrow.getLocation().clone();
				for(int i = 0 ; i < 25 ; i++) {
					new BukkitRunnable() {
						public void run() {
							Vector v = new Vector(0,-1,0);
							Location cl = l.clone();
							cl.add((rnd.nextBoolean()?1:-1)*rnd.nextInt(5) * rnd.nextDouble(),(rnd.nextBoolean()?1:-1)*rnd.nextInt(5) * rnd.nextDouble(),(rnd.nextBoolean()?1:-1)*rnd.nextInt(5) * rnd.nextDouble());
							Arrow newarrow = player.getWorld().spawnArrow(cl, v, 0.6f, 12f);
							newarrow.setDamage(formerArrow.getDamage());
							newarrow.setVelocity(v);
							arrowEffect(newarrow);
						}
					}.runTaskLater(plugin, i * 3);
				}
			}
		}.runTaskLater(plugin, 10);
	}
	
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if(e.getEntity().getShooter() == player) {
			if(e.getEntity() instanceof Egg) {
				e.setCancelled(true);
				Egg egg = (Egg)e.getEntity();
				List<Integer> portable = new ArrayList<Integer>();
				portable.addAll(eggShift.get(egg)?portableModeShift:portableMode);
				if(masterEgg.contains(egg)) {
					if(portable.contains(6)) {
						party(egg);
					}
				}
				if(portable.contains(7)) {
					bound(egg);
				}else {
					portableEffect(egg);
					egg.remove();
				}
			}
			if(kpg.getLivings().contains(e.getHitEntity()) && e.getHitEntity() != player) {
				kpg.getPlayerData(player).healFood(3);
			}
		}
	}
	
	void portableEffect(Egg egg) {
		List<Integer> portable = new ArrayList<Integer>();
		portable.addAll(eggShift.get(egg)?portableModeShift:portableMode);
		Location l = egg.getLocation();
		for(Entity ent:egg.getNearbyEntities(3.5,3.5,3.5)) {
			if(ent instanceof LivingEntity) {
				if(ent instanceof Player) {
					if(!kpg.containsLivings(ent)) {
						continue;
					}
				}
				LivingEntity le = (LivingEntity)ent;
				if(portable.contains(1)) {
					le.setVelocity(new Vector(0,2,0));
				}
				if(portable.contains(2)) {
					le.setFireTicks(80);
				}
				if(portable.contains(4)) {
					le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,160,1));
				}
				if(kpg.getLivings().contains(le) && portable.contains(5)) {
					kpg.getPlayerData((Player)le).setSilence(60);
				}
			}
		}
		if(portable.contains(3)) {
			for(int i = 0 ; i < 50 ; i++) {
				new BukkitRunnable() {
					public void run() {
						Location ll = l.clone();
						l.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, ll, 40, 1,1,1, 0.3);
						l.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, ll, 60, 1,1,1, 0.3);
						ll.add(1,0,1);
						l.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, ll, 40, 1,1,1, 0.3);
						l.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, ll, 60, 1,1,1, 0.3);
						ll.add(-2,0,-2);
						l.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, ll, 40, 1,1,1, 0.3);
						l.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, ll, 60, 1,1,1, 0.3);
						
					}
				}.runTaskLater(plugin, i*2);
			}
		}else {
			player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, l, 50,0.2,0.2,0.2,0.4);
		}
	}
	
	void party(Egg egg) {
		boolean shift = eggShift.get(egg);
		for(int i = 0 ; i < 4 ; i++) {
			Vector v = egg.getVelocity().clone();					
			v.setY(0.7);
			v.rotateAroundY(1.55 * i);
			v.multiply(0.5);
			Location l = egg.getLocation();
			l.add(0, 0.5, 0);
			Egg newegg = (Egg)player.getWorld().spawnEntity(l, EntityType.EGG);
			newegg.setVelocity(v);
			newegg.setShooter(player);
			registerSneakEgg(newegg,shift);
		}
	}
	
	void bound(Egg egg) {
		boolean shift = eggShift.get(egg);
		Vector v = egg.getVelocity().clone();
		Location l = egg.getLocation().clone();
		if(!boundTime.containsKey(egg)) {
			boundTime.put(egg, 3);
		}
		if(boundTime.get(egg) > 0) {
			l.add(0,0.5,0);
			Egg newegg = (Egg)player.getWorld().spawnEntity(l, EntityType.EGG);
			boundTime.put(newegg, boundTime.get(egg) - 1);
			int left = boundTime.get(newegg);
			v.setY(0.2 + (0.2 * left));
			v.multiply(0.5);
			newegg.setVelocity(v);
			newegg.setShooter(player);
			registerSneakEgg(newegg,shift);
			boundTime.remove(egg);
		}else {
			portableEffect(egg);
			boundTime.remove(egg);
			egg.remove();
		}
	}
	
	public void registerMasterEgg(Egg egg) {
		masterEgg.add(egg);
	}
	
	public void registerSneakEgg(Egg egg,boolean sneak) {
		eggShift.put(egg,sneak);
	}

	public int mathPartsProtable(boolean shift) {
		int sum = 0;
		for(int i = 0 ; i < portableCost.length ; i++) {
			if(shift?portableModeShift.contains(i):portableMode.contains(i)) {
				sum += portableCost[i];
			}
		}
		return sum;
	}
	
	public List<Integer> getGadgetMode(boolean shift) {
		return gadgetMode;
	}
	public void addGadgetMode(int mode,boolean shift) {
		if(mode == 0) {
			gadgetMode.clear();
		}
		if(mode != 0 && gadgetMode.contains(0)) {
			gadgetMode.remove((Object)0);
		}
		if(!gadgetMode.contains(mode)) {
			this.gadgetMode.add(mode);
		}else {
			this.gadgetMode.remove((Object)mode);
		}
	}
	public void setGadgetMode(List<Integer> list) {
		gadgetMode.clear();
		gadgetMode.addAll(list);
	}
	public List<Integer> getPortable(boolean shift){
		return shift?portableModeShift:portableMode;
	}
	public void addPortableMode(int mode,boolean shift) {
		if(!shift) {
			if(mode == 0) {
				portableMode.clear();
			}
			if(mode != 0 && portableMode.contains(0)) {
				portableMode.remove((Object)0);
			}
			if(!portableMode.contains(mode)) {
				portableMode.add(mode);
			}else {
				portableMode.remove((Object)mode);
			}
		}else {
			if(mode == 0) {
				portableModeShift.clear();
			}
			if(mode != 0 && portableModeShift.contains(0)) {
				portableModeShift.remove((Object)0);
			}
			if(!portableModeShift.contains(mode)) {
				portableModeShift.add(mode);
			}else {
				portableModeShift.remove((Object)mode);
			}
		}
	}
	public int getParts() {
		return parts;
	}
	public void setParts(int parts) {
		this.parts = parts;
		reScoreboard();
	}
	
	public void craftmode() {
		gcv = new GeekCraftView(kpg.getPlayerData(player),plugin);
		new BukkitRunnable() {
			public void run() {
				if(player.getInventory().getHeldItemSlot() != 0) {
					this.cancel();
					gcv.stop();
					gcv = null;
					kpg.getPlayerData(player).setKey(true);
					return;
				}else if(!gcv.getPlay()) {
					this.cancel();
					gcv = null;
					kpg.getPlayerData(player).setKey(true);
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	public boolean nowCraftMode() {
		return gcv != null;
	}
}
