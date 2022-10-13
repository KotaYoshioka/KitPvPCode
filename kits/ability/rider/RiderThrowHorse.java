package kits.ability.rider;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.ability.KitAbilityProjectile;

public class RiderThrowHorse extends KitAbilityProjectile{

	boolean black;
	
	
	public RiderThrowHorse(Player player, Plugin plugin, KitPvPGame kpg, Entity ent, Location sumLo,boolean black) {
		super(player, plugin, kpg, ent, sumLo, 5, 3);
		this.black = black;
	}

	@Override
	protected void after() {
		new BukkitRunnable() {
			public void run() {
				if(black)body.setVelocity(player.getLocation().getDirection().normalize().multiply(5));		
			}
		}.runTaskLater(plugin, 1);
		Vector v = player.getLocation().getDirection().clone().normalize().multiply(0.6);
		if(!black) {
			new BukkitRunnable() {
				public void run() {
					if(!death) {
						death();
					}
				}
			}.runTaskLater(plugin, 80);
		}
		new BukkitRunnable() {
			public void run() {
				if(death || !body.isValid()) {
					this.cancel();
					return;
				}
				if(black) {
					Vector v = body.getVelocity();
					if(v.getX() < 0.001 && v.getY() < 0.001 && v.getZ() < 0.001) {
						death();
						return;
					}
				}else {
					body.setVelocity(v);
				}
			}
		}.runTaskTimer(plugin, 6, 1);
	}

	@Override
	protected void hit(Entity ent) {
		if(isLivingEntity(ent) && ent != player && !(ent instanceof Horse) && !(ent instanceof Ravager)) {
			death();
		}
	}
	
	@Override
	public void death() {
		player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE,body.getLocation(),60,2,2,2,0.5);
		for(Entity ent:body.getNearbyEntities(3,3,3)) {
			if(ent instanceof LivingEntity && !(ent instanceof ArmorStand) && ent != player) {
				LivingEntity le = (LivingEntity)ent;
				if(kpg.containsLivings(le)) {
					Player target = (Player)le;
					int damage = 4;
					if(body.getType() != EntityType.HORSE) {
						damage = 8;
					}
					kpg.getPlayerData(target).damage(damage, player, "投げ飛ばし", false);
				}else {
					le.damage(4);
				}
				le.setVelocity(body.getVelocity().multiply(2));
			}
		}
		super.death();
	}


}
