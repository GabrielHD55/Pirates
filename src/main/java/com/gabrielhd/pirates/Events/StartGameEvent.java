package com.gabrielhd.pirates.Events;

import com.gabrielhd.pirates.Arena.Arena;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StartGameEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    @Getter private final Arena arena;

    public StartGameEvent(Arena arena) {
        this.arena = arena;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
}
