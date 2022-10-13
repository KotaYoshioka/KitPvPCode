package kits.ability.supernova;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kitdatas.SupernovaData;
import kits.ability.KitAbilityBase;

public class SuperBlightness extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public SuperBlightness(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
		}
		for(Player p:kpg.getLivings()) {
			if(p != player) {
				Set<Entity> entities = new HashSet<Entity>();
				for(Block b : p.getLineOfSight((HashSet<Material>) null, 100)) {
					for(Entity ent : b.getWorld().getNearbyEntities(b.getLocation(), 1, 1, 1)) {
						if(!entities.contains(ent)) {
							entities.add(ent);
						}
					}
				}
				if(entities.contains(player)) {
					kpg.getPlayerData(p).setSilence(SupernovaData.superBlightSilence);
					if(player.getExp() > 0.9f) {
						kpg.getPlayerData(p).tempStan(SupernovaData.superBlightStan);
					}
				}
			}
		}
		player.setExp(player.getExp()/2f);
		player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(),50,0.2,0.2,0.2,0.1);
	}

}
