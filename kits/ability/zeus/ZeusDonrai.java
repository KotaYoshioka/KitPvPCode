package kits.ability.zeus;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.KitZeus;
import kits.ability.KitAbilityBase;

public class ZeusDonrai extends KitAbilityBase{

	public ZeusDonrai(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		kpg.getPlayerData(player).damage(7, player, "曇雷", false);
		((KitZeus)kpg.getPlayerData(player)).addCounter();
		Location lo = player.getLocation().clone();
		Vector v = lo.getDirection().clone().normalize();
		lo.add(0,5,0);
		Random rnd = new Random();
		BukkitTask bt = new BukkitRunnable() {
			int counter = 0;
			public void run() {
				lo.add(v);
				lo.getWorld().spawnParticle(Particle.CLOUD,lo,150,4.5,1,4.5,0);
				counter++;
				if(counter % 2 == 0) {
					Location clo = lo.clone();
					clo.add((rnd.nextInt(5)+rnd.nextDouble())*(rnd.nextBoolean()?1:-1),0,(rnd.nextInt(5)+rnd.nextDouble())*(rnd.nextBoolean()?1:-1));
					for(int i = 0 ; i < 100; i++) {
						if(clo.getBlock().getType() != Material.AIR) {
							clo.setY(clo.getY() + 1);
							clo.getWorld().strikeLightning(clo);
							break;
						}else {
							clo.setY(clo.getY() - 1);
							if(clo.getY() <= 0) {
								break;
							}
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0, 13);
		new BukkitRunnable() {
			public void run() {
				bt.cancel();
			}
		}.runTaskLater(plugin, 200);
	}

}
