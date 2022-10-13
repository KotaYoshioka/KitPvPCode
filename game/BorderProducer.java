package game;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import maindatas.StageData;

public class BorderProducer extends GameProducer{

	private World world;
	private int stageID;
	
	//時間制限用ボスバー
	private BossBar bb;
	
	public BorderProducer(Plugin plugin,KitPvPGame game,World world,int stageID) {
		super(plugin,game);
		world.getWorldBorder().setCenter(StageData.stageCenter[stageID].toLocation(world));
		world.getWorldBorder().setSize(StageData.smallLevel[stageID][0]);
		timeBar(0);
	}
	
	private void timeBar(int phase) {
		if(phase != 0) {
			world.getWorldBorder().setSize(StageData.smallLevel[stageID][phase],20);
		}
		int maxtime = 240;
		for(int i = 0; i < phase ; i++) {
			maxtime /= 2;
		}
		bb = Bukkit.createBossBar("収縮まで：" + maxtime + "秒", BarColor.GREEN, BarStyle.SOLID);
		bb.setProgress(1);
		bb.setVisible(true);
		for(Player p: game.getPlayers()) {
			bb.addPlayer(p);
		}
		final int fmaxtime = maxtime;
		new BukkitRunnable() {
			int count = 0;
			public void run() {
				if(!nowgame) {
					bb.removeAll();
					this.cancel();
					return;
				}
				count++;
				bb.setTitle("収縮まで：" + (fmaxtime - count) + "秒");
				double bar = 1 - (count / (double)fmaxtime);
				if(bar <= 0) {
					bb.removeAll();
					this.cancel();
					if(phase + 1 < 3) {
						timeBar(phase + 1);
					}else {
						world.getWorldBorder().setSize(0,20);
					}
					return;
				}else {
					bb.setProgress(bar);
				}
			}
		}.runTaskTimer(plugin, 20, 20);
	}
	
	

}
