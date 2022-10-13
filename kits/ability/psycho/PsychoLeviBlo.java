package kits.ability.psycho;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.ability.KitAbilityProjectile;

public class PsychoLeviBlo extends KitAbilityProjectile{

	double ygoal;
	Vector v;
	Material m;
	public PsychoLeviBlo(Player player, Plugin plugin, KitPvPGame kpg, Entity ent, Location sumLo, int power,Material m) {
		super(player, plugin, kpg, ent, sumLo, power,1);
		this.m = m;
	}

	@Override
	protected void after() {
		Location l = player.getLocation().clone();
		ygoal = l.getY();
		if(Math.abs(body.getLocation().getY()-ygoal) < 4 ) {
			ygoal = l.clone().getY() + 5;
		}
		v = l.getDirection();
		levi();
	}
	
	void levi() {
		new BukkitRunnable() {
			public void run() {
				if(!body.isValid()) {
					this.cancel();
					return;
				}
				player.getWorld().spawnParticle(Particle.BLOCK_CRACK,body.getLocation(),5,0.5,0.3,0.5,0,m.createBlockData());
				player.getWorld().spawnParticle(Particle.BLOCK_DUST,body.getLocation(),5,0.5,0.3,0.5,0,m.createBlockData());
				player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,body.getLocation(),10,1,1,1,0);
				if(!kpg.getPlayerData(player).getLive()) {
					this.cancel();
					return;
				}
				double y = body.getLocation().getY();
				if(y > ygoal) {
					body.setVelocity(v.normalize().multiply(2));
					player.getWorld().spawnParticle(Particle.BLOCK_CRACK,body.getLocation(),30,0.5,0.5,0.5,1,m.createBlockData());
					player.getWorld().spawnParticle(Particle.BLOCK_DUST,body.getLocation(),30,0.5,0.3,0.5,1,m.createBlockData());
					player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,body.getLocation(),20,1,1,1,0);
				}else {
					body.setVelocity(new Vector(0,0.3,0));
					levi();
				}
			}
		}.runTaskLater(plugin, 1);
	}
	
	

	@Override
	protected void hit(Entity ent) {
		if(ent != player && kpg.containsLivings(ent)) {
			Player target = (Player)ent;
			kpg.getPlayerData(target).damage(5, player, "レビテーション", true);
			kpg.getPlayerData(player).tempAddSpeed(-1, 40);
			death();
		}
	}

}
