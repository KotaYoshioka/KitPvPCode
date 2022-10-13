package kits.ability.heiz;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kitdatas.HeizData;
import kits.KitHeiz;
import kits.ability.KitAbilityProjectile;

public class HeizHebiBody extends KitAbilityProjectile{

	Set<LivingEntity> already = new HashSet<LivingEntity>();
	Vector v;
	
	public HeizHebiBody(Player player, Plugin plugin, KitPvPGame kpg, Entity ent) {
		super(player, plugin, kpg, ent, player.getEyeLocation(), 1, 5);
	}

	@Override
	protected void after() {
		v = player.getLocation().getDirection().clone().normalize().multiply(2);
		Location l = player.getEyeLocation().clone();
		new BukkitRunnable() {
			public void run() {
				if(death) {
					this.cancel();
					return;
				}
				l.add(v);
				body.teleport(l);
				player.getWorld().spawnParticle(Particle.CLOUD,body.getLocation(),20,0.5,0.5,0.5,0.5);
			}
		}.runTaskTimer(plugin, 0, 2);
		new BukkitRunnable() {
			public void run() {
				if(!death) {
					death();
				}
			}
		}.runTaskLater(plugin, HeizData.hebiLiveTicks);
	}

	@Override
	protected void hit(Entity ent) {
		if(isLivingEntity(ent)) {
			if(ent != player) {
				LivingEntity le = (LivingEntity)ent;
				if(!already.contains(le)) {
					already.add(le);
					if(kpg.containsLivings(le)) {
						kpg.getPlayerData((Player)le).tempAddSpeed(-1, (int)(HeizData.hebiSlowSeconds * 20));
						((KitHeiz)kpg.getPlayerData(player)).teleport((Player)le);
					}
					Vector vv = v.clone();
					new BukkitRunnable() {
						public void run() {
							le.setVelocity(vv);
						}
					}.runTaskLater(plugin, 1);
				}
			}
		}
	}

}
