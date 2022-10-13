package kits.ability.blade;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kitdatas.BladeData;
import kits.KitBlade;
import kits.ability.KitAbilityBase;

public class BladeSpirit extends KitAbilityBase{

	boolean hukutu = false;
	
	KitBlade blade;
	
	public BladeSpirit(KitPvPGame kpg, Player player,int kitnumber,int index) {
		super(kpg, player,kitnumber,index);
		blade = (KitBlade)kpg.getPlayerData(player);
		hukutu = blade.getHukutu();
		doAbility();
	}

	@Override
	protected void doAbility() {
		//クールダウン
		blade.cooldown(abilityindex, cd);		
		
		//不屈状態の時の無敵
		if(hukutu) {
			kpg.getPlayerData(player).tempMuteki(BladeData.SPIRIT_GOD_LONG_TICKS);
		}
		
		//技再使用までのラグ
		blade.tempSetKey(false, BladeData.SPIRIT_STAN_TICKS);
		
		//スピリット状態の演出
		blade.setSpirit(true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,BladeData.SPIRIT_LONG_TICKS,1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,BladeData.SPIRIT_LONG_TICKS,8));
		new BukkitRunnable() {
			public void run() {
				blade.cancelSpirit();
			}
		}.runTaskLater(plugin, BladeData.SPIRIT_LONG_TICKS);
	}


}
