package kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import maindatas.KitData;

public class KitSparkle extends KitModel{

	List<Location> puts = new ArrayList<Location>();
	
	int chain = 0;
	
	public KitSparkle(Plugin plugin, Player player, KitPvPGame kpg, int kitnumber) {
		super(plugin, player, kpg, kitnumber);
		player.setAllowFlight(true);
		player.setExp(0.99f);
		putsVisual();
		healGauge();
	}
	
	void healGauge() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				float level = player.getExp();
				level = level + 0.01f;
				if(level >= 1) {
					level = 0.99f;
				}
				player.setExp(level);
			}
		}.runTaskTimer(plugin, 0, 10);
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	@Override
	public Object[] exDamage(Player p,double damage,Player causer,String cause,boolean canHeal,boolean damagable) {
		Object[] a = {p,damage,causer,cause,canHeal,damagable};
		if(causer == player && canHeal && player.getLevel() > 0) {
			int level = player.getLevel() - 1;
			player.setLevel(level);
			if(level == 0) {
				cooldown(0,KitData.kitweaponcool[getKitNumber()][0]);
			}
			new BukkitRunnable() {
				public void run() {
					if(kpg.getPlayerData(p).getLive()) {
						explosion(p.getLocation(),1,false);
					}
				}
			}.runTaskLater(plugin, 16);
		}
		return a;
	}
	
	public void explosion(Location lo,int level,boolean self) {
		level = level + chain;
		chain = 0;
		if(level > 12) {
			level = 12;
		}
		lo.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, lo,level * 5,level * 0.8,level*0.8,level*0.8,1);
		lo.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, lo,level * 10,0.8,0.8,0.8,0.2);
		lo.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, lo,level * 3,level,level,level,1);
		lo.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, lo,level * 10,level * 1.5,level*1.5,level*1.5,1);
		lo.getWorld().spawnParticle(Particle.FLAME, lo,level * 20,level * 1.5,level*1.5,level*1.5,0.4);
		lo.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, lo,level * 40,level * 1.5,0.5,level*1.5,0.2);
		lo.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, lo,level * 40,level * 1.5,0.5,level*1.5,0.2);
		lo.getWorld().spawnParticle(Particle.SMOKE_LARGE, lo,level * 20,level * 1.5,0.5,level*1.5,0.2);
		
		for(Entity ent:lo.getWorld().getNearbyEntities(lo, level * 2, level * 2, level * 2)) {
			if(ent instanceof LivingEntity && !(ent instanceof ArmorStand)) {
				if(!self && ent == player) {
					continue;
				}
				if(ent instanceof Player) {
					if(!kpg.containsLivings(ent)) {
						continue;
					}
					Player target = (Player)ent;
					kpg.getPlayerData(target).damage(level,player,"爆破",false);
				}else {
					LivingEntity le = (LivingEntity)ent;
					le.damage(level);
				}
				Location tlo = ent.getLocation();
				Vector v = new Vector(tlo.getX()-lo.getX(),tlo.getY()-lo.getY(),tlo.getZ()-lo.getZ()).normalize().multiply(level * 1.5);
				ent.setVelocity(v);
			}
		}
	}
	
	void putsVisual() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				for(Location l:puts) {
					player.spawnParticle(Particle.SMOKE_LARGE, l,8,0,0.2,0,0.1);
				}		
			}
		}.runTaskTimer(plugin, 0, 5);
	}
	
	@EventHandler
	public void setVelocity(PlayerToggleFlightEvent e) {
		if(e.getPlayer() == player && live) {
			e.setCancelled(true);
			if(player.getLevel() > 0) {
				int level = player.getLevel() - 1;
				player.setLevel(level);
				if(level == 0) {
					cooldown(0,KitData.kitweaponcool[getKitNumber()][0]);
				}
				explosion(player.getLocation(),1,false);
				player.setVelocity(player.getLocation().getDirection().normalize().multiply(2));
			}
		}
	}
	
	@EventHandler
	public void sneak(PlayerToggleSneakEvent e) {
		if(e.getPlayer() == player) {
			if(!player.isSneaking()) {
				if(player.getExp() - 0.1f >= 0) {
					float exp = player.getExp() - 0.1f;
					player.setExp(exp);
					player.setVelocity(new Vector(0,0.2,0));
					chain++;
					Location lo = player.getLocation();
					int level = 1;
					lo.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, lo,level * 5,level * 0.8,level*0.8,level*0.8,1);
					lo.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, lo,level * 10,0.8,0.8,0.8,0.2);
					lo.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, lo,level * 3,level,level,level,1);
					lo.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, lo,level * 10,level * 1.5,level*1.5,level*1.5,1);
					lo.getWorld().spawnParticle(Particle.FLAME, lo,level * 20,level * 1.5,level*1.5,level*1.5,0.4);
					lo.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, lo,level * 40,level * 1.5,0.5,level*1.5,0.2);
					lo.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, lo,level * 40,level * 1.5,0.5,level*1.5,0.2);
					lo.getWorld().spawnParticle(Particle.SMOKE_LARGE, lo,level * 20,level * 1.5,0.5,level*1.5,0.2);
				}
			}
		}
	}

	public void addPuts(Location lo) {
		puts.add(lo);
	}
	
	public List<Location> getPuts(){
		return puts;
	}
	
	public void clearPuts() {
		puts.clear();
	}
	
	public boolean containsPuts(Location lo) {
		return puts.contains(lo);
	}
}
