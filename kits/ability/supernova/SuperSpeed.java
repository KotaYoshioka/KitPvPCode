package kits.ability.supernova;

import org.bukkit.Particle;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitSupernova;
import kits.ability.KitAbilityBase;

public class SuperSpeed extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public SuperSpeed(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown) {
			KitSupernova ks = (KitSupernova)kpg.getPlayerData(player);
			if(ks.reduceExp(0.19f)) {
				speed();
			}	
		}else {
			speed();
		}
	}

	void speed() {
		player.setVelocity(player.getLocation().clone().getDirection().normalize().multiply(5));
		player.getWorld().spawnParticle(Particle.END_ROD, player.getEyeLocation(),50,0.86,0.85,0.85,0.65);
	}

}
