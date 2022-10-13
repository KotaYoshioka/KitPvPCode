package kits.ability.blade;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kitdatas.BladeData;
import kits.ability.KitAbilityProjectile;
import tools.EntityTools;

public class BladeZangekiBody extends KitAbilityProjectile{
	
	//ダメージ
	double damage;
	//斬撃演出量
	int amount;
	//大旋風直後
	boolean daisenpu;
	//精神統一
	boolean spirit;
	//不屈
	boolean hukutu;
	
	public BladeZangekiBody(Player player, Plugin plugin, KitPvPGame kpg, boolean daisenpu,boolean spirit,boolean hukutu) {
		super(player, plugin, kpg, EntityType.ARMOR_STAND, player.getLocation().clone(), BladeData.ZAN_POWER, BladeData.ZAN_HIT_RANGE);
		this.daisenpu = daisenpu;
		this.spirit = spirit;
		this.hukutu = hukutu;
	}

	@Override
	protected void after() {
		//アーマースタンドの設定
		ArmorStand as = (ArmorStand)body;
		as.setVisible(false);
		
		//飛んでいく処理
		final Vector zangekiDirection = player.getLocation().getDirection().clone();
		new BukkitRunnable() {
			int counter = 0;
			public void run() {
				if(death) {
					this.cancel();
					return;
				}
				
				//前方に飛んでいく処理
				as.setVelocity(zangekiDirection);
				
				//徐々にダメージが弱まっていく演出
				if(counter < 10) {
					damage = BladeData.ZAN_MAX_DAMAGE;
					amount = 30;
				}else if(counter < 20) {
					damage = BladeData.ZAN_MID_DAMAGE;
					amount = 20;
				}else {
					damage = BladeData.ZAN_MIN_DAMAGE;
					amount = 10;
				}
				
				//大旋風直後で威力が上がるやつ
				if(daisenpu) {
					damage += BladeData.ZAN_PLUS_DAMAGE;
				}
				//精神統一が出来ている場合
				if(spirit) {
					damage = damage * 2;
				}
				//不屈が出ている場合
				if(hukutu) {
					damage = damage * 2;
				}
				
				//パーティクルが飛び散る演出
				animation();
				
				//一定時間後の自壊
				counter++;
				if(counter > BladeData.ZAN_LONG_TICKS) {
					death();
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	/**
	 * 地面の土ぼこりと、剣のエフェクト
	 */
	private void animation() {
		player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, body.getLocation(), amount, 1d,1d,1d, 0);
		if(body.isOnGround()) {
			Location ulo = body.getLocation();
			ulo.setY(ulo.getY() - 1);
			body.getWorld().spawnParticle(Particle.BLOCK_CRACK, body.getLocation(),200, 0.1, 0.6, 0.1, 0.8,/*地面のパーティクル*/ulo.getBlock().getType().createBlockData());
		}
	}
	
	@Override
	protected void hit(Entity ent) {
		if(EntityTools.isLivingEntity(ent) && ent != player) {
			LivingEntity le = (LivingEntity)ent;
			if(kpg.containsLivings(le)) {
				kpg.getPlayerData((Player)le).damage(damage, player, "斬撃", true);
			}else {
				le.damage(damage);
			}
			le.setVelocity(new Vector(0,BladeData.ZAN_UPPER,0));
			death();
		}
	}
}
