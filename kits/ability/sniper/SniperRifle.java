package kits.ability.sniper;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class SniperRifle extends KitAbilityBase{

	public SniperRifle(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		Vector v = player.getEyeLocation().clone().getDirection().normalize();
		Location l = player.getEyeLocation().clone();
		for(int i = 0 ; i < 400 ; i++) {
			l.add(v);
			DustOptions dust = new DustOptions(Color.fromRGB(255,0,0),1);
			player.getWorld().spawnParticle(Particle.REDSTONE,l,2,dust);
			for(Entity ent:player.getWorld().getNearbyEntities(l, 0.5, 0.5, 0.5)) {
				if(ent != player && ent instanceof LivingEntity) {
					LivingEntity le = (LivingEntity)ent;
					if(kpg.containsLivings(le)) {
						Player target = (Player)le;
						kpg.getPlayerData(target).damage(8, target, "ライフル", false);
					}else {
						le.damage(8);
					}
					return;
				}
			}
		}
	}
}
