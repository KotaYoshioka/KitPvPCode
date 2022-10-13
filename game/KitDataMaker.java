package game;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import kits.KitBlade;
import kits.KitEnder;
import kits.KitGambler;
import kits.KitGeek;
import kits.KitGradius;
import kits.KitHarden;
import kits.KitHeiz;
import kits.KitModel;
import kits.KitMonster;
import kits.KitPillow;
import kits.KitPsycho;
import kits.KitRider;
import kits.KitSand;
import kits.KitScape;
import kits.KitShadow;
import kits.KitSniper;
import kits.KitSpacia;
import kits.KitSparkle;
import kits.KitStone;
import kits.KitSummon;
import kits.KitSupernova;
import kits.KitTime;
import kits.KitVampire;
import kits.KitZeus;

public class KitDataMaker {

	KitModel data;
	
	public KitDataMaker(Plugin plugin,KitPvPGame game,Player player,int kitid) {
		setData(plugin,game,player,kitid);
	}
	
	public void setData(Plugin plugin,KitPvPGame game,Player player,int kitid) {
		switch(kitid) {
		case 0:
			data = new KitBlade(plugin,player,game);
			break;
		case 1:
			data = new KitGeek(plugin,player,game);
			break;
		case 2:
			data = new KitHarden(plugin,player,game);
			break;
		case 3:
			data = new KitStone(plugin,player,game);
			break;
		case 5:
			data = new KitEnder(plugin,player,game);
			break;
		case 6:
			data = new KitVampire(plugin,player,game);
			break;
		case 8:
			data =  new KitRider(plugin,player,game,kitid);
			break;
		case 9:
			data = new KitGradius(plugin,player,game);
			break;
		case 11:
			data = new KitSparkle(plugin,player,game,kitid);
			break;
		case 12:
			data = new KitSpacia(plugin,player,game);
			break;
		case 13:
			data = new KitSniper(plugin,player,game);
			break;
		case 15:
			data = new KitSupernova(plugin,player,game);
			break;
		case 17:
			data = new KitMonster(plugin,player,game);
			break;
		case 20:
			data = new KitGambler(plugin,player,game);
			break;
		case 21:
			data = new KitSummon(plugin,player,game);
			break;
		case 23:
			data = new KitZeus(plugin,player,game);
			break;
		case 25:
			data = new KitPillow(plugin,player,game);
			break;
		case 28:
			data = new KitPsycho(plugin,player,game);
			break;
		case 33:
			data = new KitTime(plugin,player,game);
			break;
		case 35:
			data = new KitShadow(plugin,player,game);
			break;
		case 36:
			data = new KitHeiz(plugin,player,game,kitid);
			break;
		case 38:
			data = new KitSand(plugin,player,game);
			break;
		case 42:
			data = new KitScape(plugin,player,game);
			break;
		}
	}
	
	public KitModel getData() {
		return data;
	}
}
