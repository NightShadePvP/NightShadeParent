package me.blok601.nightshadeuhc.command.staff;

import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.command.UHCCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Blok on 7/20/2017.
 */
public class ChatStopCommand implements UHCCommand{

    @Override
    public String[] getNames() {
        return new String[]{
                "chatstop"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        if(ChatUtils.isChatFrozen()){
            ChatUtils.setChatFrozen(false);
            //p.sendMessage(ChatUtils.message("&eThe chat is now &aunfrozen"));
            Bukkit.broadcastMessage(ChatUtils.message("&eThe chat is now &aunfrozen"));
            return;
        }

        ChatUtils.setChatFrozen(true);
        //p.sendMessage(ChatUtils.message("&eThe chat is now &cfrozen"));
        Bukkit.broadcastMessage(ChatUtils.message("&eThe chat is now &cfrozen"));
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public Rank getRequiredRank() {
        return Rank.TRIAL;
    }

    @Override
    public boolean hasRequiredRank() {
        return true;
    }
}
