package me.blok601.nightshadeuhc.scenario.cmd.mole;

import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Blok on 11/17/2018.
 */
public class MoleCommand implements UHCCommand {
    @Override
    public String[] getNames() {
        return new String[]{
                "mole"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        p.sendMessage(ChatUtils.message("Some useful commands for moles games:"));
        p.sendMessage(ChatUtils.format("&e- &b/mcc - Send a message to the moles"));
        p.sendMessage(ChatUtils.format("&e- &b/mcl - Send your location to the moles"));
        p.sendMessage(ChatUtils.format("&e- &b/molekit - Redeem your molekit"));
        p.sendMessage(ChatUtils.format("&e- &b/moles - View the moles"));
    }

    @Override
    public boolean playerOnly() {
        return true;
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
