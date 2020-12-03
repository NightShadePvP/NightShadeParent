package me.blok601.nightshadeuhc.command.staff;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.PlayerUtils;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.HelpOP;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpOPReplyCommand implements UHCCommand {

    @Override
    public String[] getNames() {
        return new String[]{
                "helpopreply"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        UHCPlayer uhcPlayer = UHCPlayer.get(s);
        //hr <id> <message>
        if (args.length < 2) {
            uhcPlayer.msg(ChatUtils.message("&cUsage: /hr <id> <message>"));
            return;
        }

        if (!MathUtils.isInt(args[0])) {
            uhcPlayer.msg(ChatUtils.message("&cSupply a valid HelpOP id!"));
            return;
        }

        HelpOP helpOP = HelpOP.getHelpOP(Integer.parseInt(args[0]));
        if (helpOP == null) {
            uhcPlayer.msg(ChatUtils.message("&cThat HelpOP is invalid!!"));
            return;
        }

        //Helpop was valid

        //Get player
        Player player = Bukkit.getPlayer(helpOP.getPlayer());
        if (player == null) {
            uhcPlayer.msg(ChatUtils.message("&cThe HelpOP response couldn't be sent because the player is offline!"));
            return;
        }

        //Grab the response

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            stringBuilder.append(args[i]);
        }

        String response = stringBuilder.toString();

        player.sendMessage(ChatUtils.format("&8[&cHelpOP Reply&8] &b" + response));
        PlayerUtils.playSound(Sound.NOTE_BASS, player);
        Bukkit.getOnlinePlayers().stream().filter(o -> NSPlayer.get(o.getUniqueId()).hasRank(Rank.TRIAL)).filter(o -> UHCPlayer.get(o.getUniqueId()).isReceiveHelpop()).forEach(o -> o.sendMessage(ChatUtils.format("&8[&cHelpOP Reply->" + player.getName() + "&8] &b" + response)));
    }

    @Override
    public boolean playerOnly() {
        return false;
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
