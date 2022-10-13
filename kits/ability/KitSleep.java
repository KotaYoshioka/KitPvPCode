package kits.ability;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import kits.KitModel;

public class KitSleep implements Listener{
	Player player;
	KitModel playerdata;
	Plugin plugin;
	int dreamid;
	
	public KitSleep(KitModel playerdata,Plugin plugin) {
		this.playerdata = playerdata;
		this.plugin = plugin;
		this.player = playerdata.getPlayer();
		dreamid = new Random().nextInt(1);
		openDream();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	void openDream() {
		Inventory dream = Bukkit.createInventory(null, 54,"夢");
		player.openInventory(dream);
	}
	
	public void endDream() {
		HandlerList.unregisterAll(this);
		player.closeInventory();
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(e.getPlayer() == player) {
			openDream();
		}
	}
}
