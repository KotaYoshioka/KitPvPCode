package kits.ability.sand;

import org.bukkit.Material;
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
import kitdatas.KensiData;
import kits.KitModel;
import kits.ability.KitAbilityProjectile;

public class SandZangekiBody extends KitAbilityProjectile{

	public SandZangekiBody(Player player, Plugin plugin, KitPvPGame kpg) {
		super(player, plugin, kpg, EntityType.ARMOR_STAND, player.getLocation().clone(), 5, 1.5d);
	}

	@Override
	protected void after() {
		ArmorStand as = (ArmorStand)body;
		as.setVisible(false);
		final Vector v = player.getLocation().getDirection().clone();
		//移動及びアニメーション
		new BukkitRunnable() {
			//徐々に速くする演出のために使用
			int counter = 0;
			public void run() {
				if(death) {
					this.cancel();
					return;
				}
				//前方に飛んでいく処理
				as.setVelocity(v);
				//パーティクルが飛び散る演出
				player.getWorld().spawnParticle(Particle.FALLING_DUST, as.getLocation(), 80, 0.2d,1d,0.2d, 0.3,Material.SAND.createBlockData());
				player.getWorld().spawnParticle(Particle.BLOCK_DUST, as.getLocation(), 30, 1d,1d,1d, 0.3,Material.SAND.createBlockData());
				player.getWorld().spawnParticle(Particle.BLOCK_CRACK, as.getLocation(), 30, 1d,1d,1d, 0.3,Material.SAND.createBlockData());

				counter++;
				//一定時間後の自壊
				if(counter > 50) {
					death();
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	@Override
	protected void hit(Entity ent) {
		if(ent instanceof LivingEntity && ent != player) {
			LivingEntity le = (LivingEntity)ent;
			if(kpg.containsLivings(le)) {
				Player enemy = (Player)le;
				KitModel km = kpg.getPlayerData(enemy);
				km.damage(2, player, "這い寄る爪", true);
				km.reduceFood(4);
				km.setVelocity(new Vector(0,KensiData.zangekiUpperVelocity,0));
			}else {
				le.damage(2);
				le.setVelocity(new Vector(0,KensiData.zangekiUpperVelocity,0));
			}
			death();
		}
	}

}
