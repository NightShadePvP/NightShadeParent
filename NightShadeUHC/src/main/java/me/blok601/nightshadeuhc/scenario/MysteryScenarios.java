package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Blok on 9/30/2018.
 */
public class MysteryScenarios extends Scenario{

    public MysteryScenarios() {
        super("Mystery Scenarios", "Hides /scenarios from players", new ItemBuilder(Material.COMPASS).name("Mystery Scenarios").make());
    }

    @EventHandler
    public void onPre(PlayerCommandPreprocessEvent e){
        if(!isEnabled()) return;

        if(e.getMessage().startsWith("/scenarios") || e.getMessage().startsWith("/scens")){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatUtils.format(getPrefix() + "&cYou can't do that in mystery scenarios..."));
        }
    }
}