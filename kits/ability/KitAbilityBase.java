package kits.ability;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import game.KitPvPGame;
import kits.KitModel;
import maindatas.KitData;

public abstract class KitAbilityBase {
	
	protected Player player;
	protected KitPvPGame kpg;
	protected KitModel km;
	protected Plugin plugin;
	protected int kitnumber;
	protected int abilityindex;
	protected int cd;
	
	public KitAbilityBase(KitPvPGame kpg,Player player,int kitnumber,int abilityindex) {
		this.kpg = kpg;
		this.player = player;
		this.plugin = kpg.plugin;
		this.kitnumber = kitnumber;
		this.abilityindex = abilityindex;
		this.cd = KitData.kitweaponcool[kitnumber][abilityindex];
		km = kpg.getPlayerData(player);
	}
	
	protected abstract void doAbility();
	
	public Player getPlayer() {
		return player;
	}
	
	public KitPvPGame getKPG() {
		return kpg;
	}
}
