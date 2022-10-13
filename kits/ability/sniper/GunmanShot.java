package kits.ability.sniper;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.KitSniper;
import kits.ability.KitAbilityBase;

public class GunmanShot extends KitAbilityBase{

	KitSniper gunman;
	
	public GunmanShot(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		gunman = (KitSniper)kpg.getPlayerData(player);
		doAbility();
	}
	
	public GunmanShot(KitPvPGame kpg, Player player, int abilityindex) {
		super(kpg, player, 13, abilityindex);
		gunman = (KitSniper)kpg.getPlayerData(player);
		reduceBullet();
		sniper();
	}

	@Override
	protected void doAbility() {
		if(gunman.getGun(abilityindex) != 0) {
			int gunid = gunman.getGun(abilityindex);
			gunShot(gunid);
		}
	}
	
	void reduceBullet() {
		int bullets = gunman.getBullet(abilityindex) - 1;
		if(bullets == 0) {
			gunman.setGun(abilityindex, 0);
		}
		gunman.setBullet(abilityindex, bullets);
	}
	
	void gunShot(int gunid) {
		if(GunData.cd[gunid] != 0 && gunid != 3) {
			kpg.getPlayerData(player).cooldown(abilityindex, GunData.cd[gunid]);
		}
		if(gunid!=3)reduceBullet();
		switch(gunid) {
		case 1:
			shot(gunid);
			break;
		case 2:
			shot(gunid);
			new BukkitRunnable() {
				public void run() {
					if(gunman.getBullet(abilityindex) != 0) {
						reduceBullet();
						shot(gunid);
					}
				}
			}.runTaskLater(plugin, 3);
			break;
		case 4:
			for(int i = 0 ; i < 10 ; i++) {
				shot(gunid);
			}
			break;
		}
	}
	
	void sniper() {
		kpg.getPlayerData(player).cooldown(abilityindex, GunData.cd[3]);
		shot(3);
	}
	
	void shot(int gunid) {
		Vector v = player.getEyeLocation().clone().getDirection().normalize();
		Random rnd = new Random();
		int gosa = GunData.misrange[gunid] + 1;
		v.setX(v.getX() + (rnd.nextDouble() * (rnd.nextInt(gosa)/100d) * (rnd.nextBoolean()?1:-1)));
		v.setY(v.getY() + (rnd.nextDouble() * (rnd.nextInt(gosa)/100d) * (rnd.nextBoolean()?1:-1)));
		v.setZ(v.getZ() + (rnd.nextDouble() * (rnd.nextInt(gosa)/100d) * (rnd.nextBoolean()?1:-1)));
		Location l = player.getEyeLocation().clone();
		for(int i = 0 ; i < GunData.range[gunid] ; i++) {
			l.add(v.getX(),v.getY() - 0.005, v.getZ());
			DustOptions dust = new DustOptions(Color.fromRGB(255,0,0),1);
			player.getWorld().spawnParticle(Particle.REDSTONE,l,2,dust);
			for(Entity ent:player.getWorld().getNearbyEntities(l, 0.2, 0.2, 0.2)) {
				if(ent != player && ent instanceof LivingEntity) {
					LivingEntity le = (LivingEntity)ent;
					if(kpg.containsLivings(le)) {
						Player target = (Player)le;
						kpg.getPlayerData(target).damage(GunData.damage[gunid], target,GunData.gunName[gunid], true);
					}else {
						le.damage(GunData.damage[gunid]);
					}
					return;
				}
			}
		}
	}

}
