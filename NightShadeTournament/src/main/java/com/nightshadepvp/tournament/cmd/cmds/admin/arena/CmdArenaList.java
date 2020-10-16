package com.nightshadepvp.tournament.cmd.cmds.admin.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Visibility;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.utils.fanciful.FancyMessage;
import com.nightshadepvp.tournament.cmd.NightShadeTournamentCommand;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.handler.ArenaHandler;
import com.nightshadepvp.tournament.entity.objects.data.Arena;
import com.nightshadepvp.tournament.utils.ChatUtils;
import org.bukkit.ChatColor;

/**
 * Created by Blok on 7/18/2018.
 */
public class CmdArenaList extends NightShadeTournamentCommand {

    public CmdArenaList() {
        this.addAliases("list");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.ADMIN));
        this.setVisibility(Visibility.SECRET);
    }

    @Override
    public void perform() throws MassiveException {
        TPlayer tPlayer = TPlayer.get(sender);
        if(tPlayer.isPlayer()){
            FancyMessage message;
            tPlayer.msg(ChatUtils.message("&bArenas:"));
            tPlayer.msg(ChatUtils.format("&f&m-------------------"));
            for (Arena arena : ArenaHandler.getInstance().getArenas()){
                message = new FancyMessage("- " + arena.getName()).color(ChatColor.AQUA).command("/arena tp " + arena.getName());
                message.send(tPlayer.getPlayer());
            }
            tPlayer.msg(ChatUtils.format("&f&m-------------------"));
        }else{
            tPlayer.msg(ChatUtils.message("&bArenas:"));
            tPlayer.msg(ChatUtils.format("&f&m-------------------"));
            ArenaHandler.getInstance().getArenas().forEach(arena -> tPlayer.msg(ChatUtils.format("&b- " + arena.getName())));
            tPlayer.msg(ChatUtils.format("&f&m-------------------"));
        }
    }
}
