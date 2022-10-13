package kitpvp;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import tools.ItemTools;

public class KitPvPPrepare implements Listener{
	Plugin plugin;
	KitPvPGame game;
	Player player;
	
	boolean isReady = false;
	boolean cool = true;
	
	
	public KitPvPPrepare(Plugin plugin,KitPvPGame game,Player player) {
		this.plugin = plugin;
		this.game = game;
		this.player = player;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		setItem();
	}
	
	public void cancel() {
		HandlerList.unregisterAll(this);
	}
	
	/**
	 * 準備完了に合わせて「キット選択」と「準備完了」を配布する。
	 */
	private void setItem() {
		player.getInventory().clear();
		if(isReady) {
			player.getInventory().addItem(ItemTools.createItem("キット選択",Material.SHULKER_SHELL),ItemTools.createItem("準備完了",Material.RED_DYE));
		}else {
			player.getInventory().setItem(1, ItemTools.createItem("準備完了", Material.GREEN_DYE));
		}
	}
	
	/*
	 * アイテムの右クリック関係を作成
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getPlayer() == player && cool && ItemTools.hasItemNotNull(player)) {
			switch(player.getInventory().getHeldItemSlot()) {
			case 0:
				new KitPvPInventory(player,plugin,game);
				break;
			case 1:
				cooldown();
				isReady = !isReady;
				setItem();
				break;
			}
		}
	}
	
	private void cooldown() {
		cool = false;
		new BukkitRunnable() {
			public void run() {
				cool = true;
			}
		}.runTaskLater(plugin, 5);
	}
	
	public boolean getReady() {
		return isReady;
	}
}
