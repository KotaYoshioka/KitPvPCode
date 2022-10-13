package kits.ability.ender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kitdatas.EnderData;
import kitpvp.KitPvPTool;
import kits.KitEnder;
import kits.ability.KitAbilityBase;

public class EnderTeleport extends KitAbilityBase{

	boolean ignoreCooldown;
	List<Player> tpplayers = new ArrayList<Player>();
	
	public EnderTeleport(KitPvPGame kpg, Player player,int kitnumber,int index,boolean ignoreCooldown,boolean completeCooldown) {
		super(kpg, player,kitnumber,index);
		this.ignoreCooldown = ignoreCooldown;
		if(completeCooldown) {
			doAbility();
		}
	}
	

	@Override
	protected void doAbility() {
		if(!ignoreCooldown) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
		}
		if(kpg.getPlayerData(player) instanceof KitEnder) {
			KitEnder ke = (KitEnder)kpg.getPlayerData(player);
			ke.usen();
		}
		tpplayers.add(player);
		if(player.isSneaking()) {
			for(Entity ent:player.getNearbyEntities(EnderData.teleportNearRange, EnderData.teleportNearRange, EnderData.teleportNearRange)) {
				if(kpg.containsLivings(ent)) {
					tpplayers.add((Player)ent);
				}
			}
		}
		Set<Entity> entities = new HashSet<Entity>();
		for(Block b : player.getLineOfSight((HashSet<Material>) null, EnderData.teleportdistance * 2)) {
			for(Entity ent : b.getWorld().getNearbyEntities(b.getLocation(), 1, 1, 1)) {
				if(!entities.contains(ent)) {
					entities.add(ent);
				}
			}
		}
		for(Entity entity:entities) {
			if(kpg.containsLivings(entity) && entity != player) {
				playerTopTeleport((Player)entity);
				return;
			}
		}
		simpleTeleport();
	}
	
	void simpleTeleport() {
		Location lo = KitPvPTool.getEyeLocation(player, EnderData.teleportdistance);
		teleport(lo);
	}
	
	void playerTopTeleport(Player target) {
		Location lo = target.getLocation().clone();
		lo.add(0, 1.5, 0);
		teleport(lo);
	}
	
	void teleport(Location lo) {
		for(Player p:tpplayers) {
			Location l = lo.clone();
			l.setDirection(p.getLocation().getDirection());
			p.teleport(l);
		}
	}
}
