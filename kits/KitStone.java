package kits;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import game.KitPvPGame;
import kits.ability.stone.ThrowedStone;

public class KitStone extends KitModel{

	Set<ThrowedStone> stones = new HashSet<ThrowedStone>();
	
	
	public KitStone(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 3);
		sneakCharge();
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	void sneakCharge() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(player.isSneaking()) {
					Location l = player.getLocation();
		    		l.setY(l.getY() - 0.6);
		    		float exp = player.getExp() + (l.getBlock().getType() == Material.STONE?0.01f:0.004f);
		    		if(exp >= 1) {
		    			player.setLevel(1);
		    			exp = 0;
		    		}
		    		player.setExp(exp);
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}
	
    @EventHandler
    public void onStoneChange(EntityChangeBlockEvent e) {
    	for(ThrowedStone ts: stones) {
    		if(ts.getBody() == e.getEntity()) {
    			e.setCancelled(true);
    			ts.death();
    			stones.remove(ts);
    		}
    	}
    }
    
    public void addStones(ThrowedStone ts) {
    	stones.add(ts);
    }

    @Override
	public Object[] exDamage(Player p,double damage,Player causer,String cause,boolean canHeal,boolean damagable) {
		if(player.getLevel() > 0) {
			player.setLevel(0);
			damagable = false;
		}
    	Object[] a = {p,damage,causer,cause,canHeal,damagable};
		return a;
	}
}
