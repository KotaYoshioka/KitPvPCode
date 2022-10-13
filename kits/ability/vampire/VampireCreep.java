package kits.ability.vampire;

import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class VampireCreep extends KitAbilityBase{

	public VampireCreep(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		summonBat();
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		for(Player p:kpg.getPlayers()) {
			p.hidePlayer(plugin,player);
		}
		player.setAllowFlight(true);
		player.setFlying(true);
		new BukkitRunnable() {
			public void run() {
				if(kpg.getLivings().contains(player)) {
					for(Player p:kpg.getPlayers()) {
						p.showPlayer(plugin,player);
					}
					player.setAllowFlight(false);
					summonBat();
				}
			}
		}.runTaskLater(plugin, 50);
	}
	
	void summonBat() {
		for(int i = 0 ; i < 7 ; i++) {
			Bat b = (Bat)player.getWorld().spawnEntity(player.getLocation(),EntityType.BAT);
			b.setNoDamageTicks(99999);
			if(player.getFireTicks() > 0) {
				b.setFireTicks(99999);
			}
			new BukkitRunnable() {
				public void run() {
					if(b.isValid()) {
						b.remove();
					}
				}
			}.runTaskLater(plugin, 140);
		}
	}

}
