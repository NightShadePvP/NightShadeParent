package com.nightshadepvp.core.cmd.cmds.staff.notes;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.NightShadeCoreCommand;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.cmd.type.TypeNSPlayer;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;

public class CmdNoteRemove extends NightShadeCoreCommand {

    public CmdNoteRemove() {
        this.addAliases("remove");
        this.addParameter(TypeNSPlayer.get(), "player");
        this.addParameter(TypeInteger.get(), "number");
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
        int noteNumber = this.readArg();
        if(noteNumber < 1){
            nsPlayer.msg(ChatUtils.message("&cInvalid note!"));
            return;
        }

        try{
            target.getNotes().remove(noteNumber - 1);
        }catch (IndexOutOfBoundsException exception){
            nsPlayer.msg(ChatUtils.message("&b" + target.getName() + " &cdoes not have a note #" + noteNumber));
            return;
        }
        nsPlayer.msg(ChatUtils.message("&bNote #f" + noteNumber + 1 + " &bremoved from &f" + target.getName()));
    }
}
