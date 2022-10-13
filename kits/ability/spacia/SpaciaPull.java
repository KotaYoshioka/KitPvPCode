package kits.ability.spacia;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kits.ability.KitAbilityBase;

public class SpaciaPull extends KitAbilityBase{

	boolean ignoreCooldown;
	
	public SpaciaPull(KitPvPGame kpg, Player player, int kitnumber, int abilityindex,boolean ignoreCooldown) {
		super(kpg, player, kitnumber, abilityindex);
		this.ignoreCooldown = ignoreCooldown;
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(!ignoreCooldown) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
		}
		for(Player p:kpg.getLivings()) {
			if(p != player) {
				if(!((Entity)p).isOnGround()) {
					int height = 0;
					Location l = p.getLocation().clone();
					for(int i = 0 ; i < 200 ; i++) {
						l.add(0,-1,0);
						if(l.getBlock().getType() == Material.AIR) {
							height++;
						}else {
							break;
						}
					}
					if(height <= 190) {
						int damage = height / 7;
						if(p.hasPotionEffect(PotionEffectType.LEVITATION)) {
							p.removePotionEffect(PotionEffectType.LEVITATION);
						}
						new BukkitRunnable() {
							public void run() {
								if(((Entity)p).isOnGround()) {
									this.cancel();
									kpg.getPlayerData(p).damage(damage, player, "超引力", true);
									return;
								}
								p.setVelocity(new Vector(0,-10,0));
							}
						}.runTaskTimer(plugin, 0, 3);
					}
				}
			}else {
				if(player.hasPotionEffect(PotionEffectType.LEVITATION)) {
					player.removePotionEffect(PotionEffectType.LEVITATION);
				}
				new BukkitRunnable() {
					public void run() {
						if(!kpg.getPlayerData(p).getLive()) {
							this.cancel();
							return;
						}
						if(((Entity)p).isOnGround()) {
							this.cancel();
							return;
						}
						p.setVelocity(new Vector(0,-10,0));	
					}
				}.runTaskTimer(plugin, 0, 3);
			}
		}
	}

}
