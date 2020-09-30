package com.nightshadepvp.tournament.entity.handler;

import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.nightshadepvp.tournament.Tournament;
import com.nightshadepvp.tournament.challonge.Challonge;
import com.nightshadepvp.tournament.entity.objects.game.iMatch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

/**
 * Created by Blok on 7/18/2018.
 */
public class RoundHandler {
    private static RoundHandler ourInstance = new RoundHandler();

    public static RoundHandler getInstance() {
        return ourInstance;
    }

    private HashMap<Integer, HashSet<iMatch>> matches;
    private int round;
    private Challonge challonge;

    private RoundHandler() {

    }

    public void setup() {
        matches = new HashMap<>();
        round = 0;
    }

    public HashSet<iMatch> getMatchesByRoundNumber(int round) {
        if (matches.containsKey(round)) {
            return matches.get(round);
        } else {
            return new HashSet<>();
        }
    }

    public void incrementRound() {
        this.round++;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public void populateRound(int round) {
        matches.put(round, new HashSet<>());
    }

    public void addMatch(int r, iMatch soloMatch) {
        HashSet<iMatch> mS = getMatchesByRoundNumber(r);
        mS.add(soloMatch);
        this.matches.put(r, mS);
        MatchHandler.getInstance().setMatchID(MatchHandler.getInstance().getMatchID());
        soloMatch.setMatchID(MatchHandler.getInstance().getMatchID());
        //soloMatch.setChallongeMatchID(challonge.matchIds.get(soloMatch.getMatchID()));
    }

    public void setupChallonge() {
        challonge = Tournament.get().getChallonge();
        this.round = 1;
    }
}
