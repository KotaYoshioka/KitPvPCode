package kits.ability.geek;

import org.bukkit.ChatColor;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitGeek;
import kits.ability.KitAbilityBase;

public class GeekPortable extends KitAbilityBase{
	
	KitGeek kg;
	boolean right;
	
	public GeekPortable(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean right) {
		super(kpg, player, kitnumber, abilityindex);
		this.right = right;
		kg = (KitGeek)kpg.getPlayerData(player);
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!right) {
			new GeekPorInv(plugin,player,kpg,player.isSneaking());
		}else {
			boolean sneak = player.isSneaking();
			if(kg.mathPartsProtable(sneak) <= kg.getParts()) {
				kg.setParts(kg.getParts() - kg.mathPartsProtable(sneak));
				Egg egg = (Egg)player.getWorld().spawnEntity(player.getEyeLocation(),EntityType.EGG);
				egg.setShooter(player);
				egg.setVelocity(player.getLocation().getDirection());
				KitGeek kg = (KitGeek)kpg.getPlayerData(player);
				kg.registerMasterEgg(egg);
				kg.registerSneakEgg(egg, sneak);
			}else {
				player.sendMessage(ChatColor.RED + "部品が足りません！");
			}
		}
	}

}
