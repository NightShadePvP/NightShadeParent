package com.nightshadepvp.tournament.cmd.cmds.staff.tournament;

import com.massivecraft.massivecore.MassiveException;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.entity.NSPlayerColl;
import com.nightshadepvp.tournament.Tournament;
import com.nightshadepvp.tournament.cmd.NightShadeTournamentCommand;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.TPlayerColl;
import com.nightshadepvp.tournament.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CmdTournamentFinish extends NightShadeTournamentCommand {

    public CmdTournamentFinish() {
        this.addAliases("finish");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.TRIAL));
    }


    @Override
    public void perform() throws MassiveException {
        TPlayer tPlayer = TPlayer.get(sender);
        tPlayer.msg(ChatUtils.message("&bFinishing game tasks and restarting server..."));
        tPlayer.msg(ChatUtils.message("&bKicking all non-staff..."));
        TPlayerColl.get().getAllOnline().forEach(tPlayer1 -> {
            tPlayer1.unspec();
            tPlayer1.sendSpawn();
        });

        NSPlayerColl.get().getAllPlayersOnline().stream().filter(nsPlayer -> !nsPlayer.hasRank(Rank.TRIAL))
                .forEach(nsPlayer -> nsPlayer.getPlayer()
                        .kickPlayer("The NightShadePvP Tournament has concluded!\nJoin our Discord: discord.nightshadepvp.com\nAnd follow our Twitter: @NightShadePvPMC"));


        ChatUtils.broadcast("&bThe server will restart in &f5&b seconds...");
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
            }
        }.runTaskLater(Tournament.get(), 100L);
    }
}
