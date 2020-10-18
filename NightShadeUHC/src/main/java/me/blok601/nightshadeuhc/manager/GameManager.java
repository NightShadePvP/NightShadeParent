package me.blok601.nightshadeuhc.manager;


import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.wimbli.WorldBorder.BorderData;
import lombok.Getter;
import lombok.Setter;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.object.PlayerRespawn;
import me.blok601.nightshadeuhc.entity.object.SetupStage;
import me.blok601.nightshadeuhc.task.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by Blok on 8/5/2017, Updated by Ozzy, Database, and Austin.
 */
public class GameManager {

    public GameManager() {
        setTimer(new TimerTask());
        setMaxPlayers(60);

        shrinks = new int[]{
                500, 300, 200, 100, 50, 25, 10, 10, 10
        };

        Core.get().getLogManager().log(Logger.LogType.INFO, "GameManager has been setup!");
    }

    private static GameManager i = new GameManager();
    public static GameManager get() {
        return i;
    }

    private boolean fiirstDamage = false;
    private  HashMap<UUID, PlayerRespawn> invs = new HashMap<>();
    private  ArrayList<UUID> deathBans = new ArrayList<>();
    private  ArrayList<String> whitelist = new ArrayList<>();
    private  HashSet<String> respawnQueue = new HashSet<>(); // This saves names like whitelist and will check using ignore case
    @Getter
    private  HashMap<UUID, Integer> helpOpMutes = new HashMap<>();
    @Getter
    public HashMap<UUID, String> helpopMuteReasons = new HashMap<>();
    @Getter
    public HashSet<String> lateScatter = new HashSet<>();
    public boolean IS_SCATTERING = false;
    private  TimerTask timer;
    private  boolean whitelistEnabled;
    private  int finalHealTime = 0;
    private  int pvpTime = 0;
    private  int borderTime = 0;
    private int rrEpisodeLength = 0;
    @Getter
    @Setter
    private  int meetupTime = 0;
    private  int radius = 0;
    private  FinalHealTask finalHealTask;
    private  PvPTask pvpTask;
    private  WorldBorderTask worldBorderTask;
    @Setter
    @Getter
    private  MeetupTask meetupTask;
    @Getter
    @Setter
    private GameCountdownTask gameCountdownTask;
    @Getter
    @Setter
    private ShrinkTask shrinkTask;
    private  int[] shrinks;
    private  int borderID;
    private  HashMap<UUID, Integer> kills = new HashMap<>();
    @Getter
    public World world;
    @Getter
    private HashMap<Player, SetupStage> setupStageHashMap = new HashMap<>();
    @Getter@Setter
    private int setupRadius = 0;
    @Getter@Setter
    private int setupNetherRadius = 0;
    @Getter@Setter
    private String setupSeed = "";
    @Getter@Setter
    private boolean netherEnabled = false;
    @Getter@Setter
    private boolean overWorldPregenned = false;
    @Getter@Setter
    private boolean netherPregenned = false;
    @Getter@Setter
    private World netherWorld = null;
    @Getter
    private HashSet<UUID> toCancel = new HashSet<>();
    public ShowPlayerTask showPlayerTask;
    @Getter@Setter
    private int currentEditingBorderIndex = -1;
    @Getter@Setter
    private boolean arenaOpen = true;



    // ---------------------------------------
    //      Host GUI Game Settings START
    // ---------------------------------------

    @Getter
    @Setter
    private int starterFood = 10;
    private int maxPlayers;
    private Player host;
    private boolean isTeam;
    private double appleRates = 5;
    private double flintRates = 50;
    private boolean stripmining = false;
    private boolean postedToDiscord = false;

    // ---------------------------------------
    //      Host GUI Game Settings END
    // ---------------------------------------


    public int getFinalHealTime() {
        return finalHealTime;
    }

    public void setFinalHealTime(int finalHealTime) {
        this.finalHealTime = finalHealTime;
    }

    public int getPvpTime() {
        return pvpTime;
    }

    public void setPvpTime(int pvpTime) {
        this.pvpTime = pvpTime;
    }

    public int getBorderTime() {
        return borderTime;
    }

    public void setBorderTime(int borderTime) {
        this.borderTime = borderTime;
    }

    public boolean isIsTeam() {
        return isTeam;
    }

