package kits.ability.ender;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kitdatas.EnderData;
import kits.ability.KitAbilityBase;

public class EnderSwap extends KitAbilityBase{

	boolean ignoreCool;
	
	public EnderSwap(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCool) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCool = ignoreCool;
		doAbility();
	}

	@Override
	protected void doAbility() {
		for(Block b : player.getLineOfSight((HashSet<Material>) null, EnderData.teleportdistance * 2)) {
			for(Entity ent : b.getWorld().getNearbyEntities(b.getLocation(), 1, 1, 1)) {
				if(kpg.containsLivings(ent)) {
					if(ent != player) {
						if(!ignoreCool)kpg.getPlayerData(player).cooldown(abilityindex, cd);
						Player target = (Player)ent;
						Location tlo = target.getLocation().clone();
						target.teleport(player.getLocation().clone());
						player.teleport(tlo);
					}
				}
			}
		}
	}

}
