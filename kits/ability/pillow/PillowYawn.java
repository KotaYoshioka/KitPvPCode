package kits.ability.pillow;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kitdatas.PillowData;
import kits.KitPillow;
import kits.ability.KitAbilityBase;

public class PillowYawn extends KitAbilityBase{

	public PillowYawn(KitPvPGame kpg, Player player,int kitnumber,int index) {
		super(kpg, player,kitnumber,index);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		Location lo = player.getLocation().clone();
		Vector v = lo.getDirection().clone().normalize();
		float hirou = player.getExp() + PillowData.yawnTired;
		Set<Player> targets = new HashSet<Player>();
		Random rnd = new Random();
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				lo.add(v);
				for(int i = 0 ; i < 4 ; i ++) {
					Location llo = lo.clone();
					llo.add(rnd.nextDouble() * (rnd.nextBoolean()?1:-1),rnd.nextDouble() * (rnd.nextBoolean()?1:-1),rnd.nextDouble() * (rnd.nextBoolean()?1:-1));
					player.getWorld().spawnParticle(Particle.SPELL_MOB,llo,0,1,1,1,1);
				}
				for(Entity ent:lo.getWorld().getNearbyEntities(lo, 2, 2, 2)) {
					if(ent instanceof LivingEntity && !(ent instanceof ArmorStand) && ent != player) {
						LivingEntity le = (LivingEntity)ent;
						if(kpg.containsLivings(le)) {
							Player target = (Player)le;
							if(!targets.contains(target)) {
								targets.add(target);
								if(hirou >= 0.25f) {
									kpg.getPlayerData(target).tempAddSpeed(PillowData.yawnLevel,(int)( PillowData.yawnSeconds*20));
									if(hirou >= 0.5f) {
										kpg.getPlayerData(target).setSilence((int)(PillowData.yawnSilenceSeconds*20));
										if(hirou >= 0.75f) {
											if(!kpg.getPlayerData(target).isSleep()) {
												kpg.getPlayerData(target).setSleep((int)(PillowData.yawnSleepSeconds*20));
											}
										}
									}
								}
							}
						}
					}
				}
				lo.add(v);
			}
		}.runTaskTimer(plugin, 0, 7);
		new BukkitRunnable() {
			public void run() {
				bt.cancel();
			}
		}.runTaskLater(plugin, (int)(PillowData.yawnLiveSeconds * 20));
		((KitPillow)kpg.getPlayerData(player)).addHirou(PillowData.yawnTired);
	}
}
