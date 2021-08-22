package com.nightshadepvp.tournament.cmd.cmds.staff.tournament;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.tournament.Tournament;
import com.nightshadepvp.tournament.cmd.NightShadeTournamentCommand;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.utils.ChatUtils;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;

/**
 * Created by Blok on 7/25/2018.
 */
public class CmdTournamentSetEditLocation extends NightShadeTournamentCommand {

    public CmdTournamentSetEditLocation() {
        this.addAliases("seteditlocation");
        this.addRequirements(RequirementIsPlayer.get(), ReqRankHasAtLeast.get(Rank.ADMIN));
    }

    @Override
    public void perform() throws MassiveException {
        TPlayer tPlayer = TPlayer.get(sender);
        Tournament.get().setEditLocation(tPlayer.getPlayer().getLocation());

        Selection selection = Tournament.get().getWorldEdit().getSelection(tPlayer.getPlayer());
        if (selection == null) {
            tPlayer.msg(ChatUtils.message("&cYou must have a valid selection!"));
            return;
        }

        Tournament.get().setEditLocationSelection((CuboidSelection) Tournament.get().getWorldEdit().getSelection(tPlayer.getPlayer()));
        tPlayer.msg(ChatUtils.message("&bUpdated the kit editing location!"));
    }
}
