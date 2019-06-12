package me.blok601.nightshadeuhc.listener.game;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.component.ComponentHandler;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.CachedColor;
import me.blok601.nightshadeuhc.event.*;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.scoreboard.PlayerScoreboard;
import me.blok601.nightshadeuhc.stat.CachedGame;
import me.blok601.nightshadeuhc.stat.handler.StatsHandler;
import me.blok601.nightshadeuhc.task.ShowPlayerTask;
import me.blok601.nightshadeuhc.task.WorldBorderTask;
import me.blok601.nightshadeuhc.util.ActionBarUtil;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.PlayerUtils;
import me.blok601.nightshadeuhc.util.ScatterUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Blok on 11/11/2017.
 */
public class GameListener implements Listener {

    private GameManager gameManager;
    private ScenarioManager scenarioManager;
    private ComponentHandler componentHandler;


    public GameListener(GameManager gameManager, ScenarioManager scenarioManager, ComponentHandler componentHandler) {
        this.gameManager = gameManager;
        this.scenarioManager = scenarioManager;
        this.componentHandler = componentHandler;
    }

    @EventHandler
    public void onStart(GameStartEvent e){
        //if(UHC.players.size() >= 15){//Only count if game has 15 player fill or more
            StatsHandler.getInstance().getCachedGame().setStart(System.currentTimeMillis());
        StatsHandler.getInstance().getCachedGame().setFill(UHCPlayerColl.get().getAllPlaying().size());

            Bukkit.getServer().getScheduler().runTaskAsynchronously(UHC.get(), () -> StatsHandler.getInstance().getCachedGame().setMatchID(UHC.get().getGameCollection().count() + 1));
        ShowPlayerTask showPlayerTask = new ShowPlayerTask(UHC.get());
        showPlayerTask.runTaskTimer(UHC.get(), 0L, 2400);
        showPlayerTask.setRunning(true);
        this.gameManager.showPlayerTask = showPlayerTask;
    }

    @EventHandler
    public void onEnd(GameEndEvent e) {
        this.gameManager.showPlayerTask.cancel();
        this.gameManager.showPlayerTask.setRunning(false);
        this.gameManager.showPlayerTask = null;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(UHC.get(), () -> {
            HashMap<String, Integer> winnerKills = new HashMap<>();
            CachedGame cachedGame = StatsHandler.getInstance().getCachedGame();
            ArrayList<String> scenarios = new ArrayList<>();

            cachedGame.setEnd(System.currentTimeMillis());
            cachedGame.setHost(GameManager.get().getHost().getUniqueId().toString());
            ArrayList<String> winners = new ArrayList<>();
            NSPlayer targetNSPlayer;
            for (UUID winner : e.getWinners()){
                winners.add(winner.toString());
                targetNSPlayer = NSPlayer.get(winner); //This is just for now
                if (targetNSPlayer.getRank().getValue() < Rank.DRAGON.getValue()) {
                    //If they are less than Dragon
                    targetNSPlayer.setRank(Rank.DRAGON);
                }
            }
            cachedGame.setWinners(winners);
            scenarioManager.getEnabledScenarios().forEach(scenario -> scenarios.add(scenario.getName()));
            cachedGame.setScenarios(scenarios);

            for (UUID winner : e.getWinners()) {
                winnerKills.put(winner.toString(), GameManager.get().getKills().getOrDefault(winner, 0));
            }
            cachedGame.setWinnerKills(winnerKills);

            if (GameManager.get().isIsTeam()) {
                if (TeamManager.getInstance().isRandomTeams()) {
                    cachedGame.setTeamType("Random To" + TeamManager.getInstance().getTeamSize());
                } else {
                    //Custom
                    cachedGame.setTeamType("cTo" + TeamManager.getInstance().getTeamSize());
                }
            } else {
                cachedGame.setTeamType("FFA");
            }

            cachedGame.setServer(UHC.getServerType());

            Document document = new Document("matchID", cachedGame.getMatchID());
            document.append("host", cachedGame.getHost());
            document.append("winners", cachedGame.getWinners());
            document.append("scenarios", cachedGame.getScenarios());
            document.append("players", cachedGame.getFill());
            document.append("winnerKills", cachedGame.getWinnerKills());
            document.append("teamType", cachedGame.getTeamType());
            document.append("startTime", cachedGame.getStart());
            document.append("endTime", cachedGame.getEnd());
            document.append("server", UHC.getServerType());

            UHC.get().getGameCollection().insertOne(document);
        });
    }

