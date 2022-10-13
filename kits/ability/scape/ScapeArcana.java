package kits.ability.scape;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.KitScape;
import kits.ability.KitAbilityBase;

public class ScapeArcana extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public ScapeArcana(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
		}
		Random rnd = new Random();
		for(int i = 0 ; i < 3 ; i++) {
			Location zlo = player.getLocation().clone();
			zlo.add(rnd.nextInt(3) * (rnd.nextBoolean()?-1:1),rnd.nextInt(3),rnd.nextInt(3) * (rnd.nextBoolean()?-1:1));
			Zombie z = (Zombie)player.getWorld().spawnEntity(zlo,EntityType.ZOMBIE);	
			z.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,999999,1));
			z.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,999999,1));
			z.getEquipment().clear();
			z.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
			z.setNoDamageTicks(99999);
			if(kpg.getPlayerData(player) instanceof KitScape) {
				KitScape ks = (KitScape)kpg.getPlayerData(player);
				ks.addArcana(z);
			}
			player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, zlo, 50, 0.2, 0.6, 0.2, 0.4);
			new BukkitRunnable() {
				int counter = 0;
				public void run() {
					if(!z.isValid()) {
						this.cancel();
						return;
					}
					Location l = z.getLocation();
					l.add(0,1,0);
					player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, l, 35, 0.2, 0.4, 0.2, 0.03);
					counter++;
					if(counter >= 30) {
						z.remove();
					}
				}
			}.runTaskTimer(plugin, 0, 5);
		}
	}

}
