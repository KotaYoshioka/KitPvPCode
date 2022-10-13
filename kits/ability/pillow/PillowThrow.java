package kits.ability.pillow;

import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kitdatas.PillowData;
import kits.KitPillow;
import kits.ability.KitAbilityBase;

public class PillowThrow extends KitAbilityBase{

	public PillowThrow(KitPvPGame kpg, Player player,int kitnumber,int index) {
		super(kpg, player,kitnumber,index);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		FallingBlock fb = player.getWorld().spawnFallingBlock(player.getEyeLocation(),Material.WHITE_WOOL.createBlockData());
		fb.setDropItem(false);
		new PillowMakura(player,plugin,kpg,fb);
		((KitPillow)kpg.getPlayerData(player)).addHirou(PillowData.pillowTired);
		((KitPillow)kpg.getPlayerData(player)).addPillow(fb);
	}

}
