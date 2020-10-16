package com.nightshadepvp.core.cmd.cmds.staff;

import com.massivecraft.massivecore.MassiveException;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.NightShadeCoreCommand;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.cmd.type.TypeNSPlayer;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;

/**
 * Created by Blok on 7/6/2018.
 */
public class CmdFreeze extends NightShadeCoreCommand{

    private static CmdFreeze i = new CmdFreeze();
    public static CmdFreeze get() {return i;}

    public CmdFreeze() {
        this.addAliases("freeze");
        this.addParameter(TypeNSPlayer.get(), "player");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.TRIAL));
    }

    @Override
    public void perform() throws MassiveException {
        NSPlayer targetNSPlayer = this.readArg();
        NSPlayer nsPlayer = NSPlayer.get(sender);
        if(!targetNSPlayer.isOnline()){
            nsPlayer.msg(ChatUtils.message("&cThat player is offline!"));
            return;
        }

        if (targetNSPlayer.isFrozen()) {
            //toggle player freeze off
            targetNSPlayer.setFrozen(false);
            targetNSPlayer.msg(ChatUtils.format("&eYou have been unfrozen!"));
            nsPlayer.msg(ChatUtils.message("&aYou have unfroze &6" + targetNSPlayer.getName()));
        } else {
            //Freeze them
            targetNSPlayer.setFrozen(true);
            targetNSPlayer.msg(ChatUtils.format("&a&lYou have been frozen!"));
            targetNSPlayer.msg(ChatUtils.format("&a&lPlease don't logout!"));

            nsPlayer.msg(ChatUtils.format("&aYou have frozen &6" + targetNSPlayer.getName()));
        }
    }
}
