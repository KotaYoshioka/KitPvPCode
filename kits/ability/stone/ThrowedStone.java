package kits.ability.stone;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kitdatas.StoneData;
import kits.ability.KitAbilityProjectile;

public class ThrowedStone extends KitAbilityProjectile{

	boolean instone = false;
	
	public ThrowedStone(Player player, Plugin plugin, KitPvPGame kpg, Entity body, Location sumLo, int power) {
		super(player, plugin, kpg, body, sumLo, power, 0.8d);
		Location l = player.getLocation();
		l.add(0,-1,0);
		if(l.getBlock().getType() == Material.STONE) {
			instone = true;
		}
	}

	@Override
	protected void after() {
		body.setVelocity(player.getLocation().getDirection().normalize().multiply(2));
		new BukkitRunnable() {
			public void run() {
				if(!body.isValid()) {
					this.cancel();
					return;
				}
				body.getWorld().spawnParticle(Particle.BLOCK_CRACK,body.getLocation(),5,0.1,0.1,0.1,Material.STONE.createBlockData());
			}
		}.runTaskTimer(plugin, 0, 5);
	}

	@Override
	protected void hit(Entity ent) {
		if(ent != player && kpg.containsLivings(ent)) {
			Player target = (Player)ent;
			int damage = StoneData.throwDamage;
			if(instone) {
				damage += StoneData.throwAddDamage;
			}
			kpg.getPlayerData(target).damage(damage, player, "フリースロー", false);
			kpg.getPlayerData(target).tempAddSpeed(-4, 50);
			target.setVelocity(new Vector(0,-5,0));
			body.getWorld().spawnParticle(Particle.BLOCK_CRACK,body.getLocation(),40,0.3,0.3,0.3,Material.STONE.createBlockData());
			death();
		}
	}

}
