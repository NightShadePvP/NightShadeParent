package me.blok601.nightshadeuhc.listener.game;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.events.MatchpostUpdateEvent;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.component.ComponentHandler;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.event.*;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.stat.CachedGame;
import me.blok601.nightshadeuhc.stat.handler.StatsHandler;
import me.blok601.nightshadeuhc.task.ShowPlayerTask;
import me.blok601.nightshadeuhc.task.WorldBorderTask;
import me.blok601.nightshadeuhc.util.*;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Blok on 11/11/2017.
 */
public class GameListener implements Listener {

    private UHC uhc;
    private GameManager gameManager;
    private ScenarioManager scenarioManager;
    private ComponentHandler componentHandler;


    public GameListener(UHC uhc, GameManager gameManager, ScenarioManager scenarioManager, ComponentHandler componentHandler) {
        this.uhc = uhc;
        this.gameManager = gameManager;
        this.scenarioManager = scenarioManager;
        this.componentHandler = componentHandler;
    }

    @EventHandler
    public void onStart(GameStartEvent e){
        //if(UHC.players.size() >= 15){//Only count if game has 15 player fill or more
            StatsHandler.getInstance().getCachedGame().setStart(System.currentTimeMillis());
        StatsHandler.getInstance().getCachedGame().setFill(UHCPlayerColl.get().getAllPlaying().size());

        Bukkit.getServer().getScheduler().runTaskAsynchronously(uhc, () -> StatsHandler.getInstance().getCachedGame().setMatchID(uhc.getGameCollection().count() + 1));
        ShowPlayerTask showPlayerTask = new ShowPlayerTask(uhc);
        showPlayerTask.runTaskTimer(uhc, 0L, 30 * 20);
        showPlayerTask.setRunning(true);
        this.gameManager.showPlayerTask = showPlayerTask;
    }

    @EventHandler
    public void onEnd(GameEndEvent e) {
        this.gameManager.showPlayerTask.cancel();
        this.gameManager.showPlayerTask.setRunning(false);
        this.gameManager.showPlayerTask = null;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(uhc, () -> {
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

            uhc.getGameCollection().insertOne(document);

            try {
                List<String> winnerNames = Lists.newArrayList();
                for (String uuid : cachedGame.getWinners()){
                    winnerNames.add(UHCPlayer.get(UUID.fromString(uuid)).getName());
                }
                Core.get().getTwitter().updateStatus("Congratulations to " + Joiner.on(", ").join(winnerNames) + " on winning a NightShadePvP UHC with " + MathUtils.cummulativeList(cachedGame.getWinnerKills().values()) + " kills!");
            } catch (TwitterException twitterException) {
                twitterException.printStackTrace();
            }
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
                    ActionBarUtil.sendActionBarMessage(player, "§bBorder Shrink §8» " + get(WorldBorderTask.counter));
                });
            }
        }.runTaskTimer(uhc, 0, 20);


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
    public void onMatchpostUpdate(MatchpostUpdateEvent event) {
        String post = event.getNewMatchPost();
        if (!post.contains("https://c.uhc.gg/")) {
            return;
        }

        String[] s = post.split("https://c.uhc.gg/#/post/");
        String id = s[1];
        new BukkitRunnable() {
            @Override
            public void run() {
                String url = "https://hosts.uhc.gg/api/matches/" + id;
                JsonNode jsonNode = null;
                try {
                    jsonNode = Unirest.get(url).asJson().getBody();
                    JSONArray jsonArray = jsonNode.getArray();
                    JSONObject object = jsonArray.getJSONObject(0);
                    JSONArray j = object.getJSONArray("scenarios");
                    String opens = object.getString("opens");

                    Util.staffLog("Data received from matchpost! Testing print now:");
                    ArrayList<String> scenarios = new ArrayList<>();
                    for (int i = 0; i < j.length(); i++) {
                        scenarios.add(j.getString(i));
                    }
                    scenarios.forEach(scen -> {
                        if (scenarioManager.getScen(scen) != null) {
                            scenarioManager.getScen(scen).setEnabled(true);
                            if (event.getUpdater() != null) {
                                event.getUpdater().sendMessage(ChatUtils.message("&eEnabled &a" + scen));
                            }
                        }
                    });
                    Util.staffLog("Opens: " + opens);
                    DateTime date = ISODateTimeFormat.dateTimeParser().parseDateTime(opens);
                    date.withZone(DateTimeZone.UTC);
                    Util.staffLog("Test Date:");
                    Util.staffLog("Date: " + date.getMonthOfYear() + "/" + date.getDayOfMonth() + "/" + date.getYear());
                    Util.staffLog("Time: " + date.getHourOfDay() +":" + date.getMinuteOfHour() + ":" + date.getSecondOfMinute());
//                    new BukkitRunnable(){
//
//                    }.runTaskLaterAsynchronously(uhc, date.minus )
                } catch (UnirestException e) {
                    e.printStackTrace();
                }


            }
        }.runTaskAsynchronously(uhc);

    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event){
        if(event.getEntity().getType() == EntityType.RABBIT && (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpectate(PlayerStartSpectatingEvent e) {
        Player p = e.getPlayer();
        p.setPlayerListName("§7§o" + p.getName());
    }

    @EventHandler
    public void onStopSpectate(PlayerStopSpectatingEvent e) {
        Player p = e.getPlayer();
        p.setPlayerListName(p.getName());
    }

    private String get(int i){
        int m = i/60;
        int s = i%60;

        return "§b" + m + "§fm§b" + s + "§fs";
    }

}
