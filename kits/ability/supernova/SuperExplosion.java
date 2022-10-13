package kits.ability.supernova;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class SuperExplosion extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public SuperExplosion(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown)kpg.getPlayerData(player).cooldown(abilityindex, cd);
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(),40,1,1,1,0.3);
				player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getLocation(),40,1,1,1,0.3);
				kpg.getPlayerData(player).addSpeed(1);
			}
		}.runTaskTimer(plugin,0, 19);
		new BukkitRunnable() {
			public void run() {
				bt.cancel();
				player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(),100,1,1,1,0.5);
				player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getLocation(),100,1,1,1,0.5);
				player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(),50,1,1,1,0.5);
				player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(),50,1,1,1,0.5);
				player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,400,1));
				kpg.getPlayerData(player).addSpeed(-5);
				for(Entity ent:player.getNearbyEntities(3, 3, 3)) {
					if(ent != player && ent instanceof LivingEntity) {
						LivingEntity le = (LivingEntity)ent;
						if(kpg.containsLivings(le)) {
							Player target = (Player)le;
							kpg.getPlayerData(target).damage(7, player, "スーパーエクスプレス",false);
							target.setVelocity(new Vector(0,2,0));
						}else {
							le.damage(7);
						}
					}
				}
			}
		}.runTaskLater(plugin, 80);
	}

}
