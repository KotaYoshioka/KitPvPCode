package kits.ability.gambler;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kitdatas.GamblerData;
import kits.KitGambler;
import kits.ability.KitAbilityBase;

public class GamblerBet extends KitAbilityBase{

	KitGambler gambler;

	
	public GamblerBet(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		gambler = (KitGambler)kpg.getPlayerData(player);
		doAbility();
	}

	@Override
	protected void doAbility() {
		if(gambler.getCoin() > 1 && gambler.getCoin() <= GamblerData.maxCoin) {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
			int beded = gambler.getCoin()/2;
			Random rnd = new Random();
			int cid = rnd.nextInt(GamblerData.betContents.length);
			player.sendMessage(ChatColor.RED + GamblerData.betContents[cid] + "   " + ChatColor.WHITE + "" + GamblerData.betTimes[cid] + "秒");
			for(Player p:kpg.getPlayers()) {
				p.sendMessage(ChatColor.RED + player.getDisplayName() + "が賭けを行いました。");
			}
			gambler.setBed(cid, beded);
		}
	}

}
