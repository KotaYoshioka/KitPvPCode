package kits.ability.gradius;

import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitGradius;
import kits.ability.KitAbilityBase;

public class GradiusHagoromo extends KitAbilityBase{

	public GradiusHagoromo(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		KitGradius gradius = (KitGradius)kpg.getPlayerData(player);
		gradius.hagoromo();
	}


}
