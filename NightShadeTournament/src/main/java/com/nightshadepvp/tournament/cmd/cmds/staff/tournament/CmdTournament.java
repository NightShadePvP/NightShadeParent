package com.nightshadepvp.tournament.cmd.cmds.staff.tournament;

import com.massivecraft.massivecore.util.MUtil;
import com.nightshadepvp.tournament.cmd.NightShadeTournamentCommand;

import java.util.List;

/**
 * Created by Blok on 7/18/2018.
 */
public class CmdTournament extends NightShadeTournamentCommand {

    private static CmdTournament i = new CmdTournament();
    public static CmdTournament get() {return i;}

    public CmdTournamentClaimHost cmdTournamentClaimHost = new CmdTournamentClaimHost();
    public CmdTournamentSetKit cmdTournamentSetKit = new CmdTournamentSetKit();
    public CmdTournamentSeed cmdTournamentSeed = new CmdTournamentSeed();
    public CmdTournamentStart cmdTournamentStart = new CmdTournamentStart();
    public CmdTournamentSetSpawn cmdTournamentSetSpawn = new CmdTournamentSetSpawn();
    public CmdTournamentSlots cmdTournamentSlots = new CmdTournamentSlots();
    public CmdTournamentSetEditLocation cmdTournamentSetEditLocation = new CmdTournamentSetEditLocation();
    public CmdTournamentPost cmdTournamentPost = new CmdTournamentPost();
    public CmdTournamentFinish cmdTournamentFinish = new CmdTournamentFinish();
    public CmdTournamentForceWinner cmdTournamentForceWinner = new CmdTournamentForceWinner();

    @Override
    public List<String> getAliases() {
        return MUtil.list("tournament", "tourney");
    }
}
