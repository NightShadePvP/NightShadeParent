package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.Util;
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
        if(!isEnabled()) return;
        World world = gameManager.getWorld();
        if(world == null){
            Util.staffLog("&cCouldn't start " + getName() + " because the world wasn't set!");
            return;
        }
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setTime(13000);
        broadcast("&bIt is now night...");
    }
}
