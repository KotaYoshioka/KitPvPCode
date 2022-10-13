package kits.ability.supernova;

import org.bukkit.Particle;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kits.KitSupernova;
import kits.ability.KitAbilityBase;

public class SuperSonic extends KitAbilityBase{

	KitSupernova ks;
	
	public SuperSonic(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		ks = (KitSupernova)kpg.getPlayerData(player);
		player.getWorld().spawnParticle(Particle.END_ROD, player.getEyeLocation(),50,0.86,0.85,0.85,0.65);
		player.getWorld().spawnParticle(Particle.COMPOSTER, player.getEyeLocation(),20,0.3,0.3,0.3,0.2);
		player.getWorld().spawnParticle(Particle.CRIT, player.getEyeLocation(),40,0.86,0.85,0.85,0.65);
		player.getWorld().spawnParticle(Particle.CRIT_MAGIC, player.getEyeLocation(),20,0.86,0.85,0.85,0.65);
		player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getEyeLocation(),10,1,1,1,0.5);
		ks.delay();
		if(!ks.getGo() && ks.getStacktime() == 0) {
			doAbility();
		}else if(!ks.getGo()) {
			ks.dashCounter();
		}
	}

	@Override
	protected void doAbility() {
		ks.stacktime();
		ks.dashCounter();
	}

}
