package kits.ability.pillow;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import game.KitPvPGame;
import kitdatas.PillowData;
import kits.ability.KitAbilityProjectile;

public class PillowMakura extends KitAbilityProjectile{

	
	public PillowMakura(Player player, Plugin plugin, KitPvPGame kpg, Entity ent) {
		super(player, plugin, kpg, ent, player.getEyeLocation().clone(), 2, 2);
	}

	@Override
	protected void after() {
		body.setVelocity(player.getLocation().clone().getDirection().normalize().multiply(player.isSneaking()?1:2));
	}

	@Override
	protected void hit(Entity ent) {
		if(isLivingEntity(ent)) {
			if(ent != player) {
				LivingEntity le = (LivingEntity)ent;
				if(kpg.containsLivings(le)) {
					Player target = (Player)le;
					if(!kpg.getPlayerData(player).isSleep()) {
						kpg.getPlayerData(target).setSleep(PillowData.pillowSleepSeconds * 20);
					}else {
						kpg.getPlayerData(target).damage(PillowData.pillowEnemyDamage, player, "まくら",true);
					}
				}else {
					le.damage(PillowData.pillowDamage);
				}
				death();
			}else{
				if(kpg.getPlayerData(player).isSleep()) {
					kpg.getPlayerData(player).damage(PillowData.pillowSelfDamage, player, "まくら", false);
					death();
				}
			}
		}
	}

}
