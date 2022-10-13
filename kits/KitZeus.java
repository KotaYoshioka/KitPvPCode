package kits;

import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

import game.KitPvPGame;

public class KitZeus extends KitModel{

	public KitZeus(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 23);
	}
	
	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	public void addCounter() {
		float exp = player.getExp() + 0.33f;
		if(exp >= 0.99f) {
			exp = 0;
			int level = player.getLevel() + 1;
			if(level >= 7) {
				level = 7;
			}
			player.setLevel(level);
		}
		player.setExp(exp);
	}
	
	@Override
	public void onLeftClick() {
		int level = player.getLevel() - 1;
		if(level >= 0) {
			player.setLevel(level);
			ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(),EntityType.ARMOR_STAND);
			as.setVisible(false);
			Vector v = player.getLocation().getDirection().clone();
			BukkitTask bt = new BukkitRunnable() {
				public void run() {
					as.setVelocity(v);
					player.getWorld().spawnParticle(Particle.GLOW,as.getLocation(),10,0.5,0.5,0.5,0.5);
					for(Entity ent: as.getNearbyEntities(2, 2, 2)) {
						if(ent instanceof Player && ent != player && kpg.containsLivings(ent)) {
							Player target = (Player)ent;
							kpg.getPlayerData(target).tempStan(15);
							player.getWorld().spawnParticle(Particle.GLOW,target.getEyeLocation(),40,0.8,0.8,0.8,0.3);
							this.cancel();
							as.remove();
							return;
						}
					}
				}
			}.runTaskTimer(plugin, 0, 2);
			new BukkitRunnable() {
				public void run() {
					if(!bt.isCancelled()) {
						bt.cancel();
						as.remove();
					}
				}
			}.runTaskLater(plugin, 40);
		}
	}
}
