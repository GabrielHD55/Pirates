package com.gabrielhd.pirates.Managers;

import com.gabrielhd.pirates.Config.YamlConfig;
import com.gabrielhd.pirates.Menus.Submenus.GamesMenu;
import com.gabrielhd.pirates.NPCs.CustomNPC;
import com.gabrielhd.pirates.NPCs.NPCType;
import com.gabrielhd.pirates.Pirates;
import com.gabrielhd.pirates.Utils.LocUtils;
import com.gabrielhd.pirates.Utils.Utils;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NPCManager implements Listener {

	private final Pirates plugin;

	@Getter private final YamlConfig config;
	@Getter private final List<CustomNPC> npcs;

    public NPCManager(Pirates plugin) {
    	this.plugin = plugin;
		this.npcs = Lists.newArrayList();
    	this.config = new YamlConfig(plugin,"NPCs");

        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.setup();
    }
    
    public void setup() {
        this.npcs.clear();

        Bukkit.getScheduler().runTask(plugin, () -> {
        	if(!config.getString("NPCs.Arenas.Loc", "none").equalsIgnoreCase("none")) {
        		Location loc = LocUtils.StringToLocation(config.getString("NPCs.Arenas.Loc"));
        		loc.setX(loc.getBlockX() + 0.5);
        		loc.setZ(loc.getBlockZ() + 0.5);

        		NPCType npcType = NPCType.ARENAS;

        		addNPC("arenas", config.getString("NPCs.Arenas.Name"), loc, npcType, config.getString("NPCs.Arenas.Skin"));
        	}

        	if(config.isSet("NPCs.Shop.Locs") && !config.getStringList("NPCs.Shop.Locs").isEmpty()) {
				NPCType npcType = NPCType.SHOP;
				String name = config.getString("NPCs.Shop.Name");
				String skin = config.getString("NPCs.Shop.Skin");

        		for(String locs : config.getStringList("NPCs.Shop.Locs")) {
        			if(locs == null || locs.isEmpty()) continue;

        			Location loc = LocUtils.StringToLocation(locs);
        			if(loc == null) continue;

					loc.setX(loc.getBlockX() + 0.5);
					loc.setZ(loc.getBlockZ() + 0.5);

					addNPC("shop-"+npcs.size(), name, loc, npcType, skin);
				}
			}

            this.plugin.getLogger().info("Loaded " + npcs.size() + " NPC(s)");
        });
    }
    
    public CustomNPC addNPC(String key, String name, Location loc, NPCType npcType, String skinName) {
		ItemStack hand;

    	if(npcType == NPCType.SHOP) {
			hand = new ItemStack(Material.BOOK);
		} else {
			hand = new ItemStack(Material.BOW);
		}

		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Utils.Color(name));

		npc.spawn(loc, SpawnReason.CREATE);
		npc.getOrAddTrait(SkinTrait.class).setSkinName(skinName);
		npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, hand);

    	CustomNPC customNPC = new CustomNPC(key, npc, npcType);
		npcs.add(customNPC);
    	return customNPC;
    }

    public void saveNPCs() {
    	CustomNPC gamesNPC = getNPC(NPCType.ARENAS);

    	config.set("NPCs.Arenas.Loc", LocUtils.LocationToString(gamesNPC.getNPC().getStoredLocation()));

    	List<String> npcLocs = Lists.newArrayList();
    	for(CustomNPC customNPC : npcs) {
    		if(customNPC.getNpcType() == NPCType.SHOP) {
    			npcLocs.add(LocUtils.LocationToString(customNPC.getNPC().getStoredLocation()));
			}
		}

    	config.set("NPCs.Arenas.Locs", npcLocs);
    	config.save();
	}
    
    public boolean removeNPC(CustomNPC npc) {
		if (npc == null) {
			return false;
		}

    	npc.getNPC().destroy();
    	npcs.remove(npc);
    	return true;
    }

    public CustomNPC getNPC(String key) {
    	for(CustomNPC npc : npcs) {
    		if(npc.getKey().equalsIgnoreCase(key)) {
    			return npc;
			}
		}

    	return null;
	}

	public CustomNPC getNPC(NPCType type) {
		for (CustomNPC npc : npcs) {
			if (npc.getNpcType() == type) {
				return npc;
			}
		}
		return null;
	}

	public CustomNPC getNPC(NPC npc) {
    	for(CustomNPC npc2 : npcs) {
    		if(npc == npc2.getNPC()) {
    			return npc2;
			}
		}
    	return null;
	}
    
    public void destroy() {
    	npcs.forEach(npc -> npc.getNPC().destroy());

    	npcs.clear();
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent e) {
    	CustomNPC npc = getNPC(e.getNPC());
    	if (npc == null) {
    		return;
		}
    	
    	Player player = e.getClicker();
    	if(npc.getNpcType() == NPCType.ARENAS) {
			new GamesMenu(this.plugin).openInventory(player);
		}
    }

    @EventHandler
	public void onLeftClick(NPCLeftClickEvent e) {
		CustomNPC npc = getNPC(e.getNPC());
		if (npc == null) {
			return;
		}

		Player player = e.getClicker();
		if(npc.getNpcType() == NPCType.ARENAS) {
			new GamesMenu(this.plugin).openInventory(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDisable(PluginDisableEvent event) {
    	if(event.getPlugin().getName().equalsIgnoreCase("Citizens")) {
    		destroy();
		}
	}
}
