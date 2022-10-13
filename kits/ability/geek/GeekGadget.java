package kits.ability.geek;

import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class GeekGadget extends KitAbilityBase{

	public GeekGadget(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		new GeekGadInv(plugin,player,kpg,player.isSneaking());
	}

}
