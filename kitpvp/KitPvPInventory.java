package kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import game.KitPvPGame;
import maindatas.KitData;

public class KitPvPInventory implements Listener{

	Plugin plugin;
	Player player;
	KitPvPGame kpg;
	Inventory inv;
	
	public KitPvPInventory(Player player,Plugin plugin,KitPvPGame kpg) {
		this.player = player;
		this.plugin = plugin;
		this.kpg = kpg;
		Inventory inv = Bukkit.createInventory(null, 54,"キット一覧");
		for(int i = 0 ; i < KitData.kitdis.length ; i++) {
			if(!KitData.kitdis[i][0][0].equals("")) {
				ItemStack item = new ItemStack(KitData.kitm[i]);
				ItemMeta itemm = item.getItemMeta();
				itemm.setDisplayName(KitData.kitdis[i][0][0]);
				item.setItemMeta(itemm);
				inv.setItem(i, item);
			}
		}
		player.openInventory(inv);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onInvetoryClick(InventoryClickEvent e) {
		if(e.getWhoClicked() == player) {
			e.setCancelled(true);
			int raw = e.getSlot();
			kpg.setKitNumver(player, raw);
			player.closeInventory();
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(e.getPlayer() == player) {
			HandlerList.unregisterAll(this);
		}
	}
}
