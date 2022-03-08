package com.gabrielhd.pirates.Teams;

import org.bukkit.*;
import org.bukkit.entity.*;
import com.google.common.collect.*;
import com.gabrielhd.pirates.Arena.*;
import java.util.*;

public class Team
{
    private Location loc;
    private final TeamColor team;
    private final List<Player> members;
    
    public Team(TeamColor team) {
        this.team = team;
        this.loc = null;
        this.members = Lists.newArrayList();
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
    
    public int getMembers() {
        return this.members.size();
    }
    
    public void addMember(Player player) {
        this.members.add(player);
    }
    
    public void removeMember(Player player) {
        this.members.remove(player);
    }
    
    public List<Player> getMembersList() {
        return this.members;
    }
    
    public Location getLoc() {
        return this.loc;
    }
    
    public void setLoc(Location loc) {
        this.loc = loc;
    }
    
    public TeamColor getTeam() {
        return this.team;
    }
}
