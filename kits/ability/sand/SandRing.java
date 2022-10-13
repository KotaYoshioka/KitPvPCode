package kits.ability.sand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class SandRing extends KitAbilityBase{

	List<Player> targets = new ArrayList<Player>();
	List<LivingEntity> catchers = new ArrayList<LivingEntity>();
	
	public SandRing(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		Location location = player.getLocation();
		int size = 2;
		for(int d = 0 ; d <= 90 ; d +=3) {
			Location loc = location.clone();
			loc.setX(location.getX() + Math.cos(d) * size);
			loc.setZ(location.getZ() + Math.sin(d) * size);
			ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			as.setVisible(false);
			Location plo = player.getLocation().clone();
			Vector v = new Vector(loc.getX() - plo.getX(),0,loc.getZ() - plo.getZ()).clone();
			wave(as,v);
		}
	}
	
	void wave(ArmorStand as,Vector v) {
		List<Location> loc = new ArrayList<Location>();
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				for(Entity ent:as.getNearbyEntities(1.5, 1.5, 1.5)) {
					if(ent != player && kpg.containsLivings(ent)) {
						Player target = (Player)ent;
						if(!targets.contains(target)) {
							targets.add(target);
							kpg.getPlayerData(target).tempAddSpeed(-2, 80);
						}
					}
				}
				as.setVelocity(v);
				Location l = as.getLocation();
				loc.add(l.clone());
				l.add(0,0.7,0);
				as.getWorld().spawnParticle(Particle.FALLING_DUST,l,20,0.5,0.5,0.5,0,Material.SAND.createBlockData());
			}
		}.runTaskTimer(plugin, 0, 2);
		new BukkitRunnable() {
			public void run() {
				bt.cancel();
				returnWave(as,loc);
			}
		}.runTaskLater(plugin, 20);
	}
	
	void returnWave(ArmorStand as,List<Location> loc) {
		Collections.reverse(loc);
		List<LivingEntity> reves = new ArrayList<LivingEntity>();
		returnWavePhase(as,loc,0,reves);
	}
	
	void returnWavePhase(ArmorStand as,List<Location> loc,int index,List<LivingEntity> reves) {
		Location l = loc.get(index).clone();
		as.getWorld().spawnParticle(Particle.FALLING_DUST,l,20,0.5,0.5,0.5,0,Material.SAND.createBlockData());
		as.teleport(l);
		for(LivingEntity le:reves) {
			Location lc = l.clone();
			lc.setDirection(le.getLocation().getDirection());
			le.teleport(lc);
		}
		for(Entity ent:as.getNearbyEntities(1.5, 1.5, 1.5)) {
			if(ent instanceof LivingEntity && !catchers.contains(ent)) {
				LivingEntity le = (LivingEntity)ent;
				if(le != player) {
					catchers.add(le);
					reves.add(le);
				}
			}
		}
		new BukkitRunnable() {
			public void run() {
				if(index + 1 != loc.size()) {
					returnWavePhase(as,loc,index+1,reves);
				}else {
					as.remove();
				}
			}
		}.runTaskLater(plugin, 4);
	}

}
