package kits.ability.sand;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class SandZangeki extends KitAbilityBase{


	boolean ignoreCooldown = false;
	
	public SandZangeki(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCool) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCool;
		doAbility();
	}

	@Override
	protected void doAbility() {
		//クールダウン処理
		if(!ignoreCooldown) {
			km.cooldown(abilityindex,cd);	
		}
		for(int i = 0 ; i < 3 ; i++) {
			new BukkitRunnable() {
				public void run() {
					new SandZangekiBody(player,plugin,kpg);
				}
			}.runTaskLater(plugin, 7 * i);
		}
	}

}
