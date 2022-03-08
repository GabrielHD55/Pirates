package com.gabrielhd.pirates.Events;

import com.gabrielhd.pirates.Arena.Arena;
import com.gabrielhd.pirates.Teams.Team;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FinishGameEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    @Getter private final Team winner;
    @Getter private final Arena arena;

    public FinishGameEvent(Team winner, Arena arena) {
        this.winner = winner;
        this.arena = arena;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
}
