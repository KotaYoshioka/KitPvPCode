package kits.ability.ender;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kitdatas.EnderData;
import kits.KitEnder;
import kits.ability.KitAbilityBase;

public class EnderReturn extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public EnderReturn(KitPvPGame kpg, Player player, int kitnumber, int abilityindex, boolean ignoreCooldown,boolean completeCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		if(completeCooldown) {
			doAbility();
		}
	}

	@Override
	protected void doAbility() {
		KitEnder ke = (KitEnder)kpg.getPlayerData(player);
		if(ke.hasTargets()) {
			allReturn();
		}else {
			setPlayers();
		}
	}
	
	void setPlayers() {
		HashMap<Player,Location> set = new HashMap<Player,Location>();
		for(Entity ent:player.getNearbyEntities(EnderData.returnRange, EnderData.returnRange, EnderData.returnRange)) {
			if(kpg.containsLivings(ent)) {
				Player target = (Player)ent;
				Location lo = target.getLocation().clone();
				set.put(target, lo);
			}
		}
		if(set.size() != 0) {
			((KitEnder)kpg.getPlayerData(player)).setTargets(set);
			new BukkitRunnable() {
				public void run() {
					if(((KitEnder)kpg.getPlayerData(player)).hasTargets()) {
						allReturn();
					}
				}
			}.runTaskLater(plugin, EnderData.returnLong);
		}
	}
	
	void allReturn() {
		if(!ignoreCooldown) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
		}
		HashMap<Player,Location> set = ((KitEnder)kpg.getPlayerData(player)).issueTargets();
		for(Player p:set.keySet()) {
			p.teleport(set.get(p));
		}
		((KitEnder)kpg.getPlayerData(player)).usen();
	}

}
