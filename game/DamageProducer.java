package game;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;

import kits.KitModel;
import kits.KitStone;
import kits.KitZeus;

public class DamageProducer extends GameProducer{

	public DamageProducer(Plugin plugin, KitPvPGame game) {
		super(plugin, game);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(game.getLivings().contains(e.getEntity())) {
			Player target = (Player)e.getEntity();
			KitModel data = game.getPlayerData(target);
			switch(e.getCause()) {
			case FALL:
				e.setCancelled(true);
				break;
			case LIGHTNING:
				if(data instanceof KitZeus) {
					e.setCancelled(true);
				}else {
					data.damage(e.getDamage(),null, "雷", false);
					data.tempStan(10);
					e.setDamage(0);
				}
				break;
			case FALLING_BLOCK:
				if(data instanceof KitStone) {
					e.setDamage(0);
					e.setCancelled(true);
				}else {
					data.damage(e.getDamage(),null,"落下ブロック",false);
				}
				break;
			case SUFFOCATION:
				data.damage(e.getDamage(), null, "窒息",false);
				e.setDamage(0);
				break;
			case CUSTOM:
			case ENTITY_ATTACK:
				break;
			default:
				data.damage(e.getDamage(), null, "ダメージ", false);
				e.setDamage(0);
				break;
			}
		}else if(game.getDeads().contains(e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(game.getLivings().contains(e.getDamager())) {
			if(game.getPlayerData((Player)e.getDamager()).attackSupport && ((Player)e.getDamager()).getAttackCooldown() < 1) {
				e.setCancelled(true);
				return;
			}
		}
		if(game.containsLivings(e.getEntity())) {
			Player damaged = (Player)e.getEntity();
			KitModel damagedData = game.getPlayerData(damaged);
			if(game.containsLivings(e.getDamager())) {
				Player damager = (Player)e.getDamager();
				damagedData.damage(e.getDamage(), damager, "攻撃", true);
				e.setDamage(0);
			}else if(e.getDamager() instanceof Projectile) {
				Projectile pr = (Projectile)e.getDamager();
				if(game.getLivings().contains(pr.getShooter())) {
					Player damager = (Player)pr.getShooter();
					damagedData.damage(e.getDamage(),damager, "飛び道具", true);
					e.setDamage(0);
				}
			}else {
				if(e.getCause() != DamageCause.LIGHTNING) {
					damagedData.damage(e.getDamage(), null, "その他", false);
					e.setDamage(0);
				}
			}
		}
	}
	
	
	@EventHandler
	public void onEntityCombustEvent(EntityCombustEvent e) {
		if(e.getEntity() instanceof Player) {
			return;
		}
		e.setCancelled(true);
	}
}
