package kitpvp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.minecraft.network.protocol.game.PacketPlayInSteerVehicle;

public class KitPvPSteer extends PacketAdapter{

	Player master;
	
	public KitPvPSteer(Plugin plugin,Player master, PacketType... types) {
		super(plugin, types);
		this.master = master;
	}
	
	@Override
	public void onPacketReceiving(PacketEvent e) {
		if(e.getPacketType().equals(PacketType.Play.Client.STEER_VEHICLE) && master == e.getPlayer()) {
			PacketPlayInSteerVehicle packet = (PacketPlayInSteerVehicle)e.getPacket().getHandle();
            LivingEntity dragon = (LivingEntity) e.getPlayer().getVehicle();
            boolean speed = false;
            Location l = dragon.getLocation().clone();
            l.add(0,-1,0);
            if(l.getBlock().getType() != Material.AIR) {
            	speed = true;
            }
            l.add(1,0,0);
            if(l.getBlock().getType() != Material.AIR) {
            	speed = true;
            }
            l.add(-2,0,0);
            if(l.getBlock().getType() != Material.AIR) {
            	speed = true;
            }
            l.add(1,0,1);
            if(l.getBlock().getType() != Material.AIR) {
            	speed = true;
            }
            l.add(0,0,-2);
            if(l.getBlock().getType() != Material.AIR) {
            	speed = true;
            }
            if(dragon instanceof Ravager) {
                float swSpeed = packet.b();
                float fwSpeed = packet.c();
                Location ploc = e.getPlayer().getLocation().clone();
                dragon.setRotation(ploc.getYaw(),ploc.getPitch());
                Vector forwardDir = ploc.getDirection();
                Vector sideways = forwardDir.clone().crossProduct(new Vector(0,-1,0));
                Vector total = forwardDir.multiply(fwSpeed).add(sideways.multiply(swSpeed)).normalize().multiply(speed?0.3:0.01);
                dragon.setVelocity(dragon.getVelocity().add(total));
            }
		}
	}

}
