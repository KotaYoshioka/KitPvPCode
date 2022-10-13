package kits.ability.harden;

import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitHarden;
import kits.ability.KitAbilityBase;

public class HardenCrush extends KitAbilityBase{

	
	public HardenCrush(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		((KitHarden)kpg.getPlayerData(player)).toggleEarth();
	}
	

}
