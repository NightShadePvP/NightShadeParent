package com.nightshadepvp.hub.gui;

import com.nightshadepvp.core.gui.GuiBuilder;
import com.nightshadepvp.hub.Hub;
import org.bukkit.entity.Player;

public class ServerSelectorGUI {


    public ServerSelectorGUI(Player player, Hub plugin) {
        GuiBuilder selector = new GuiBuilder().name("&5Servers").rows(5);
    }
}
