package com.nightshadepvp.core.cmd.cmds.staff.effect;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.TypePotionEffectType;
import com.massivecraft.massivecore.command.type.combined.TypePotionEffectWrap;
import com.massivecraft.massivecore.command.type.sender.TypePlayer;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.NightShadeCoreCommand;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class CmdEffectRemove extends NightShadeCoreCommand {

    public CmdEffectRemove() {
        this.addAliases("remove");
        this.addParameter(TypePotionEffectType.get(), "effect");
        this.addParameter(TypePlayer.get(), "player");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.TRIAL));
    }

    @Override
    public void perform() throws MassiveException {
        NSPlayer nsPlayer = NSPlayer.get(sender);
        PotionEffectType type = this.readArg();
        Player target = this.readArg();
        if (target == null || !target.isOnline()) {
            nsPlayer.msg(ChatUtils.message("&cThat player couldn't be found!"));
            return;
        }

        if (target.hasPotionEffect(type)) {
            target.removePotionEffect(type);
            nsPlayer.msg(ChatUtils.message("&bRemoved &f" + type.getName() + " &bfrom &f" + target.getName()));
            return;
        }

        nsPlayer.msg(ChatUtils.message("&e" + target.getName() + " &cdoes not have the effect &e" + type.getName()));

    }
}
