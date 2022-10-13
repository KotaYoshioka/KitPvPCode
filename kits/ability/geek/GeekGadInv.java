package kits.ability.geek;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import game.KitPvPGame;
import kits.KitGeek;

public class GeekGadInv implements Listener{

	Plugin plugin;
	Player player;
	KitPvPGame kpg;
	KitGeek kg;
	boolean shift;
	
	String[] name = {"通常","フレイム","スロー","フラッシュ","ブラインドネス","ケルベロス","ヘッジホッグ","レイン","リコイル"};
	Material[] look = {Material.ARROW,Material.RED_DYE,Material.BLUE_DYE,Material.YELLOW_DYE,Material.BLACK_DYE,Material.BROWN_DYE,
			Material.LIME_DYE,Material.CYAN_DYE,Material.PURPLE_DYE};
	
	public GeekGadInv(Plugin plugin,Player player,KitPvPGame kpg,boolean shift) {
		this.plugin = plugin;
		this.player = player;
		this.kpg = kpg;
		this.shift = shift;
		this.kg = (KitGeek)kpg.getPlayerData(player);
		Inventory inv = Bukkit.createInventory(null, 9,"ガジェット制作");
		player.openInventory(inv);
		openInventory();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	void openInventory() {
		Inventory inv = player.getOpenInventory().getTopInventory();
		List<Integer> now = kg.getGadgetMode(shift);
		for(int i = 0; i < name.length ; i++) {
			ItemStack item = new ItemStack(look[i]);
			ItemMeta itemm = item.getItemMeta();
			itemm.setDisplayName(name[i]);
			//itemm.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GOLD + "矢：" + kg.gadgetModeCost[i],ChatColor.GREEN + "部品：" + kg.partsCost[i])));
			if(now.contains(i)) {
				itemm.addEnchant(Enchantment.LUCK, 1,true);
				itemm.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			item.setItemMeta(itemm);
			inv.setItem(i, item);
		}
	}
	
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getWhoClicked() == player) {
			e.setCancelled(true);
			if(e.getRawSlot() < 9) {
				kg.addGadgetMode(e.getRawSlot(),shift);
				openInventory();
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(e.getPlayer() == player) {
			HandlerList.unregisterAll(this);
		}
	}
}
