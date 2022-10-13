package kits.ability.vampire;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.KitVampire;
import kits.ability.KitAbilityBase;

public class VampireBlood extends KitAbilityBase{

	public VampireBlood(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		KitVampire kv = (KitVampire)kpg.getPlayerData(player);
		kv.cooldown(abilityindex, cd);
		for(Player p:kpg.getLivings()) {
			Location l = p.getLocation().clone();
			Location ll = l.clone();
			Random rnd = new Random();
			for(int i = 0 ; i < 999; i++) {
				ll = l.clone();
				ll.add(rnd.nextInt(3) * (rnd.nextBoolean()?-1:1),0,rnd.nextInt(3) * (rnd.nextBoolean()?-1:1));
				if(ll.getBlock().getType() == Material.AIR) {
					break;
				}
			}
			Zombie z = (Zombie)player.getWorld().spawnEntity(ll, EntityType.ZOMBIE);
			z.setFireTicks(999999);
			z.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,999999,2));
			kv.addZombie(z);
			new BukkitRunnable() {
				public void run() {
					if(z.isValid()) {
						z.remove();
					}
				}
			}.runTaskLater(plugin, 600);
		}
		kv.setMissCounter(kv.getMissCounter() + 1);
	}

}
