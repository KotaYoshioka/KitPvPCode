package kitpvp;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.mojang.authlib.GameProfile;

import game.KitPvPGame;
import maindatas.KitData;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;

public class KitPvPCommand implements CommandExecutor {

	Plugin mainPlugin;
	KitPvPGame kpg;
	ArmorStand as;

	public KitPvPCommand(Plugin plugin) {
		mainPlugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String args2, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			if(args[0].equals("newgame")) {
				kpg = new KitPvPGame(mainPlugin);
				kpg.nowgame = true;
			}else if(args[0].equals("alltest")) {
				player = Bukkit.getPlayer(args[1]);
				if(kpg == null) {
					player.sendMessage("先に、KitPvPGameをセッティングしてください。");
				}else {
					kpg.joinGame(player);
					int kitnumber = Integer.parseInt(args[2]);
					kpg.setPlayerData(player, kitnumber);
					kpg.getPlayerData(player).setKey(true);
					kpg.setLivings(player);
					KitData.SetAllWeapon(player,kitnumber,0,false);
				}
			}else if(args[0].equals("start")) {
				kpg.prepare();
			}else if(args[0].equals("join")) {
				if(args[1].equals("@a")) {
					for(Player p:player.getWorld().getPlayers()) {
						kpg.joinGame(p);
					}
				}else {
					Player target = Bukkit.getPlayer(args[1]);
					kpg.joinGame(target);
				}
			}else if(args[0].equals("kansen")) {
				kpg.joinWatcher(player); 
			}else if(args[0].equals("test")) {
				//WorldServer world = ((CraftWorld)Bukkit.getWorld(player.getWorld().getName())).getHandle();
				//EntityEnderman entityEnderman = new EntityEnderman(EntityTypes.w, world);
				//PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
				//PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(entityEnderman);
				//connection.sendPacket(spawnPacket);
			}else if(args[0].equals("npc")) {
				MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
				WorldServer world = ((CraftWorld)player.getWorld()).getHandle();
				GameProfile profile = new GameProfile(UUID.randomUUID(),"testNPC");
				EntityPlayer npc = new EntityPlayer(server,world,profile,null);
				Location l = player.getLocation().clone();
				npc.b(l.getX(),l.getY(),l.getZ(),l.getYaw(),l.getPitch());
				PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
				connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,npc));
				connection.a(new PacketPlayOutNamedEntitySpawn(npc));
			}else if(args[0].equals("remove")) {
				for(NPCRegistry npcr: CitizensAPI.getNPCRegistries()) {
					for(NPC npc: npcr.sorted()) {
						npc.destroy();
					}
				}
			}else if(args[0].equals("sound")) {
				int soundid = Integer.parseInt(args[1]);
				player.playSound(player.getLocation(),Sound.values()[soundid], 1, 1);
			}else if(args[0].equals("soundstop")) {
				player.stopAllSounds();
			}
		}
		return true;
	}

}
