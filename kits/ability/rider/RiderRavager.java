package kits.ability.rider;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityRavager;

public class RiderRavager extends EntityRavager{

	public RiderRavager(Location loc) {
		super(EntityTypes.ay, ((CraftWorld)loc.getWorld()).getHandle());
		this.b(loc.getX(),loc.getY(),loc.getZ());
	}

	@Override
	public void u() {
	}
}
