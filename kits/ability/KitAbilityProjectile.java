package kits.ability;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;

public abstract class KitAbilityProjectile {
	
	protected Plugin plugin;
	protected KitPvPGame kpg;
	protected Player player;
	//実体
	protected Entity body;
	//現在も存在するかどうか
	protected boolean death = false;
	//飛び道具威力
	//飛び道具同士がぶつかり合った時、より高い方が生き残り、低い方は消される。
	protected int power;
	//当たり判定の大きさ
	protected double hitRange;
	
	public KitAbilityProjectile(Player player ,Plugin plugin, KitPvPGame kpg,EntityType body,Location sumLo,int power,double hitRange) {
		this.player = player;
		this.plugin = plugin;
		this.kpg = kpg;
		this.power = power;
		this.hitRange = hitRange;
		summon(body,sumLo);
		hitRepeater();
	}
	public KitAbilityProjectile(Player player ,Plugin plugin, KitPvPGame kpg,Entity ent,Location sumLo,int power,double hitRange) {
		this.player = player;
		this.plugin = plugin;
		this.kpg = kpg;
		this.power = power;
		this.hitRange = hitRange;
		this.body = ent;
		kpg.addProjectile(body, this);
		after();
		hitRepeater();
	}
	
	/**
	 * 飛び道具を出現させる
	 * @param et
	 * @param l
	 */
	void summon(EntityType et,Location l) {
		this.body = player.getWorld().spawnEntity(l,et);
		kpg.addProjectile(body, this);
		after();
	}
	
	protected abstract void after();
	
	/**
	 * 当たり判定の処理
	 */
	void hitRepeater() {
		if(death) {
			return;
		}
		if(body.isDead() || !body.isValid()) {
			death = true;
			return;
		}
		for(Entity ent:body.getNearbyEntities(hitRange, hitRange, hitRange)) {
			//飛び道具同士の相殺
			if(kpg.containProjectile(ent) && ent != body && kpg.getProjectileData(ent).getMaster() != getMaster()) {
				offset(ent);
			}else {
				//観戦者の可能性を事前に排除
				if(ent instanceof Player && !kpg.containsLivings(ent)) {
					continue;
				}
				hit(ent);
			}
		}
		new BukkitRunnable() {
			public void run() {
				hitRepeater();	
			}
		}.runTaskLater(plugin, 1);
	}
	
	/**
	 * 相殺処理
	 */
	void offset(Entity ent) {
		KitAbilityProjectile anotherData = kpg.getProjectileData(ent);
		if(getPower() > anotherData.getPower()) {
			reducePower(anotherData.getPower());
			anotherData.death();
		}else if(getPower() == anotherData.getPower()) {
			anotherData.death();
			death();
			return;
		}
	}
	
	protected abstract void hit(Entity ent);
	
	/**
	 * 飛び道具が消えるとき
	 */
	public void death() {
		if(!death) {
			death = true;
			body.remove();
			kpg.removeProjectile(body);
		}
	}
	
	//GetterとSetter
	public int getPower() {
		return power;
	}
	public void setPower(int power) {
		this.power = power;
	}
	public void reducePower(int power) {
		int newpower = this.power - power;
		if(newpower <= 0) {
			newpower = 0;
		}
		setPower(newpower);
	}
	public Entity getBody() {
		return body;
	}
	
	public boolean isLivingEntity(Entity ent) {
		if(ent instanceof LivingEntity && !(ent instanceof ArmorStand)) {
			return true;
		}else {
			return false;
		}
	}
	
	public Player getMaster() {
		return player;
	}
}
