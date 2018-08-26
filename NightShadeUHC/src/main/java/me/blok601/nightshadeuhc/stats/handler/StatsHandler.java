package me.blok601.nightshadeuhc.stats.handler;

import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.stats.CachedGame;
import me.blok601.nightshadeuhc.tasks.StatUpdateTask;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by Blok on 7/9/2018.
 */
public class StatsHandler {
    private static StatsHandler ourInstance = new StatsHandler();

    public static StatsHandler getInstance() {
        return ourInstance;
    }

    // -----------------------------
    //          Cached Game
    // -----------------------------
   CachedGame cachedGame;

    private TreeMap<UUID, Integer> rating;

    private ArrayList<UHCPlayer> winners;
    private ArrayList<UHCPlayer> kills;

    private StatsHandler() {
        rating = new TreeMap<>();
    }

    public TreeMap<UUID, Integer> getRating() {
        return rating;
    }

    public Integer getPlace(UUID uuid){
        if(rating.containsKey(uuid)){
            return rating.get(uuid);
        }

        return null;
    }

    public void setup(){
        this.winners = new ArrayList<>();
        this.kills = new ArrayList<>();
        this.cachedGame = new CachedGame(null);
        new StatUpdateTask().runTaskTimerAsynchronously(UHC.get(), 0, 2400); //2 min
    }

    public ArrayList<UHCPlayer> getWinners() {
        return winners;
    }

    public ArrayList<UHCPlayer> getKills() {
        return kills;
    }

    public CachedGame getCachedGame() {
        return cachedGame;
    }
}
