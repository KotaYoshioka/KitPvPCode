package kits.ability.sand;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class SandKutitakaze extends KitAbilityBase{

	public SandKutitakaze(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		ArmorStand arasi = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(),EntityType.ARMOR_STAND);
		arasi.setVisible(false);
		arasi(arasi);
	}
	
	void arasi(ArmorStand arasi) {
		Set<Player> players = new HashSet<Player>();
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				Location lo = arasi.getLocation();
				for(int i = 0 ; i < 10 ; i++) {
					lo.getWorld().spawnParticle(Particle.BLOCK_DUST,lo,30,0.5 * i,1,0.5 * i,0.1,Material.SAND.createBlockData());
					lo.getWorld().spawnParticle(Particle.FALLING_DUST,lo,15,0.5 * i,1,0.5 * i,0.1,Material.SAND.createBlockData());
					for(Entity ent:lo.getWorld().getNearbyEntities(lo, 0.5 * i , 1, 0.5 * i)) {
						if(kpg.containsLivings(ent) && ent != player) {
							Player target = (Player)ent;
							if(!players.contains(target)) {
								players.add(target);
								kpg.getPlayerData(target).tempStan(20);
								new BukkitRunnable() {
									public void run() {
										target.setVelocity(new Vector(0,3,0));
										kpg.getPlayerData(target).damage(4, player, "朽ちた風", true);
										new BukkitRunnable() {
											public void run() {
												players.remove(target);
											}
										}.runTaskLater(plugin, 80);
									}
								}.runTaskLater(plugin,21);
							}
						}
					}
					lo.add(0,1,0);
				}
			}
		}.runTaskTimer(plugin, 0, 3);
		new BukkitRunnable() {
			public void run() {
				bt.cancel();
				arasi.remove();
			}
		}.runTaskLater(plugin, 260);
	}

}
