package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;

public class PermaNightScenario extends Scenario{

    private GameManager gameManager;
    public PermaNightScenario(GameManager gameManager) {
        super("PermaNight", "It is always night", new ItemBuilder(Material.DAYLIGHT_DETECTOR).name("&ePermaNight").make());
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onStart(GameStartEvent e){
        World world = gameManager.getWorld();
        world.setTime(13000);
        world.setGameRuleValue("doDaylightCycle", "false");
        broadcast("&bIt is now night...");
    }
}
