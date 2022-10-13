package kits.ability.vampire;

import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import game.KitPvPGame;
import kits.KitVampire;
import kits.ability.KitAbilityBase;

public class VampireOkite extends KitAbilityBase{

	public VampireOkite(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		KitVampire kv = (KitVampire)kpg.getPlayerData(player);
		int counter = 0;
		for(Entity ent:kv.getGenuses()) {
			if(!(ent instanceof Player)) {
				if(ent instanceof Bat) {
					if(ent.getFireTicks() > 0) {
						counter++;
					}
				}else if(ent instanceof Zombie) {
					counter++;
				}
				ent.remove();	
			}
			counter++;
		}
		if(counter > 0) {
			int delay = counter * 20;
			kpg.getPlayerData(player).tempAddSpeed(((int)counter/2) + 1, delay);
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,delay,3));
			player.setFireTicks(0);
		}else {
			player.setFireTicks((kv.getMissCounter()+1) * 50);
			for(Player p:kv.getPlayerGenus()) {
				int delay = (kv.getMissCounter()+1) * 50;
				if(delay > 400) {
					delay = 400;
				}
				p.setFireTicks(delay);
			}
			kv.clearPlayerGenus();
		}
	}

}
