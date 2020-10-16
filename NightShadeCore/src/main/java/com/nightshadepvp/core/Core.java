package com.nightshadepvp.core;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.massivecraft.massivecore.MassivePlugin;
import com.massivecraft.massivecore.store.DriverFlatfile;
import com.massivecraft.massivecore.store.DriverMongo;
import com.nightshadepvp.core.cmd.cmds.player.CmdTwitter;
import com.nightshadepvp.core.entity.MConf;
import com.nightshadepvp.core.entity.NSPlayerColl;
import com.nightshadepvp.core.listener.LiteBansListener;
import com.nightshadepvp.core.lunar.LunarClientImplementation;
import com.nightshadepvp.core.lunar.api.LunarClientAPI;
import com.nightshadepvp.core.lunar.api.event.impl.AuthenticateEvent;
import com.nightshadepvp.core.lunar.api.module.border.BorderManager;
import com.nightshadepvp.core.lunar.api.module.hologram.HologramManager;
import com.nightshadepvp.core.lunar.api.module.waypoint.WaypointManager;
import com.nightshadepvp.core.lunar.api.user.User;
import com.nightshadepvp.core.lunar.api.user.UserManager;
import com.nightshadepvp.core.lunar.listener.ClientListener;
import com.nightshadepvp.core.lunar.listener.PlayerListener;
import com.nightshadepvp.core.lunar.module.border.BorderManagerImplementation;
import com.nightshadepvp.core.lunar.module.hologram.HologramManagerImplementation;
import com.nightshadepvp.core.lunar.module.waypoint.WaypointManagerImplementation;
import com.nightshadepvp.core.punishment.PunishmentHandler;
import com.nightshadepvp.core.store.NSStore;
import com.nightshadepvp.core.store.NSStoreConf;
import com.nightshadepvp.core.ubl.UBLHandler;
import com.nightshadepvp.core.utils.BufferUtils;
import com.nightshadepvp.core.utils.ChatUtils;
import litebans.api.Events;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class Core extends MassivePlugin implements PluginMessageListener {

    private String matchpost = "uhc.gg";

    private static Core i;

    public Core() {
        Core.i = this;
    }

    private UBLHandler ublHandler = new UBLHandler(this);

    private Logger logger;
    private Jedis jedis;
    private Announcer announcer;
    private HashMap<UUID, ArrayList<Consumer<UUID>>> loginTasks;
    private Location spawn;
    private Twitter twitter;

    //Lunar Api
    private LunarClientAPI api;
    /* Lunar Managers */
    private UserManager userManager;
    private HologramManager hologramManager;
    private WaypointManager waypointManager;
    private BorderManager borderManager;

    @Override
    public void onEnableInner() {
        NSStoreConf.get().load();
        NSStore.registerDriver(DriverMongo.get());
        NSStore.registerDriver(DriverFlatfile.get());

        this.activateAuto();

        logger = new Logger();
        this.loginTasks = new HashMap<>();

        //LUNAR
        api = new LunarClientImplementation(this);

        // Construct manager classes
        this.userManager = new UserManager();
        this.hologramManager = new HologramManagerImplementation(this);
        this.waypointManager = new WaypointManagerImplementation(this);
        this.borderManager = new BorderManagerImplementation(this);

        // Setup configuration
        //this.loadSpawn();


        // Add our channel listeners
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "Lunar-Client");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "Lunar-Client", (channel, player, bytes) -> {
            if (bytes[0] == 26) {
                final UUID outcoming = BufferUtils.getUUIDFromBytes(Arrays.copyOfRange(bytes, 1, 30));

                // To stop server wide spoofing.
                if (!outcoming.equals(player.getUniqueId())) {
                    return;
                }

                User user = getApi().getUserManager().getPlayerData(player);

                if (user != null && !user.isLunarClient()) {
                    user.setLunarClient(true);
                    new AuthenticateEvent(player).call(this);
                }

                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (getApi().isAuthenticated(other)) {
                        other.sendPluginMessage(this, channel, bytes);
                    }
                }
            }
        });

        // Register listeners
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ClientListener(), this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "Maintenance");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "Maintenance", this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "staffchat");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "staffchat", this);

        getConfig().options().copyDefaults(true);
        saveConfig();
        Events.get().register(new LiteBansListener());
        setupTwitter();
        PunishmentHandler.getInstance().setup();
        ServerType.setType(MConf.get().getServerType());
        if(ServerType.getType() == ServerType.UHC){
            this.spawn = new Location(Bukkit.getWorld("spawntest"), 0, 11, 0);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                jedis = new Jedis("localhost");
                //It will be set by: key: random code generator value: discord unique ID
            }
        }.runTaskAsynchronously(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                NSPlayerColl.get().getAllOnline().forEach(nsPlayer -> {
                    nsPlayer.setCurrentAFKTime(nsPlayer.getCurrentAFKTime() + 1);
                    if (nsPlayer.getCurrentAFKTime() >= 10) {
                        nsPlayer.setAFK(true); //If they haven't done shit for 10 seconds they are afk
                    }
                });
            }
        }.runTaskTimer(this, 0, 20);


        getLogManager().log(Logger.LogType.INFO, "Starting UBL Tasks...");
        ublHandler.setup();
        this.announcer = new Announcer(this);
        Bukkit.getConsoleSender().sendMessage(ChatUtils.message("&eLoading NightShadeCore version: " + getDescription().getVersion() + ". Server Type: " + ServerType.getType().toString()));
    }


    @Override
    public void onDisable() {
        i = null;
        MConf.get().setAnnouncerMessages(this.announcer.getMessages());
        //this.saveSpawn()
    }


    public static Core get() {
        return i;
    }

    public Logger getLogManager() {
        return this.logger;
    }


    /**
     * A method that will be thrown when a PluginMessageSource sends a plugin
     * message on a registered channel.
     *
     * @param channel Channel that the message was sent through.
     * @param player  Source of the message.
     * @param message The raw message that was sent.
     */
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equalsIgnoreCase("staffchat")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String playerName = in.readUTF();
            //String server = in.readUTF();
            String msg = in.readUTF();

            NSPlayerColl.get().getAllOnline().stream().filter(nsPlayer -> nsPlayer.hasRank(Rank.TRIAL)).forEach(nsPlayer -> nsPlayer.msg(ChatUtils.format("&8[&cStaff Chat&8] &a" + playerName + "&8: &r" + msg)));
        } else if (channel.equalsIgnoreCase("Maintenance")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String playerName = in.readUTF();
            boolean state = in.readBoolean();

            //if(MConf.get().isMaintenance() == state) return;

            MConf.get().setMaintenance(state);
            MConf.get().changed();

            if (state) {
                if (MConf.get().isMaintenance()) {
                    Bukkit.broadcastMessage(ChatUtils.format("&5Proxy&8 » &b" + playerName + " &ahas enabled maintenance mode!"));
                    Bukkit.broadcastMessage(ChatUtils.format("&5Proxy&8 » &aThe server will enter maintenance mode in 5 seconds..."));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            NSPlayerColl.get().getAllOnline().stream().filter(nsPlayer -> !nsPlayer.hasRank(Rank.TRIAL)).forEach(nsPlayer -> {
                                nsPlayer.getPlayer().kickPlayer("§cThe Server is under maintenance!");
                            });
                            Bukkit.broadcastMessage(ChatUtils.format("&5Proxy&8 » &bMaintenance Mode &aenabled!"));
                        }
                    }.runTaskLater(this, 100);
                }
            } else {
                if (!MConf.get().isMaintenance()) {
                    Bukkit.broadcastMessage(ChatUtils.format("&5Proxy&8 » &b" + playerName + " &chas disabled maintenance mode!"));
                    Bukkit.broadcastMessage(ChatUtils.format("&5Proxy&8 » &cThe server will exit maintenance mode..."));
                    MConf.get().setMaintenance(false);
                    MConf.get().changed();
                    Bukkit.broadcastMessage(ChatUtils.format("&5Proxy&8 » &bMaintenance Mode &cdisabled!"));
                }
            }
        }
    }

    public Jedis getJedis() {
        if (jedis == null || !jedis.isConnected()) {
            jedis = new Jedis("localhost");
        }

        return jedis;
    }

    public String getMatchpost() {
        return matchpost;
    }

    public void setMatchpost(String matchpost) {
        this.matchpost = matchpost;
    }

    public UBLHandler getUblHandler() {
        return ublHandler;
    }

    public Announcer getAnnouncer() {
        return announcer;
    }

    public HashMap<UUID, ArrayList<Consumer<UUID>>> getLoginTasks() {
        return loginTasks;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public WaypointManager getWaypointManager() {
        return waypointManager;
    }

    public BorderManager getBorderManager() {
        return borderManager;
    }

    public LunarClientAPI getApi() {
        return api;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    private void loadSpawn() {
        FileConfiguration config = this.getConfig();
        if (!config.contains("spawn")) {
            this.spawn = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
            return;
        }

        double x, y, z;
        float pitch, yaw;
        World world;
        world = Bukkit.getWorld(config.getString("spawn.x"));
        x = config.getDouble("spawn.x");
        y = config.getDouble("spawn.y");
        z = config.getDouble("spawn.z");
        yaw = config.getFloat("spawn.yaw");
        pitch = config.getFloat("spawn.pitch");
        this.spawn = new Location(world, x, y, z, yaw, pitch);
    }

    private void saveSpawn() {
        FileConfiguration config = this.getConfig();
        config.set("spawn.world", spawn.getWorld().getName());
        config.set("spawn.x", spawn.getX());
        config.set("spawn.y", spawn.getY());
        config.set("spawn.z", spawn.getZ());
        config.set("spawn.yaw", spawn.getYaw());
        config.set("spawn.pitch", spawn.getPitch());
        this.saveConfig();
    }

    private void setupTwitter(){
        new BukkitRunnable(){
            @Override
            public void run() {
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey("xNPGesrTUA9mmm2cIse0Lsm8X")
                        .setOAuthConsumerSecret("iLAJSVOtom6NgnHZBwnqYqGZiZWWl4NwsoQnAjJnjeLt8VvqNL")
                        .setOAuthAccessToken("4825184481-lSGqD5Xdaio9sjokNF2cXhZ62rd0uASmrVHGkn8")
                        .setOAuthAccessTokenSecret("LCS59nRfmEAOqIfMwwoGQVFj1XxyiUXQhAtckr6T4zN9m");
                TwitterFactory tf = new TwitterFactory(cb.build());
                twitter = tf.getInstance();
                Core.get().getLogManager().log(Logger.LogType.INFO, "Successfully connected to twitter!");
            }
        }.runTaskAsynchronously(this);
    }

    public Twitter getTwitter() {
        return twitter;
    }
}
