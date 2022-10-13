package kits.ability.sparkle;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import game.KitPvPGame;
import kitdatas.SparkleData;
import kits.KitSparkle;
import kits.ability.KitAbilityBase;

public class SparklePut extends KitAbilityBase{

	KitSparkle kb;
	
	public SparklePut(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean right) {
		super(kpg, player, kitnumber, abilityindex);
		kb = (KitSparkle)kpg.getPlayerData(player);
		if(right) {
			doAbility();	
		}else {
			if(kb.getPuts().size() > 0)explosion(kb.getPuts());
		}
	}

	@Override
	protected void doAbility() {
		kb.cooldown(abilityindex, cd);
		Location lo = player.getLocation().clone();
		kb.addPuts(lo);
		BukkitTask task = new BukkitRunnable() {
			public void run() {
				if(kb.containsPuts(lo)) {
					player.getWorld().spawnParticle(Particle.SMOKE_LARGE, lo,8,0,0.2,0,0.1);
					player.getWorld().spawnParticle(Particle.FLAME, lo,8,0,0.2,0,0.1);
				}
			}
		}.runTaskTimer(plugin, 0, 5);
		new BukkitRunnable() {
			public void run() {
				task.cancel();
			}
		}.runTaskLater(plugin, SparkleData.ornamentVisibleTicks);
	}
	
	void explosion(List<Location> los) {
		Location l = los.get(0);
		List<Location> near = new ArrayList<Location>();
		for(Location ls: los) {
			if(l.distance(ls) < SparkleData.ornamentDistance) {
				near.add(ls);
			}
		}
		for(Location ne:near) {
			los.remove(ne);
		}
		int level = near.size();
		if(level > 8) {
			level = 8;
		}
		kb.explosion(l,level, true);
		if(los.size() > 0) {
			explosion(los);
		}else {
			kb.clearPuts();	
		}
	}

}
