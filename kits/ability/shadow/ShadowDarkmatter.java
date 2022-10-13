package kits.ability.shadow;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitShadow;
import kits.ability.KitAbilityBase;

public class ShadowDarkmatter extends KitAbilityBase{

	KitShadow ks;
	
	public ShadowDarkmatter(KitPvPGame kpg, Player player,int kitnumber,int index) {
		super(kpg, player,kitnumber,index);
		if(kpg.getPlayerData(player) instanceof KitShadow) {
			ks = (KitShadow)kpg.getPlayerData(player);
		}
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		kpg.getPlayerData(player).damage(3, player, "ダークマター", false);
		ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
		ShadowDarkBody sdb = new ShadowDarkBody(as,plugin,player,kpg);
		ks.addDarkmatter(sdb);
		ks.reScoreboard();
	}

}
