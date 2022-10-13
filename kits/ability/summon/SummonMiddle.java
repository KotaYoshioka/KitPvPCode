package kits.ability.summon;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.KitSummon;
import kits.ability.KitAbilityBase;

public class SummonMiddle extends KitAbilityBase{
	boolean ignoreCooldown;
	
	public SummonMiddle(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(kpg.getPlayerData(player) instanceof KitSummon) {
			KitSummon ks = (KitSummon)kpg.getPlayerData(player);
			if(ks.containSummon(2)) {
				if(!ignoreCooldown) {
					kpg.getPlayerData(player).cooldown(abilityindex, cd);
				}
				ks.removeSummon(2);
			}else {
				Object[] result = detectNearPlayer();
				if((Player)result[0] != null) {
					Player target = (Player)result[0];
					double distance = (double)result[1];
					if(distance < 8) {
						Monster zombie = (Monster)player.getWorld().spawnEntity(player.getLocation(), EntityType.WITHER_SKELETON);
						zombie.setTarget(target);
						ks.summon(zombie);
					}else if(distance < 16) {
						Monster skeleton = (Monster)player.getWorld().spawnEntity(player.getLocation(), EntityType.BLAZE);
						skeleton.setTarget(target);
						ks.summon(skeleton);
					}else {
						Phantom enderman = (Phantom)player.getWorld().spawnEntity(player.getLocation(), EntityType.PHANTOM);
						enderman.setTarget(target);
						ks.summon(enderman);
					}
				}
			}
		}else {
			Object[] result = detectNearPlayer();
			if((Player)result[0] != null) {
				if(!ignoreCooldown) {
					kpg.getPlayerData(player).cooldown(abilityindex, cd);
				}
				Player target = (Player)result[0];
				double distance = (double)result[1];
				Monster a;
				if(distance < 8) {
					a = (Monster)player.getWorld().spawnEntity(player.getLocation(), EntityType.WITHER_SKELETON);
					a.setTarget(target);
					new BukkitRunnable() {
						public void run() {
							a.remove();
						}
					}.runTaskLater(plugin, 200);
				}else if(distance < 16) {
					a = (Monster)player.getWorld().spawnEntity(player.getLocation(), EntityType.BLAZE);
					a.setTarget(target);
					new BukkitRunnable() {
						public void run() {
							a.remove();
						}
					}.runTaskLater(plugin, 200);
				}else {
					Phantom ph = (Phantom)player.getWorld().spawnEntity(player.getLocation(), EntityType.PHANTOM);
					ph.setTarget(target);
					new BukkitRunnable() {
						public void run() {
							ph.remove();
						}
					}.runTaskLater(plugin, 200);
				}
				
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
