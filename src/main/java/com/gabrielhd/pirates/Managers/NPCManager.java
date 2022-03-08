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
import net.jitse.npclib.NPCLib;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.Skin;
import net.jitse.npclib.api.state.NPCSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NPCManager implements Listener {

	private final NPCLib library;
	private final Pirates plugin;

	private Skin shopSkin, gameSkin;

	@Getter private final YamlConfig config;
	@Getter private final List<CustomNPC> npcs;

    public NPCManager(Pirates plugin) {
    	this.plugin = plugin;

    	this.config = new YamlConfig(plugin,"NPCs");
		NPCLib library;
        try {
        	library = new NPCLib(plugin);
		} catch (Exception e) {
        	library = null;
		}
        this.library = library;
		this.npcs = Lists.newArrayList();

        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.setup();
    }
    
    public void setup() {
        this.npcs.clear();

        String gameValue = config.getString("NPCs.Arenas.Skin.Value", "ewogICJ0aW1lc3RhbXAiIDogMTYxOTM4OTc1NjY5MSwKICAicHJvZmlsZUlkIiA6ICJkODAwZDI4MDlmNTE0ZjkxODk4YTU4MWYzODE0Yzc5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ0aGVCTFJ4eCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hODQzNTdmZGFkNjc5YTRmNWUwOGRkNGM0M2QwYWFiZmMzOWM2NWI5ZTlkY2U0ZDU1NjM0NTE2N2UzNmIyN2QyIgogICAgfQogIH0KfQ==");
        String gameSignature = config.getString("NPCs.Arenas.Skin.Signature", "JjPHWP0QLVdV5GWCdWBB4g956S6fmNmtVeZdIzxI/rAJlcmGojoeBnKywk9/YDzqjLWItBUQ6TncV0xhVJnckRdoxMu2ybAklHXl/62XA4doOqBLyMf2RItXEItwb3rpA19e+MXmmZxMmpP9H8V8A6Kxk0ldP2+28QWlNfXzfogpClJAOmyUmra8DkZP6xZS5jvqgAX/Ykb+l/H67rcsMwhwlYRqAbmGFc1Pew3wuxBOXw9YsDZUbv+9VmJAGiMKKkACeQNpMYvgfv5fSEUUicAe7DBpDsP7O0MypeaxII5qOJ8uL3Q8yW8BJmDjPgT+I3dUwodrMMxsybj+fvJJ2aQJ1i3KvagK8DuTsK0wJGfhAR6GECFd474Nbs4Bcp2QSdWBdv3hlOlw6DzNxFCghML2zzp3F31SGTvx2A4gbR9wqgoRGYDh1+gN+8qlrvKET0DOMl16xh/Tfur6tkFHxdVbjdxJmaLmwrzu1i8hGG9dKYAJ4wEGHhruJkXPSbX3nzT0FRC1Elm1fCSJv7Uo9EM4DMw/TvVdJ4UiNlPemsonaTF+HoEydhhpUkcnuE1+Qk15cypolWwKVFaFQMSJGygpBHTN6P69QXoaWnWAQ/ClWzEQmbAWk8ZtLj4uMHGjgENsQmypBgSfpC5hdAffvo2xirfasZsQae7lt56OoTE=");
        this.gameSkin = new Skin(gameValue, gameSignature);

		String shopValue = config.getString("NPCs.Shop.Skin.Value", "ewogICJ0aW1lc3RhbXAiIDogMTYxOTM4OTc1NjY5MSwKICAicHJvZmlsZUlkIiA6ICJkODAwZDI4MDlmNTE0ZjkxODk4YTU4MWYzODE0Yzc5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ0aGVCTFJ4eCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hODQzNTdmZGFkNjc5YTRmNWUwOGRkNGM0M2QwYWFiZmMzOWM2NWI5ZTlkY2U0ZDU1NjM0NTE2N2UzNmIyN2QyIgogICAgfQogIH0KfQ==");
		String shopSignature = config.getString("NPCs.Shop.Skin.Signature", "JjPHWP0QLVdV5GWCdWBB4g956S6fmNmtVeZdIzxI/rAJlcmGojoeBnKywk9/YDzqjLWItBUQ6TncV0xhVJnckRdoxMu2ybAklHXl/62XA4doOqBLyMf2RItXEItwb3rpA19e+MXmmZxMmpP9H8V8A6Kxk0ldP2+28QWlNfXzfogpClJAOmyUmra8DkZP6xZS5jvqgAX/Ykb+l/H67rcsMwhwlYRqAbmGFc1Pew3wuxBOXw9YsDZUbv+9VmJAGiMKKkACeQNpMYvgfv5fSEUUicAe7DBpDsP7O0MypeaxII5qOJ8uL3Q8yW8BJmDjPgT+I3dUwodrMMxsybj+fvJJ2aQJ1i3KvagK8DuTsK0wJGfhAR6GECFd474Nbs4Bcp2QSdWBdv3hlOlw6DzNxFCghML2zzp3F31SGTvx2A4gbR9wqgoRGYDh1+gN+8qlrvKET0DOMl16xh/Tfur6tkFHxdVbjdxJmaLmwrzu1i8hGG9dKYAJ4wEGHhruJkXPSbX3nzT0FRC1Elm1fCSJv7Uo9EM4DMw/TvVdJ4UiNlPemsonaTF+HoEydhhpUkcnuE1+Qk15cypolWwKVFaFQMSJGygpBHTN6P69QXoaWnWAQ/ClWzEQmbAWk8ZtLj4uMHGjgENsQmypBgSfpC5hdAffvo2xirfasZsQae7lt56OoTE=");
        this.shopSkin = new Skin(shopValue, shopSignature);

        Bukkit.getScheduler().runTask(plugin, () -> {
        	if(!config.getString("NPCs.Arenas.Loc", "none").equalsIgnoreCase("none")) {
        		Location loc = LocUtils.StringToLocation(config.getString("NPCs.Arenas.Loc"));
        		loc.setX(loc.getBlockX() + 0.5);
        		loc.setZ(loc.getBlockZ() + 0.5);

        		NPCType npcType = NPCType.ARENAS;
        		NPC npc = addNPC("arenas", config.getStringList("NPCs.Arenas.Name"), loc, npcType).getNPC();

        		Bukkit.getOnlinePlayers().forEach(npc::show);
        	}

        	if(config.isSet("NPCs.Shop.Locs") && !config.getStringList("NPCs.Shop.Locs").isEmpty()) {
				NPCType npcType = NPCType.SHOP;
				List<String> name = config.getStringList("NPCs.Shop.Name");

        		for(String locs : config.getStringList("NPCs.Shop.Locs")) {
        			if(locs == null || locs.isEmpty()) continue;

        			Location loc = LocUtils.StringToLocation(locs);
        			if(loc == null) continue;

					loc.setX(loc.getBlockX() + 0.5);
					loc.setZ(loc.getBlockZ() + 0.5);

					NPC npc = addNPC("shop-"+npcs.size(), name, loc, npcType).getNPC();

					Bukkit.getOnlinePlayers().forEach(npc::show);
				}
			}

            this.plugin.getLogger().info("&eLoaded &f" + npcs.size() + "&e NPC(s)");
        });
    }
    
    public CustomNPC addNPC(String key, List<String> lines, Location loc, NPCType npcType) {
    	Skin skin;
		ItemStack hand;

    	if(npcType == NPCType.SHOP) {
			skin = shopSkin;
			hand = new ItemStack(Material.BOOK);
		} else {
			skin = gameSkin;
			hand = new ItemStack(Material.BOW);
		}

		for (int i = 0; i < lines.size(); i++) {
			lines.set(i, Utils.Color(lines.get(i)));
		}

		NPC npc = library.createNPC(lines);
		npc.setLocation(loc);
		npc.setSkin(skin);
		npc.setItem(NPCSlot.MAINHAND, hand);

    	CustomNPC customNPC = new CustomNPC(key, npc.create(), npcType);
		npcs.add(customNPC);
    	return customNPC;
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
        for (CustomNPC npc : npcs) {
        	if (npc.getNPC().getId().equals(key) || npc.getKey().equalsIgnoreCase(key)) {
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
    
    public void destroy() {
    	npcs.forEach(npc -> npc.getNPC().destroy());
    	npcs.clear();
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
    	Player player = e.getPlayer();
    	Bukkit.getScheduler().runTask(this.plugin, () -> {
    		if (player.isOnline()) {
    		    npcs.forEach(customNPC -> customNPC.getNPC().show(player));
            }
    	});
    }
    
    @EventHandler
    public void onInteract(NPCInteractEvent e) {
    	CustomNPC npc = getNPC(e.getNPC().getId());
    	if (npc == null) {
    		return;
		}
    	
    	Player player = e.getWhoClicked();
    	if(npc.getNpcType() == NPCType.ARENAS) {
			new GamesMenu(this.plugin).openInventory(player);
		}
    }
}
