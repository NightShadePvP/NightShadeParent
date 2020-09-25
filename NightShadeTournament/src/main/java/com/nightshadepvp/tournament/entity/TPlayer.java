package com.nightshadepvp.tournament.entity;

import com.massivecraft.massivecore.store.SenderEntity;
import com.massivecraft.massivecore.util.MUtil;
import com.nightshadepvp.core.utils.ItemBuilder;
import com.nightshadepvp.tournament.Tournament;
import com.nightshadepvp.tournament.entity.enums.PlayerStatus;
import com.nightshadepvp.tournament.entity.handler.GameHandler;
import com.nightshadepvp.tournament.entity.handler.MatchHandler;
import com.nightshadepvp.tournament.entity.handler.RoundHandler;
import com.nightshadepvp.tournament.entity.objects.data.Kit;
import com.nightshadepvp.tournament.entity.objects.player.PlayerInv;
import com.nightshadepvp.tournament.scoreboard.ScoreboardLib;
import com.nightshadepvp.tournament.scoreboard.ScoreboardSettings;
import com.nightshadepvp.tournament.scoreboard.common.EntryBuilder;
import com.nightshadepvp.tournament.scoreboard.type.Entry;
import com.nightshadepvp.tournament.scoreboard.type.Scoreboard;
import com.nightshadepvp.tournament.scoreboard.type.ScoreboardHandler;
import com.nightshadepvp.tournament.utils.ChatUtils;
import com.nightshadepvp.tournament.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Blok on 7/18/2018.
 */
public class TPlayer extends SenderEntity<TPlayer> {

    public static TPlayer get(Object oid) {
        return TPlayerColl.get().get(oid);
    }

    private int fightsWon = 0;
    private int fightsLost = 0;
    private int tournamentsWon = 0;
    private int tournamentsPlayed = 0;
    private int tournamentsHosted = 0;

    // -------------------------------------------- //
    // Prefs start
    // -------------------------------------------- //
    private boolean specMatchAlert = true;

    private transient int seed;

    private transient Scoreboard scoreboard;


    private transient boolean spectator;
    private transient boolean frozen;
    private transient boolean usingOldVersion;
    private transient boolean staffMode;
    private transient PlayerStatus status;
    private transient boolean receivingHelpop;

    private transient Map<Kit, PlayerInv> playerKits = new HashMap<>();

    private transient FileConfiguration playerFile;


    @Override
    public TPlayer load(TPlayer that) {
        this.setLastActivityMillis(that.lastActivityMillis);

        this.setFightsWon(that.fightsWon);
        this.setFightsLost(that.fightsLost);
        this.setTournamentsWon(that.tournamentsWon);
        this.setTournamentsPlayed(that.tournamentsPlayed);
        this.setTournamentsHosted(that.tournamentsHosted);

        this.receivingHelpop = true;

        return this;
    }

    public int getFightsWon() {
        return fightsWon;
    }

    public void setFightsWon(int fightsWon) {
        this.fightsWon = fightsWon;
    }

    public int getFightsLost() {
        return fightsLost;
    }

    public void setFightsLost(int fightsLost) {
        this.fightsLost = fightsLost;
    }

    public int getTournamentsWon() {
        return tournamentsWon;
    }

    public void setTournamentsWon(int tournamentsWon) {
        this.tournamentsWon = tournamentsWon;
    }

    public int getTournamentsPlayed() {
        return tournamentsPlayed;
    }

    public void setTournamentsPlayed(int tournamentsPlayed) {
        this.tournamentsPlayed = tournamentsPlayed;
    }

    public String getKdRatio() {
        DecimalFormat format = new DecimalFormat("##.##");
        if(getFightsLost() == 0){
            return format.format(getFightsWon());
        }

        double kd = getFightsWon()/(getFightsLost() * 100.0);
        return format.format(kd);

    }


    public String getWG(){
        DecimalFormat format = new DecimalFormat("##.##");
        if(getTournamentsPlayed() == 0){
            return format.format(getTournamentsWon());
        }
        double wg = getTournamentsWon()/(getTournamentsPlayed() * 100.0);
        return format.format(wg);
    }

    private long lastActivityMillis = System.currentTimeMillis();

    public long getLastActivityMillis() {
        return this.lastActivityMillis;
    }

