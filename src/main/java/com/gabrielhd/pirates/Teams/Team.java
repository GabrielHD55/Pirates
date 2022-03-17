package com.gabrielhd.pirates.Teams;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.*;
import com.google.common.collect.*;
import com.gabrielhd.pirates.Arena.*;
import java.util.*;

public class Team {

    @Getter @Setter private Location loc;
    @Getter private final String name;
    @Getter private final TeamColor team;
    @Getter private final List<Player> members;
    
    public Team(TeamColor team) {
        this.team = team;
        this.loc = null;
        this.members = Lists.newArrayList();

        if(team == TeamColor.RED) {
            this.name = "&4Red";
        } else {
            this.name = "&1Blue";
        }
    }
    
    public boolean isAlive(Arena arena) {
        if (!this.members.isEmpty()) {
            for (Player player : this.members) {
                if (arena.getAlivePlayers().contains(player)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void resetTeam() {
        this.members.clear();
    }
    
    public boolean isMember(Player player) {
        return this.members.contains(player);
    }
    
    public void addMember(Player player) {
        this.members.add(player);
    }
    
    public void removeMember(Player player) {
        this.members.remove(player);
    }
}
