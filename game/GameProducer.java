package game;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class GameProducer implements Listener{

	Plugin plugin;
	KitPvPGame game;
	
	boolean nowgame = true;
	
	public GameProducer(Plugin plugin,KitPvPGame game) {
		this.plugin = plugin;
		this.game = game;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void endGame() {
		nowgame = false;
		HandlerList.unregisterAll(this);
	}
}
