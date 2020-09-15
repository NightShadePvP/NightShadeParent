package me.blok601.nightshadeuhc;

import com.comphenix.protocol.ProtocolLibrary;
import com.massivecraft.massivecore.MassivePlugin;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.nightshadepvp.core.entity.NSPlayer;
import com.onarandombox.MultiverseCore.MultiverseCore;
import de.robingrether.idisguise.api.DisguiseAPI;
import me.blok601.nightshadeuhc.command.CommandHandler;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.component.ComponentHandler;
import me.blok601.nightshadeuhc.component.GoldenHeadRecipe;
import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.listener.gui.EnchantHider;
import me.blok601.nightshadeuhc.manager.*;
import me.blok601.nightshadeuhc.packet.OldEnchanting;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scoreboard.ScoreboardHandler;
import me.blok601.nightshadeuhc.stat.handler.StatsHandler;
import me.blok601.nightshadeuhc.task.*;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.Lag;
import me.blok601.nightshadeuhc.util.Util;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class UHC extends MassivePlugin implements PluginMessageListener {

    private static UHC i;

    public UHC() {
        UHC.i = this;
    }

    public static UHC get() {
        return i;
    }

    private DisguiseAPI api;
    private MultiverseCore multiverseCore;

    private ScenarioManager scenarioManager;
    private static ScoreboardHandler scoreboardManager;
    private ListenerHandler listenerHandler;
    private CommandHandler commandHandler;
    private ComponentHandler componentHandler;
    private GameManager gameManager;


    private MongoCollection<Document> gameCollection;

    public static HashSet<UUID> players = new HashSet<>();
    public static HashSet<UUID> loggedOutPlayers = new HashSet<>();

    private static String serverType;

    @Override
    public void onEnableInner() {

        if (!setupMultiverse()) {
            Core.get().getLogManager().log(Logger.LogType.SEVERE, "Multiverse wasn't found! Disabling plugin!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.activateAuto();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

        if (Bukkit.getPluginManager().getPlugin("ViaRewind") == null) {
            serverType = "UHC1";
        } else {

            serverType = "UHC2";

            hideEnchants();
            new OldEnchanting(this);

        }

        api = getServer().getServicesManager().getRegistration(DisguiseAPI.class).getProvider();
        setupExtraDatabase().thenAcceptAsync(aBoolean -> StatsHandler.getInstance().setup());


        getConfig().options().copyDefaults(true);
        saveConfig();
        SettingsManager.getInstance().setup(this);
        new GoldenHeadRecipe();
        FakePlayerManager.getInstance().setup(this);
        LoggerManager.getInstance().setup();


        this.gameManager = new GameManager();
        this.componentHandler = new ComponentHandler(gameManager, scenarioManager, this);
        this.componentHandler.setup();
        this.scenarioManager = new ScenarioManager(this, gameManager, componentHandler);
        this.scenarioManager.setup();
        this.commandHandler = new CommandHandler(this, GameManager.get(), scenarioManager, componentHandler);
        this.listenerHandler = new ListenerHandler(this, Core.get(), scenarioManager, GameManager.get(), componentHandler);
        this.listenerHandler.complete();
        scoreboardManager = new ScoreboardHandler(this, gameManager, scenarioManager);
        TeamManager.getInstance().setup();

        setupTasks();


        Bukkit.getConsoleSender().sendMessage(ChatUtils.message("&aNightShadePvPUHC " + getDescription().getVersion() + " has been successfully enabled!"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.message("&eDetected Server&8: &3" + serverType));
        GameState.setState(GameState.WAITING);
    }


    public void onDisable() {
        //LoggerHandler.getInstance().getLoggers().forEach(combatLogger -> LoggerHandler.getInstance().removeLogger(combatLogger));
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord", this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");

    }

    private void setupTasks() {

        new ScoreboardHealthTask(scoreboardManager).runTaskTimerAsynchronously(this, 0, 60);
        new StaffTrackTask().runTaskTimer(this, 0, 100);
        new PregenTask().runTaskTimer(this, 0, 40);

//        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
//            scoreboardManager.getPlayerScoreboards().values().forEach(PlayerScoreboard::update);
//        }, 0L, 35L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Lag.getTPS() > 18) {
                    Util.staffLog("&aThe TPS is currently: " + Lag.getTPS() + " &6Status: &aGREAT");
                } else if (Lag.getTPS() < 18 && Lag.getTPS() > 16) {
                    Util.staffLog("&eThe TPS is currently: " + Lag.getTPS() + " &6Status: &eUnstable. Consider stopping mob spawns");
                } else {
                    Util.staffLog("&cThe TPS is currently: " + Lag.getTPS() + " &6Status: &cBAD. Contact an admin");
                }

            }
        }.runTaskTimer(this, 1, 120 * 20);

        new WorldLoadTask(() -> {

            Core.get().getLogManager().log(Logger.LogType.INFO, "Successfully loaded all worlds!");

        }, this).run();

        new MobCheckTask(this, gameManager).runTaskTimer(this, 0, 200);

    }


    private boolean setupMultiverse() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Multiverse-Core");

        if (plugin instanceof MultiverseCore) {
            multiverseCore = (MultiverseCore) plugin;
            return true;
        }

        return false;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (this.commandHandler.getCommands() == null || this.commandHandler.getCommands().isEmpty()) {
            new CommandHandler(this, gameManager, scenarioManager, componentHandler);
        }

        for (UHCCommand ci : this.commandHandler.getCommands()) {
            List<String> cmds = new ArrayList<String>();
            if (ci.getNames() != null) {
                //Add all the possible aliases
                cmds.addAll(Arrays.asList(ci.getNames()));
            }

            for (String n : cmds) {
                if (cmd.getName().equalsIgnoreCase(n)) {
                    if (ci.playerOnly()) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(ChatUtils.message("&cThis is a player only command!"));
                            return false;
                        }
                    }

                    if (!(sender instanceof Player)) {
                        try {
                            ci.onCommand(sender, cmd, label, args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }

                    Player p = (Player) sender;
                    NSPlayer user = NSPlayer.get(p.getUniqueId());
                    if (ci.hasRequiredRank()) {
                        if (!(user.hasRank(ci.getRequiredRank()))) {
                            p.sendMessage(com.nightshadepvp.core.utils.ChatUtils.message("&cYou require the " + ci.getRequiredRank().getPrefix() + "&crank to do this command!"));
                            return false;
                        }
                    }

                    try {
                        ci.onCommand(sender, cmd, label, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }


    private CompletableFuture<Boolean> setupExtraDatabase() {
        return CompletableFuture.supplyAsync(() -> {
                final String URI = "mongodb://localhost:27017/network";
                MongoClient mongoClient = new MongoClient(new MongoClientURI(URI));

                MongoDatabase mongoDatabase = mongoClient.getDatabase("network");
                this.gameCollection = mongoDatabase.getCollection("uhcGames");
                Core.get().getLogManager().log(Logger.LogType.SERVER, "Successfully connected to Mongo DB!");
                return true;
        });
    }

    private void hideEnchants() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new EnchantHider(this));
    }


    public static DisguiseAPI getApi() {
        return get().getDisguiseAPI();
    }

    public static MultiverseCore getMultiverseCore() {
        return get().getMultiverseCorePlugin();
    }

    public DisguiseAPI getDisguiseAPI() {
        return api;
    }

    public MultiverseCore getMultiverseCorePlugin() {
        return multiverseCore;
    }

    public static ScoreboardHandler getScoreboardManager() {
        return scoreboardManager;
    }

    public MongoCollection<Document> getGameCollection() {
        return gameCollection;
    }

    public static String getServerType() {
        return serverType;
    }

    public ScenarioManager getScenarioManager() {
        return scenarioManager;
    }

    public ComponentHandler getComponentHandler() {
        return componentHandler;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
