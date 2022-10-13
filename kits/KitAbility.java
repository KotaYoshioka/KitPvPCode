package kits;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import game.KitPvPGame;
import kits.ability.blade.BladeDaisenpu;
import kits.ability.blade.BladeSpirit;
import kits.ability.blade.BladeZangeki;
import kits.ability.ender.EnderReturn;
import kits.ability.ender.EnderSphere;
import kits.ability.ender.EnderSwap;
import kits.ability.ender.EnderTeleport;
import kits.ability.gambler.GamblerBet;
import kits.ability.gambler.GamblerGGacha;
import kits.ability.gambler.GamblerGacha;
import kits.ability.geek.GeekChange;
import kits.ability.geek.GeekCraft;
import kits.ability.geek.GeekPortable;
import kits.ability.gradius.GradiusHagoromo;
import kits.ability.gradius.GradiusMidarezakura;
import kits.ability.gradius.GradiusToriame;
import kits.ability.harden.HardenCrush;
import kits.ability.harden.HardenFalse;
import kits.ability.heiz.HeizKasumi;
import kits.ability.heiz.HeizKirihebi;
import kits.ability.heiz.HeizUnsan;
import kits.ability.monster.MonsterLaser;
import kits.ability.monster.MonsterMeteo;
import kits.ability.pillow.PillowSleep;
import kits.ability.pillow.PillowThrow;
import kits.ability.pillow.PillowYawn;
import kits.ability.psycho.PsychoCritical;
import kits.ability.psycho.PsychoLevitation;
import kits.ability.psycho.PsychoNomad;
import kits.ability.rider.RiderBlackWhite;
import kits.ability.rider.RiderMultiply;
import kits.ability.rider.RiderRaveger;
import kits.ability.sand.SandKutitakaze;
import kits.ability.sand.SandRing;
import kits.ability.sand.SandZangeki;
import kits.ability.scape.ScapeArcana;
import kits.ability.scape.ScapeLock;
import kits.ability.scape.ScapeSoul;
import kits.ability.shadow.ShadowDarkmatter;
import kits.ability.shadow.ShadowGate;
import kits.ability.shadow.ShadowInqure;
import kits.ability.sniper.GunmanShot;
import kits.ability.spacia.SpaciaBlack;
import kits.ability.spacia.SpaciaGravity;
import kits.ability.spacia.SpaciaPull;
import kits.ability.sparkle.SparkleChase;
import kits.ability.sparkle.SparklePut;
import kits.ability.sparkle.SparkleSmall;
import kits.ability.stone.StoneMountain;
import kits.ability.stone.StonePointed;
import kits.ability.stone.StoneThrow;
import kits.ability.summon.SummonMiddle;
import kits.ability.summon.SummonUnder;
import kits.ability.summon.SummonUpper;
import kits.ability.supernova.SuperExplosion;
import kits.ability.supernova.SuperSonic;
import kits.ability.supernova.SuperSpeed;
import kits.ability.time.TimeReturn;
import kits.ability.time.TimeStop;
import kits.ability.time.TimeStrange;
import kits.ability.vampire.VampireBlood;
import kits.ability.vampire.VampireCreep;
import kits.ability.vampire.VampireOkite;
import kits.ability.zeus.ZeusDonrai;
import kits.ability.zeus.ZeusRakurai;
import kits.ability.zeus.ZeusSinrai;

public class KitAbility {

	KitPvPGame kpg;
	Plugin mainPlugin;

	public KitAbility(KitPvPGame kpg,Plugin plugin) {
		this.kpg = kpg;
		mainPlugin = plugin;
	}

