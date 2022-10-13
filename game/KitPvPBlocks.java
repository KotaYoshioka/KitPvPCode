package game;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.block.data.type.PointedDripstone.Thickness;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class KitPvPBlocks {

	Plugin plugin;
	
	//ブロック設置
	private HashMap<Location,Material> blockType = new HashMap<Location,Material>();
	//ブロック情報
	private HashMap<Location,BlockData> blockData = new HashMap<Location,BlockData>();
	//ブロック変更情報
	private HashMap<Location,BukkitTask> blockTask = new HashMap<Location,BukkitTask>();
	
	public KitPvPBlocks(Plugin plugin) {
		this.plugin = plugin;
	}
	
	
	/**
	 * 一般的なブロック設置
	 * @param l
	 * @param m
	 * @param delayForTick
	 */
	public void putBlock(Location l,Material m,int delayForTick) {
		final Location lo = l.getBlock().getLocation();
		if(blockType.containsKey(lo)) {
			blockTask.get(lo).cancel();
		}else {
			Block b = lo.getBlock();
			blockType.put(lo, b.getType());
			blockData.put(lo, b.getBlockData());
		}
		lo.getBlock().setType(m);
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				lo.getBlock().setType(blockType.get(lo));
				lo.getBlock().setBlockData(blockData.get(lo));
				blockType.remove(lo);
				blockData.remove(lo);
				blockTask.remove(lo);
			}
		}.runTaskLater(plugin, delayForTick);
		blockTask.put(lo, bt);
	}
	/**
	 * ブロック破壊のアニメーション付き
	 * @param l
	 * @param m
	 * @param delayForTick
	 * @param crack
	 */
	public void putBlock(Location l,Material m,int delayForTick,Material crack) {
		final Location lo = l.getBlock().getLocation();
		if(blockType.containsKey(lo)) {
			blockTask.get(lo).cancel();
		}else {
			Block b = lo.getBlock();
			blockType.put(lo, b.getType());
			blockData.put(lo, b.getBlockData());
		}
		lo.getBlock().setType(m);
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				lo.getBlock().setType(blockType.get(lo));
				lo.getBlock().setBlockData(blockData.get(lo));
				blockType.remove(lo);
				blockData.remove(lo);
				blockTask.remove(lo);
				l.getWorld().spawnParticle(Particle.BLOCK_CRACK, lo,40,0.3,0.3,0.3,crack.createBlockData());			
			}
		}.runTaskLater(plugin, delayForTick);
		blockTask.put(lo, bt);
	}
	/**
	 * 鍾乳石用のブロック設置
	 * @param l
	 * @param m
	 * @param delayForTick
	 * @param thick
	 * @param bf
	 */
	public void putBlock(Location l,Material m,int delayForTick,Thickness thick,BlockFace bf) {
		final Location lo = l.getBlock().getLocation();
		if(blockType.containsKey(lo)) {
			blockTask.get(lo).cancel();
		}else {
			Block b = lo.getBlock();
			blockType.put(lo, b.getType());
			blockData.put(lo, b.getBlockData());
		}
		lo.getBlock().setType(m);
		if(lo.getBlock().getBlockData() instanceof PointedDripstone) {
			PointedDripstone pd = (PointedDripstone)lo.getBlock().getBlockData();
			pd.setThickness(thick);
			pd.setVerticalDirection(bf);
			lo.getBlock().setBlockData(pd);
		}
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				lo.getBlock().setType(blockType.get(lo));
				lo.getBlock().setBlockData(blockData.get(lo));
				blockType.remove(lo);
				blockData.remove(lo);
				blockTask.remove(lo);
				l.getWorld().spawnParticle(Particle.BLOCK_CRACK, lo,40,0.3,0.3,0.3,Material.POINTED_DRIPSTONE.createBlockData());		
			}
		}.runTaskLater(plugin, delayForTick);
		blockTask.put(lo, bt);
	}
}
