package com.nightshadepvp.tournament.entity.handler;

import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.nightshadepvp.tournament.Tournament;
import com.nightshadepvp.tournament.challonge.Challonge;
import com.nightshadepvp.tournament.challonge.MatchWrapper;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.TPlayerColl;
import com.nightshadepvp.tournament.entity.enums.TournamentState;
import com.nightshadepvp.tournament.entity.objects.data.Kit;
import com.nightshadepvp.tournament.entity.objects.game.SoloMatch;
import com.nightshadepvp.tournament.entity.objects.game.iMatch;
import com.nightshadepvp.tournament.task.StartRoundTask;
import com.nightshadepvp.tournament.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by Blok on 6/13/2018.
 */
public class GameHandler {

    private static GameHandler ourInstance = new GameHandler();

    public static GameHandler getInstance() {
        return ourInstance;
    }

    private Kit kit;
    private boolean teams;
    private Player host;
    private int slots;
    private int teamSize;
    private boolean chatFrozen;

    private String bracketLink;

    private HashSet<String> whitelist;
    private boolean whitelistOn;

    private boolean seeded;
    private StartRoundTask startRoundTask;

    private iMatch championship;

    private HashMap<UUID, Location> teleportQueue;
    private TournamentState tournamentState;


    public void setup() {
        this.kit = null;
        this.teams = false;
        this.host = null;
        this.teamSize = 1;
        this.seeded = false;
        this.startRoundTask = null;
        this.teleportQueue = new HashMap<>();
        this.chatFrozen = false;
        this.whitelist = new HashSet<>();
        this.whitelistOn = false;
        this.tournamentState = TournamentState.WAITING;


        this.bracketLink = "https://challonge.com/users/nightshadepvp/tournaments";

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        if (scoreboard.getObjective("health") != null) scoreboard.getObjective("health").unregister();
        Objective health = scoreboard.registerNewObjective("health", "health");
        health.setDisplayName(ChatColor.RED + "❤");
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public boolean isTeams() {
        return teams;
    }

    public void setTeams(boolean teams) {
        this.teams = teams;
    }

    public Player getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public void assignSeeds() {
        seeded = false;
        HashMap<TPlayer, Double> wins = new HashMap<>();
        TPlayerColl.get().getAllOnline().stream().filter(TPlayer::isPlayer).filter(tPlayer -> !tPlayer.isSpectator())
                .forEach(tPlayer -> wins.put(tPlayer, tPlayer.getWinPCT()));

        LinkedHashMap<TPlayer, Double> f = wins.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (aDouble, aDouble2) -> aDouble, LinkedHashMap::new));

        int i = 1;
        TPlayer tPlayer;
        Challonge challonge = Tournament.get().getChallonge();
        challonge.getParticipants().clear();
        for (Map.Entry<TPlayer, Double> entry : f.entrySet()) {
            tPlayer = entry.getKey();
            tPlayer.setSeed(i);
            tPlayer.msg(ChatUtils.message("&bYou are seeded: &f" + i));
            challonge.getParticipants().add(tPlayer.getName());
            i++;
        }
        seeded = true;
    }


    public void assignMatches() {
        Challonge challonge = Tournament.get().getChallonge();
        int round = RoundHandler.getInstance().getRound();
        if (getTeamSize() == 1) {
            RoundHandler.getInstance().populateRound(round);
            try {
                Core.get().getLogManager().log(Logger.LogType.DEBUG, "There will be " + challonge.getMatchesByRound(round).get().size() + " matches in this round:" + round + "");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            try {
                for (JSONObject match : challonge.getMatchesByRound(round).get()) {
                    String[] players = MatchWrapper.getPlayerNames(match.getInt("id"), challonge);
                    if (players == null) continue;
                    TPlayer tPlayer1 = TPlayer.get(Bukkit.getPlayer(players[0]));
                    TPlayer tPlayer2 = TPlayer.get(Bukkit.getPlayer(players[1]));
                    SoloMatch soloMatch = new SoloMatch(tPlayer1, tPlayer2);
                    Core.get().getLogManager().log(Logger.LogType.DEBUG, "Players: " + tPlayer1.getName() + " And " + tPlayer2.getName());
                    MatchHandler.getInstance().addMatch(soloMatch);
                    RoundHandler.getInstance().addMatch(round, soloMatch);
                }
                new StartRoundTask().runTaskTimer(Tournament.get(), 0, 20);
                Core.get().getLogManager().log(Logger.LogType.DEBUG, "Amount of matches in this list: " + RoundHandler.getInstance().getMatchesByRoundNumber(round).size());
                int rounds = challonge.getRounds().get();
                if (round == rounds) {
                    //Its the champ game
                    setChampionship(RoundHandler.getInstance().getMatchesByRoundNumber(round).stream().findFirst().get());
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public boolean isSeeded() {
        return seeded;
    }

    public void setSeeded(boolean seeded) {
        this.seeded = seeded;
    }

    public StartRoundTask getStartRoundTask() {
        return startRoundTask;
    }

    public void setStartRoundTask(StartRoundTask startRoundTask) {
        this.startRoundTask = startRoundTask;
    }

    public iMatch getChampionship() {
        return championship;
    }

    public void setChampionship(iMatch championship) {
        this.championship = championship;
    }

    public HashMap<UUID, Location> getTeleportQueue() {
        return teleportQueue;
    }

    public boolean isChatFrozen() {
        return chatFrozen;
    }

    public void setChatFrozen(boolean chatFrozen) {
        this.chatFrozen = chatFrozen;
    }

    public HashSet<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(HashSet<String> whitelist) {
        this.whitelist = whitelist;
    }

    public boolean isWhitelistOn() {
        return whitelistOn;
    }

    public void setWhitelistOn(boolean whitelistOn) {
        this.whitelistOn = whitelistOn;
    }


    public String getBracketLink() {
        return bracketLink;
    }

    public void setBracketLink(String bracketLink) {
        this.bracketLink = bracketLink;
    }

    public TournamentState getTournamentState() {
        return tournamentState;
    }

    public void setTournamentState(TournamentState tournamentState) {
        this.tournamentState = tournamentState;
    }

    public boolean isRunning(){
        return this.tournamentState != TournamentState.WAITING;
    }
}