	public void doAbility(int kitnumber,int index,Player master) {
		doAbility(kitnumber,index,master,null,false);
	}
	public void doAbility(int kitnumber,int index,Player master,boolean ignoreCool) {
		doAbility(kitnumber,index,master,null,ignoreCool);
	}
	public void doAbility(int kitnumber,int index,Player master,Entity target) {
		doAbility(kitnumber,index,master,target,false);
	}
	public void doAbility(int kitnumber,int index,Player master,Entity target,boolean ignoreCool) {
		//ターゲットがいるかどうか
		//boolean isTarget = target != null;
		//プレイヤーデータ
		KitModel km = kpg.getPlayerData(master);
		//クールダウンが完了しているかどうか
		boolean cooldown = km.getCooldown(index);
		//無視してよい場合、無視する
		if(ignoreCool) {
			cooldown = true;
		}
		//技の発動
		if(cooldown) {
			switch(kitnumber) {
			//ブレード
			case 0:
				switch(index) {
				case 0:
					new BladeZangeki(kpg,master,kitnumber,index,ignoreCool);
					break;
				case 1:
					new BladeDaisenpu(kpg,master,kitnumber,index,ignoreCool);
					break;
				case 2:
					new BladeSpirit(kpg,master,kitnumber,index);
					break;
				}
				break;
			//ギーク
			case 1:
				switch(index) {
				case 0:
					new GeekCraft(kpg,master,kitnumber,index);
					break;
				case 1:
					new GeekPortable(kpg,master,kitnumber,index,true);
					break;
				case 2:
					new GeekChange(kpg,master,kitnumber,index);
					break;
				}
				break;
			//ハーデン
			case 2:
				switch(index) {
				case 1:
					new HardenCrush(kpg,master,kitnumber,index);
					break;
				case 2:
					new HardenFalse(kpg,master,kitnumber,index);
					break;
				}
				break;
			//ストーン
			case 3:
				switch(index) {
				case 0:
					new StoneMountain(kpg,master,kitnumber,index,ignoreCool);
					break;
				case 1:
					new StonePointed(kpg,master,kitnumber,index,ignoreCool);
					break;
				case 2:
					new StoneThrow(kpg,master,kitnumber,index,ignoreCool,false);
					break;
				}
				break;
			//エンダー
			case 5:
				switch(index) {
				case 0:
					new EnderReturn(kpg,master,kitnumber,index,ignoreCool,true);
					break;
				case 1:
					new EnderTeleport(kpg,master,kitnumber,index,ignoreCool,true);
					break;
				case 2:
					new EnderSwap(kpg,master,kitnumber,index,ignoreCool);
					break;
				}
				break;
			//ヴァンパイア
			case 6:
				switch(index) {
				case 0:
					new VampireOkite(kpg,master,kitnumber,index);
					break;
				case 1:
					new VampireBlood(kpg,master,kitnumber,index);
					break;
				case 2:
					new VampireCreep(kpg,master,kitnumber,index);
					break;
				}
				break;
			//ライダー
			case 8:
				switch(index) {
				case 0:
					new RiderBlackWhite(kpg,master,kitnumber,index);
					break;
				case 1:
					new RiderRaveger(kpg,master,kitnumber,index);
					break;
				case 2:
					new RiderMultiply(kpg,master,kitnumber,index);
					break;
				}
				break;
			//グラディウス
			case 9:
				switch(index) {
				case 0:
					new GradiusMidarezakura(kpg,master,kitnumber,index,ignoreCool);
					break;
				case 1:
					new GradiusHagoromo(kpg,master,kitnumber,index);
					break;
				case 2:
					new GradiusToriame(kpg,master,kitnumber,index,ignoreCool);
					break;
				}
				break;
			//スパークル
			case 11:
				switch(index) {
				case 0:
					new SparkleSmall(kpg,master,kitnumber,index);
					break;
				case 1:
					new SparkleChase(kpg,master,kitnumber,index);
					break;
				case 2:
					new SparklePut(kpg,master,kitnumber,index,true);
					break;
				}
				break;
			//スペーシア
			case 12:
				switch(index) {
				case 0:
					new SpaciaGravity(kpg,master,kitnumber,index);
					break;
				case 1:
					new SpaciaPull(kpg,master,kitnumber,index,ignoreCool);
					break;
				case 2:
					new SpaciaBlack(kpg,master,kitnumber,index,ignoreCool);
					break;
				}
				break;
			//ガンマン
			case 13:
				switch(index) {
				case 0:
					new GunmanShot(kpg,master,kitnumber,index);
					break;
				case 1:
					new GunmanShot(kpg,master,kitnumber,index);
					break;
				case 2:
					new GunmanShot(kpg,master,kitnumber,index);
					break;
				}
				break;
			//スーパーノヴァ
			case 15:
				switch(index) {
				case 0:
					new SuperSonic(kpg,master,kitnumber,index);
					break;
				case 1:
					new SuperSpeed(kpg,master,kitnumber,index,ignoreCool);
					break;
				case 2:
					new SuperExplosion(kpg,master,kitnumber,index,ignoreCool);
					break;
				}
				break;
			//モンスター
			case 17:
				switch(index) {
				case 0:
					new MonsterMeteo(kpg,master,kitnumber,index);
					break;
				case 1:
					new MonsterLaser(kpg,master,kitnumber,index);
					break;
				}
				break;
			//ギャンブラー
			case 20:
				switch(index) {
				case 0:
					new GamblerGacha(kpg,master,kitnumber,index);
					break;
				case 1:
					new GamblerGGacha(kpg,master,kitnumber,index);
					break;
				case 2:
					new GamblerBet(kpg,master,kitnumber,index);
					break;
				}
				break;
			//サモンコマンダー
			case 21:
				switch(index) {
				case 0:
					new SummonUnder(kpg,master,kitnumber,index,ignoreCool);
					break;
				case 1:
					new SummonMiddle(kpg,master,kitnumber,index,ignoreCool);
					break;
				case 2:
					new SummonUpper(kpg,master,kitnumber,index,ignoreCool);
					break;
				}
				break;
			//ゼウス
			case 23:
				switch(index) {
				case 0:
					new ZeusRakurai(kpg,master,kitnumber,index,ignoreCool);
					break;
				case 1:
					new ZeusDonrai(kpg,master,kitnumber,index);
					break;
				case 2:
					new ZeusSinrai(kpg,master,kitnumber,index);
					break;
				}
				break;
			//ピロー
			case 25:
				switch(index) {
				case 0:
					new PillowSleep(kpg,master,kitnumber,index,false);
					break;
				case 1:
					new PillowThrow(kpg,master,kitnumber,index);
					break;
				case 2:
					new PillowYawn(kpg,master,kitnumber,index);
					break;
				}
				break;
			//サイコ
			case 28:
				switch(index) {
				case 0:
					new PsychoLevitation(kpg,master,kitnumber,index);
					break;
				case 1:
					new PsychoNomad(kpg,master,kitnumber,index);
					break;
				case 2:
					new PsychoCritical(kpg,master,kitnumber,index);
					break;
				}
				break;
			//タイムハッカー
			case 33:
				switch(index) {
				case 0:
					new TimeStrange(kpg,master,kitnumber,index);
					break;
				case 1:
					new TimeStop(kpg,master,kitnumber,index);
					break;
				case 2:
					new TimeReturn(kpg,master,kitnumber,index);
					break;
				}
				break;
			//シャドウ
			case 35:
				switch(index) {
				case 0:
					new ShadowDarkmatter(kpg,master,kitnumber,index);
					break;
				case 1:
					new ShadowInqure(kpg,master,kitnumber,index);
					break;
				case 2:
					new ShadowGate(kpg,master,kitnumber,index);
					break;
				}
				break;
			//ヘイズ
			case 36:
				switch(index) {
				case 0:
					new HeizUnsan(kpg,master,kitnumber,index);
					break;
				case 1:
					new HeizKirihebi(kpg,master,kitnumber,index);
					break;
				case 2:
					new HeizKasumi(kpg,master,kitnumber,index);
					break;
				}
				break;
			//砂
			case 38:
				switch(index) {
				case 0:
					new SandZangeki(kpg,master,kitnumber,index,false);
					break;
				case 1:
					new SandKutitakaze(kpg,master,kitnumber,index);
					break;
				case 2:
					new SandRing(kpg,master,kitnumber,index);
					break;
				}
				break;
			//スケープ
			case 42:
				switch(index) {
				case 0:
					new ScapeSoul(kpg,master,kitnumber,index,ignoreCool);
					break;
				case 1:
					new ScapeLock(kpg,master,kitnumber,index,ignoreCool);
					break;
				case 2:
					new ScapeArcana(kpg,master,kitnumber,index,ignoreCool);
					break;
				}
				break;
			}
		}else {
			//クールダウン末完了時の処理
			IncompleteDo(kitnumber,index,master,target);
		}
	}
	
