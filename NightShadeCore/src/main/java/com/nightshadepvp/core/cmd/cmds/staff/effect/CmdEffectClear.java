package com.nightshadepvp.core.cmd.cmds.staff.effect;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.NightShadeCoreCommand;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CmdEffectClear extends NightShadeCoreCommand {


    public CmdEffectClear() {
        this.addAliases("clear");
        this.addParameter(TypeString.get(), "player/*");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.TRIAL));
    }

    @Override
    public void perform() throws MassiveException {
        NSPlayer nsPlayer = NSPlayer.get(sender);
        String string = this.readArg();
        if (string.equalsIgnoreCase("all") || string.equalsIgnoreCase("*")) {
            //All
            Bukkit.getOnlinePlayers().forEach(player -> player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType())));
            nsPlayer.msg(ChatUtils.message("&bRemoved all potion effects from &ball players"));
            return;
        }

        Player target = Bukkit.getPlayer(string);
        if (target == null) {
            nsPlayer.msg(ChatUtils.message("&cThat player couldn't be found!"));
            return;
        }

        target.getActivePotionEffects().forEach(potionEffect -> target.removePotionEffect(potionEffect.getType()));
        nsPlayer.msg(ChatUtils.message("&bRemoved all potion effects from &f" + target.getName()));
    }
}
