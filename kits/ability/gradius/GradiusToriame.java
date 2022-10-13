package kits.ability.gradius;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kitdatas.GradiusData;
import kits.KitGradius;
import kits.ability.KitAbilityBase;

public class GradiusToriame extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public GradiusToriame(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown)kpg.getPlayerData(player).cooldown(abilityindex, cd);
		Location lo = player.getLocation().clone();
		Vector v = player.getLocation().getDirection().normalize();
		double y = lo.getY() + 20;
		Random rnd = new Random();
		int cc = 0;
		for(int i = 0 ; i < 20 ; i++) {
			lo.add(v);
			if(i % 3 == 0) {
				cc++;
				Location clo = lo.clone();
				clo.setY(y);
				int ccc = cc;
				for(int k = 0 ; k < 3 ; k++) {
					new BukkitRunnable() {
						public void run() {
							Location cclo = clo.clone();
							cclo.add(rnd.nextInt(2) * (rnd.nextBoolean()?-1:1),0,rnd.nextInt(2) * (rnd.nextBoolean()?-1:1));
							ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(cclo,EntityType.ARMOR_STAND);
							as.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
							as.setVisible(false);
							new BukkitRunnable() {
								int counter = 0;
								public void run() {
									Vector v = as.getVelocity();
									if(counter > 15 && v.getX() < 0.001 && v.getY() < 0.001 && v.getZ() < 0.001) {
										this.cancel();
										as.remove();
										return;
									}
									boolean hit = false;
									for(Entity ent : as.getNearbyEntities(1, 1, 1)) {
										if(ent instanceof LivingEntity && !(ent instanceof ArmorStand) && ent != player) {
											LivingEntity le = (LivingEntity)ent;
											if(kpg.containsLivings(le)) {
												Player target = (Player)le;
												kpg.getPlayerData(target).damage(GradiusData.toriDamage, player, "徹雨", true);
												kpg.getPlayerData(target).setSilence(GradiusData.toriSilenceTick);
											}else {
												le.damage(GradiusData.toriDamage);
											}
											hit = true;
										}
									}
									if(hit) {
										this.cancel();
										as.remove();
										if(!ignoreCooldown)((KitGradius)kpg.getPlayerData(player)).healGauge(GradiusData.hyakkaHeal);
									}
									counter++;
								}
							}.runTaskTimer(plugin, 0, 2);
						}
					}.runTaskLater(plugin, (5 * ccc)  + (k * 2));
				}
			}
		}
	}

}