    public void setIsTeam(boolean isTeam) {
        this.isTeam = isTeam;
    }

    public Player getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getFirstShrink() {
        return getShrinks()[0];
    }

    public void setFirstShrink(int firstShrink) {

        getShrinks()[0] = firstShrink;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public ArrayList<String> getWhitelist() {
        return whitelist;
    }

    public boolean isWhitelistEnabled() {
        return whitelistEnabled;
    }

    public void setWhitelistEnabled(boolean whitelistEnabled) {
        this.whitelistEnabled = whitelistEnabled;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public TimerTask getTimer() {
        return timer;
    }

    public void setTimer(TimerTask timer) {
        this.timer = timer;
    }

    public double getBorderSize(){

        if(world == null) return 0;

        BorderData bd = com.wimbli.WorldBorder.WorldBorder.plugin.getWorldBorder(world.getName());

        return bd.getRadiusX();
    }

    public HashMap<UUID, PlayerRespawn> getInvs() {
        return invs;
    }

    public ArrayList<UUID> getDeathBans() {
        return deathBans;
    }

    public double getAppleRates() {
        return appleRates;
    }

    public void setAppleRates(double appleRates) {
        this.appleRates = appleRates;
    }

    public double getFlintRates() {
        return flintRates;
    }

    public void setFlintRates(double flintRates) {
        this.flintRates = flintRates;
    }

    public FinalHealTask getFinalHealTask() {
        return finalHealTask;
    }

    public void setFinalHealTask(FinalHealTask finalHealTask) {
        this.finalHealTask = finalHealTask;
    }

    public PvPTask getPvpTask() {
        return pvpTask;
    }

    public void setPvpTask(PvPTask pvpTask) {
        this.pvpTask = pvpTask;
    }

    public WorldBorderTask getWorldBorderTask() {
        return worldBorderTask;
    }

    public void setWorldBorderTask(WorldBorderTask worldBorderTask) {
        this.worldBorderTask = worldBorderTask;
    }

    public int[] getShrinks() {
        return shrinks;
    }

    public int getBorderID() {
        return borderID;
    }

    public void setBorderID(int borderID) {
        this.borderID = borderID;
    }

    public HashMap<UUID, Integer> getKills() {
        return kills;
    }

    public void genWalls(int size, World w) {
        UHC.get().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb " + w.getName() + " set " + size + " " + size + " 0 0");
        UHC.get().getServer().dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
        Location loc = new Location(w, 0.0D, 59.0D, 0.0D);
        if (Bukkit.getPluginManager().getPlugin("ViaRewind") == null) {
            w.getWorldBorder().setCenter(loc);
            w.getWorldBorder().setDamageAmount(0);
            w.getWorldBorder().setDamageBuffer(6969);
            w.getWorldBorder().setSize(size * 2);

        } else {
            int i = 4;
            while (i < 4 + 4) {
                for (int x = loc.getBlockX() - size; x <= loc.getBlockX() + size; x++) {
                    for (int y = 59; y <= 59; y++) {
                        for (int z = loc.getBlockZ() - size; z <= loc.getBlockZ() + size; z++) {
                            if ((x == loc.getBlockX() - size) || (x == loc.getBlockX() + size) || (z == loc.getBlockZ() - size) || (z == loc.getBlockZ() + size)) {
                                Location loc2 = new Location(w, x, y, z);
                                loc2.setY(w.getHighestBlockYAt(loc2));
                                loc2.getBlock().setType(Material.BEDROCK);
                            }
                        }
                    }
                }
                i++;
            }
        }
    }

    public HashSet<String> getRespawnQueue() {
        return respawnQueue;
    }

    public boolean isPostedToDiscord() {
        return postedToDiscord;
    }

    public void setPostedToDiscord(boolean postedToDiscord) {
        this.postedToDiscord = postedToDiscord;
    }

    public int getRrEpisodeLength() {
        return rrEpisodeLength;
    }

    public void setRrEpisodeLength(int rrEpisodeLength) {
        this.rrEpisodeLength = rrEpisodeLength;
    }

    public boolean isFiirstDamage() {
        return fiirstDamage;
    }

    public void setFiirstDamage(boolean fiirstDamage) {
        this.fiirstDamage = fiirstDamage;
    }
}
