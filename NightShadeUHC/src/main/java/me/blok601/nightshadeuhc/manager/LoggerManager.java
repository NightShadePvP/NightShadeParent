package me.blok601.nightshadeuhc.manager;

import com.google.common.collect.Sets;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.object.LoggedOutPlayer;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by Blok on 8/23/2019.
 */
public class LoggerManager {

    private static LoggerManager ourInstance = new LoggerManager();

    public static LoggerManager getInstance() {
        return ourInstance;
    }

    private LoggerManager() {
    }

    private HashSet<LoggedOutPlayer> loggedOutPlayers;
    private HashSet<LoggedOutPlayer> deadLoggers;

    public void setup() {
        this.loggedOutPlayers = Sets.newHashSet();
        this.deadLoggers = Sets.newHashSet();
    }


    public LoggedOutPlayer getLogger(UUID uuid) {
        for (LoggedOutPlayer loggedOutPlayer : this.loggedOutPlayers) {
            if (loggedOutPlayer.getUuid().equals(uuid)) {
                return loggedOutPlayer;
            }
        }
        return null;
    }

    public void removeLogger(UUID uuid){
        LoggedOutPlayer loggedOutPlayer = getLogger(uuid);
        if(loggedOutPlayer == null) return;
        if(deadLoggers.contains(loggedOutPlayer)) return;

        UHC.get().getServer().getScheduler().cancelTask(loggedOutPlayer.getTaskID());
        this.loggedOutPlayers.remove(loggedOutPlayer);
    }

    public LoggedOutPlayer getDeadLogger(UUID uuid) {
        for (LoggedOutPlayer loggedOutPlayer : this.deadLoggers) {
            if (loggedOutPlayer.getUuid().equals(uuid)) {
                return loggedOutPlayer;
            }
        }
        return null;
    }

    public boolean hasLogger(UUID uuid) {
        return getLogger(uuid) != null;
    }

    public boolean isDeadLogger(UUID uuid){
        for (LoggedOutPlayer loggedOutPlayer : this.deadLoggers) {
            if (loggedOutPlayer.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public HashSet<LoggedOutPlayer> getLoggedOutPlayers() {
        return loggedOutPlayers;
    }


    public HashSet<LoggedOutPlayer> getDeadLoggers() {
        return deadLoggers;
    }
}
