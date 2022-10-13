package kits.ability.geek;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitGeek;
import kits.ability.KitAbilityBase;

public class GeekCraft extends KitAbilityBase{

	public GeekCraft(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!((KitGeek)kpg.getPlayerData(player)).nowCraftMode() && player.getInventory().getItemInMainHand().getType() != Material.BOW) {
			((KitGeek)kpg.getPlayerData(player)).craftmode();
			kpg.getPlayerData(player).setKey(false);
		}
	}

}
