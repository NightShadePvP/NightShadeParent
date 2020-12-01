package com.nightshadepvp.core.cmd.cmds.staff.notes;

import com.massivecraft.massivecore.MassiveException;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.NightShadeCoreCommand;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.cmd.type.TypeNSPlayer;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;

public class CmdNoteClear extends NightShadeCoreCommand {

    public CmdNoteClear() {
        this.addAliases("clear");
        this.addParameter(TypeNSPlayer.get(), "player");
    }

    @Override
    public void perform() throws MassiveException{
        NSPlayer nsPlayer = NSPlayer.get(sender);
        if(!nsPlayer.hasRank(Rank.TRIAL)){
            nsPlayer.msg(ChatUtils.message("&cYou require the " + Rank.TRIAL.getPrefix() + "&crank to do this command"));
            return;
        }
        NSPlayer target = this.readArg();
        if(target == null){
            nsPlayer.msg(ChatUtils.message("&cThat player couldn't be found!"));
            return;
        }

        if(target.hasRank(Rank.TRIAL)){
            nsPlayer.msg(ChatUtils.message("&cYou can't manage notes of other staff members!"));
            return;
        }

        target.getNotes().clear();
        nsPlayer.msg(ChatUtils.message("&bCleared &f" + target.getName() + "'s &bnotes!"));
    }
}
