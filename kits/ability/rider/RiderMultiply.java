package kits.ability.rider;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.KitRider;
import kits.ability.KitAbilityBase;

public class RiderMultiply extends KitAbilityBase{

	public RiderMultiply(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		LivingEntity riden = (LivingEntity)((KitRider)kpg.getPlayerData(player)).getRide();
		if(riden != null) {
			for(int i = 1 ; i <= 4 ; i++) {
				Location l = player.getLocation().clone();
				Vector v = player.getLocation().getDirection().clone().normalize().multiply(i%2==0?6:3);
				v.rotateAroundY(i<3?90:-90);
				l.add(v);
				LivingEntity ent = (LivingEntity)player.getWorld().spawnEntity(l, riden.getType());
				if(riden instanceof Horse) {
					Horse horse = (Horse)ent;
					Horse h = (Horse)riden;
					if(h.getColor() == Color.BLACK) {
						horse.setColor(Color.BLACK);
					}else {
						horse.setColor(Color.WHITE);
					}
					horse.setStyle(Style.NONE);
				}
				ent.setNoDamageTicks(9999);
				((KitRider)kpg.getPlayerData(player)).addOthers(ent,i);
			}
		}else {
			for(int i = 1 ; i <= 4 ; i++) {
				Location l = player.getLocation().clone();
				Vector v = player.getLocation().getDirection().clone().normalize().multiply(i%2==0?6:3);
				v.rotateAroundY(i<3?90:-90);
				v.setY(1);
				l.add(v);
				SkeletonHorse horse = (SkeletonHorse)player.getWorld().spawnEntity(l,EntityType.SKELETON_HORSE);
				Skeleton skeleton = (Skeleton)player.getWorld().spawnEntity(l, EntityType.SKELETON);
				player.getWorld().strikeLightningEffect(l);
				horse.addPassenger(skeleton);
				horse.setNoDamageTicks(9999);
				horse.setTamed(true);
				skeleton.setNoDamageTicks(9999);
				double distance = 999999;
				Player target = null;
				for(Player p:kpg.getLivings()) {
					Location plo = p.getLocation();
					Location hlo = horse.getLocation();
					double dis = plo.distance(hlo);
					if(dis < distance) {
						target = p;
					}
				}
				if(target != null) {
					horse.setTarget(target);
					skeleton.setTarget(target);
				}
				((KitRider)kpg.getPlayerData(player)).addSkeletons(skeleton);
				((KitRider)kpg.getPlayerData(player)).addSkeletons(horse);
			}
		}
	}

}
