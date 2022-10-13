package kits.ability.shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitShadow;
import kits.ability.KitAbilityBase;

public class ShadowFumble extends KitAbilityBase{

	KitShadow ks;
	
	public ShadowFumble(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		ks = (KitShadow)kpg.getPlayerData(player);
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(ks.getDarkMatters().size() != 0) {
			List<Player> enemies = new ArrayList<Player>();
			enemies.addAll(kpg.getLivings());
			enemies.remove(player);
			if(enemies.size() > 0) {
				Collections.shuffle(enemies);
				Player target = enemies.get(0);
				Location tl = target.getLocation().clone();
				ShadowDarkBody near = null;
				double distance = 999999;
				for(ShadowDarkBody matter : ks.getDarkMatters()) {
					Location ml = matter.getBody().getLocation();
					if(tl.distance(ml) < distance) {
						distance = tl.distance(ml);
						near = matter;
					}
				}
				if(near != null) {
					kpg.getPlayerData(player).cooldown(abilityindex, cd);
					player.teleport(near.getBody().getLocation());
					near.death();
				}
			}
		}
	}

}
