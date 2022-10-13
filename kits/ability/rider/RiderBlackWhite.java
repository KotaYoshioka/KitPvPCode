package kits.ability.rider;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.KitRider;
import kits.ability.KitAbilityBase;

public class RiderBlackWhite extends KitAbilityBase{

	public RiderBlackWhite(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		((KitRider)kpg.getPlayerData(player)).setNoCount(true);
		new BukkitRunnable() {
			public void run() {
				((KitRider)kpg.getPlayerData(player)).setNoCount(false);
			}
		}.runTaskLater(plugin, 4);
		boolean black = ((KitRider)kpg.getPlayerData(player)).getBlack();
		Horse horse = (Horse)player.getWorld().spawnEntity(player.getLocation(),EntityType.HORSE);
		horse.setColor(black?Horse.Color.BLACK:Horse.Color.WHITE);
		horse.setStyle(Style.NONE);
		horse.setOwner(player);
		horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		if(black) {
			horse.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,9999999,7));
			horse.setJumpStrength(10);
		}else {
			new BukkitRunnable() {
				public void run() {
					if(!horse.isValid() || !kpg.getPlayerData(player).getLive()) {
						this.cancel();
						return;
					}
					horse.setVelocity(player.getLocation().getDirection().normalize().multiply(0.6));
				}
			}.runTaskTimer(plugin, 0, 1);
		}
		horse.addPassenger(player);
		KitRider rider = (KitRider)kpg.getPlayerData(player);
		if(rider.getRide() != null) {
			rider.getRide().remove();
		}
		((KitRider)kpg.getPlayerData(player)).setRide(horse);
	}
}
