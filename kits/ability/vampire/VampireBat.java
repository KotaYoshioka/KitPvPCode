package kits.ability.vampire;

import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;

public class VampireBat implements Listener{

	Bat bat;
	Player master;
	KitPvPGame kpg;
	Plugin plugin;
	boolean end = false;
	
	public VampireBat(Bat b,Player master,KitPvPGame kpg,Plugin plugin) {
		bat = b;
		this.master = master;
		this.kpg = kpg;
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		follow();
	}
	
	void follow() {
		new BukkitRunnable() {
			public void run() {
				if(end) {
					this.cancel();
					return;
				}
				bat.teleport(master.getLocation().clone());
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() == bat) {
			double damage = e.getDamage() * 2;
			e.setCancelled(true);
			kpg.getPlayerData(master).damage(damage, master, "羽をもがれたコウモリ", false);
			cancel();
		}
	}
	
	public void cancel() {
		EntityDamageEvent.getHandlerList().unregister(this);
		bat.remove();
		for(Player p:kpg.getPlayers()) {
			p.showPlayer(plugin,master);
		}
		master.setAllowFlight(false);
		end = true;
	}
}
