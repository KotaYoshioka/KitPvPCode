package kits.ability.sparkle;

import org.bukkit.entity.Player;

import game.KitPvPGame;
import kitdatas.SparkleData;
import kits.KitSparkle;
import kits.ability.KitAbilityBase;

public class SparkleSmall extends KitAbilityBase{

	
	KitSparkle kb;
	
	public SparkleSmall(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kb = (KitSparkle)kpg.getPlayerData(player);
		levelReduce();
		kb.explosion(player.getLocation(), 1, false);
	}
	
	void levelReduce() {
		if(player.getLevel() == 0) {
			player.setLevel(SparkleData.semiTime);
		}else {
			int level = player.getLevel() - 1;
			player.setLevel(level);
			if(level == 0) {
				kb.cooldown(abilityindex, cd);
			}
		}
	}

}
