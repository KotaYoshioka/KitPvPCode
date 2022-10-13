package kits.ability.sparkle;

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
import kitdatas.SparkleData;
import kits.KitSparkle;
import kits.ability.KitAbilityBase;

public class SparkleChase extends KitAbilityBase{

	boolean bomb = false;
	int counter = 0;
	ArmorStand as;
	
	public SparkleChase(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		as = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(),EntityType.ARMOR_STAND);
		as.setVisible(false);
		as.getEquipment().setHelmet(new ItemStack(Material.SKELETON_SKULL));
		moveForward(player.getLocation().getDirection().normalize());
		check();
		new BukkitRunnable() {
			public void run() {
				if(bomb) {
					this.cancel();
					return;
				}
				counter++;
				if(counter >= SparkleData.murderExpSeconds) {
					bomb();
				}
			}
		}.runTaskTimer(plugin, 20, 20);
	}
	
	/**
	 * 爆発する処理
	 */
	void bomb() {
		bomb = true;
		KitSparkle kb = (KitSparkle)kpg.getPlayerData(player);
		kb.explosion(as.getLocation(), (counter%2) + 1,false);
		as.remove();
	}
	/**
	 * 爆発範囲に敵がいるかどうかを判定する。
	 * 敵がいた場合、爆発する。
	 */
	void check() {
		new BukkitRunnable() {
			public void run() {
				if(bomb) {
					this.cancel();
					return;
				}
				for(Entity ent:as.getNearbyEntities(1.5,1.5,1.5)) {
					if(ent != player && ent instanceof LivingEntity && !(ent instanceof ArmorStand)) {
						if(ent instanceof Player) {
							if(!kpg.containsLivings(ent)) {
								continue;
							}
						}
						bomb();
					}
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}
	/**
	 * 与えられた方向に移動し続ける。
	 * また、索敵範囲に敵がいるかどうか判定し続ける。
	 * @param v
	 */
	void moveForward(Vector v) {
		new BukkitRunnable() {
			public void run() {
				if(bomb) {
					this.cancel();
					return;
				}
				as.setVelocity(v);
				for(Entity ent : as.getNearbyEntities(SparkleData.murderSearchRange, SparkleData.murderSearchRange, SparkleData.murderSearchRange)) {
					if(ent != player && ent instanceof LivingEntity && !(ent instanceof ArmorStand)) {
						if(ent instanceof Player) {
							if(!kpg.containsLivings(ent)) {
								continue;
							}
						}
						this.cancel();
						chase((LivingEntity)ent);
					}
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}
	/**
	 * 与えられた敵を執拗に追いかけ続ける。
	 * @param target
	 */
	void chase(LivingEntity target) {
		new BukkitRunnable() {
			public void run() {
				if(bomb) {
					this.cancel();
					return;
				}
				Location fbl = as.getLocation();
				Location tlo = target.getLocation();
				Vector v = new Vector(tlo.getX()-fbl.getX(),tlo.getY()-fbl.getY(),tlo.getZ()-fbl.getZ()).normalize();
				as.setVelocity(v);
			}
		}.runTaskTimer(plugin, 0, 2);
	}

}
