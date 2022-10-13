package kits.ability.blade;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kitdatas.BladeData;
import kits.KitBlade;
import kits.ability.KitAbilityBase;
import tools.EntityTools;

public class BladeDaisenpu extends KitAbilityBase{

	private boolean z;
	private boolean x;
	private boolean y;
	private double partx;
	private double party;
	private double partz;
	private boolean spirit;
	private boolean hukutu;
	
	private boolean ignoreCooldown;
	
	
	
	public BladeDaisenpu(KitPvPGame kpg, Player player,int kitnumber,int index,boolean ignoreCooldown) {
		super(kpg, player,kitnumber,index);
		spirit = false;
		if(kpg.getPlayerData(player) instanceof KitBlade) {
			KitBlade blade = (KitBlade)kpg.getPlayerData(player);
			spirit = blade.getSpirit();
			if(spirit) {
				blade.cancelSpirit();
			}
			hukutu = blade.getHukutu();
		}
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		//クールダウン処理
		if(!ignoreCooldown) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
		}
		//実際に飛ぶ処理
		moveInfront();
	}
	
	/**
	 * 前方に飛んでいく処理
	 */
	void moveInfront() {
		//発動中、他の技を使用できなくする
		kpg.getPlayerData(player).tempSetKey(false, BladeData.SENPU_LONG_TICKS);
		
		//飛んでいく方向を決める
		Location lo = player.getTargetBlock(null, 20).getLocation().clone();
		Location plo = player.getLocation().clone();
		x = false;
		y = false;
		z = false;
		if(plo.getX() < lo.getX()) {
			x = true;
		}
		if(plo.getY() < lo.getY()) {
			y = true;
		}
		if(plo.getZ() < lo.getZ()) {
			z = true;
		}
		final double disx = Math.abs(plo.getX() - lo.getX());
		final double disy = Math.abs(plo.getY() - lo.getY());
		final double disz = Math.abs(plo.getZ() - lo.getZ());
		partx = disx / (double)BladeData.SENPU_LONG_TICKS;
		party = disy / (double)BladeData.SENPU_LONG_TICKS;
		partz = disz / (double)BladeData.SENPU_LONG_TICKS;
		
		//飛んでいく処理
		new BukkitRunnable() {
			int counter = 1;
			@Override
			public void run() {
				//移動
				double addx = partx * counter;
				double addy = party * counter;
				double addz = partz * counter;
				Location nlo = new Vector(plo.getX() + (x?addx:-addx),plo.getY() + (y?addy:-addy),plo.getZ() + (z?addz:-addz)).toLocation(player.getWorld());
				nlo.setDirection(player.getLocation().getDirection());
				player.teleport(nlo);
				
				//エフェクト
				player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 25, 1.5, 1.5, 1.5, 0);
				
				//当たり判定
				if(counter%2 == 0) {
					attack();
				}
				counter++;
				
				//一定時間後、自壊する。
				if(counter >= BladeData.SENPU_LONG_TICKS) {
					this.cancel();
					//直近で打ったことを記録する
					if(kpg.getPlayerData(player) instanceof KitBlade) {
						((KitBlade)kpg.getPlayerData(player)).nearlyDaisenpu(BladeData.SENPU_ZANUP_LONG_TICKS);
					}
				}
			}
		}.runTaskTimer(plugin,0,1);
	}
	
	/**
	 * 大旋風でダメージを与える処理
	 * また、近くに飛び道具があった場合、消す処理
	 */
	void attack() {
		double range = BladeData.SENPU_HIT_RANGE;
		int damage = BladeData.SENPU_DAMAGE;
		if(spirit) {
			damage = damage * 2;
		}
		if(hukutu) {
			damage = damage * 2;
		}
		for(Entity e:player.getNearbyEntities(range, range, range)) {
			if(EntityTools.isLivingEntity(e)) {
				LivingEntity le = (LivingEntity)e;
				if(le instanceof Player) {
					if(kpg.containsLivings(e)) {
						Player target = (Player)e;
						kpg.getPlayerData(target).damage(damage, player, "大旋風",true);
					}
				}else {
					le.damage(damage);
				}
				Random rnd = new Random();
				le.setVelocity(new Vector(rnd.nextDouble() - 0.5d,1,rnd.nextDouble() - 0.5d));
			}else if(kpg.containProjectile(e)) {
				kpg.getProjectileData(e).death();
			}
		}
	}

}
