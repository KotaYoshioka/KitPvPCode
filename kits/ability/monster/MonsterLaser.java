package kits.ability.monster;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;


public class MonsterLaser extends KitAbilityBase{

	public MonsterLaser(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		Vector v = player.getLocation().clone().getDirection().normalize();
		Location l = player.getLocation().clone();
		l.add(v.multiply(10));
		player.playEffect(EntityEffect.WOLF_SMOKE);
	}

}
