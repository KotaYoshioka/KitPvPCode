package kits;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import game.KitPvPGame;

public class KitSpacia extends KitModel{

	int gravity = 0;
	Set<LivingEntity> others = new HashSet<LivingEntity>();
	
	public KitSpacia(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 12);
		ittai();
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	void ittai() {
		new BukkitRunnable() {
			public void run() {
				if(player.isSneaking()) {
					Set<LivingEntity> areas = new HashSet<LivingEntity>();
					for(Entity ent:player.getNearbyEntities(6, 6, 6)) {
						if(ent instanceof LivingEntity) {
							LivingEntity le = (LivingEntity)ent;
							if(le instanceof Player) {
								if(!kpg.containsLivings(le)) {
									continue;
								}
							}
							areas.add(le);
						}
					}
					Set<LivingEntity> ds = new HashSet<LivingEntity>();
					for(LivingEntity l: others) {
						if(!areas.contains(l)) {
							ds.add(l);
						}
					}
					//もう範囲外になった生物達
					for(LivingEntity l:ds) {
						others.remove(l);
						if(l.hasPotionEffect(PotionEffectType.LEVITATION)) {
							l.removePotionEffect(PotionEffectType.LEVITATION);
						}
					}
					//新たに範囲に入ってきた生物達
					for(LivingEntity l:areas) {
						if(!others.contains(l)) {
							others.add(l);
							gravity(l);
						}
					}
				}else {
					for(LivingEntity l:others) {
						if(l.hasPotionEffect(PotionEffectType.LEVITATION)) {
							l.removePotionEffect(PotionEffectType.LEVITATION);
						}
					}
					others.clear();
				}
			}
		}.runTaskTimer(plugin, 0, 5);
	}

	public int getGravity() {
		return gravity;
	}
	public void addGravity() {
		setGravity(gravity + 1);
	}
	public void setGravity(int set) {
		gravity = set;
		gravity(player);
		for(LivingEntity p:others) {
			gravity(p);
		}
	}
	
	void gravity(LivingEntity player) {
		if(player.hasPotionEffect(PotionEffectType.LEVITATION)) {
			player.removePotionEffect(PotionEffectType.LEVITATION);
		}
		if(gravity != 0) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,9999999,gravity));
		}
	}
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		if(e.getPlayer() == player) {
			if(!player.isSneaking()) {
				
			}else {
				
			}
		}
	}
}
