package kits;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import game.KitPvPGame;
import kitdatas.EnderData;
import kitpvp.KitPvPTool;

public class KitEnder extends KitModel{

	HashMap<Player,Location> set = new HashMap<Player,Location>();
	boolean usen = false;
	Location l = null;
	
	public KitEnder(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 5);
		showTeleport();
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	@Override
	public Object[] exDamage(Player p,double damage,Player causer,String cause,boolean canHeal,boolean damagable) {
		if(causer == player) {
			if(usen) {
				damage += 2;
				p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,40,1));
				kpg.getPlayerData(p).tempAddSpeed(-2, 40);
				usen = false;
			}
		}
		Object[] a = {p,damage,causer,cause,canHeal,damagable};
		return a;
	}
	
	public void setTargets(HashMap<Player,Location> newset) {
		set.putAll(newset);
	}
	
	public HashMap<Player,Location> issueTargets(){
		HashMap<Player,Location> result = new HashMap<Player,Location>();
		result.putAll(set);
		set.clear();
		return result;
	}
	
	public boolean hasTargets() {
		return set.size()!=0;
	}
	
	@EventHandler
	public void onProjectile(ProjectileHitEvent e) {
		if(kpg.containsLivings(e.getHitEntity())) {
			if(e.getEntity() instanceof EnderPearl) {
				if(e.getEntity().getShooter() == player) {
					e.setCancelled(true);
					e.getHitEntity().teleport(player.getLocation());
					e.getEntity().remove();
					kpg.getPlayerData((Player)e.getHitEntity()).tempStan(1);
				}
			}
		}
	}
	
	public void usen() {
		if(!usen) {
			usen = true;
			new BukkitRunnable() {
				public void run() {
					usen = false;
				}
			}.runTaskLater(plugin, 20);
		}
	}
	
	void showTeleport() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(player.getInventory().getItemInMainHand() != null) {
					if(player.getInventory().getHeldItemSlot() == 1) {
						Location ll = KitPvPTool.getEyeLocation(player, EnderData.teleportdistance);
						if(l != null && l != ll) {
							player.sendBlockChange(l, Material.AIR.createBlockData());
						}
						if(player.getLocation().distance(ll) <= 3) {
							player.sendBlockChange(ll, Material.AIR.createBlockData());
							l = null;
						}else {
							l = KitPvPTool.getEyeLocation(player, EnderData.teleportdistance);
							player.sendBlockChange(l, Material.LIME_STAINED_GLASS.createBlockData());
						}
					}else if(l != null) {
						player.sendBlockChange(l, Material.AIR.createBlockData());
						l = null;
					}
				}		
			}
		}.runTaskTimer(plugin, 0, 1);
	}
}
