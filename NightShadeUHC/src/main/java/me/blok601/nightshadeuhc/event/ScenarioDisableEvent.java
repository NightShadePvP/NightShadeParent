package me.blok601.nightshadeuhc.event;


import me.blok601.nightshadeuhc.scenario.Scenario;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Blok on 3/7/2018.
 */
public class ScenarioDisableEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private Scenario scenario;
    private boolean cancelled;
    private String message;
    private Player player;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public ScenarioDisableEvent(Scenario scenario, Player player) {
        this.scenario = scenario;
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
}
