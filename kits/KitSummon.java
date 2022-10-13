package kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import game.KitPvPGame;

public class KitSummon extends KitModel{

	List<LivingEntity> summons = new ArrayList<LivingEntity>();
	
	public KitSummon(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 21);
		player.setExp(0.99f);
		exp();
		cost();
	}
	
	@Override
	public void cancelEvents() {
		super.cancelEvents();
		EntityDamageByEntityEvent.getHandlerList().unregister(this);
		EntityDamageEvent.getHandlerList().unregister(this);
		EntityDeathEvent.getHandlerList().unregister(this);
		for(LivingEntity ent:summons) {
			ent.remove();
		}
	}
	
	void exp() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				float newexp = player.getExp() + 0.01f;
				if(newexp >= 1) {
					newexp = 0.99f;
				}
				player.setExp(newexp);
			}
		}.runTaskTimer(plugin, 0, 5);
	}
	
	void cost() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				float sum = 0f;
				for(LivingEntity le:summons) {
					switch(le.getType()) {
					case ZOMBIE:
					case SKELETON:
					case ENDERMAN:
						sum += 0.05f;
						break;
					case WITHER_SKELETON:
					case BLAZE:
					case PHANTOM:
						sum += 0.08f;
						break;
					case WITHER:
					case ENDER_DRAGON:
						sum += 0.1f;
						break;
					default:
						sum += 0;
						break;
					}
				}
				float newexp = player.getExp() - sum;
				if(newexp <= 0) {
					newexp = 0;
					player.setExp(newexp);
					for(LivingEntity le:summons) {
						le.remove();
					}
					summons.clear();
					return;
				}
				player.setExp(newexp);
			}
		}.runTaskTimer(plugin, 0, 10);
	}
	
	@Override
	public void death(){
		super.death();
		for(LivingEntity le:summons) {
			le.remove();
		}
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	public void summon(LivingEntity le) {
		summons.add(le);
	}
	
	public boolean containSummon(int type) {
		for(LivingEntity le:summons) {
			if(le.getType() == EntityType.ZOMBIE || le.getType() == EntityType.SKELETON || le.getType() == EntityType.ENDERMAN) {
				if(type == 1) {
					return true;
				}
			}else if(le.getType() == EntityType.WITHER_SKELETON || le.getType() == EntityType.BLAZE || le.getType() == EntityType.PHANTOM) {
				if(type == 2) {
					return true;
				}
			}else if(le.getType() == EntityType.WITHER || le.getType() == EntityType.ENDER_DRAGON) {
				if(type == 3) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void removeSummon(int type) {
		for(LivingEntity le:summons) {
			if(le.getType() == EntityType.ZOMBIE || le.getType() == EntityType.SKELETON || le.getType() == EntityType.ENDERMAN) {
				if(type == 1) {
					summons.remove(le);
					le.remove();
					return;
				}
			}else if(le.getType() == EntityType.WITHER_SKELETON || le.getType() == EntityType.BLAZE || le.getType() == EntityType.PHANTOM) {
				if(type == 2) {
					summons.remove(le);
					le.remove();
					return;
				}
			}else if(le.getType() == EntityType.WITHER || le.getType() == EntityType.ENDER_DRAGON) {
				if(type == 3) {
					summons.remove(le);
					le.remove();
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onSummonsDeath(EntityDeathEvent e) {
		if(summons.contains(e.getEntity())) {
			summons.remove(e.getEntity());
		}
	}
	
	@EventHandler
	public void onSummonsDamage(EntityDamageEvent e) {
		if(summons.contains(e.getEntity())) {
			double damage = e.getDamage();
			this.damage(damage/2d, player, "召喚規定",false);
		}
	}
	
	@EventHandler
	public void onSummonsAttack(EntityDamageByEntityEvent e) {
		if(summons.contains(e.getDamager())) {
			float f = player.getExp();
			f += 0.1f;
			player.setExp(f);
			kpg.getPlayerData(player).healFood(2);
		}
	}
	
	@EventHandler
	public void onSummonsTarget(EntityTargetEvent e) {
		if(summons.contains(e.getEntity())) {
			if(e.getTarget() == player || summons.contains(e.getTarget())) {
				e.setCancelled(true);
			}
		}
	}

}
