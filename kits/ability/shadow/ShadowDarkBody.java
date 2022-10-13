package kits.ability.shadow;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kitdatas.ShadowData;
import kits.KitShadow;

public class ShadowDarkBody{
	ArmorStand darkmatter;
	boolean alive = true;
	Player master;
	KitShadow ks;
	Plugin plugin;
	KitPvPGame kpg;
	boolean move = false;
	int health = 3;
	
	public ShadowDarkBody(ArmorStand darkmatter,Plugin plugin,Player master,KitPvPGame kpg) {
		this.darkmatter = darkmatter;
		this.plugin = plugin;
		this.master = master;
		this.kpg = kpg;
		darkmatter.setVisible(false);
		darkmatter.setGravity(false);
		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta meta = (LeatherArmorMeta)chestplate.getItemMeta();
		meta.setColor(Color.BLACK);
		chestplate.setItemMeta(meta);
		darkmatter.getEquipment().setChestplate(chestplate);
		if(kpg.getPlayerData(master) instanceof KitShadow) {
			ks = (KitShadow)kpg.getPlayerData(master);
		}
		ks.reScoreboard();
		effect();
		move();
	}
	
	public void effect() {
		new BukkitRunnable() {
			public void run() {
				if(!alive || !ks.getLive()) {
					this.cancel();
					alive = false;
					return;
				}
				Location lo = darkmatter.getLocation().clone();
				lo.add(0,0.7,0);
				darkmatter.getWorld().spawnParticle(Particle.SMOKE_LARGE,lo,60,0.2,0.2,0.2,0);
			}
		}.runTaskTimer(plugin,0, 3);
		new BukkitRunnable() {
			public void run() {
				if(alive) {
					death();
				}
			}
		}.runTaskLater(plugin,(int)(ShadowData.darkmatterlive * 20));
	}
	
	void move() {
		Location nowl = master.getEyeLocation().clone();
		Location tol = master.getTargetBlock(null, 6).getLocation();
		double addx = (tol.getX() - nowl.getX()) * 0.1;
		double addy = (tol.getY() - nowl.getY()) * 0.1;
		double addz = (tol.getZ() - nowl.getZ()) * 0.1;
		new BukkitRunnable() {
			Location l = nowl.clone();
			int counter = 0;
			public void run() {
				if(!alive) {
					return;
				}
				Location ll = l.clone();
				ll.add(addx * counter,addy * counter,addz * counter);
				darkmatter.teleport(ll);
				counter += 2;
				if(counter > 50) {
					this.cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}
	
	public void death() {
		alive = false;
		darkmatter.getWorld().spawnParticle(Particle.SMOKE_LARGE,darkmatter.getLocation(),70,2,2,2,1);
		for(Entity ent : darkmatter.getNearbyEntities(3, 3, 3)) {
			if(kpg.containsLivings(ent) && ent != master) {
				Player target = (Player)ent;
				kpg.getPlayerData(target).damage(6, target, "闇の消失波動", false);
				target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,100,1));
			}
		}
		darkmatter.remove();
		ks.removeDarkmatter(this);
		ks.reScoreboard();
	}
	
	public void addDamage() {
		health -= 1;
		if(health <= 0 && alive) {
			death();
		}
	}
	
	public ArmorStand getBody() {
		return darkmatter;
	}
}
