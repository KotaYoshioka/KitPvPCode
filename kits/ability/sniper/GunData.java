package kits.ability.sniper;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GunData {

	public static String[] gunName = {"","リボルバー","サブマシンガン","スナイパーライフル","ショットガン"};
	public static Material[] gunLooks = {null,Material.IRON_INGOT,Material.IRON_HORSE_ARMOR,Material.SPYGLASS,Material.GOLDEN_HORSE_ARMOR};
	public static int[] cd = {0,5,0,12,18};
	public static int[] bullets = {0,5,50,2,3};
	public static int[] damage = {0,7,1,15,13};
	public static int[] range = {0,15,32,400,10};
	public static int[] misrange = {0,8,35,0,60};
	
	public static ItemStack getGun(int gunid) {
		ItemStack gun = new ItemStack(gunLooks[gunid]);
		ItemMeta gunm = gun.getItemMeta();
		gunm.setDisplayName(gunName[gunid]);
		gun.setItemMeta(gunm);
		return gun;
	}
}