    @EventHandler
    public void on(PvPEnableEvent e){

        new BukkitRunnable(){
            @Override
            public void run() {

                if(WorldBorderTask.counter <= 0){
                    this.cancel();
                    return;
                }

                Bukkit.getOnlinePlayers().forEach(player ->{
                    ActionBarUtil.sendActionBarMessage(player, "§5Border shrink in " + get(WorldBorderTask.counter));
                });
            }
        }.runTaskTimer(UHC.get(), 0, 20);


    }

    @EventHandler
    public void onMeetup(MeetupStartEvent e) {
        for (UHCPlayer uhcPlayer : UHCPlayerColl.get().getAllPlaying()) {
            if (uhcPlayer.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) {
                ScatterUtil.scatterPlayer(gameManager.getWorld(), gameManager.getShrinks()[gameManager.getBorderID()], uhcPlayer.getPlayer());
                uhcPlayer.msg(ChatUtils.message("&eYou have been scattered out of the nether!"));
            }
        }
        componentHandler.getComponent("Nether").setEnabled(false);
    }

    @EventHandler
    public void onLateStart(PlayerJoinGameLateEvent e){
        Player player = e.getPlayer();
        UHCPlayer uhcPlayer = UHCPlayer.get(player);
        uhcPlayer.setChangedLevel(0);
        for (Scenario scenario : scenarioManager.getEnabledScenarios()) {
            if (scenario instanceof StarterItems) {

                StarterItems starterItems = (StarterItems) scenario;

                //UHCPlayerColl.get().getAllPlaying().forEach(uhcPlayer -> PlayerUtils.giveBulkItems(uhcPlayer.getPlayer(), starterItems.getStarterItems
                for (ItemStack stack : starterItems.getStarterItems()) {
                    PlayerUtils.giveItem(stack, player);
                }
            }
        }
    }

    @EventHandler
    public void onSpectate(PlayerStartSpectatingEvent e) {
        Player p = e.getPlayer();

        UHCPlayerColl.get().getAllOnlinePlayers().forEach(u -> {
            PlayerScoreboard playerScoreboard = UHC.get().getScoreboardManager().getPlayerScoreboard(u.getPlayer());
            org.bukkit.scoreboard.Team specTeam = playerScoreboard.getBukkitScoreboard().getTeam("spec");
            if (specTeam != null) {
                specTeam.addEntry(p.getName());
            }
            CachedColor cachedColor = new CachedColor(p.getName());
            cachedColor.setColor("&7&o");
            cachedColor.setPlayer(p.getName());
            TeamManager.getInstance().getCachedColors().add(cachedColor);
        });
    }

    @EventHandler
    public void onStopSpectate(PlayerStopSpectatingEvent e) {
        Player p = e.getPlayer();
        UHCPlayerColl.get().getSpectators().forEach(u -> {
            PlayerScoreboard playerScoreboard = UHC.get().getScoreboardManager().getPlayerScoreboard(u.getPlayer());
            org.bukkit.scoreboard.Team specTeam = playerScoreboard.getBukkitScoreboard().getTeam("spec");
            if (specTeam != null) {
                specTeam.removeEntry(p.getName());
            }
        });

        Collection<CachedColor> cachedColors = TeamManager.getInstance().getCachedColors(p);
        if (!cachedColors.isEmpty()) {
            TeamManager.getInstance().getCachedColors().removeAll(cachedColors); //Clear them out of there
        }

        //Now update every player's board
        UHCPlayerColl.get().getAllOnlinePlayers().forEach(uhcPlayer -> {
            PlayerScoreboard playerScoreboard = UHC.get().getScoreboardManager().getPlayerScoreboard(uhcPlayer.getPlayer());
            Team newTeam;
            String name;
            String playerString = p.getName().length() >= 13 ? p.getName().substring(0, 11) : p.getName();
            name = "UHC" + playerString;
            if (playerScoreboard.getBukkitScoreboard().getTeam(name) != null) {
                playerScoreboard.getBukkitScoreboard().getTeam(name).unregister();
            }

            newTeam = playerScoreboard.getBukkitScoreboard().registerNewTeam(name);

            newTeam.setPrefix(ChatUtils.format("&7&o&m"));
            newTeam.addEntry(p.getName()); //Updated that for everyone
        });
    }

    private String get(int i){
        int m = i/60;
        int s = i%60;

        return "§3" + m + "§5m§3" + s + "§5s";
    }

}
