package kits;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;

import game.KitPvPGame;

public class KitMonster extends KitModel{

	public KitMonster(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 17);
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}

}