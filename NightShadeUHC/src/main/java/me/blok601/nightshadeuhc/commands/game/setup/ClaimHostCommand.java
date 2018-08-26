package me.blok601.nightshadeuhc.commands.game.setup;


import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.utils.ChatUtils;
import me.blok601.nightshadeuhc.utils.Util;
import me.blok601.nightshadeuhc.commands.CmdInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Blok on 9/10/2017.
 */
public class ClaimHostCommand implements CmdInterface {
    @Override
    public String[] getNames() {
        return new String[]{
                "claimhost"
        };
    }

    @Override
    public void onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player p = (Player) commandSender;

        if(GameManager.getHost() != null){
            if(GameManager.getHost().getName().equalsIgnoreCase(p.getName())){
                p.sendMessage(ChatUtils.message("&cYou are already the host for this game!"));
                return;
            }
        }

        GameManager.setHost(p);
        p.sendMessage(ChatUtils.message("&cYou are now the host!"));
        Util.staffLog(ChatUtils.format("&b" + p.getName() + " is now the host!"));

    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public Rank getRequiredRank() {
        return Rank.HOST;
    }

    @Override
    public boolean hasRequiredRank() {
        return true;
    }
}
