package kitpvp;

import org.bukkit.plugin.java.JavaPlugin;

public class KitPvPMain extends JavaPlugin{
	@Override
	public void onEnable() {
		getCommand("kitpvp").setExecutor(new KitPvPCommand(this));
	}
}
