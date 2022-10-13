package kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

import game.KitPvPGame;

public class KitScape extends KitModel{

	double addx;
	double addy;
	double addz;
	Vector v;
	boolean snmode = false;
	FallingBlock[] meChain = {null,null};
	List<Zombie> arcana = new ArrayList<Zombie>();
	
	public KitScape(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 42);
		sneakMove();
	}
	
	void sneakMove() {
		new BukkitRunnable() {
			int counter = 0;
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(snmode) {
					Location plo = player.getLocation().clone();
					plo.add(v);
					player.teleport(plo);
					if(counter % 10 == 0) {
						for(FallingBlock chains:meChain) {
							player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, chains.getLocation(), 5, 0.4, 0.4, 0.4, 0);
							chains.remove();
						}
						plo.add(0,1.5,0);
						for(int i = 0 ; i < 2 ; i++) {
							if(i == 1) {
								plo.add(0,1,0);
							}
							meChain[i] = player.getWorld().spawnFallingBlock(plo,Material.CHAIN.createBlockData());
							meChain[i].teleport(plo);
							player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, plo, 4, 0.4, 0.4, 0.4, 0);
						}
					}
					for(FallingBlock chains:meChain) {
						chains.setVelocity(player.getLocation().getDirection());
					}
					counter++;
				}else {
					counter = 0;
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	@EventHandler
	public void onSneaking(PlayerToggleSneakEvent e) {
		if(e.getPlayer() == player) {
			if(live) {
				if(!player.isSneaking()) {
					Location nowl = player.getEyeLocation().clone();
					Location tol = player.getTargetBlock(null, 6).getLocation();
					addx = (tol.getX() - nowl.getX()) * 0.1;
					addy = (tol.getY() - nowl.getY()) * 0.1;
					addz = (tol.getZ() - nowl.getZ()) * 0.1;
					v = new Vector(addx,addy,addz);
					v.normalize();
					Location l = player.getLocation().clone();
					l.add(0,1.5,0);
					for(int i = 0 ; i < 2 ; i++) {
						if(i == 1) {
							l.add(0,1,0);
						}
						meChain[i] = player.getWorld().spawnFallingBlock(l,Material.CHAIN.createBlockData());
						meChain[i].teleport(l);
						meChain[i].setVelocity(player.getLocation().getDirection());
						player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, l, 10, 0.1, 0.1, 0.1, 0);
					}
					snmode = true;
				}else {
					for(FallingBlock chains:meChain) {
						player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, chains.getLocation(), 10, 0.1, 0.1, 0.1, 0);
						chains.remove();
					}
					snmode = false;
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e) {
		if(arcana.contains(e.getEntity())) {
			if(e.getTarget() == player) {
				e.setCancelled(true);
			}
		}
	}
	
	public void addArcana(Zombie z) {
		arcana.add(z);
	}

}
