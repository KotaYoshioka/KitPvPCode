package kits.ability.shadow;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.KitShadow;
import kits.ability.KitAbilityBase;

public class ShadowInqure extends KitAbilityBase{

	KitShadow ks;
	
	public ShadowInqure(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		ks = (KitShadow)kpg.getPlayerData(player);
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(ks.getDarkMatters().size() != 0) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
			for(ShadowDarkBody start: ks.getDarkMatters()) {
				for(ShadowDarkBody end: ks.getDarkMatters()) {
					if(start != end) {
						List<Player> targets = new ArrayList<Player>();
						Location loc2= start.getBody().getLocation().clone();
						Location l= start.getBody().getLocation().clone();
						Location loc= end.getBody().getLocation().clone();
						int _distance=(int) loc.distance(loc2)*20;
						loc.subtract(loc2.getX(), loc2.getY(), loc2.getZ());
						double d=1D/_distance;
						double x=loc.getX()*d;
						double y=loc.getY()*d;
						double z=loc.getZ()*d;
						for(int i=0;i<_distance;i++){
							Location spawn = new Vector(l.getX()+x*i, l.getY()+y*i,l.getZ()+z*i).toLocation(start.getBody().getWorld());
							spawn.getWorld().spawnParticle(Particle.SMOKE_LARGE,spawn,1,0.2,0.2,0.2,0);
				        	for(Entity ent: spawn.getWorld().getNearbyEntities(spawn, 1, 1, 1)) {
				        		if(kpg.containsLivings(ent) && ent != player) {
				        			Player t = (Player)ent;
				        			if(!targets.contains(t)) {
				        				targets.add(t);
				        			}
				        		}
				        	}
						}
				        
				        for(Player p:targets) {
				        	kpg.getPlayerData(p).damage(2, player, "インクワイア", true);
				        	p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,100,1));
				        }
					}
				}
			}
			List<ShadowDarkBody> kss = new ArrayList<ShadowDarkBody>();
			kss.addAll(ks.getDarkMatters());
			for(ShadowDarkBody sb:kss) {
				sb.addDamage();
			}
		}
	}

}
