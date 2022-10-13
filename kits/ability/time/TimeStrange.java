package kits.ability.time;

import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitTime;
import kits.ability.KitAbilityBase;

public class TimeStrange extends KitAbilityBase{

	public TimeStrange(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		((KitTime)kpg.getPlayerData(player)).toggleStrange();
	}

}
