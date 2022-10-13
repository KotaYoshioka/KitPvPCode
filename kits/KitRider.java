package kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import game.KitPvPGame;
import kitpvp.KitPvPSteer;
import kits.ability.rider.RiderThrowHorse;

public class KitRider extends KitModel{

	LivingEntity ride = null;
	List<LivingEntity> others = new ArrayList<LivingEntity>();
	HashMap<LivingEntity,Integer> positions = new HashMap<LivingEntity,Integer>();
	List<LivingEntity> skeletons = new ArrayList<LivingEntity>();
	

	boolean nocount = false;
	
	boolean black = true;
	
	ProtocolManager pm;
	
	public KitRider(Plugin plugin, Player player, KitPvPGame kpg, int kitnumber) {
		super(plugin, player, kpg, kitnumber);
		pm = ProtocolLibrary.getProtocolManager();
		pm.addPacketListener(new KitPvPSteer(plugin,player,PacketType.Play.Client.STEER_VEHICLE));
		this.addSpeed(-3);
		this.addJump(-2);
		renkou();
	}
	
	void renkou() {
		new BukkitRunnable() {
			public void run() {
				for(LivingEntity other:others) {
					Location l = player.getLocation().clone();
					Vector v = player.getLocation().getDirection().clone().normalize().multiply(positions.get(other)%2==0?6:3);
					v.rotateAroundY(positions.get(other)<3?90:-90);
					v.setY(0);
					l.add(v);
					l.setDirection(player.getLocation().getDirection());
					other.teleport(l);
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	@Override
	public void death() {
		super.death();
		pm.removePacketListeners(plugin);
	}
	
	@Override
	public void kitScoreboard(Objective o) {
		
	}

	public void setRide(LivingEntity ride) {
		this.ride = ride;
	}
	
	public LivingEntity getRide() {
		return ride;
	}
	
	public boolean getBlack() {
		black = !black;
		return !black;
	}
	
	@EventHandler
	public void onDamageHorse(EntityDamageEvent e) {
		if(e.getEntity() == ride || others.contains(e.getEntity())) {
			if(e.getCause() == DamageCause.FALL)e.setCancelled(true);
			e.setDamage(0);
		}
	}
	
	@EventHandler
	public void onExit(VehicleExitEvent e) {
		if(e.getExited() == player && !nocount) {
			if(ride == e.getVehicle()) {
				new RiderThrowHorse(player,plugin,kpg,ride,ride.getLocation(),!black);
				kpg.getPlayerData(player).cooldown(0, 10);
				ride = null;
				if(others.size() != 0) {
					for(LivingEntity other : others) {
						boolean blaaa = true;
						if(other instanceof Horse) {
							Horse horse = (Horse)other;
							if(horse.getColor() == Color.WHITE) {
								blaaa = false;
							}
						}
						new RiderThrowHorse(player,plugin,kpg,other,other.getLocation(),blaaa);
					}
					others.clear();
				}
			}
		}
	}
	
	@EventHandler
	public void onTarget(EntityTargetEvent e) {
		if(e.getEntity() == ride || others.contains(e.getEntity()) || skeletons.contains(e.getEntity())) {
			if(e.getTarget() == player) {
				e.setCancelled(true);
			}
		}
	}
	
	public boolean getNoCount() {
		return nocount;
	}
	
	public void setNoCount(boolean set) {
		nocount = set;
	}
	
	public void addOthers(LivingEntity other,int position) {
		others.add(other);
		positions.put(other, position);
	}
	
	public List<LivingEntity> getOthers(){
		return others;
	}
	public void clearOthers() {
		others.clear();
	}
	
	public void addSkeletons(LivingEntity skeleton) {
		skeletons.add(skeleton);
	}
	
	public void clearSkeletons() {
		skeletons.clear();
	}
}
