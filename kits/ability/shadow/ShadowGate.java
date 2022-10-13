package kits.ability.shadow;

import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitShadow;
import kits.ability.KitAbilityBase;

public class ShadowGate extends KitAbilityBase{

	KitShadow ks;
	
	public ShadowGate(KitPvPGame kpg, Player player,int kitnumber,int index) {
		super(kpg, player,kitnumber,index);
		if(kpg.getPlayerData(player) instanceof KitShadow) {
			ks = (KitShadow)kpg.getPlayerData(player);
		}
		doAbility();
	}

	@Override
	protected void doAbility() {

	}

}
