package kits.ability.gambler;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import game.KitPvPGame;
import kitdatas.GamblerData;
import kits.KitGambler;
import kits.ability.KitAbilityBase;
import maindatas.KitData;

public class GamblerGGacha extends KitAbilityBase implements Listener{

	KitGambler gambler;
	
	int[][] kouho = {{-1,-1},{-1,-1},{-1,-1}};
	
	public GamblerGGacha(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		gambler = (KitGambler)kpg.getPlayerData(player);
		if(gambler.getGGacha()[0] != -1) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
			kpg.ka.doAbility(gambler.getGGacha()[0], gambler.getGGacha()[1], player,true);
			gambler.setGGacha(-1, -1);
		}else {
			doAbility();
		}
	}

	@Override
	protected void doAbility() {
		if(gambler.reduceCoin(GamblerData.richGachaCost)) {
			kpg.getPlayerData(player).cooldown(abilityindex, 1);
			Inventory inv = Bukkit.createInventory(null, 9,"1つ選んでください");
			for(int i = 0 ; i < 3 ; i++) {
				inv.setItem(i + 3, getItem(i));
			}
			player.openInventory(inv);
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
		}else {
			player.sendMessage(ChatColor.RED + "コインが足りません。");
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getWhoClicked() == player) {
			e.setCancelled(true);
			int ind = e.getRawSlot() - 3;
			if(ind < 3 && ind > -1) {
				gambler.setGGacha(kouho[ind][0],kouho[ind][1]);
				player.closeInventory();
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(e.getPlayer() == player) {
			HandlerList.unregisterAll(this);
		}
	}
	
	ItemStack getItem(int in) {
		int kitid = -1;
		int index = -1;
		Random rnd = new Random();
		for(int i = 0 ; i < 1000 ; i++) {
			int kkitid = rnd.nextInt(KitData.kitdis.length);
			if(KitData.kitdis[kkitid][0][0] != "") {
				int kindex = rnd.nextInt(3);
				if(KitData.copiable[kkitid][kindex]) {
					kitid = kkitid;
					index = kindex;
					break;
				}
			}
		}
		if(kitid != -1) {
			kouho[in][0] = kitid;
			kouho[in][1] = index;
			return KitData.getWeapon(kitid, index, 0, false);
		}else {
			kouho[in][0] = 0;
			kouho[in][1] = 0;
			return KitData.getWeapon(0, 0, 0, false);
		}
	}

}
