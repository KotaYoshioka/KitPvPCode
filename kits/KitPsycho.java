package kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import game.KitPvPGame;
import kits.ability.psycho.PsychoLeviBlo;

public class KitPsycho extends KitModel{

	List<PsychoLeviBlo> blocks = new ArrayList<PsychoLeviBlo>();
	
	public KitPsycho(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 28);
		PsyGage();
		player.setAllowFlight(true);
		player.setExp(0.5f);
		reScoreboard();
	}
	
	public void addBlocks(PsychoLeviBlo plb) {
		blocks.add(plb);
	}

	@Override
	public void kitScoreboard(Objective o) {
		if(!player.getAllowFlight()) {
			Score cf = o.getScore(ChatColor.GRAY + "[不安定期]");
			cf.setScore(29);
		}
	}
	
    @EventHandler
    public void onStoneChange(EntityChangeBlockEvent e) {
    	for(PsychoLeviBlo levi: blocks) {
    		if(levi.getBody() == e.getEntity()) {
    			e.setCancelled(true);
    			levi.death();
    			blocks.remove(levi);
    		}
    	}
    }
	
	void PsyGage() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(player.isFlying()) {
					if(!UsePsy(0.03f)) {
						player.setAllowFlight(false);
						reScoreboard();
						new BukkitRunnable() {
							public void run() {
								if(live) {
									player.setAllowFlight(true);
									reScoreboard();
								}
							}
						}.runTaskLater(plugin, 300);
					}
				}
				float exp = player.getExp();
				exp += 0.01f;
				if(exp > 1) {
					exp = 0.99f;
				}
				player.setExp(exp);
			}
		}.runTaskTimer(plugin, 0, 5);
	}

	public boolean UsePsy(float f) {
		float exp = player.getExp();
		if(exp - f < 0) {
			return false;
		}else {
			exp -= f;
			player.setExp(exp);
			return true;
		}
	}
}
