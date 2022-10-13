package kits.ability.time;

import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitTime;
import kits.ability.KitAbilityBase;

public class TimeReturn extends KitAbilityBase{

	public TimeReturn(KitPvPGame kpg, Player player, int kitnumber, int index) {
		super(kpg, player,kitnumber,index);		
		doAbility();
	}

	@Override
	protected void doAbility() {
		KitTime kt = (KitTime)kpg.getPlayerData(player);
		kt.toggleReturn();
	}

}
