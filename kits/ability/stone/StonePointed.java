package kits.ability.stone;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.PointedDripstone.Thickness;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kitdatas.StoneData;
import kits.ability.KitAbilityBase;

public class StonePointed extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public StonePointed(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown)kpg.getPlayerData(player).cooldown(abilityindex, cd);
		int counter = 0;
		for(Block b : player.getLineOfSight((HashSet<Material>) null, 70)) {
			if(counter > 4) {
				Location bl = b.getLocation();
				int about = (int)counter/2;
				findLocation(bl,about);	
			}
			counter++;
		}
	}
	
	void findLocation(Location l,int about) {
		for(int i = 0 ; i < 25 ; i++) {
			Location pointed = l.clone();
			pointed.add(0,-i,0);
			if(pointed.getBlock().getType() != Material.AIR && pointed.getBlock().getType() != Material.POINTED_DRIPSTONE) {
				boolean bonus = false;
				if(pointed.getBlock().getType() == Material.STONE) {
					bonus = true;
				}
				pointed.add(0,1,0);
				putLocation(pointed,true,about,bonus);
				break;
				//kpg.putBlock(pointed, Material.POINTED_DRIPSTONE,StoneData.lanceLong);
			}
			pointed.add(0,2 * i,0);
			if(pointed.getBlock().getType() != Material.AIR && pointed.getBlock().getType() != Material.POINTED_DRIPSTONE) {
				boolean bonus = false;
				if(pointed.getBlock().getType() == Material.STONE) {
					bonus = true;
				}
				pointed.add(0,-1,0);
				putLocation(pointed,false,about,bonus);
				break;
				//kpg.putBlock(pointed, Material.POINTED_DRIPSTONE,StoneData.lanceLong);
			}
		}
	}
	
	void putLocation(Location l,boolean upper,int about,boolean bonus) {
		new BukkitRunnable() {
			public void run() {
				//長さ調査
				int length = 1;
				for(int i = 1; i <= 2 ; i++) {
					Location clone = l.clone();
					clone.add(0,upper?i:-i,0);
					if(clone.getBlock().getType() == Material.AIR) {
						length++;
					}
				}
				int trulyLength = new Random().nextInt(length) + 1;
				for(int i = 0 ; i < trulyLength ; i++) {
					kpg.blocks.putBlock(l, Material.POINTED_DRIPSTONE,bonus?StoneData.lanceBonusLong:StoneData.lanceLong,getThickness(i,trulyLength-1),upper?BlockFace.UP:BlockFace.DOWN);
					stoneDamage(l,upper,bonus);
					l.add(0,upper?1:-1,0);
				}		
			}
		}.runTaskLater(plugin, about);
	} 
	
	
	void stoneDamage(Location l,boolean upper,boolean bonus) {
		for(Entity ent:player.getWorld().getNearbyEntities(l,1,1,1)) {
			if(ent instanceof LivingEntity) {
				LivingEntity le = (LivingEntity)ent;
				if(le instanceof Player) {
					if(kpg.containsLivings(le)) {
						Player target = (Player)le;
						if(ent != player) {
							kpg.getPlayerData(target).damage(bonus?StoneData.lanceBonusDamage:StoneData.lanceDamage, target, "ロックランス",true);
							kpg.getPlayerData(target).tempAddSpeed(-StoneData.lanceSlowLevel, StoneData.lanceSlowLong);
						}
					}
				}else {
					le.damage(StoneData.lanceDamage);
				}
				le.setVelocity(new Vector(0,upper?2.5:-2.5,0));
			}
		}
	}
	
	Thickness getThickness(int now,int all) {
		switch(all) {
		case 0:
			return Thickness.TIP;
		case 1:
			switch(now) {
			case 0:
				return Thickness.FRUSTUM;
			default:
				return Thickness.TIP;
			}
		default:
			switch(now) {
			case 0:
				return Thickness.MIDDLE;
			case 1:
				return Thickness.FRUSTUM;
			default:
				return Thickness.TIP;
			}
		}
	}

}
