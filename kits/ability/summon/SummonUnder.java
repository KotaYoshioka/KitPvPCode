package kits.ability.summon;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.KitSummon;
import kits.ability.KitAbilityBase;

public class SummonUnder extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public SummonUnder(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(kpg.getPlayerData(player) instanceof KitSummon) {
			KitSummon ks = (KitSummon)kpg.getPlayerData(player);
			if(ks.containSummon(1)) {
				if(!ignoreCooldown) {
					kpg.getPlayerData(player).cooldown(abilityindex, cd);
				}
				ks.removeSummon(1);
			}else {
				Object[] result = detectNearPlayer();
				if((Player)result[0] != null) {
					Player target = (Player)result[0];
					double distance = (double)result[1];
					if(distance < 8) {
						Monster zombie = (Monster)player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
						zombie.setTarget(target);
						ks.summon(zombie);
					}else if(distance < 16) {
						Monster skeleton = (Monster)player.getWorld().spawnEntity(player.getLocation(), EntityType.SKELETON);
						skeleton.setTarget(target);
						ks.summon(skeleton);
					}else {
						Monster enderman = (Monster)player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDERMAN);
						enderman.setTarget(target);
						ks.summon(enderman);
					}
				}
			}
		}else {
			Object[] result = detectNearPlayer();
			if((Player)result[0] != null) {
				Player target = (Player)result[0];
				double distance = (double)result[1];
				Monster a;
				if(distance < 8) {
					a = (Monster)player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
					a.setTarget(target);
				}else if(distance < 16) {
					a = (Monster)player.getWorld().spawnEntity(player.getLocation(), EntityType.SKELETON);
					a.setTarget(target);
				}else {
					a = (Monster)player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDERMAN);
					a.setTarget(target);
				}
				new BukkitRunnable() {
					public void run() {
						a.remove();
					}
				}.runTaskLater(plugin, 200);
			}
		}
		
	}
	
	Object[] detectNearPlayer() {
		Location plo = player.getLocation();
		Player target = null;
		double min = 9999999;
		for(Player p:kpg.getLivings()) {
			if(p == player) {
				continue;
			}
			if(p.getLocation().distance(plo) < min) {
				min = p.getLocation().distance(plo);
				target = p;
			}
		}
		Object[] result = {target,min};
		return result;
	}

}
