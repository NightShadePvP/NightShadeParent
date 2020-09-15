package me.blok601.nightshadeuhc.command.staff;

import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Blok on 1/28/2018.
 */
public class DeathBanCommand implements UHCCommand{
    @Override
    public String[] getNames() {
        return new String[]{
                "deathban"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        if(args.length < 2){
            p.sendMessage(ChatUtils.message("&cUsage: /deathban <player> <reason>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null){
            p.sendMessage(ChatUtils.message("&cThat player couldn't be found!"));
            return;
        }

        StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++){
            reason.append(args[i]).append(" ");
        }

//        UHC.players.remove(target.getUniqueId());
        UHCPlayer.get(target).setPlayerStatus(PlayerStatus.LOBBY);
        UHC.getScoreboardManager().applyBoard(target);
        GameManager.get().getWhitelist().remove(target.getName().toLowerCase());
        target.damage(target.getMaxHealth());
        UHC.get().getServer().getScheduler().scheduleSyncDelayedTask(UHC.get(), () -> target.kickPlayer("You have been DQed!\nReason: " + reason), 1);
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
