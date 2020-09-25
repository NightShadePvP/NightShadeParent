package com.nightshadepvp.tournament.cmd.cmds.staff.tournament;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeDouble;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.tournament.cmd.NightShadeTournamentCommand;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.enums.TournamentState;
import com.nightshadepvp.tournament.entity.handler.ArenaHandler;
import com.nightshadepvp.tournament.entity.handler.GameHandler;
import com.nightshadepvp.tournament.event.TournamentStartEvent;
import com.nightshadepvp.tournament.utils.ChatUtils;
import com.nightshadepvp.tournament.utils.NumberUtils;

public class CmdTournamentSlots extends NightShadeTournamentCommand {

    public CmdTournamentSlots() {
        this.addAliases("slots");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.TRIAL));
        this.addParameter(TypeInteger.get(), "amount");
    }

    @Override
    public void perform() throws MassiveException {
        TPlayer tPlayer = TPlayer.get(sender);
        if(GameHandler.getInstance().isRunning()){
            tPlayer.msg(ChatUtils.message("&cThe slots can't be changed while a tournament is running!"));
            return;
        }


        int slots = this.readArg();
        int max = ArenaHandler.getInstance().getArenas().size()*2;

        if(slots > max){
            tPlayer.msg(ChatUtils.message("&cThere are not enough arenas for that amount of players! The max slots are &e" + max));
        }

        GameHandler.getInstance().setSlots(slots);
        tPlayer.msg(ChatUtils.message("&bYou have set the slots to &f" + slots));
        return;
    }
}
