package kits.ability.heiz;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kitdatas.HeizData;
import kits.ability.KitAbilityBase;

public class HeizUnsan extends KitAbilityBase{

	public HeizUnsan(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		for(Player p:kpg.getPlayers()) {
			p.hidePlayer(plugin,player);
		}
		player.getWorld().spawnParticle(Particle.CLOUD,player.getLocation(),40,0.5,0.5,0.5,0.5);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,(int)(HeizData.unsanSeconds*20),1));
		new BukkitRunnable() {
			public void run() {
				if(kpg.getPlayerData(player).getLive()) {
					for(Player p:kpg.getPlayers()) {
						p.showPlayer(plugin,player);
					}
				}
			}
		}.runTaskLater(plugin,(int)(HeizData.unsanSeconds*20));
	}
}
