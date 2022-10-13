package kits.ability.heiz;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class HeizKirihebi extends KitAbilityBase{

	public HeizKirihebi(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(player.getLevel() > 0) {
			int level = player.getLevel() - 1;
			player.setLevel(level);
			ArmorStand hebi = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(),EntityType.ARMOR_STAND);
			hebi.setInvisible(true);
			new HeizHebiBody(player,plugin,kpg,hebi);
		}
	}

}
