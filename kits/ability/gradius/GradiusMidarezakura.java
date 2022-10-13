package kits.ability.gradius;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class GradiusMidarezakura extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public GradiusMidarezakura(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown)kpg.getPlayerData(player).cooldown(abilityindex, cd);
		Location goal = player.getLocation().clone();
		goal.add(player.getLocation().getDirection().normalize().multiply(15));
		Random rnd = new Random();
		for(int i = 0 ; i < 6 ; i++) {
			int ii = i;
			new BukkitRunnable() {
				public void run() {
					Material[] ms = {Material.WOODEN_SWORD,Material.STONE_SWORD,Material.GOLDEN_SWORD,Material.IRON_SWORD,
							Material.DIAMOND_SWORD,Material.NETHERITE_SWORD};
					Material[] os = {Material.WOODEN_AXE,Material.STONE_AXE,Material.GOLDEN_AXE,Material.IRON_AXE,
							Material.DIAMOND_AXE,Material.NETHERITE_AXE};
					boolean axe = rnd.nextInt(10)==0;
					Location l = player.getLocation().clone();
					l.add(rnd.nextInt(5) * (rnd.nextBoolean()?1:-1),rnd.nextInt(4),rnd.nextInt(5) * (rnd.nextBoolean()?1:-1));
					ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
					as.setVisible(false);
					as.getEquipment().setItemInMainHand(new ItemStack(axe?os[ii]:ms[ii]));
					new GradiusSword(player,plugin,kpg,as,as.getLocation(),goal,axe,ii,ignoreCooldown);
				}
			}.runTaskLater(plugin, rnd.nextInt(15) + 3);
		}
		
	}

}
