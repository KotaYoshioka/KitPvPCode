package kits.ability.spacia;

import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitSpacia;
import kits.ability.KitAbilityBase;

public class SpaciaGravity extends KitAbilityBase{

	public SpaciaGravity(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!player.isSneaking()) {
			KitSpacia ks = (KitSpacia)kpg.getPlayerData(player);
			ks.addGravity();
		}else {
			KitSpacia ks = (KitSpacia)kpg.getPlayerData(player);
			ks.setGravity(0);
		}
	}

}
