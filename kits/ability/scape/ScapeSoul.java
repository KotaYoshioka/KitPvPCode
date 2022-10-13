package kits.ability.scape;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class ScapeSoul extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public ScapeSoul(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
		}
		Location nowl = player.getEyeLocation().clone();
		Location tol = player.getTargetBlock(null, 6).getLocation();
		double addx = (tol.getX() - nowl.getX()) * 0.1;
		double addy = (tol.getY() - nowl.getY()) * 0.1;
		double addz = (tol.getZ() - nowl.getZ()) * 0.1;
		new BukkitRunnable() {
			Location l = nowl.clone();
			int counter = 0;
			public void run() {
				Location ll = l.clone();
				ll.add(addx * counter,addy * counter,addz * counter);
				Bukkit.getLogger().info( "" + ll);
				player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, ll, 60, 0.3, 0.3, 0.3, 0);
				for(Entity ent:player.getWorld().getNearbyEntities(ll, 2,2,2)) {
					if(ent == player) {
						continue;
					}
					if(ent instanceof LivingEntity) {
						LivingEntity le = (LivingEntity)ent;
						if(le instanceof Player) {
							if(kpg.getLivings().contains(le)) {
								
							}else {
								continue;
							}
						}
						le.setFireTicks(100);
					}
				}
				counter += 2;
				if(counter >= 41) {
					this.cancel();
					player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, ll, 100,0.5,0.5,0.5,0.4);
					for(Entity ent:player.getWorld().getNearbyEntities(ll,3,3,3)) {
						if(ent instanceof LivingEntity) {
							LivingEntity le = (LivingEntity)ent;
							if(le instanceof Player) {
								if(kpg.getLivings().contains(le)) {
									Player target = (Player)le;
									kpg.getPlayerData(target).damage(10, player, "グナット", true);
									kpg.getPlayerData(target).reduceFood(9);
								}else {
									continue;
								}
							}else {
								le.damage(10);
							}
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}

}
