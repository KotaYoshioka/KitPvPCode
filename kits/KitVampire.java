package kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import game.KitPvPGame;

public class KitVampire extends KitModel{

	List<Player> genus = new ArrayList<Player>();
	List<Zombie> zombies = new ArrayList<Zombie>();
	int misscounter = 0;
	int regene = 0;
	
	public KitVampire(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 6);
		targets();
		husi();
	}
	
	void husi() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(regene > 0) {
					regene--;
				}else{
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,9999999,2));
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	void targets() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(player.getInventory().getItemInMainHand() != null) {
					if(player.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD) {
						for(Entity ent:getGenuses()) {
							Location l = ent.getLocation().clone();
							l.add(0,2,0);
							player.spawnParticle(Particle.COMPOSTER, l, 3,0,0,0,0);
						}
					}
				}		
			}
		}.runTaskTimer(plugin, 0, 1);
		
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	@Override
	public Object[] exDamage(Player p,double damage,Player causer,String cause,boolean canHeal,boolean damagable) {
		if(p == player) {
			regene = 8;
			if(player.hasPotionEffect(PotionEffectType.REGENERATION)) {
				player.removePotionEffect(PotionEffectType.REGENERATION);
			}
		}else if(causer == player) {
			if(player.getFireTicks() != 0) {
				int longer = player.getFireTicks();
				if(longer > 200) {
					longer = 200;
				}
				p.setFireTicks(longer);
			}
		}
		Object[] a = {p,damage,causer,cause,canHeal,damagable};
		return a;
	}
	
	public List<Entity> getGenuses(){
		List<Entity> result = new ArrayList<Entity>();
		for(Entity ent:player.getNearbyEntities(9,6,9)) {
			if(genus.contains(ent)) {
				result.add(ent);
			}else if(zombies.contains(ent)) {
				result.add(ent);
			}else if(ent instanceof Bat) {
				result.add(ent);
			}
		}
		return result;
	}
	
	public void addZombie(Zombie z) {
		zombies.add(z);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(zombies.contains(e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onTarget(EntityTargetEvent e) {
		if(zombies.contains(e.getEntity())) {
			if(e.getTarget() == player) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onZombieAttack(EntityDamageByEntityEvent e) {
		if(zombies.contains(e.getDamager())) {
			if(kpg.containsLivings(e.getEntity())) {
				Player target = (Player)e.getEntity();
				kpg.getPlayerData(target).tempAddSpeed(-1, 80);
				genus.add(target);
			}
		}
	}
	public int getMissCounter() {
		return misscounter;
	}
	public void setMissCounter(int counter) {
		misscounter = counter;
	}
	public List<Player> getPlayerGenus(){
		return genus;
	}
	public void clearPlayerGenus() {
		genus.clear();
	}
}
