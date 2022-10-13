package kits.ability.geek;

import java.util.ArrayList;
import java.util.Arrays;

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
import net.md_5.bungee.api.ChatColor;

public class GeekPorInv implements Listener{

	Plugin plugin;
	Player player;
	KitPvPGame kpg;
	KitGeek kg;
	boolean shift;
	
	String[] names = {"リセット","ウィンド","フレイム","スモーク","フラッシュ","サイレンス","パーティ","バウンド"};
	Material[] looks = {Material.BARRIER,Material.GREEN_STAINED_GLASS,Material.RED_STAINED_GLASS,Material.GRAY_STAINED_GLASS,Material.YELLOW_STAINED_GLASS,
			Material.WHITE_STAINED_GLASS,Material.PINK_STAINED_GLASS,Material.PURPLE_STAINED_GLASS};
	
	public GeekPorInv(Plugin plugin,Player player,KitPvPGame kpg,boolean shift) {
		this.plugin = plugin;
		this.player = player;
		this.kpg = kpg;
		this.shift = shift;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		kg = (KitGeek)kpg.getPlayerData(player);
		Inventory inv = Bukkit.createInventory(null, 9,"ポータブル制作");
		player.openInventory(inv);
		setInventory();
	}
	
	void setInventory() {
		Inventory inv = player.getOpenInventory().getTopInventory();
		for(int i = 0 ; i < names.length ; i++) {
			ItemStack item = new ItemStack(looks[i]);
			ItemMeta itemm = item.getItemMeta();
			itemm.setDisplayName(names[i]);
			itemm.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GREEN + "部品：" + kg.portableCost[i])));
			if(kg.getPortable(shift).contains(i)) {
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
			if(e.getRawSlot() < names.length) {
				kg.addPortableMode(e.getRawSlot(),shift);
				setInventory();
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
