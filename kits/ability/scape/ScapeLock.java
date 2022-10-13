package kits.ability.scape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class ScapeLock extends KitAbilityBase{

	boolean ignoreCooldown;
	int height;
	
	public ScapeLock(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}
	

	@Override
	protected void doAbility() {
		LivingEntity target = null;
		boolean bre = false;
		for(Block b : player.getLineOfSight((HashSet<Material>) null, 100)) {
			for(Entity ent : b.getWorld().getNearbyEntities(b.getLocation(), 1, 1, 1)) {
				if(ent == player) {
					continue;
				}
				if(ent instanceof LivingEntity) {
					target = (LivingEntity)ent;
					bre = true;
					break;
				}
			}
			if(bre) {
				break;
			}
		}
		if(target != null) {
			Location l = target.getLocation().clone();
			for(int i = 0 ; i < 15 ; i++) {
				l.add(0,1,0);
				if(l.getBlock().getType() == Material.AIR) {
					height = i+1;
				}else {
					break;
				}
			}
			if(height > 7) {
				if(!ignoreCooldown) {
					kpg.getPlayerData(player).cooldown(abilityindex, cd);
				}	
				flowUp(target);
			}else {
				player.sendMessage(ChatColor.RED + "十分な高さがありません！");
			}
		}
	}
	
	void flowUp(LivingEntity target) {
		Location nlo = target.getLocation().clone();
		double addy = (height-4) / 20d;
		for(int i = 1 ; i <= 20 ; i++) {
			int ii = i;
			new BukkitRunnable() {
				public void run() {
					Location tlo = nlo.clone();
					tlo.add(0,addy * ii,0);
					player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, tlo, 3, 0.2, 0.2, 0.2, 0.1);
					target.teleport(tlo);
					if(ii == 10) {
						lock(target,tlo);
					}
				}
			}.runTaskLater(plugin, i);
		}
	}
	
	void lock(LivingEntity target, Location lock) {
		List<FallingBlock> fbs = new ArrayList<FallingBlock>();
		for(int i = 1 ; i <= 2 ; i++) {
			Location clone = lock.clone();
			clone.add(0,i + 2,0);
			FallingBlock chain = player.getWorld().spawnFallingBlock(clone,Material.CHAIN.createBlockData());
			player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, clone, 15, 0.2, 0.2, 0.2, 0.1);
			chain.setGravity(false);
			fbs.add(chain);
		}
		new BukkitRunnable() {
			int counter = 0;
			public void run() {
				target.teleport(lock);
				counter++;
				if(counter >= 50) {
					this.cancel();
					for(FallingBlock fb:fbs) {
						player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,fb.getLocation(), 15, 0.4, 0.4, 0.4, 0.1);
						fb.remove();
					}
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

}
