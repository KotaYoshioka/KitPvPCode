package kits;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kitdatas.HardenData;
import maindatas.KitData;

public class KitHarden extends KitModel{

	double armor;
	int damagecounter = 0;
	
	boolean dash = false;
	int upspeed = 0;
	int dashcounter = 0;
	
	boolean earthcrash = false;
	boolean falsebreak = false;
	
	public KitHarden(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 2);
		armor = player.getAttribute(Attribute.GENERIC_ARMOR).getBaseValue();
		tackle();
	}
	
	void tackle() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(dash) {
					if(player.getInventory().getItemInMainHand().getType() != Material.SHIELD || !player.isSprinting()) {
						kpg.getPlayerData(player).setMuteki(false);
						kpg.getPlayerData(player).addSpeed(-upspeed);
						dash = false;
						kpg.getPlayerData(player).cooldown(0, KitData.kitweaponcool[2][0]);
					}else {
						dashcounter++;
						if(dashcounter == 10) {
							upspeed += 2;
							kpg.getPlayerData(player).addSpeed(2);
						}else if(dashcounter == 20) {
							upspeed += 2;
							kpg.getPlayerData(player).addSpeed(2);
						}else if(dashcounter == 30) {
							upspeed += 2;
							kpg.getPlayerData(player).addSpeed(2);
						}else if(dashcounter >= 40) {
							kpg.getPlayerData(player).addSpeed(-upspeed);
							dash = false;
							kpg.getPlayerData(player).setMuteki(false);
							kpg.getPlayerData(player).cooldown(0, KitData.kitweaponcool[2][0]);
							kpg.getPlayerData(player).tempStan(160);
							kpg.getPlayerData(player).damage(HardenData.impaSelfDamage, player, "走りすぎ", false);
							return;
						}
						for(Entity ent:player.getNearbyEntities(1.7, 1.7, 1.7)) {
							int spedame = (speed/2) - 1;
							if(ent instanceof LivingEntity) {
								LivingEntity le = (LivingEntity)ent;
								if(kpg.containsLivings(le)) {
									Player target = (Player)le;
									kpg.getPlayerData(target).damage(spedame, player, "インパルサ―", true);
								}else {
									le.damage(spedame);
								}
								Vector v = player.getLocation().getDirection().normalize().multiply(2).clone();
								v.setY(1);
								le.setVelocity(v);
							}
						}
					}
				}
				if(player.getInventory().getItemInMainHand().getType() == Material.SHIELD && !dash && player.isSprinting() && kpg.getPlayerData(player).getCooldown(0)) {
					dashcounter = 0;
					dash = true;
					kpg.getPlayerData(player).setMuteki(true);
					kpg.getPlayerData(player).addSpeed(4);
					upspeed = 4;
				}
			}
		}.runTaskTimer(plugin, 0, 3);
	}
	
	@Override
	public Object[] exDamage(Player p,double damage,Player causer,String cause,boolean canHeal,boolean damagable) {
		if(p == player) {
			damagecounter += damage;
			if(damagecounter >= 3) {
				damagecounter = 0;
				harden();
			}
		}
		Object[] a = {p,damage,causer,cause,canHeal,damagable};
		return a;
	}
	
	@Override
	public void kitScoreboard(Objective o) {
		if(earthcrash) {
			Score earth = o.getScore(ChatColor.AQUA + "[アースクラッシュ]");
			earth.setScore(29);
		}
		if(falsebreak) {
			Score falseb = o.getScore(ChatColor.YELLOW + "[フォールスブレイク]");
			falseb.setScore(28);
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
	  boolean isJumping = player.getVelocity().getY() > -0.0784000015258789;
	  if(event.getPlayer() == player && isJumping) {
		 if(earthcrash && getCooldown(1)) {
			kpg.getPlayerData(player).cooldown(1, KitData.kitweaponcool[getKitNumber()][1]);
			Vector v = player.getLocation().getDirection().normalize();
			v.setY(1);
			player.setVelocity(v.multiply(2));
			crash();
		}
	  }
	}
	
	void crash() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				Vector v = player.getVelocity();
				Location l = player.getLocation().clone();
				l.add(0,-0.3,0);
				if(v.getY() <= 0.01 && l.getBlock().getType() != Material.AIR) {
					Block block = l.getBlock();
					player.getWorld().spawnParticle(Particle.BLOCK_CRACK,player.getLocation(),400,3,0.5,3,1,block.getType().createBlockData());
					for(Entity ent:player.getNearbyEntities(HardenData.crashRange,2,HardenData.crashRange)) {
						if(ent instanceof LivingEntity && !(ent instanceof ArmorStand)) {
							LivingEntity le = (LivingEntity)ent;
							if(kpg.containsLivings(le)) {
								Player target = (Player)le;
								kpg.getPlayerData(target).damage((armor/2)+1,player,"アースクラッシュ",true);
								kpg.getPlayerData(target).tempStan(((int)armor) * HardenData.crashStan);
							}else {
								le.damage((armor/2)+1);
							}
						}
					}
					this.cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	@Override
	boolean checkSilence() {
		if(falsebreak && getCooldown(2) && armor > 5) {
			setArmor(armor/2);
			return false;
		}else {
			return true;
		}
	}
	
	@Override
	boolean checkStan() {
		if(falsebreak && getCooldown(2) && armor > 5) {
			setArmor(armor/2);
			return false;
		}else {
			return true;
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		if(player == e.getPlayer()) {
			if(e.getItemDrop().getItemStack().getType() == Material.SHIELD) {
				e.getItemDrop().remove();
				//player.getInventory().setItem(0, new ItemStack(Material.AIR));
				ArmorStand shield = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
				shield.getEquipment().setItemInMainHand(new ItemStack(Material.SHIELD));
				shield.setVisible(false);
				Vector v = player.getLocation().getDirection().clone().normalize();
				shield.setVelocity(v.multiply(3));
				Set<LivingEntity> targets = new HashSet<LivingEntity>();
				BukkitTask bt = new BukkitRunnable() {
					public void run() {
						if(!live) {
							this.cancel();
							shield.remove();
							return;
						}
						for(Entity ent:shield.getNearbyEntities(2,2,2)) {
							if(ent instanceof LivingEntity && !(ent instanceof ArmorStand) && ent != player) {
								LivingEntity le = (LivingEntity)ent;
								if(!targets.contains(le)) {
									targets.add(le);
									if(kpg.containsLivings(le)) {
										Player target = (Player)le;
										kpg.getPlayerData(target).damage(HardenData.throwDamage, player, "シールドブーメラン", true);
										target.setVelocity(v);
									}
								}
							}
						}
					}
				}.runTaskTimer(plugin, 0, 2);
				new BukkitRunnable() {
					public void run() {
						bt.cancel();
						targets.clear();
						new BukkitRunnable() {
							public void run() {
								if(!live) {
									this.cancel();
									shield.remove();
									return;
								}
								Location sl = shield.getLocation().clone();
								Location pl = player.getLocation();
								Vector v = new Vector(pl.getX() - sl.getX(),pl.getY() - sl.getY(),pl.getZ() - sl.getZ()).normalize();
								sl.add(v);
								shield.teleport(sl);
								for(Entity ent:shield.getNearbyEntities(2,2,2)) {
									if(ent instanceof LivingEntity && !(ent instanceof ArmorStand) && ent != player) {
										LivingEntity le = (LivingEntity)ent;
										if(!targets.contains(le)) {
											targets.add(le);
											if(kpg.containsLivings(le)) {
												Player target = (Player)le;
												kpg.getPlayerData(target).damage(HardenData.throwDamage, player, "シールドブーメラン", true);
											}
										}
									}else if(ent == player) {
										shield.remove();
										player.getInventory().setItem(0, KitData.getWeapon(2, 0, 0, false));
										this.cancel();
										return;
									}
								}
							}
						}.runTaskTimer(plugin, 0, 2);
					}
				}.runTaskLater(plugin, 100);
			}
		}
	}
	
	void harden() {
		if(armor < 20) {
			armor += 1;
			if(armor >= 20) {
				armor = 20;
			}
			player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armor);
		}
	}
	
	public void setArmor(double newarmor) {
		armor = newarmor;
	}
	
	boolean reduceLevel(int level) {
		int nl = player.getLevel() - level;
		if(nl < 0) {
			return false;
		}else {
			player.setLevel(nl);
			return true;
		}
	}
	
	public void toggleEarth() {
		earthcrash = !earthcrash;
		reScoreboard();
	}
	public void toggleFalse() {
		falsebreak = !falsebreak;
		reScoreboard();
	}
}