	public void IncompleteDo(int kitnumber,int index,Player master,Entity target) {
		//技の発動分岐
		switch(kitnumber) {
		//ハーデン
		case 2:
			switch(index) {
			case 1:
				new HardenCrush(kpg,master,kitnumber,index);
				break;
			case 2:
				new HardenFalse(kpg,master,kitnumber,index);
				break;
			}
			break;
		//ストーン
		case 3:
			switch(index) {
			case 2:
				new StoneThrow(kpg,master,kitnumber,index,true,true);
				break;
			}
			break;
		//エンダー
		case 5:
			switch(index) {
			//テレポート
			case 0:
				if(kpg.getPlayerData(master) instanceof KitEnder) {
					new EnderTeleport(kpg,master,kitnumber,index,true,false);
				}
				break;
			case 1:
				if(kpg.getPlayerData(master) instanceof KitEnder) {
					new EnderReturn(kpg,master,kitnumber,index,true,false);
				}
				break;
			case 2:
				if(kpg.getPlayerData(master) instanceof KitEnder) {
					new EnderSphere(kpg,master,kitnumber,index,true,false);
				}
				break;
			}
			break;
		}
	}
	
	public void LeftClickDo(int kitnumber,int index,Player master) {
		switch(kitnumber) {
		//爆弾の
		case 11:
			switch(index) {
			case 2:
				new SparklePut(kpg,master,kitnumber,index,false);
				break;
			}
			break;
		}
	}

}
