package kits.ability.monster;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class MonsterMeteo extends KitAbilityBase{

	public MonsterMeteo(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		Fireball fireball = (Fireball)player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREBALL);
		fireball.setShooter(player);
		fireball.setVelocity(player.getLocation().getDirection());
	}

}
