package kits.ability.time;

import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitTime;
import kits.ability.KitAbilityBase;

public class TimeStop extends KitAbilityBase{

	KitTime kt;
	
	public TimeStop(KitPvPGame kpg, Player player,int kitnumber, int index) {
		super(kpg, player,kitnumber,index);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kt = (KitTime)kpg.getPlayerData(player);
		if(kt.getTimestop()) {
			kt.timeStart();
		}else {
			kt.timeStop();
		}
	}

}
