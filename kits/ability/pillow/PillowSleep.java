package kits.ability.pillow;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kitdatas.PillowData;
import kits.ability.KitAbilityBase;

public class PillowSleep extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public PillowSleep(KitPvPGame kpg, Player player,int kitnumber,int index,boolean ignoreCooldown) {
		super(kpg, player,kitnumber,index);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
		}
		if(kpg.getPlayerData(player).isSleep()) {
			return;
		}
		kpg.getPlayerData(player).setSleep(99999);
		float exp = player.getExp();
		if(exp >= PillowData.sleepGauge) {
			new BukkitRunnable() {
				public void run() {
					if(!kpg.getPlayerData(player).isSleep()) {
						this.cancel();
						return;
					}
					float nowexp = player.getExp() - PillowData.sleepGauge;
					if(nowexp <= 0) {
						nowexp = 0;
						kpg.getPlayerData(player).awake();
					}
					player.setExp(nowexp);
					if(!ignoreCooldown) {
						kpg.getPlayerData(player).heal(1, player, "眠り");
					}
				}
			}.runTaskTimer(plugin, 0, 10);
		}
	}

}
