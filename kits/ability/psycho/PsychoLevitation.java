package kits.ability.psycho;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitPsycho;
import kits.ability.KitAbilityBase;

public class PsychoLevitation extends KitAbilityBase{

	KitPsycho kp;
	
	public PsychoLevitation(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		kp = (KitPsycho)kpg.getPlayerData(player);
		doAbility();
	}

	@Override
	protected void doAbility() {
		Random rnd = new Random();
		int x = rnd.nextInt(2) * (rnd.nextBoolean()?-1:1);
		int z = rnd.nextInt(2) * (rnd.nextBoolean()?-1:1);
		Location pl = player.getLocation().clone();
		pl.add(x,0,z);
		Material m = Material.AIR;
		for(int i = 0 ; i < 30 ; i++) {
			if(pl.getBlock().getType() != Material.AIR) {
				m = pl.getBlock().getType();
				break;
			}else {
				pl.add(0,-1,0);
			}
		}
		if(m != Material.AIR) {
			if(!kp.UsePsy(0.05f)) {
				kp.cooldown(abilityindex, cd);
			}
			pl.add(0,1.5,0);
			FallingBlock fb = (FallingBlock)player.getWorld().spawnFallingBlock(pl,m.createBlockData());
			fb.setDropItem(false);
			kp.addBlocks(new PsychoLeviBlo(player,plugin,kpg,fb,pl,10,m));
		}
	}

}
