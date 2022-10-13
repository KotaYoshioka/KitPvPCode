package kits.ability.ender;

import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kitdatas.EnderData;
import kits.ability.KitAbilityBase;

public class EnderSphere extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public EnderSphere(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown,boolean completeCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		if(completeCooldown) {
			doAbility();
		}else {
			incomplete();
		}
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
		}
		EnderPearl ep = (EnderPearl)player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDER_PEARL);
		ep.setVelocity(player.getLocation().getDirection().multiply(2));
		ep.setShooter(player);
	}
	
	void incomplete() {
		if(kpg.getPlayerData(player).getCooldowntime(abilityindex) > EnderData.pearlShortCool) {
			kpg.getPlayerData(player).damage(EnderData.pearlShortDamage, player,"短縮の代償", false);
			kpg.getPlayerData(player).setCooldowntime(abilityindex, EnderData.pearlShortCool);
		}
	}

}
