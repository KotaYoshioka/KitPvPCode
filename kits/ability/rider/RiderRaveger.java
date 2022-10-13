package kits.ability.rider;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.KitRider;
import kits.ability.KitAbilityBase;

public class RiderRaveger extends KitAbilityBase{

	Ravager rava;
	
	public RiderRaveger(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
		player.setExp(1);
		repeat();
	}
	
	void repeat() {
		new BukkitRunnable() {
			public void run() {
				if(!rava.getPassengers().contains(player)) {
					this.cancel();
					return;
				}
				Location l = rava.getLocation().clone();
				l.setY(l.getY() - 1);
				if(l.getBlock().getType() != Material.AIR) {
					for(Entity ent:rava.getNearbyEntities(5, 2, 5)) {
						if(ent instanceof LivingEntity && ent != player) {
							LivingEntity le = (LivingEntity)ent;
							player.getWorld().spawnParticle(Particle.BLOCK_CRACK,le.getLocation(),40,0.2,0.5,0.2,0.2,l.getBlock().getType().createBlockData());
							if(kpg.containsLivings(le)) {
								Player target = (Player)le;
								if(!kpg.getPlayerData(target).getStan()) {
									kpg.getPlayerData(target).damage(3, player,"ラヴェジャー",true);
									if(new Random().nextInt(6) == 0) {
										kpg.getPlayerData(target).tempStan(60);
									}
								}
							}else {
								le.damage(3);
							}
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0, 20);
		new BukkitRunnable() {
			public void run() {
				if(!rava.getPassengers().contains(player)) {
					this.cancel();
					return;
				}
				float exp = player.getExp() - 0.02f;
				if(exp <= 0) {
					exp = 0;
					rava.removePassenger(player);
				}
				player.setExp(exp);
				Location l = rava.getLocation().clone();
				l.setY(l.getY() - 1);
				if(l.getBlock().getType() != Material.AIR) {
					player.getWorld().spawnParticle(Particle.BLOCK_CRACK,rava.getLocation(),100,4,1,4,0.5,l.getBlock().getType().createBlockData());
				}
			}
		}.runTaskTimer(plugin, 0, 5);
		new BukkitRunnable() {
			public void run() {
				if(!rava.getPassengers().contains(player)) {
					new RiderThrowHorse(player,plugin,kpg,rava,rava.getLocation(),true);
					for(LivingEntity le:((KitRider)kpg.getPlayerData(player)).getOthers()) {
						boolean blaaa = true;
						if(le instanceof Horse) {
							Horse horse = (Horse)le;
							if(horse.getColor() == Color.WHITE) {
								blaaa = false;
							}
						}
						new RiderThrowHorse(player,plugin,kpg,le,le.getLocation(),blaaa);
					}
					((KitRider)kpg.getPlayerData(player)).clearOthers();
					if(kpg.getPlayerData(player).getCooldown(0)) {
						kpg.getPlayerData(player).cooldown(0, 10);
					}
					this.cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	@Override
	protected void doAbility() {
		((KitRider)kpg.getPlayerData(player)).setNoCount(true);
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		/*
		WorldServer ws = ((CraftWorld)player.getWorld()).getHandle();
		RiderRavager ravas = new RiderRavager(player.getLocation().clone());
		ws.b(ravas);
		rava = (Ravager)ravas.getBukkitEntity();
		*/
		rava = (Ravager)player.getWorld().spawnEntity(player.getLocation(),EntityType.RAVAGER);
		rava.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,9999999,5));
		rava.addPassenger(player);
		KitRider rider = (KitRider)kpg.getPlayerData(player);
		if(rider.getRide() != null) {
			rider.getRide().remove();
		}
		((KitRider)kpg.getPlayerData(player)).setRide(rava);
	}

}