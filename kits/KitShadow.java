package kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import game.KitPvPGame;
import kitdatas.ShadowData;
import kits.ability.shadow.ShadowDarkBody;
import net.md_5.bungee.api.ChatColor;

public class KitShadow extends KitModel{

	private List<ShadowDarkBody> darkmatters = new ArrayList<ShadowDarkBody>();
	
	public KitShadow(Plugin plugin, Player player, KitPvPGame kpg) {
		super(plugin, player, kpg, 35);
	}

	public void addDarkmatter(ShadowDarkBody darkmatter) {
		darkmatters.add(darkmatter);
	}
	public void removeDarkmatter(ShadowDarkBody darkmatter) {
		darkmatters.remove(darkmatter);
	}
	public List<ShadowDarkBody> getDarkMatters(){
		return darkmatters;
	}
	
	@Override
	public void onLeftClick() {
		for(ShadowDarkBody sdb: darkmatters) {
			for(Entity ent:sdb.getBody().getNearbyEntities(ShadowData.darkwavereach, ShadowData.darkwavereach, ShadowData.darkwavereach)) {
				boolean use = false;
				if(ent instanceof LivingEntity) {
					LivingEntity le = (LivingEntity)ent;
					if(le == player) {
						continue;
					}
					use = true;
					if(kpg.containsPlayers(le)) {
						Player p = (Player)le;
						kpg.getPlayerData(p).damage(ShadowData.darkwavedamage, player, "ダークウェーブ",true);
					}else {
						le.damage(ShadowData.darkwavedamage);
					}
				}
				if(use) {
					sdb.addDamage();
				}
			}
		}
	}

	
	@Override
	public void damage(double damage,Player causer,String cause,boolean canHeal) {
		super.damage(damage, causer, cause,canHeal);
		if(causer.hasPotionEffect(PotionEffectType.BLINDNESS)) {
			heal(1,player,"闇の吸収");
		}
	}

	@Override
	public void kitScoreboard(Objective o) {
		Score midare = o.getScore(ChatColor.GRAY + "ダークマター：" + (darkmatters==null?0:darkmatters.size()));
		midare.setScore(32);
	}

}
