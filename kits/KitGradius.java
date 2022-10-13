package kits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kitdatas.GradiusData;
import kits.ability.gradius.GradiusSword;

public class KitGradius extends KitModel{

	List<ArmorStand> swords = new ArrayList<ArmorStand>();
	
	public KitGradius(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 9);
		player.setExp(0.5f);
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}

	@Override
	public void onLeftClick() {
		float exp = player.getExp() - GradiusData.hyakkaReduce;
		if(exp < 0) {
			return;
		}
		player.setExp(exp);
		Random rnd = new Random();
		int ii = rnd.nextInt(6);
		Material[] ms = {Material.WOODEN_SWORD,Material.STONE_SWORD,Material.GOLDEN_SWORD,Material.IRON_SWORD,
				Material.DIAMOND_SWORD,Material.NETHERITE_SWORD};
		Material[] os = {Material.WOODEN_AXE,Material.STONE_AXE,Material.GOLDEN_AXE,Material.IRON_AXE,
				Material.DIAMOND_AXE,Material.NETHERITE_AXE};
		boolean axe = rnd.nextInt(10)==0;
		Location l = player.getLocation().clone();
		l.add(rnd.nextInt(8) * (rnd.nextBoolean()?1:-1),rnd.nextInt(7),rnd.nextInt(8) * (rnd.nextBoolean()?1:-1));
		ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
		as.setVisible(false);
		as.getEquipment().setItemInMainHand(new ItemStack(axe?os[ii]:ms[ii]));
		Vector v = player.getLocation().getDirection().normalize().multiply(2);
		as.setVelocity(v);
		Location goal = player.getLocation().clone();
		goal.add(player.getLocation().getDirection().normalize().multiply(15));
		new GradiusSword(player,plugin,kpg,as,as.getLocation(),goal,axe,ii,false);
	}
	
	public void healGauge(float heal) {
		float exp = player.getExp() + heal;
		if(exp >= 1) {
			exp = 0.99f;
		}
		player.setExp(exp);
	}
	
	public void hagoromo() {
		for(int i = 0 ; i < 12 ; i++) {
			swords.add(summonSword(30 * i));
		}
	}
	
	
	ArmorStand summonSword(int first) {
		ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(),EntityType.ARMOR_STAND);
		as.setVisible(false);
		as.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
		as.setRightArmPose(new EulerAngle(0,0,0));
		
		HashSet<LivingEntity> entity = new HashSet<LivingEntity>();
		BukkitTask bt = new BukkitRunnable() {
			int counter = first;
			public void run() {
				if(!as.isValid()) {
					this.cancel();
					return;
				}
				Location location = player.getLocation().clone();
				double size = 2;
				location.add(Math.sin(Math.toRadians(counter))*size,0.1,Math.cos(Math.toRadians(counter))*size);
		        as.teleport(location);
				counter += 9;
				if(counter>=360)counter=0;
				for(Entity ent:as.getNearbyEntities(1, 1, 1)) {
					if(ent instanceof LivingEntity && !(ent instanceof ArmorStand) && ent != player) {
						LivingEntity le = (LivingEntity)ent;
						if(entity.contains(le)) {
							continue;
						}
						entity.add(le);
						new BukkitRunnable() {
							public void run() {
								entity.remove(le);
							}
						}.runTaskLater(plugin, 20);
						if(kpg.containsLivings(le)) {
							Player target = (Player)le;
							kpg.getPlayerData(target).damage(1,player,"刃衣", false);
						}else {
							le.damage(1);
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0, 1);
		new BukkitRunnable() {
			public void run() {
				if(as.isValid()) {
					bt.cancel();
					Vector vs = player.getLocation().getDirection();
					if(vs.getY() < 0.5) {
						vs.setY(0.5);
					}
					as.setVelocity(vs.normalize().multiply(4));
					new BukkitRunnable() {
						int counter = 0;
						public void run() {
							counter++;
							Vector v = as.getVelocity();
							if(counter > 5 && v.getX() <= 0.001 && v.getY() <= 0.001 && v.getZ() <= 0.001) {
								this.cancel();
								as.remove();
								return;
							}
							boolean hit = false;
							for(Entity ent:as.getNearbyEntities(1.3, 1.3, 1.3)) {
								if(ent instanceof LivingEntity && ent != player&& !(ent instanceof ArmorStand)) {
									LivingEntity le = (LivingEntity)ent;
									if(kpg.containsLivings(le)) {
										Player target = (Player)le;
										kpg.getPlayerData(target).damage(3, player, "刃衣",true);
									}else {
										le.damage(3);
									}
									hit = true;
									((KitGradius)kpg.getPlayerData(player)).healGauge(0.1f);
								}
							}
							if(hit) {
								this.cancel();
								as.remove();
								return;
							}
						}
					}.runTaskTimer(plugin,0,2);
				}
			}
		}.runTaskLater(plugin, 300);
		return as;
	}
	
	@EventHandler
	public void onPlayerInteractArmorStand(PlayerInteractAtEntityEvent e) {
		if(e.getPlayer() == player && e.getRightClicked() instanceof ArmorStand) {
			e.setCancelled(true);
		}
	}
}
