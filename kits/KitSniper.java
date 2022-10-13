package kits;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import game.KitPvPGame;
import kits.ability.sniper.GunData;
import kits.ability.sniper.GunmanShot;
import maindatas.KitData;

public class KitSniper extends KitModel{


	int[] bullets = {0,0,0};
	int[] gunID = {0,0,0};
	
	Set<Location> locs = new HashSet<Location>();
	
	public KitSniper(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 13);
		new BukkitRunnable() {
			public void run() {
				Random rnd = new Random();
				setGun(0,rnd.nextInt(GunData.gunName.length));
			}
		}.runTaskLater(plugin, 5);
		gap();
		showGap();
		loop();
	}
	
	void showGap() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				for(Location l:locs) {
					player.spawnParticle(Particle.ELECTRIC_SPARK,l,20,0.5,0.5,0.5,0.5);
				}
			}
		}.runTaskTimer(plugin, 0, 5);
	}
	
	void loop() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				float exp = player.getExp() + 0.002f;
				if(exp >= 1) {
					gap();
					exp = 0;
				}
				player.setExp(exp);
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	void gap() {
		Location l = player.getLocation().clone();
		Random rnd = new Random();
		l.add(rnd.nextInt(10) * (rnd.nextBoolean()?-1:1),15,rnd.nextInt(10) * (rnd.nextBoolean()?-1:1));
		under(l);
	}
	
	void under(Location l) {
		player.spawnParticle(Particle.ELECTRIC_SPARK,l,20,1,1,1,1);
		if(l.getBlock().getType() != Material.AIR) {
			l.add(0,1,0);
			locs.add(l);
			return;
		}else {
			l.add(0,-1,0);
			if(l.getY() < 2) {
				locs.add(l);
				return;
			}
		}
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				under(l);
			}
		}.runTaskLater(plugin, 1);
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		if(e.getPlayer() == player) {
			if(!player.isSneaking()) {
				if(player.getInventory().getHeldItemSlot() < 3) {
					int index = player.getInventory().getHeldItemSlot();
					if(getCooldown(index) && gunID[index] == 3) {
						new GunmanShot(kpg,player,index);
					}
				}
			}else if(player.getInventory().getHeldItemSlot() < 3){
				Location l = player.getLocation().clone();
				Location target = null;
				for(Location cl:locs) {
					if(l.distance(cl) < 2) {
						target = cl;
					}
				}
				if(target != null) {
					locs.remove(target);
					setGun(player.getInventory().getHeldItemSlot(),new Random().nextInt(GunData.gunName.length));
				}
			}
		}
	}
	
	public void setGun(int index,int newGunID) {
		gunID[index] = newGunID;
		if(newGunID != 0) {
			setBullet(index,GunData.bullets[newGunID]);
			player.getInventory().setItem(index, GunData.getGun(newGunID));
		}else {
			player.getInventory().setItem(index, KitData.getWeapon(13, index, 0, false));
		}
	}
	
	public int getGun(int index) {
		return gunID[index];
	}
	
	public int getBullet(int index) {
		return bullets[index];
	}
	
	public void setBullet(int index, int newBullet) {
		bullets[index] = newBullet;
		player.setLevel(newBullet);
	}
	
	@EventHandler
	public void onHeldItem(PlayerItemHeldEvent e) {
		if(e.getPlayer() == player) {
			if(e.getNewSlot() < 3) {
				player.setLevel(getBullet(e.getNewSlot()));
			}else {
				player.setLevel(0);
			}
		}
	}
	
}
