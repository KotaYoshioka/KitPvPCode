package kits.ability.psycho;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class PsychoCritical extends KitAbilityBase{

	public PsychoCritical(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		Set<Player> targets = new HashSet<Player>();
		for(int i = 0 ; i < 100 ; i++) {
			int ii = i;
			new BukkitRunnable() {
				public void run() {
					Vector v = player.getLocation().getDirection().normalize().multiply(0.4 * ii);
					Location l = player.getEyeLocation().clone();
					l.add(v);
					if(ii != 99) {
						l.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, l,2,0,0,0,0);
						for(Entity ent:l.getWorld().getNearbyEntities(l, 1, 1, 1)) {
							if(kpg.containsLivings(ent) && ent != player) {
								Player target = (Player)ent;
								if(!targets.contains(target)) {
									targets.add(target);
									if(98-ii != 0) {
										kpg.getPlayerData(target).tempStan(98-ii);
									}
								}
							}
						}
					}else {
						for(Player p:targets) {
							explosion(p.getEyeLocation(),new Vector(0,2,0));
							float exp = player.getExp() + 0.25f;
							if(exp >= 1) {
								exp = 0.99f;
							}
							player.setExp(exp);
						}
					}
				}
			}.runTaskLater(plugin, i);
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
      		  if(ent instanceof Player && !kpg.containsLivings(ent)) {
      			Player target = (Player)ent;
      			kpg.getPlayerData(target).damage(3,player, "アルファハルト", false);
      		  }else {
      			le.damage(3);
      		  }
      	  }
        }
	}

}
