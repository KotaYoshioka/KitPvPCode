package kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

import game.KitPvPGame;

public class KitSand extends KitModel{

	
	boolean out = false;
	
	ArmorStand armorsand = null;
	
	List<FallingBlock> fs = new ArrayList<FallingBlock>();
	List<Block> sands = new ArrayList<Block>();
	
	public KitSand(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 38);
		player.setExp(0.99f);
		healGage();
		sandMove();
	}
	
	void healGage() {
		new BukkitRunnable() {
			public void run() {
				if(!getLive()) {
					this.cancel();
					return;
				}
				float exp = player.getExp() + 0.01f;
				if(exp >= 1) {
					exp = 0.99f;
				}
				player.setExp(exp);
			}
		}.runTaskTimer(plugin, 0, 5);
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	void sandMove() {
		new BukkitRunnable() {
			public void run() {
				if(!getLive()) {
					this.cancel();
					return;
				}
				if(player.getInventory().getHeldItemSlot() == 3) {
					if(!out && reduceGage(0.03f)) {
						player.setVelocity(player.getLocation().getDirection());
						player.getWorld().spawnParticle(Particle.FALLING_DUST,player.getLocation(),30,0.4,1,0.4,0.2,Material.SAND.createBlockData());
						player.getWorld().spawnParticle(Particle.BLOCK_CRACK,player.getLocation(),30,0.4,1,0.4,0.2,Material.SAND.createBlockData());
					}
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}
	
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		if(fs.contains(e.getEntity())) {
			sands.add(e.getBlock());
		}
	}
	
	boolean reduceGage(float f) {
		float exp = player.getExp();
		if(exp - f < 0) {
			out = true;
			new BukkitRunnable() {
				public void run() {
					out = false;
				}
			}.runTaskLater(plugin, 80);
			return false;
		}else {
			exp = exp - f;
			player.setExp(exp);
			return true;
		}
	}
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		if(e.getPlayer() == player) {
			if(!player.isSneaking() && player.isSprinting()) {
				armorsand = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(),EntityType.ARMOR_STAND);
				armorsand.setVisible(false);
				teleportsand(player.getLocation().getDirection().clone());
			}else if(player.isSneaking() && armorsand != null) {
				armorsand.getWorld().spawnParticle(Particle.FALLING_DUST,armorsand.getLocation(),40,1,1,1,0.2,Material.SAND.createBlockData());
				armorsand.getWorld().spawnParticle(Particle.BLOCK_CRACK,armorsand.getLocation(),20,1,1,1,0.2,Material.SAND.createBlockData());
				armorsand.getWorld().spawnParticle(Particle.FALLING_DUST,player.getLocation(),40,1,1,1,0.2,Material.SAND.createBlockData());
				armorsand.getWorld().spawnParticle(Particle.BLOCK_CRACK,player.getLocation(),20,1,1,1,0.2,Material.SAND.createBlockData());
				player.teleport(armorsand);
				armorsand.remove();
				armorsand = null;
				kpg.getPlayerData(player).reduceFood(1);
			}
		}
	}
	
	void teleportsand(Vector v) {
		new BukkitRunnable() {
			public void run() {
				if(armorsand != null) {
					armorsand.setVelocity(v);
					armorsand.getWorld().spawnParticle(Particle.FALLING_DUST,armorsand.getLocation(),8,0.4,1,0.4,0.2,Material.SAND.createBlockData());
				}else {
					this.cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}

	public void crashSand() {
		for(Block b : sands) {
			b.setType(Material.AIR);
			player.getWorld().spawnParticle(Particle.FALLING_DUST,b.getLocation(),20,1,1,1,0.2,Material.SAND.createBlockData());
			player.getWorld().spawnParticle(Particle.BLOCK_CRACK,b.getLocation(),20,1,1,1,0.2,Material.SAND.createBlockData());
		}
		sands.clear();
		fs.clear();
	}
	
	public void addSandf(FallingBlock fb) {
		fs.add(fb);
	}
}
