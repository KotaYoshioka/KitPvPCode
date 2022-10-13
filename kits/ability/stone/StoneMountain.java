package kits.ability.stone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import game.KitPvPGame;
import kitdatas.StoneData;
import kits.ability.KitAbilityBase;

public class StoneMountain extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public StoneMountain(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
		}
		BukkitTask bt = new BukkitRunnable() {
			int counter = 0;
			public void run() {
				if(!kpg.getPlayerData(player).getLive()) {
					this.cancel();
				}
				Random rnd = new Random();
				Location l = player.getTargetBlock(null, 12).getLocation().clone();
				if(l.getBlock().getType() != Material.STONE) {
					putLocation(l,counter);
				}else {
					Location ll = l.clone();
					ll.add((rnd.nextBoolean()?1:-1) *  rnd.nextInt(2), 0, (rnd.nextBoolean()?1:-1) *  rnd.nextInt(2));
					putLocation(ll,counter);
				}
				l.add((rnd.nextBoolean()?1:-1) *  rnd.nextInt(2), 0, (rnd.nextBoolean()?1:-1) *  rnd.nextInt(2));
				if(l.getBlock().getType() != Material.STONE) {
					putLocation(l,counter);
				}else {
					Location ll = l.clone();
					ll.add((rnd.nextBoolean()?1:-1) *  rnd.nextInt(2), 0, (rnd.nextBoolean()?1:-1) *  rnd.nextInt(2));
					putLocation(ll,counter);
				}
				l.add((rnd.nextBoolean()?1:-1) *  rnd.nextInt(2), 0, (rnd.nextBoolean()?1:-1) *  rnd.nextInt(2));
				if(l.getBlock().getType() != Material.STONE) {
					putLocation(l,counter);
				}else {
					Location ll = l.clone();
					ll.add((rnd.nextBoolean()?1:-1) *  rnd.nextInt(2), 0, (rnd.nextBoolean()?1:-1) *  rnd.nextInt(2));
					putLocation(ll,counter);
				}
				counter++;
			}
		}.runTaskTimer(plugin, 0, 1);
		new BukkitRunnable() {
			public void run() {
				if(!bt.isCancelled()) {
					bt.cancel();
				}
			}
		}.runTaskLater(plugin, StoneData.mountainLong);
	}
	
	void putLocation(Location l,int delay) {
		List<Location> pls = new ArrayList<Location>();
		Location plo = player.getLocation().getBlock().getLocation();
		pls.add(plo.clone());
		plo.add(0,1,0);
		pls.add(plo.clone());
		if(test(l.clone())) {
			if(!pls.contains(l)) {
				kpg.blocks.putBlock(l, Material.STONE, StoneData.mountainDelay - delay,Material.STONE);
			}
		}else {
			for(int i = 0 ; i < 10 ; i ++) {
				l.add(0,-1,0);
				if(l.getBlock().getType() != Material.AIR) {
					l.add(0,1,0);
					if(!pls.contains(l)) {
						kpg.blocks.putBlock(l, Material.STONE,StoneData.mountainDelay - delay,Material.STONE);	
					}
					break;
				}
			}
		}
	}
	
	boolean test(Location l) {
		l.add(-1,0,0);
		if(l.getBlock().getType() != Material.AIR) {
			return true;
		}
		l.add(2,0,0);
		if(l.getBlock().getType() != Material.AIR) {
			return true;
		}
		l.add(-1,0,-1);
		if(l.getBlock().getType() != Material.AIR) {
			return true;
		}
		l.add(0,0,2);
		if(l.getBlock().getType() != Material.AIR) {
			return true;
		}
		l.add(0,1,-1);
		if(l.getBlock().getType() != Material.AIR) {
			return true;
		}
		return false;
	}
	

}
