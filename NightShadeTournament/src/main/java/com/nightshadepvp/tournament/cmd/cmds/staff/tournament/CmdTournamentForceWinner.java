package com.nightshadepvp.tournament.cmd.cmds.staff.tournament;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.store.SenderEntity;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.cmd.type.TypeNSPlayer;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.entity.NSPlayerColl;
import com.nightshadepvp.tournament.cmd.NightShadeTournamentCommand;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.handler.MatchHandler;
import com.nightshadepvp.tournament.entity.objects.game.iMatch;
import com.nightshadepvp.tournament.utils.ChatUtils;
import org.bukkit.entity.Player;

public class CmdTournamentForceWinner extends NightShadeTournamentCommand {

    public CmdTournamentForceWinner() {
        this.addAliases("forcewinner");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.TRIAL));
        this.addParameter(TypeNSPlayer.get(), "winner or player on winning team");
    }

    @Override
    public void perform() throws MassiveException {
        TPlayer tPlayer = TPlayer.get(sender);
        NSPlayer winner = this.readArg();
        if(winner == null || !winner.isPlayer()){
            tPlayer.msg(ChatUtils.message("&cThat player couldn't be found!"));
            return;
        }
        Player winnerPlayer = winner.getPlayer();

        iMatch match = MatchHandler.getInstance().getActiveMatch(winnerPlayer);
        if(match == null){
            tPlayer.msg(ChatUtils.message("&cThat player isn't in a match right now!"));
            return;
        }

        match.getOpponents(tPlayer).stream().filter(SenderEntity::isOnline).forEach(tPlayer1 -> tPlayer1.getPlayer().setHealth(0));
        //Should end the match


    }
}