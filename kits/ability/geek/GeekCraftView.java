package kits.ability.geek;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import kits.KitGeek;
import kits.KitModel;

public class GeekCraftView implements Listener{

	KitModel playerdata;
	Player player;
	Plugin plugin;
	
	ArmorStand as;
	
	boolean play = true;
	
	boolean cool = true;
	
	int nowcursor = 0;
	List<Integer> nowselected = new ArrayList<Integer>();
	String[] names = {"フレイム","スロー","フラッシュ","盲目","ケルベロス","ヘッジホッグ","レイン","リコイル"};
	Material[] looks = {Material.RED_DYE,Material.BLUE_DYE,Material.YELLOW_DYE,Material.BLACK_DYE,
			Material.PRISMARINE_CRYSTALS,Material.GUNPOWDER,Material.WATER_BUCKET,Material.SLIME_BALL};
	public int[] partsCost = {4,3,3,4,5,8,13,1};
	int allCost = 0;
	
	public GeekCraftView(KitModel playerdata,Plugin plugin) {
		this.playerdata = playerdata;
		this.player = playerdata.getPlayer();
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		view();
	}
	
	void view() {
		as = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(),EntityType.ARMOR_STAND);
		as.setVisible(false);
		updateArmorStand();
		new BukkitRunnable() {
			public void run() {
				if(!play) {
					this.cancel();
					return;
				}
				Location l = player.getLocation().clone();
				Vector v = player.getLocation().getDirection().normalize();
				l.add(v.multiply(3));
				as.teleport(l);
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	void updateArmorStand() {
		int now = ((KitGeek)playerdata).getParts();
		int kari = allCost + partsCost[nowcursor];
		ItemStack item = new ItemStack(looks[nowcursor]);
		ItemMeta itemm = item.getItemMeta();
		boolean contain = false;
		boolean none = false;
		if(nowselected.contains(nowcursor + 1)) {
			itemm.addEnchant(Enchantment.LUCK, 1,true);
			contain = true;
		}else if(now < kari) {
			none = true;
		}
		item.setItemMeta(itemm);
		as.getEquipment().setHelmet(item);
		as.setCustomName((contain?ChatColor.AQUA:(none?ChatColor.GRAY:ChatColor.WHITE)) + names[nowcursor]);
		as.setCustomNameVisible(true);
	}
	
	public void stop() {
		play = false;
		HandlerList.unregisterAll(this);
		as.remove();
	}
	
	void cursor(int add) {
		nowcursor += add;
		if(nowcursor == names.length) {
			nowcursor = 0;
		}else if(nowcursor < 0) {
			nowcursor = names.length - 1;
		}
		updateArmorStand();
	}
	
	void addOrOut(int index) {
		if(nowselected.contains(index+1)) {
			nowselected.remove((Object)(index+1));
		}else {
			nowselected.add(index+1);
		}
		updateArmorStand();
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if(e.getPlayer() == player && cool) {
			cool = false;
			new BukkitRunnable() {
				public void run() {
					cool = true;
				}
			}.runTaskLater(plugin, 5);
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(player.isSneaking()) {
					int now = ((KitGeek)playerdata).getParts();
					int kari = allCost + partsCost[nowcursor];
					if(now >= kari) {
						allCost += partsCost[nowcursor];
						addOrOut(nowcursor);
					}else {
						player.sendMessage(ChatColor.RED + "部品が足りません！");
					}
				}else {
					cursor(1);	
				}
			}else if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
				if(player.isSneaking()) {
					((KitGeek)playerdata).setGadgetMode(nowselected);
					((KitGeek)playerdata).setParts(((KitGeek)playerdata).getParts() - allCost);
					ItemStack bow = new ItemStack(Material.BOW);
					ItemMeta bowm = bow.getItemMeta();
					bowm.setUnbreakable(true);
					bow.setItemMeta(bowm);
					player.getInventory().setItem(0, bow);
					stop();
					player.setExp(0.99f);
				}else {
					cursor(-1);	
				}
			}
		}
	}
	
	@EventHandler
	public void onRight(PlayerInteractAtEntityEvent e) {
		if(e.getPlayer() == player && cool) {
			cool = false;
			new BukkitRunnable() {
				public void run() {
					cool = true;
				}
			}.runTaskLater(plugin, 5);
			if(player.isSneaking()) {
				addOrOut(nowcursor);
			}else {
				cursor(1);	
			}
		}
	}
	
	public boolean getPlay() {
		return play;
	}
}
