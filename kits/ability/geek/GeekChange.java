package kits.ability.geek;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import game.KitPvPGame;
import kits.KitGeek;
import kits.ability.KitAbilityBase;

public class GeekChange extends KitAbilityBase{

	KitGeek kg;
	
	public GeekChange(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		kg = (KitGeek)kpg.getPlayerData(player);
		doAbility();
	}

	@Override
	protected void doAbility() {
		int arrowamount = 0;
		if(player.getInventory().contains(Material.ARROW)) {
			arrowamount = player.getInventory().getItem(player.getInventory().first(Material.ARROW)).getAmount();
		}
		if(player.isSneaking()) {
			if(arrowamount <= 1) {
				player.sendMessage(ChatColor.RED + "弓矢が足りません！");
				return;
			}else if(kg.getParts() >= 100) {
				player.sendMessage(ChatColor.RED + "部品はこれ以上、持てません！");
				return;
			}
			player.getInventory().getItem(player.getInventory().first(Material.ARROW)).setAmount(arrowamount-2);
			kg.setParts(kg.getParts() + 1);
		}else {
			if(kg.getParts() <= 1) {
				player.sendMessage(ChatColor.RED + "部品が足りません！");
				return;
			}else if(arrowamount >= 64) {
				player.sendMessage(ChatColor.RED + "弓矢はこれ以上、持てません！");
				return;
			}
			kg.setParts(kg.getParts() - 2);
			player.getInventory().addItem(new ItemStack(Material.ARROW));
		}
	}

}
