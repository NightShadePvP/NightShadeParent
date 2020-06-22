package com.nightshadepvp.core.lunar.listener;


import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.lunar.api.event.impl.AuthenticateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WaypointListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAuthenticate(AuthenticateEvent event) {
        Core.get().getApi().getWaypointManager().reloadWaypoints(event.getPlayer(), true);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Core.get().getApi().getWaypointManager().reloadWaypoints(event.getPlayer(), true);
    }
}
