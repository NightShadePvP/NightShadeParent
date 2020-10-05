package me.blok601.nightshadeuhc.command.player;

import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.component.ComponentHandler;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.gui.ConfigGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Blok on 11/10/2017.
 */
public class ConfigCommand implements UHCCommand{

    private GameManager gameManager;
    private ComponentHandler componentHandler;

    public ConfigCommand(GameManager gameManager, ComponentHandler componentHandler) {
        this.gameManager = gameManager;
        this.componentHandler = componentHandler;
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "config"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        if(GameManager.get().getHost() == null) {
            p.sendMessage(ChatUtils.message("&cThe game hasn't been set up yet!"));
            return;
        }

        p.sendMessage(ChatUtils.message("&eOpening the game config!"));

        new ConfigGUI(p, gameManager, componentHandler);

    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public Rank getRequiredRank() {
        return null;
    }

    @Override
    public boolean hasRequiredRank() {
        return false;
    }
}
