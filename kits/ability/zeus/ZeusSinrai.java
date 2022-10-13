package kits.ability.zeus;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import game.KitPvPGame;
import kits.KitZeus;
import kits.ability.KitAbilityBase;

public class ZeusSinrai extends KitAbilityBase{

	
	public ZeusSinrai(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		int level = player.getLevel() - 3;
		if(level >= 0) {
			player.setLevel(level);
		}else {
			kpg.getPlayerData(player).damage(7, player, "神雷", false);
			((KitZeus)kpg.getPlayerData(player)).addCounter();
		}
		for(Player p:kpg.getLivings()) {
			p.getWorld().strikeLightning(p.getLocation().clone());
			p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,200,1));
		}
	}

}
