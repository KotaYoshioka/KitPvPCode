package kits.ability.gambler;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import game.KitPvPGame;
import kitdatas.GamblerData;
import kits.KitGambler;
import kits.ability.KitAbilityBase;
import maindatas.KitData;

public class GamblerGacha extends KitAbilityBase{

	KitGambler gambler;

	public GamblerGacha(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		gambler = (KitGambler)kpg.getPlayerData(player);
		if(gambler.getGacha()[0] == -1) {
			doAbility();
		}else {
			kpg.getPlayerData(player).cooldown(abilityindex, cd);
			kpg.ka.doAbility(gambler.getGacha()[0], gambler.getGacha()[1], player,true);
			gambler.setGacha(-1, -1);
		}
	}

	@Override
	protected void doAbility() {
		if(gambler.reduceCoin(GamblerData.gachaCost)) {
			kpg.getPlayerData(player).cooldown(abilityindex, 1);
			int kitid = -1;
			int index = -1;
			Random rnd = new Random();
			for(int i = 0 ; i < 1000 ; i++) {
				int kkitid = rnd.nextInt(KitData.kitdis.length);
				if(KitData.kitdis[kkitid][0][0] != "") {
					int kindex = rnd.nextInt(3);
					if(KitData.copiable[kkitid][kindex]) {
						kitid = kkitid;
						index = kindex;
						break;
					}
				}
			}
			if(kitid != -1) {
				gambler.setGacha(kitid, index);
			}
		}else {
			player.sendMessage(ChatColor.RED + "コインが足りません。");
		}
	}

}
