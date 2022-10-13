package kits.ability.summon;

import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.KitSummon;
import kits.ability.KitAbilityBase;

public class SummonUpper extends KitAbilityBase{
	boolean ignoreCooldown;
	public SummonUpper(KitPvPGame kpg, Player player, int kitnumber, int abilityindex, boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}
	
	void move(EnderDragon ed) {
		new BukkitRunnable() {
			public void run() {
				if(ed.isDead() || !ed.isValid()) {
					this.cancel();
					return;
				}
				ed.setVelocity(player.getLocation().getDirection());
			}
		}.runTaskTimer(plugin, 0, 2);
	}
	
	@Override
	protected void doAbility() {
		if(kpg.getPlayerData(player) instanceof KitSummon) {
			KitSummon ks = (KitSummon)kpg.getPlayerData(player);
			if(ks.containSummon(3)) {
				if(!ignoreCooldown) {
					kpg.getPlayerData(player).cooldown(abilityindex, cd);
				}
				ks.removeSummon(3);
			}else {
				Object[] result = detectNearPlayer();
				if((Player)result[0] != null) {
					Player target = (Player)result[0];
					if(!((Entity)player).isOnGround()) {
						EnderDragon dragon = (EnderDragon)player.getWorld().spawnEntity(player.getLocation(),EntityType.ENDER_DRAGON);
						ks.summon(dragon);
						dragon.addPassenger(player);
						dragon.setTarget(target);
						move(dragon);
					}else {
						Monster wither = (Monster)player.getWorld().spawnEntity(player.getLocation(),EntityType.WITHER);
						ks.summon(wither);
						wither.setTarget(target);
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
				Monster a;
				a = (Monster)player.getWorld().spawnEntity(player.getLocation(),EntityType.WITHER);
				a.setTarget(target);
				new BukkitRunnable() {
					public void run() {
						a.remove();
					}
				}.runTaskLater(plugin, 100);
				
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
