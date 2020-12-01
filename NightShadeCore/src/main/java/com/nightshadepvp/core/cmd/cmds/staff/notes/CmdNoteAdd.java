package com.nightshadepvp.core.cmd.cmds.staff.notes;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.NightShadeCoreCommand;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.cmd.type.TypeNSPlayer;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.events.NoteAddEvent;
import com.nightshadepvp.core.utils.ChatUtils;

import java.util.Date;

public class CmdNoteAdd extends NightShadeCoreCommand {

    public CmdNoteAdd() {
        this.addAliases("add");
        this.addParameter(TypeNSPlayer.get(), "player");
        this.addParameter(TypeString.get(), "note", true);
    }

    @Override
    public void perform() throws MassiveException {
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

        String note = this.readArg();
        Date now = new Date();
        target.getNotes().add("&7" + note + " &8(&7" + nsPlayer.getName() + " on " + ChatUtils.formatMillisToDate(System.currentTimeMillis()) + "&8)");
        nsPlayer.msg(ChatUtils.message("&bSuccessfully added note to &f" + target.getName()));
        Core.get().getServer().getPluginManager().callEvent(new NoteAddEvent(note, now, nsPlayer, target));
    }
}
