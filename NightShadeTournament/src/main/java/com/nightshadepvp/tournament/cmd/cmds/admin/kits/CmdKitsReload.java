package com.nightshadepvp.tournament.cmd.cmds.admin.kits;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.tournament.cmd.NightShadeTournamentCommand;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.handler.KitHandler;
import com.nightshadepvp.tournament.utils.ChatUtils;
import org.bukkit.entity.Player;

/**
 * Created by Blok on 7/18/2018.
 */
public class CmdKitsReload extends NightShadeTournamentCommand {

    public CmdKitsReload() {
        this.addAliases("reload");
        this.addRequirements(RequirementIsPlayer.get(), ReqRankHasAtLeast.get(Rank.ADMIN));
        this.setVisibility(Visibility.SECRET);
    }

    @Override
    public void perform() throws MassiveException {
        Player p = TPlayer.get(sender).getPlayer();
        KitHandler.getInstance().reload();
        p.sendMessage(ChatUtils.message("&bReloaded kits!"));
    }
}