    public void setLastActivityMillis(long lastActivityMillis) {
        // Clean input
        long target = lastActivityMillis;

        // Detect Nochange
        if (MUtil.equals(this.lastActivityMillis, target)) return;

        // Apply
        this.lastActivityMillis = target;

        // Mark as changed
        this.changed();
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public boolean isSpectator() {
        return spectator;
    }

    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public boolean isUsingOldVersion() {
        return usingOldVersion;
    }

    public void setUsingOldVersion(boolean usingOldVersion) {
        this.usingOldVersion = usingOldVersion;
    }

    public boolean isStaffMode() {
        return staffMode;
    }

    public void setStaffMode(boolean staffMode) {
        this.staffMode = staffMode;
    }

    public double getWinPCT() {
        if (tournamentsPlayed == 0) {
            return 0;
        }

        if (tournamentsWon == 0) {
            return tournamentsPlayed;
        }

        return tournamentsWon / tournamentsPlayed;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public boolean isSeeded() {
        return seed > 0;
    }

    public void spec() {
        Player p = getPlayer();
        setSpectator(true);
        setStatus(PlayerStatus.SPECTATING);
        TPlayer tPlayer;
        for (Player pl : Bukkit.getOnlinePlayers()) {
            tPlayer = TPlayer.get(pl);
            if (!tPlayer.isSpectator()) {
                pl.hidePlayer(p);
            }
        }
        PlayerUtils.clearPlayer(p, true);

        ItemStack specPlayers = new ItemBuilder(Material.PAPER).name("&e» Show Matches «").lore("&6Right click to spectate tournament match").make();
        ItemStack disableSpec = new ItemBuilder(Material.REDSTONE_TORCH_ON).name("&e» Toggle Spectator Mode «").lore("&6Right click to toggle spectator mode").make();
        ItemStack random = new ItemBuilder(Material.COMPASS).name("&e» Spectate Random Match «").lore("&6Right click to spectate a random tournament match").make();

        p.getInventory().setItem(0, specPlayers);
        p.getInventory().setItem(4, disableSpec);
        p.getInventory().setItem(8, random);

        p.setCanPickupItems(false);
        p.setAllowFlight(true);
        p.setFlying(true);
        p.setFlySpeed(0.2F);
        p.setGameMode(GameMode.CREATIVE);
        p.sendMessage(ChatUtils.message("&6You are now a spectator!"));
    }

    public void unspec() {
        if (!isSpectator()) return;
        setSpectator(false);
        Player p = getPlayer();

        for (Player pl : Bukkit.getOnlinePlayers()) {
            pl.showPlayer(p);
        }

        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.setGameMode(GameMode.SURVIVAL);
        p.setFlying(false);
        p.setCanPickupItems(true);
        sendSpawn();
        MatchHandler.getInstance().getActiveMatches().stream().filter(iMatch -> iMatch.getSpectators().contains(this)).forEach(iMatch -> iMatch.getSpectators().remove(this));
        p.sendMessage(ChatUtils.message("&6You are no longer a spectator!"));
    }

    public void sendSpawn() {
        Player p = getPlayer();
        p.teleport(Tournament.get().getSpawnLocation());
        setStatus(PlayerStatus.LOBBY);
        PlayerUtils.clearPlayer(p, true);
        p.setFlySpeed(0.1F);
        p.setFlying(false);
        p.setAllowFlight(false);

        Bukkit.getOnlinePlayers().forEach(o -> o.showPlayer(p));
        if (scoreboard != null) scoreboard.deactivate();

        scoreboard = ScoreboardLib.createScoreboard(p).setHandler(new ScoreboardHandler() {

            @Override
            public String getTitle(Player player) {
                return "&5NightShadePvP";
            }

            @Override
            public List<Entry> getEntries(Player player) {
                return new EntryBuilder()
                        .next((GameHandler.getInstance().getHost() != null && GameHandler.getInstance().getHost().getName().length() >= 10 ? ScoreboardSettings.SCOREBOARD_SPACER_LARGE : ScoreboardSettings.SPACER) + ScoreboardSettings.SPACER + ScoreboardSettings.SPACER)
                        .next("&fHost: &b" + (GameHandler.getInstance().getHost() == null ? "Not Set" : GameHandler.getInstance().getHost().getName()))
                        .blank()
                        .next("&fPlayers: &b" + TPlayerColl.get().getAllOnline().stream().filter(TPlayer::isPlayer).filter(tPlayer -> !tPlayer.isSpectator()).count() + "/" + GameHandler.getInstance().getSlots())
                        .blank()
                        .next("&fKit: &b" + (GameHandler.getInstance().getKit() == null ? "Not Set" : GameHandler.getInstance().getKit().getName()))
                        .blank()
                        .next("&fRound: &b" + RoundHandler.getInstance().getRound())
                        .next((GameHandler.getInstance().getHost() != null && GameHandler.getInstance().getHost().getName().length() >= 10 ? ScoreboardSettings.SCOREBOARD_SPACER_LARGE : ScoreboardSettings.SPACER) + ScoreboardSettings.SPACER + ScoreboardSettings.SPACER)
                        .build();
            }
        }).setUpdateInterval(100L);
        scoreboard.activate(); //Set the lobby board for them

        if (spectator) {
            unspec();
        }

        ItemBuilder item = new ItemBuilder(Material.BOOK).name("&5Kit Editor");

        p.getInventory().setItem(4, item.make());

    }

    public boolean isInSpawn() {
        if (Tournament.get().getSpawnLocation() != null) {
            return isOnline() && getPlayer().getWorld().getName().equalsIgnoreCase(Tournament.get().getSpawnLocation().getWorld().getName());
        }

        return false;
    }

    public void setKit(Kit kit, PlayerInv inv) {
        if (playerKits.containsKey(kit)) {
            playerKits.replace(kit, inv);
            return;
        }

        playerKits.put(kit, inv);
    }

    public PlayerInv getInv(Kit kit) {
        return playerKits.getOrDefault(kit, null);
    }


    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public void editKits(Kit kit) {
        this.status = PlayerStatus.EDITING;
        Player p = getPlayer();
        PlayerUtils.clearPlayer(p, true);
        p.teleport(Tournament.get().getEditLocation());
        p.getInventory().setArmorContents(kit.getArmor());
        p.getInventory().setContents(kit.getItems());
        p.sendMessage(ChatUtils.message("&bNow edting kit: &f" + kit.getName()));

    }

    public Map<Kit, PlayerInv> getPlayerKits() {
        return playerKits;
    }

    public FileConfiguration getPlayerFile() {
        return playerFile;
    }

    public void setPlayerFile(FileConfiguration playerFile) {
        this.playerFile = playerFile;
    }

    public boolean isReceivingHelpop() {
        return receivingHelpop;
    }

    public void setReceivingHelpop(boolean receivingHelpop) {
        this.receivingHelpop = receivingHelpop;
    }

    public boolean isSpecMatchAlert() {
        return specMatchAlert;
    }

    public int getTournamentsHosted() {
        return tournamentsHosted;
    }

    public void setTournamentsHosted(int tournamentsHosted) {
        this.tournamentsHosted = tournamentsHosted;
    }
}
