package kits;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import game.KitPvPGame;
import maindatas.KitData;

public class KitGambler extends KitModel{

	int coin = 40;
	
	int[] gacha = {-1,-1};
	int[] ggacha = {-1,-1};
	
	int challenge = -1;
	int bedded = -1;
	
	public KitGambler(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 20);
		reScoreboard();
	}

	@Override
	public void kitScoreboard(Objective o) {
		Score coinscore = o.getScore(ChatColor.GOLD + "コイン：" + coin);
		coinscore.setScore(29);
	}
	
	@Override
	public Object[] exDamage(Player p,double damage,Player causer,String cause,boolean canHeal,boolean damagable) {
		Object[] a = {p,damage,causer,cause,canHeal,damagable};
		if(causer == player && challenge == 0) {
			player.sendMessage(ChatColor.YELLOW + "成功");
			addCoin(bedded);
			challenge = -1;
		}else if(p == player && challenge == 1) {
			player.sendMessage(ChatColor.RED + "失敗");
			reduceCoin(bedded);
			challenge = -1;
		}else if(p == player && challenge == 2) {
			player.sendMessage(ChatColor.YELLOW + "成功");
			addCoin(bedded);
			challenge = -1;
		}
		Random rnd = new Random();
		if(rnd.nextInt(8) == 0 && coin < 50) {
			addCoin(1);
		}
		return a;
	}
	
	void success() {
		
	}
	
	public int getCoin() {
		return coin;
	}
	
	public void addCoin(int newCoin) {
		int newcoin = coin + newCoin;
		setCoin(newcoin);
	}
	
	public void setCoin(int newCoin) {
		coin = newCoin;
		if(coin <= 0) {
			death();
		}else {
			player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(coin);
		}
		reScoreboard();
	}
	
	public boolean reduceCoin(int reduce) {
		int newcoin = coin - reduce;
		if(newcoin < 0) {
			return false;
		}else {
			setCoin(newcoin);
			return true;
		}
	}
	
	public int[] getGacha() {
		return gacha;
	}
	public void setGacha(int kitindex,int abilityindex) {
		gacha[0] = kitindex;
		gacha[1] = abilityindex;
		if(kitindex == -1) {
			player.getInventory().setItem(0, KitData.getWeapon(20, 0, 0, false));
		}else {
			player.getInventory().setItem(0, KitData.getWeapon(kitindex, abilityindex, 0, false));
		}
		reScoreboard();
	}
	
	public int[] getGGacha() {
		return ggacha;
	}
	public void setGGacha(int kitindex,int abilityindex) {
		ggacha[0] = kitindex;
		ggacha[1] = abilityindex;
		if(kitindex == -1) {
			player.getInventory().setItem(1, KitData.getWeapon(20, 1, 0, false));
		}else {
			player.getInventory().setItem(1, KitData.getWeapon(kitindex, abilityindex, 0, false));
		}
		reScoreboard();
	}
	
	public void setBed(int bedid,int bedded) {
		challenge = bedid;
		this.bedded = bedded;
		switch(bedid) {
		case 0:
			new BukkitRunnable() {
				public void run() {
					if(challenge == 0) {
						player.sendMessage(ChatColor.RED + "失敗");
						challenge = -1;
						reduceCoin(bedded);
					}
				}
			}.runTaskLater(plugin, 160);
			break;
		case 1:
			new BukkitRunnable() {
				public void run() {
					if(challenge == 1) {
						player.sendMessage(ChatColor.YELLOW + "成功");
						challenge = -1;
						addCoin(bedded);
					}
				}
			}.runTaskLater(plugin, 300);
			break;
		case 2:
			new BukkitRunnable() {
				public void run() {
					if(challenge == 2) {
						player.sendMessage(ChatColor.RED + "失敗");
						challenge = -1;
						reduceCoin(bedded);
					}
				}
			}.runTaskLater(plugin, 140);
			break;
		}
	}

}
