package kits.ability.spacia;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class SpaciaBlack extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public SpaciaBlack(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
		}
		FallingBlock fb = (FallingBlock)player.getWorld().spawnFallingBlock(player.getEyeLocation(), Material.BLACK_CONCRETE.createBlockData());
		Vector v = player.getLocation().getDirection().multiply(0.5);
		v.setY(1.8);
		fb.setVelocity(v);
		new BukkitRunnable() {
			public void run() {
				fb.setGravity(false);
				fb.setVelocity(new Vector(0,0,0));
				blackhole(fb);
			}
		}.runTaskLater(plugin, 10);
	}
	
	void blackhole(FallingBlock fb) {
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				for(Entity ent : player.getWorld().getEntities()) {
					if(ent == player) {
						continue;
					}else if(player instanceof Player && !kpg.containsLivings((Player)ent)) {
						continue;
					}
					if(ent instanceof LivingEntity) {
						LivingEntity le = (LivingEntity)ent;
						Location lelo = le.getLocation().clone();
						Location plo = fb.getLocation().clone();
						Vector v = new Vector(plo.getX() - lelo.getX(), plo.getY() - lelo.getY(), plo.getZ() - lelo.getZ()).normalize().multiply(0.45);
						if(le.hasPotionEffect(PotionEffectType.LEVITATION)) {
							v = v.multiply(2.5);
						}
						le.setVelocity(v);
					}
				}
			}
		}.runTaskTimer(plugin, 0, 8);
		new BukkitRunnable() {
			public void run() {
				for(Entity ent:fb.getNearbyEntities(8,8,8)) {
					if(ent instanceof LivingEntity && ent != player) {
						LivingEntity le = (LivingEntity)ent;
						le.setVelocity(new Vector(0,5,0));
					}
				}
				fb.remove();
				bt.cancel();
			}
		}.runTaskLater(plugin, 100);
	}

}
