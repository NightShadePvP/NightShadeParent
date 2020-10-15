package me.blok601.nightshadeuhc.command.staff;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Created by Blok on 8/22/2018.
 */
public class HelpopMuteCommand implements UHCCommand {

    @Override
    public String[] getNames() {
        return new String[]{
                "helpopmute"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        UHCPlayer uhcPlayer = UHCPlayer.get(p);
        //Command -> /helpopmute <player> <time in mins> <reason>

        if (args.length <= 2) {
            p.sendMessage(ChatUtils.message("&cUsage: /helpopmute <player> <time in minutes> <reason>"));
            return;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            p.sendMessage(ChatUtils.message("&cThat user is offline!"));
            return;
        }

        NSPlayer nsPlayer = NSPlayer.get(target);
        if (nsPlayer.hasRank(Rank.TRIAL)) {
            p.sendMessage(ChatUtils.message("&cYou can't helpop mute other staff! What's the point anyway??"));
            return;
        }
        //Got player

        if (!TimeUtils.isTime(args[1])) {
            p.sendMessage(ChatUtils.message("&cThat is not a valid time! Ex: 5m, 5mins, 5minutes"));
            return;
        }

        int seconds = TimeUtils.parseTimeAsSeconds(TimeUtils.parseAsInt(args[1]));
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            stringBuilder.append(args[i]).append(" ");
        }

        String reason = stringBuilder.toString().trim();
        GameManager.get().getHelpOpMutes().put(target.getUniqueId(), seconds);
        GameManager.get().getHelpopMuteReasons().put(target.getUniqueId(), reason);
        target.sendMessage(ChatUtils.message("&eYou have been &4HelpOP Muted&e for a period of&8: &3" + args[1] + " &efor&8: &3" + reason));
        p.sendMessage(ChatUtils.message("&eYou have helpop muted " + targetName));

        UUID uuid = p.getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (GameManager.get().getHelpOpMutes().get(uuid) <= 0) {
                    //Could go negative if not multiple of 5
                    if(Bukkit.getPlayer(uuid).isOnline()){
                        Bukkit.getPlayer(uuid).sendMessage(ChatUtils.message("&eYou are no longer helpop muted!"));
                        GameManager.get().getHelpOpMutes().remove(uuid);
                        GameManager.get().getHelpopMuteReasons().remove(uuid);
                        return;
                    }else{
                        GameManager.get().getHelpOpMutes().remove(uuid);
                        GameManager.get().getHelpopMuteReasons().remove(uuid);
                    }
                    cancel();
                    return;
                }
                GameManager.get().getHelpOpMutes().replace(uuid, GameManager.get().getHelpOpMutes().get(uuid) - 5);

            }
        }.runTaskTimer(UHC.get(), 0, 100L); //Update every 5 seconds - no strain
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
