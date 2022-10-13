package kitpvp;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class KitPvPTool {

	public static Location getEyeLocation(Player player,int delay) {
		Location ll = player.getLocation().clone();
		Block bl = null;
		for(Block b : player.getLineOfSight((HashSet<Material>) null, delay)) {
			if(b.getType() == Material.AIR) {
				bl = b;
			}else {
				break;
			}
		}
		if(bl != null) {
			ll = bl.getLocation().clone();
		}
		return ll;
	}
}
