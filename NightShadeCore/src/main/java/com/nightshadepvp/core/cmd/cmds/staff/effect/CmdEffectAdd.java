package com.nightshadepvp.core.cmd.cmds.staff.effect;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.TypePotionEffectType;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.massivecraft.massivecore.command.type.sender.TypePlayer;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.NightShadeCoreCommand;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;
import com.nightshadepvp.core.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CmdEffectAdd extends NightShadeCoreCommand {

    public CmdEffectAdd() {
        this.addAliases("add", "give");
        this.addParameter(TypeString.get(), "player/*");
        this.addParameter(TypePotionEffectType.get(), "effect");
        this.addParameter(TypeInteger.get(), "amplifier");
        this.addParameter(TypeInteger.get(), "length in seconds");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.TRIAL));
    }


    @Override
    public void perform() throws MassiveException {
        NSPlayer nsPlayer = NSPlayer.get(sender);
        String name = this.readArg();
        PotionEffectType type = this.readArg();
        int amp = this.readArg();
        int seconds = this.readArg();
        PotionEffect potionEffect = new PotionEffect(type, (seconds * 20), (amp - 1), true, true);

        if (name.equalsIgnoreCase("*") || name.equalsIgnoreCase("all")) {
            Bukkit.getOnlinePlayers().forEach(player -> player.addPotionEffect(potionEffect));
            nsPlayer.msg(ChatUtils.message("&bGave &feveryone &f" + type.getName() + " " + amp + " &bfor &f" + seconds + " &bseconds"));
            return;
        }

        Player target = Bukkit.getPlayer(name);
        if (target == null) {
            nsPlayer.msg(ChatUtils.message("&cThat player couldn't be found!"));
            return;
        }
        nsPlayer.msg(ChatUtils.message("&bGiven &f" + type.getName() + " " + (amp) + " &bto &f" + target.getName() + " &bfor &f" + seconds + " &bseconds"));
    }
}
