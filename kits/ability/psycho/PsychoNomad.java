package kits.ability.psycho;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.KitPsycho;
import kits.ability.KitAbilityBase;

public class PsychoNomad extends KitAbilityBase{

	KitPsycho kp;
	int hit = 2;
	
	public PsychoNomad(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		kp = (KitPsycho)kpg.getPlayerData(player);
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!kp.UsePsy(0.2f)) {
			kp.cooldown(abilityindex, cd);
		}
		move();
	}
	
	void move() {
		Vector v = player.getLocation().clone().getDirection().normalize().multiply(8);
		Location l = player.getEyeLocation().clone();
		for(int i = 0 ; i < 4 ; i++) {
			new BukkitRunnable() {
				public void run() {
					l.add(v);
					Bukkit.getLogger().info(l.toString());
					explosion(l,v);
				}
			}.runTaskLater(plugin, 15 * i);
		}
	}
	
	void explosion(Location l,Vector v) {
		Location ll = l.clone();
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
           double radius = Math.sin(i);
           double y = Math.cos(i);
           for (double a = 0; a < Math.PI * 2; a+= Math.PI / 10) {
              double x = Math.cos(a) * radius;
              double z = Math.sin(a) * radius;
              ll.add(x, y, z);
              ll.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,ll,1,0,0,0,0.5);
              ll.subtract(x, y, z);
           }
        }
        for(Entity ent:player.getWorld().getNearbyEntities(l,3, 3, 3)) {
      	  if(ent instanceof LivingEntity && ent != player) {
      		  LivingEntity le = (LivingEntity)ent;
      		  le.setVelocity(v.normalize().multiply(2));
      		  if(ent instanceof Player && kpg.containsLivings(ent)) {
      			Player target = (Player)ent;
      			kpg.getPlayerData(target).damage(hit,player, "ノマド", false);
      			hit++;
      			int cd = kpg.getPlayerData(player).getCooldowntime(2);
      			if(cd > 3) {
      				cd -= 3;
          			kpg.getPlayerData(player).setCooldowntime(2, cd);
      			}
      		  }else {
      			le.damage(3);
      		  }
      	  }
        }
	}

}
