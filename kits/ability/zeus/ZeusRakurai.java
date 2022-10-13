package kits.ability.zeus;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.KitZeus;
import kits.ability.KitAbilityBase;

public class ZeusRakurai extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public ZeusRakurai(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
		}
		((KitZeus)kpg.getPlayerData(player)).addCounter();
		kpg.getPlayerData(player).damage(3, player, "過電", false);
		Random rnd = new Random();
		for(int i = 1 ; i <= 3 ; i ++) {
			new BukkitRunnable() {
				public void run() {
					Location clo = player.getLocation().clone();
					clo.add((rnd.nextInt(6) + rnd.nextDouble()) * (rnd.nextBoolean()?1:-1),0,(rnd.nextInt(6) + rnd.nextDouble()) * (rnd.nextBoolean()?1:-1));
					player.getWorld().strikeLightning(clo);
				}
			}.runTaskLater(plugin, 5 * i);
		}
	}

}
