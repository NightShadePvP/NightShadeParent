package com.nightshadepvp.tournament.cmd.cmds.admin.arena;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.tournament.Tournament;
import com.nightshadepvp.tournament.cmd.NightShadeTournamentCommand;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.handler.ArenaHandler;
import com.nightshadepvp.tournament.entity.objects.data.Arena;
import com.nightshadepvp.tournament.utils.ChatUtils;
import com.sk89q.worldedit.LocalSession;
import org.bukkit.entity.Player;

/**
 * Created by Blok on 7/18/2018.
 */
public class CmdArenaCreate extends NightShadeTournamentCommand  {

    public CmdArenaCreate() {

        this.addAliases("create");

        this.addParameter(TypeString.get(), "arenaname");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.ADMIN));
        this.addRequirements(RequirementIsPlayer.get());
        this.setVisibility(Visibility.SECRET);
    }


    @Override
    public void perform() throws MassiveException {
        Player p = TPlayer.get(sender).getPlayer();
        String name = this.readArg();
        if(ArenaHandler.getInstance().isArena(name)){
            p.sendMessage(ChatUtils.message("&cThat arena has already been created! Make sure it is set up properly!"));
            return;
        }


        if(Tournament.get().getWorldEdit().getSelection(p) == null){
            p.sendMessage(ChatUtils.message("&cYou must have a valid World Edit selection!"));
            return;
        }

        Arena arena = new Arena(name);
        arena.setCreator(p.getName());
        arena.setWorld(p.getWorld());
        arena.setSelection(Tournament.get().getWorldEdit().getSelection(p));

        ArenaHandler.getInstance().getArenas().add(arena);
        p.sendMessage(ChatUtils.message("&fSuccessfully created arena: &b" + name));
        p.sendMessage(ChatUtils.message("&fMake sure to finish setting the arena up!"));
        return;
    }
}
