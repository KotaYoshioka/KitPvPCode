package kits;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import game.KitPvPGame;
import kitdatas.TimeData;
import maindatas.KitData;

public class KitTime extends KitModel{
	
	Deque<Object[]> deque = new ArrayDeque<>();
	
	HashMap<Entity,Location> elo = new HashMap<Entity,Location>();
	
	private boolean returnmode = false;
	
	private boolean timestop = false;
	
	private boolean strange = false;
	
	public KitTime(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 33);
		player.setExp(0.99f);
		regenerate();
		record();
		strange();
	}
	
	public void regenerate() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					if(timestop) {
						timeStart();
					}
					this.cancel();
					return;
				}
				if(!timestop) {
					healGauge(TimeData.gaugeHeal);
				}else {
					if(!payGauge(TimeData.timestopreduce)) {
						timeStart();
					}
				}
			}
		}.runTaskTimer(plugin, 0, TimeData.gaugeSpeed);
	}
	
	public void record() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(!returnmode) {
					Object[] d = {player.getLocation(),player.getHealth()};
					deque.push(d);
					if(deque.size() >= 100) {
						deque.removeLast();
					}
				}else {
					if(payGauge(TimeData.returnreduce)) {
						if(deque.size() >= 1) {
							Object[] o = deque.poll();
							player.teleport((Location)o[0]);
							player.setHealth((double)o[1]);
						}else {
							toggleReturn();
							player.sendMessage(ChatColor.RED + "もう過去がありません。");
						}
					}else {
						toggleReturn();
					}
				}
				
			}
		}.runTaskTimer(plugin, 0, TimeData.returnflex);
	}
	
	void strange() {
		new BukkitRunnable() {
			int counter = 0;
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				counter++;
				if(!strange) {
					counter = 0;
				}else {
					if(payGauge(TimeData.strangeReduce)) {
						for(Entity ent: player.getNearbyEntities(TimeData.strangeRange, TimeData.strangeRange, TimeData.strangeRange)) {
							if(kpg.getLivings().contains(ent)) {
								Player target = (Player)ent;
								int food = target.getFoodLevel() - 1;
								if(food < 0) {
									food = 0;
								}
								target.setFoodLevel(food);
							}
						}
						if(counter % 3 == 0) {
							int food = player.getFoodLevel() -1;
							if(food < 0) {
								food = 0;
							}
							player.setFoodLevel(food);
						}
					}else {
						toggleStrange();
					}
				}
			}
		}.runTaskTimer(plugin, 0, 10);
	}
	
	public void timeStop() {
		timestop = true;
		reScoreboard();
		for(Player p:kpg.getLivings()) {
			if(p != player) {
				kpg.getPlayerData(p).setStop(true);
				p.hidePlayer(plugin, player);
			}
		}
		for(Entity e:player.getWorld().getEntities()) {
			if(!(e instanceof Player)) {
				elo.put(e, e.getLocation());
			}
		}
		new BukkitRunnable() {
			public void run() {
				if(timestop) {
					for(Entity e:elo.keySet()) {
						e.teleport(elo.get(e));
					}
				}else {
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	public void timeStart() {
		timestop = false;
		reScoreboard();
		for(Player p:kpg.getLivings()) {
			if(p != player) {
				kpg.getPlayerData(p).setStop(false);
				p.showPlayer(plugin, player);
			}
		}
		kpg.getPlayerData(player).cooldown(1, KitData.kitweaponcool[getKitNumber()][1]);
	}
	
	@Override
	public Object[] exDamage(Player p,double damage,Player causer,String cause,boolean canHeal,boolean damagable) {
		if(causer == player) {
			Random rnd = new Random();
			for(int i = 0 ; i < 3 ; i++) {
				if(!kpg.getPlayerData(p).getCooldown(i)) {
					kpg.getPlayerData(p).setCooldowntime(i, kpg.getPlayerData(p).getCooldowntime(i) + 1);
				}else if(rnd.nextBoolean()) {
					kpg.getPlayerData(p).setCooldowntime(i, 1);
				}
			}
			if(strange) {
				kpg.getPlayerData(p).tempAddSpeed(-TimeData.strangeSlow, TimeData.strangeSlowLong);
			}
		}
		Object[] a = {p,damage,causer,cause,canHeal,damagable};
		return a;
	}
	
	public boolean getTimestop() {
		return timestop;
	}
	
	public void healGauge(float f) {
		float gauge = player.getExp();
		if(gauge + f >= 1) {
			gauge = 0.99f;
		}else {
			gauge = gauge + f;
		}
		player.setExp(gauge);
	}
	
	public boolean payGauge(float f) {
		float gauge = player.getExp();
		if(gauge - f < 0) {
			player.sendMessage(ChatColor.RED + "ゲージが足りません。");
			return false;
		}else {
			gauge = gauge - f;
			player.setExp(gauge);
			return true;
		}
	}
	
	public void toggleReturn() {
		returnmode = !returnmode;
		if(!returnmode) {
			kpg.getPlayerData(player).cooldown(2, KitData.kitweaponcool[getKitNumber()][2]);
		}
		reScoreboard();
	}
	
	public void toggleStrange() {
		strange = !strange;
		kpg.getPlayerData(player).cooldown(0, KitData.kitweaponcool[getKitNumber()][0]);
		if(!strange) {
			kpg.getPlayerData(player).addSpeed(-TimeData.strangeSpeed);
		}else {
			kpg.getPlayerData(player).addSpeed(TimeData.strangeSpeed);
		}
		reScoreboard();
	}

	@Override
	public void kitScoreboard(Objective o) {
		if(strange) {
			Score strangemode = o.getScore(ChatColor.LIGHT_PURPLE + "[変拍子]");
			strangemode.setScore(33);
		}
		if(timestop) {
			Score stopmode = o.getScore(ChatColor.RED + "[時間停止]");
			stopmode.setScore(32);
		}
		if(returnmode) {
			Score returnm = o.getScore(ChatColor.GREEN + "[撒き戻し]");
			returnm.setScore(31);
		}

	}

}
