package kits;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import game.KitPvPGame;
import net.md_5.bungee.api.ChatColor;

public class KitBlade extends KitModel{


	//大旋風直後
	private boolean nearDaisenpu = false;
	
	//精神統一
	private boolean spirit = false;

	//不屈
	private boolean hukutu = false;


	public KitBlade(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg,0);
		hukutu();
	}

	/**
	 * パッシブ「不屈」の常時効果
	 * 体力が10以下ならhukutuがtrueに、それ以外の場合はhukutuがfalseになる。
	 */
	public void hukutu() {
		new BukkitRunnable() {
			public void run() {
				if(!getLive()) {
					this.cancel();
					return;
				}
				if(player.getHealth() <= 10) {
					if(!hukutu) {
						hukutu = true;
						reScoreboard();
					}
				}else {
					if(hukutu) {
						hukutu = false;
						reScoreboard();
					}
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}

	@Override
	public void kitScoreboard(Objective o) {
		if(spirit) {
			Score sp = o.getScore(ChatColor.RED + "[精神統一]");
			sp.setScore(32);
		}
		if(hukutu) {
			Score h = o.getScore(ChatColor.GRAY + "[不屈]");
			h.setScore(31);
		}
	}
	
	//GetterとSetter
	public boolean getHukutu() {
		return hukutu;
	}
	
	public void nearlyDaisenpu(int delayTicks) {
		nearDaisenpu = true;
		new BukkitRunnable() {
			public void run() {
				nearDaisenpu = false;
			}
		}.runTaskLater(plugin, delayTicks);
	}
	
	public boolean getDaisenpu() {
		return nearDaisenpu;
	}
	
	public boolean getSpirit() {
		return spirit;
	}
	public void setSpirit(boolean newspirit) {
		spirit = newspirit;
		reScoreboard();
	}
	public void cancelSpirit() {
		if(getSpirit()) {
			setSpirit(false);
			player.removePotionEffect(PotionEffectType.GLOWING);
			player.removePotionEffect(PotionEffectType.SLOW_FALLING);
		}
	}
	
	/**
	 * 精神統一中であった場合、被ダメージが2倍になる処理
	 */
	@Override
	public Object[] exDamage(Player p,double damage,Player causer,String cause,boolean canHeal,boolean damagable) {
		if(p == player && spirit) {
			damage = damage * 2;
			cancelSpirit();
		}
		Object[] a = {p,damage,causer,cause,canHeal,damagable};
		return a;
	}

}
