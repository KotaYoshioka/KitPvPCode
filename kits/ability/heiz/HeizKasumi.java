package kits.ability.heiz;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import game.KitPvPGame;
import kitdatas.HeizData;
import kits.KitHeiz;
import kits.ability.KitAbilityBase;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;

public class HeizKasumi extends KitAbilityBase{

	KitHeiz heiz;
	
	public HeizKasumi(KitPvPGame kpg, Player player, int kitnumber, int abilityindex) {
		super(kpg, player, kitnumber, abilityindex);
		heiz = (KitHeiz)kpg.getPlayerData(player);
		doAbility();
	}

	@Override
	protected void doAbility() {
		kpg.getPlayerData(player).cooldown(abilityindex, cd);
		spawnNPC();
	}
	
	void spawnNPC() {
		/*
		MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
		WorldServer world = ((CraftWorld)player.getWorld()).getHandle();
		GameProfile profile = new GameProfile(UUID.randomUUID(),player.getDisplayName());
		profile.getProperties().put("textures", new Property("textures", heiz.getSkin(), heiz.getSignature()));
		EntityPlayer npc = new EntityPlayer(server,world,profile,null);
		Location l = player.getLocation().clone();
		npc.b(l.getX(),l.getY(),l.getZ(),l.getYaw(),l.getPitch());
		for(Player p:kpg.getPlayers()) {
			PlayerConnection connection = ((CraftPlayer)p).getHandle().b;
			connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,npc));
			connection.a(new PacketPlayOutNamedEntitySpawn(npc));
		}
		heiz.addKasumi(npc);
		*/
		for(Player p:kpg.getLivings()) {
			if(p != player) {
				NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER,player.getDisplayName());
				npc.spawn(p.getLocation());
				npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.HAND,new ItemStack(Material.WOODEN_SWORD));
				npc.setProtected(false);
				npc.getNavigator().setTarget(p, true);
				heiz.addKasumi(npc);
				new BukkitRunnable() {
					public void run() {
						heiz.removeKasumi(npc);
					}
				}.runTaskLater(plugin,(int)(HeizData.kasumiLiveSeconds*20));
			}
		}
	}

}
