package kits.ability.gradius;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kitdatas.GradiusData;
import kits.KitGradius;
import kits.ability.KitAbilityProjectile;

public class GradiusSword extends KitAbilityProjectile{

	Location goal;
	Location sumLo;
	int counter = 0;
	boolean axe;
	int type = 0;
	boolean ignore;
	
	public GradiusSword(Player player, Plugin plugin, KitPvPGame kpg, Entity ent, Location sumLo,Location goal,boolean axe,int type,boolean ignore) {
		super(player, plugin, kpg, ent, sumLo, 5, 1.3);
		this.sumLo = sumLo;
		this.goal = goal;
		this.axe = axe;
		this.type = type;
		this.ignore = ignore;
	}

	@Override
	protected void after() {
		Vector v = new Vector(goal.getX()-sumLo.getX(),goal.getY()-sumLo.getY(),goal.getZ()-sumLo.getZ()).normalize().multiply(2);
		body.setVelocity(v);
		new BukkitRunnable() {
			public void run() {
				if(death) {
					this.cancel();
					return;
				}
				counter++;
				Vector v = body.getVelocity();
				if(counter > 5 && v.getX() <= 0.001 && v.getY() <= 0.001 && v.getZ() <= 0.001) {
					death();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	@Override
	protected void hit(Entity ent) {
		if(isLivingEntity(ent)) {
			LivingEntity le = (LivingEntity)ent;
			int damage = type + (axe?GradiusData.midareAxe:GradiusData.midareSword);
			if(kpg.containsLivings(le)) {
				Player target = (Player)le;
				kpg.getPlayerData(target).damage(damage, player, "乱桜",true);
			}else {
				le.damage(damage);
			}
			if(!ignore)((KitGradius)kpg.getPlayerData(player)).healGauge(0.1f);
			death();
		}
	}

}
