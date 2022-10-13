package kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import game.KitPvPGame;
import kits.ability.pillow.PillowSleep;

public class KitPillow extends KitModel{

	List<FallingBlock> pillows = new ArrayList<FallingBlock>();
	
	public KitPillow(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 25);
		busy();
	}
	
	void busy() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(isSleep()) {
					return;
				}
				addHirou(0.005f);
			}
		}.runTaskTimer(plugin, 0, 10);
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	public void addHirou(float hirou) {
		float exp = player.getExp() + hirou;
		if(exp >= 1) {
			exp = 0.5f;
			new PillowSleep(kpg,player,25,0,true);
		}
		player.setExp(exp);
	}
	
    @EventHandler
    public void onStoneChange(EntityChangeBlockEvent e) {
    	for(FallingBlock fb: pillows) {
    		if(fb == e.getEntity()) {
    			e.setCancelled(true);
    		}
    	}
    }
    
    public void addPillow(FallingBlock pl) {
    	pillows.add(pl);
    }
}
