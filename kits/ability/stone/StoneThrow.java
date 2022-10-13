package kits.ability.stone;

import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.KitStone;
import kits.ability.KitAbilityBase;

public class StoneThrow extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public StoneThrow(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown,boolean nocool) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		if(nocool) {
			if(kpg.getPlayerData(player) instanceof KitStone) {
				if(player.isInsideVehicle()) {
					if(player.getVehicle() instanceof FallingBlock) {
						away();
						return;
					}
				}
			}
		}else {
			doAbility();
		}
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown)kpg.getPlayerData(player).cooldown(abilityindex, cd);
		FallingBlock fb = (FallingBlock)player.getWorld().spawnFallingBlock(player.getEyeLocation(),Material.STONE.createBlockData());
		ThrowedStone ts = new ThrowedStone(player,plugin,kpg,fb,player.getLocation(),10);
		fb.setDropItem(false);
		if(!ignoreCooldown)((KitStone)kpg.getPlayerData(player)).addStones(ts);
		if(!player.isSneaking()) {
			fb.addPassenger(player);
		}
	}
	
	public void away() {
		FallingBlock fb = (FallingBlock)player.getVehicle();
		player.leaveVehicle();
		Vector v = player.getLocation().getDirection();
		v.setY(1);
		player.setVelocity(v);
		fb.setVelocity(new Vector(0,-1.5,0));
	}
}
