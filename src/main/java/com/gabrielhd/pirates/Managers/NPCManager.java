package com.gabrielhd.pirates.Managers;

import com.gabrielhd.pirates.Config.YamlConfig;
import com.gabrielhd.pirates.Menus.Submenus.GamesMenu;
import com.gabrielhd.pirates.NPCs.CustomNPC;
import com.gabrielhd.pirates.NPCs.NPCType;
import com.gabrielhd.pirates.Pirates;
import com.gabrielhd.pirates.Utils.Utils;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
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
        	if(config.getInt("NPCs.Arenas.ID", -1) != -1) {
        		NPCType npcType = NPCType.ARENAS;

        		addNPC(config.getInt("NPCs.Arenas.ID"), npcType);
        	}

        	if(config.isSet("NPCs.Shop.IDs") && !config.getIntegerList("NPCs.Shop.IDs").isEmpty()) {
				NPCType npcType = NPCType.SHOP;

        		for(int ids : config.getIntegerList("NPCs.Shop.IDs")) {
					addNPC(ids, npcType);
				}
			}

            this.plugin.getLogger().info("Loaded " + npcs.size() + " NPC(s)");
        });
    }

    public void createNPC(Location loc, String name, NPCType npcType, String skinName) {
		ItemStack hand;

		if(npcType == NPCType.SHOP) {
			hand = new ItemStack(Material.BOOK);
		} else {
			hand = new ItemStack(Material.BOW);
		}

		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, Utils.Color(name));

		npc.getOrAddTrait(SkinTrait.class).setSkinName(skinName, true);
		npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, hand);
		npc.spawn(loc);

		CustomNPC customNPC = new CustomNPC(String.valueOf(npc.getId()), npc, npcType);
		npcs.add(customNPC);
	}
    
    public void addNPC(int key, NPCType npcType) {
		ItemStack hand;

    	if(npcType == NPCType.SHOP) {
			hand = new ItemStack(Material.BOOK);
		} else {
			hand = new ItemStack(Material.BOW);
		}

		NPC npc = CitizensAPI.getNPCRegistry().getById(key);

    	if(npc != null) {
    		if(!npc.isSpawned()) return;

			npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, hand);

			CustomNPC customNPC = new CustomNPC(String.valueOf(key), npc, npcType);
			npcs.add(customNPC);
		}
    }

    public void saveNPCs() {
    	CustomNPC gamesNPC = getNPC(NPCType.ARENAS);

    	if(gamesNPC != null && gamesNPC.getNPC() != null && gamesNPC.getNPC().isSpawned()) {
			config.set("NPCs.Arenas.ID", gamesNPC.getNPC().getId());
		}

    	List<Integer> npcLocs = Lists.newArrayList();
    	for(CustomNPC customNPC : npcs) {
    		if(customNPC.getNpcType() == NPCType.SHOP && customNPC.getNPC() != null && customNPC.getNPC().isSpawned()) {
    			npcLocs.add(customNPC.getNPC().getId());
			}
		}

    	config.set("NPCs.Shop.IDs", npcLocs);
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
