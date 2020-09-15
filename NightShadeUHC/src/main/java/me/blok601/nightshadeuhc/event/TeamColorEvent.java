package me.blok601.nightshadeuhc.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamColorEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public TeamColorEvent(){

    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
