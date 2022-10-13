package kits.ability.blade;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kitdatas.BladeData;
import kits.KitBlade;
import kits.ability.KitAbilityBase;

public class BladeZangeki extends KitAbilityBase{
	
	boolean ignoreCooldown;
	
	public BladeZangeki(KitPvPGame kpg,Player player,int kitnumber,int index,boolean ignoreCooldown) {
		super(kpg,player,kitnumber,index);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}
	
	protected void doAbility() {
		//クールダウン処理
		if(!ignoreCooldown) {
			km.cooldown(abilityindex,cd);	
		}
		
		//空中で使用した場合に後ろ側に反動が発生する処理
		if(!((LivingEntity)player).isOnGround()) {
			Vector v = player.getLocation().getDirection().normalize().multiply(-1 * BladeData.ZAN_RECOIL);
			kpg.getPlayerData(player).setVelocity(v);
		}
		
		//斬撃を召喚
		boolean daisenpu = false;
		boolean spirit = false;
		boolean hukutu = false;
		if(kpg.getPlayerData(player) instanceof KitBlade) {
			KitBlade kk = (KitBlade)kpg.getPlayerData(player);
			daisenpu = kk.getDaisenpu();
			spirit = kk.getSpirit();
			hukutu = kk.getHukutu();
			if(spirit) {
				kk.cancelSpirit();
			}
		}
		new BladeZangekiBody(player,plugin,kpg,daisenpu,spirit,hukutu);
	}
	
}
