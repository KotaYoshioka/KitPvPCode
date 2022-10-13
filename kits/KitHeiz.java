package kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

import game.KitPvPGame;
import kitdatas.HeizData;
import net.citizensnpcs.api.npc.NPC;

public class KitHeiz extends KitModel{

	List<NPC> kasumis = new ArrayList<NPC>();
	
	public KitHeiz(Plugin plugin, Player player, KitPvPGame kpg, int kitnumber) {
		super(plugin, player, kpg, kitnumber);
		this.addSpeed(-3);
		this.addJump(-2);
		player.setLevel(1);
		healGauge();
	}
	
	void healGauge() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(player.getLevel() == HeizData.hebiStack) {
					return;
				}
				float exp = player.getExp();
				exp += HeizData.hebiStackGauge;
				if(exp >= 1) {
					exp = 0;
					int level = player.getLevel() + 1;
					if(level > HeizData.hebiStack) {
						level = HeizData.hebiStack;
					}
					player.setLevel(level);
				}
				player.setExp(exp);
			}
		}.runTaskTimer(plugin, 0, 3);
	}

	@Override
	public void kitScoreboard(Objective o) {
		
	}
	
	@Override
	public void death() {
		super.death();
		for(NPC npc:kasumis){
			npc.destroy();
		}
	}
	
	@EventHandler
	public void onNPCDamage(EntityDamageEvent e) {
		HashMap<NPC,Entity> entities = new HashMap<NPC,Entity>();
		for(NPC npcs:kasumis) {
			entities.put(npcs, npcs.getEntity());
		}
		for(NPC npc: entities.keySet()) {
			Entity ent = entities.get(npc);
			if(ent == e.getEntity()) {
				removeKasumi(npc);
				break;
			}
		}
	}
	/*
	
	void setSkin() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://api.ashcon.app/mojang/v2/user/%s",player.getDisplayName())).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                ArrayList<String> lines = new ArrayList<>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                reader.lines().forEach(lines::add);
                String reply = String.join(" ",lines);
                int indexOfValue = reply.indexOf("\"value\": \"");
                int indexOfSignature = reply.indexOf("\"signature\": \"");
                skin = reply.substring(indexOfValue + 10, reply.indexOf("\"", indexOfValue + 10));
                signature = reply.substring(indexOfSignature + 14, reply.indexOf("\"", indexOfSignature + 14));
            }
            else {
                Bukkit.getConsoleSender().sendMessage("Connection could not be opened when fetching player skin (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	*/
	
	/*
	public String getSkin() {
		return skin;
	}
	public String getSignature() {
		return signature;
	}
	*/
	
	public void addKasumi(NPC kasumi) {
		kasumis.add(kasumi);
	}
	public List<NPC> getKasumis(){
		return kasumis;
	}
	public boolean removeKasumi(NPC kasumi) {
		if(kasumis.contains(kasumi)) {
			player.getWorld().spawnParticle(Particle.CLOUD,player.getLocation(),100,4,4,4,0.5);
			for(Entity ent:kasumi.getEntity().getNearbyEntities(HeizData.kasumiRange, HeizData.kasumiRange, HeizData.kasumiRange)) {
				if(kpg.containsLivings(ent) && ent != player) {
					Player target = (Player)ent;
					kpg.getPlayerData(target).setSilence((int)(HeizData.kasumiSilenceSeconds * 20));
					kpg.getPlayerData(target).tempAddSpeed(HeizData.kasumiSlowLevel, (int)(HeizData.kasumiSlowSeconds * 20));
					teleport(target);
				}
			}
			kasumis.remove(kasumi);
			kasumi.destroy();
			return true;
		}
		return false;
	}
	public void teleport(Player target) {
		if(player.isSneaking()) {
			player.getWorld().spawnParticle(Particle.CLOUD,player.getLocation(),30,0.5,0.5,0.5,0.5);
			Location l = target.getLocation().clone();
			Vector v = target.getLocation().getDirection().clone();
			v.setY(0);
			v.multiply(-5);
			l.add(v);
			player.getWorld().spawnParticle(Particle.CLOUD,l,30,0.5,0.5,0.5,0.5);
			player.teleport(l.clone());
		}
	}
}
