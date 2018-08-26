package me.blok601.nightshadeuhc.commands.extras;

import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.utils.ChatUtils;
import me.blok601.nightshadeuhc.commands.CmdInterface;
import me.blok601.nightshadeuhc.gui.ConfigGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Blok on 11/10/2017.
 */
public class ConfigCommand implements CmdInterface{
    @Override
    public String[] getNames() {
        return new String[]{
                "config"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        if(GameManager.getHost() == null) {
            p.sendMessage(ChatUtils.message("&cThe game hasn't been set up yet!"));
            return;
        }

        p.sendMessage(ChatUtils.message("&eOpening the game config!"));

        new ConfigGUI(p);

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
