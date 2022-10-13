package kits;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;

import game.KitPvPGame;
import maindatas.KitData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class KitSupernova extends KitModel{
	
	boolean delay = false;
	BukkitTask bt;
	
	
	int stacktime = 0;
	boolean go = false;
	
	boolean charge = false;
	
	int dashcounter = 0;
	
	public KitSupernova(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 15);
		player.setExp(0.99f);
		healExp();
	}
	
	public void healExp() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(delay) {
					return;
				}
				float exp = player.getExp() + 0.04f;
				if(exp >= 1) {
					exp = 0.99f;
				}
				player.setExp(exp);
			}
		}.runTaskTimer(plugin, 0, 2);
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	public boolean reduceExp(float exp) {
		float ex = player.getExp() - exp;
		delay();
		if(ex >= 0) {
			player.setExp(ex);
			return true;
		}else {
			return false;
		}
	}
	
	public void delay() {
		if(bt != null) {
			bt.cancel();
		}
		delay = true;
		bt = new BukkitRunnable() {
			public void run() {
				delay = false;
			}
		}.runTaskLater(plugin, 60);
	}
	
	public void stacktime() {
		charge = true;
		stacktime = 3;
		go = false;
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(getMeter()));
		new BukkitRunnable() {
			public void run() {
				if(go) {
					this.cancel();
					return;
				}
				stacktime--;
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(getMeter()));
				if(stacktime == 0) {
					this.cancel();
					go();
					return;
				}
			}
		}.runTaskTimer(plugin, 20, 20);
	}
	
	String getMeter() {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.RED + "" + stacktime);
		for(int i = 0 ; i < stacktime ; i++) {
			sb.append(ChatColor.GREEN + " | ");
		}
		for(int i = stacktime ; i < 3 ; i++) {
			sb.append(ChatColor.GRAY + " | ");
		}
		return sb.toString();
	}
	
	public int getStacktime() {
		return stacktime;
	}

	public boolean getGo() {
		return go;
	}
	
	public void go() {
		go = true;
		charge = false;
		player.setVelocity(player.getLocation().getDirection().multiply(2));
		kpg.getPlayerData(player).cooldown(0, KitData.kitweaponcool[15][0]);
		player.getWorld().spawnParticle(Particle.END_ROD, player.getEyeLocation(),50,1,1,1,0.65);
		player.getWorld().spawnParticle(Particle.COMPOSTER, player.getEyeLocation(),20,1,1,1,0.2);
		player.getWorld().spawnParticle(Particle.CRIT, player.getEyeLocation(),40,1,1,15,0.65);
		player.getWorld().spawnParticle(Particle.CRIT_MAGIC, player.getEyeLocation(),20,1,1,1,0.65);
		player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getEyeLocation(),10,1,1,1,0.5);
		player.getWorld().spawnParticle(Particle.FLASH, player.getEyeLocation(),20,0,0,0,0);
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				player.getWorld().spawnParticle(Particle.END_ROD, player.getEyeLocation(),6,0.3,0.3,0.3,0.2);
				player.getWorld().spawnParticle(Particle.COMPOSTER, player.getEyeLocation(),6,0.3,0.3,0.3,0.2);
				player.getWorld().spawnParticle(Particle.CRIT_MAGIC, player.getEyeLocation(),6,0.3,0.3,0.3,0.2);
			}
		}.runTaskTimer(plugin, 0, 2);
		new BukkitRunnable() {
			public void run() {
				go = false;
				addSpeed(-dashcounter);
				dashcounter = 0;
				bt.cancel();
			}
		}.runTaskLater(plugin, 200);
	}
	
	public void dashCounter() {
		dashcounter++;
		this.addSpeed(1);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(e.getPlayer() == player) {
			if(charge) {
				e.setCancelled(true);
			}
		}
	}
	
	@Override
	void alterSpeed() {
		super.alterSpeed();
		if(player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
			player.removePotionEffect(PotionEffectType.FAST_DIGGING);
		}
		int level = speed / 3;
		level -= 1;
		if(level >= 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,9999999,level));
		}
	}
}
