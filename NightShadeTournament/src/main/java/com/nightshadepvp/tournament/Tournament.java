package com.nightshadepvp.tournament;


import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.massivecraft.massivecore.MassivePlugin;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.nightshadepvp.core.ServerType;
import com.nightshadepvp.core.utils.ItemBuilder;
import com.nightshadepvp.tournament.challonge.Challonge;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.handler.ArenaHandler;
import com.nightshadepvp.tournament.entity.handler.GameHandler;
import com.nightshadepvp.tournament.entity.handler.KitHandler;
import com.nightshadepvp.tournament.entity.handler.RoundHandler;
import com.nightshadepvp.tournament.entity.objects.data.CachedGame;
import com.nightshadepvp.tournament.scoreboard.ScoreboardLib;
import com.nightshadepvp.tournament.task.WeatherTask;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class Tournament extends MassivePlugin {

    private static Tournament i;

    public Tournament() {
        Tournament.i = this;
    }

    private Location spawnLocation;
    private Location editLocation;
    private Challonge challonge;
    private CuboidSelection editLocationSelection;
    private CachedGame cachedGame;
    private MongoCollection<Document> tourneyCollection;
    private ArrayList<ItemStack> hofInventoryItems;


    @Override
    public void onEnableInner() {
        this.activateAuto();

        getConfig().options().copyDefaults(true);
        saveConfig();

        Settings.getSettings().createFiles(this);
        ArenaHandler.getInstance().setup();
        KitHandler.getInstance().setup();
        GameHandler.getInstance().setup();
        RoundHandler.getInstance().setup();
        ScoreboardLib.setPluginInstance(this);
        loadSpawn();
        loadEdit();
        setupExtraDatabase().thenAcceptAsync(aBoolean -> {
            Core.get().getLogManager().log(Logger.LogType.INFO, "Attempting to load HOF");
            setupHOF();
        });


        new WeatherTask(this).runTaskTimer(this, 0, 3600);
        ServerType.setType(ServerType.PRACTICE); //TODO: Change
        Core.get().getLogManager().log(Logger.LogType.INFO, "Tournaments v" + this.getDescription().getVersion() + " by " + Joiner.on(", ").join(this.getDescription().getAuthors()));
        this.cachedGame = new CachedGame();
    }



    @Override
    public void onDisable() {
        saveSpawn();
        saveEdit();
        ArenaHandler.getInstance().save();
        KitHandler.getInstance().save();
        Settings.getSettings().saveFiles();
        i = null;
    }

    private void loadSpawn(){

        FileConfiguration config = Settings.getSettings().getArenas();

        if (!config.contains("spawn")) {
            return;
        }

        World world = Bukkit.getWorld(config.getString("spawn.world"));
        double x = config.getDouble("spawn.x");
        double y = config.getDouble("spawn.y");
        double z = config.getDouble("spawn.z");
        if(world == null) world = Bukkit.getWorlds().get(0);
        this.spawnLocation = new Location(world, x, y, z);
    }

    private void saveSpawn(){
        FileConfiguration config = Settings.getSettings().getArenas();

        config.set("spawn.world", getSpawnLocation().getWorld().getName());
        config.set("spawn.x", getSpawnLocation().getX());
        config.set("spawn.y", getSpawnLocation().getY());
        config.set("spawn.z", getSpawnLocation().getZ());
        Settings.getSettings().saveArenas();
    }

    private void loadEdit() {
        FileConfiguration config = Settings.getSettings().getArenas();

        if (!config.contains("edit")) {
            return;
        }
        ConfigurationSection section = config.getConfigurationSection("edit");

        World world = Bukkit.getWorld(section.getString("world"));
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        if (world == null) world = Bukkit.getWorlds().get(0);
        this.editLocation = new Location(world, x, y, z);
        Core.get().getLogManager().log(Logger.LogType.INFO, "Registered kit editing location");

        if(!section.contains("selection")) return;

        double maxx = section.getDouble("selection.max.x");
        double maxy = section.getDouble("selection.max.y");
        double maxz = section.getDouble("selection.max.z");
        double minx = section.getDouble("selection.min.x");
        double miny = section.getDouble("selection.min.y");
        double minz = section.getDouble("selection.min.z");
        Location max = new Location(world, maxx, maxy, maxz);
        Location min = new Location(world, minx, miny, minz);
        this.editLocationSelection = new CuboidSelection(world, min, max);
        Core.get().getLogManager().log(Logger.LogType.INFO, "Registered kit editing selection!");
    }

    private void saveEdit() {
        FileConfiguration config = Settings.getSettings().getArenas();

        ConfigurationSection section = config.getConfigurationSection("edit");
        section.set("world", getEditLocation().getWorld().getName());
        section.set("x", getEditLocation().getX());
        section.set("y", getEditLocation().getY());
        section.set("z", getEditLocation().getZ());

        if(this.editLocationSelection != null){
            section.set("selection.max.x", this.editLocationSelection.getMaximumPoint().getX());
            section.set("selection.max.y", this.editLocationSelection.getMaximumPoint().getY());
            section.set("selection.max.z", this.editLocationSelection.getMaximumPoint().getZ());

            section.set("selection.min.x", this.editLocationSelection.getMinimumPoint().getX());
            section.set("selection.min.y", this.editLocationSelection.getMinimumPoint().getY());
            section.set("selection.min.z", this.editLocationSelection.getMinimumPoint().getZ());
        }


        Settings.getSettings().saveArenas();
    }


    public static Tournament get() {
        return i;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getEditLocation() {
        return editLocation;
    }

    public void setEditLocation(Location editLocation) {
        this.editLocation = editLocation;
    }

    public WorldEditPlugin getWorldEdit(){
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
        else return null;
    }

    public Challonge getChallonge() {
        return challonge;
    }

    public void setChallonge(Challonge challonge) {
        this.challonge = challonge;
    }

    public CuboidSelection getEditLocationSelection() {
        return editLocationSelection;
    }

    public void setEditLocationSelection(CuboidSelection editLocationSelection) {
        this.editLocationSelection = editLocationSelection;
    }

    public CachedGame getCachedGame() {
        return cachedGame;
    }

    private CompletableFuture<Boolean> setupExtraDatabase() {
        return CompletableFuture.supplyAsync(() -> {
            final String URI = "mongodb://localhost:27017/network";
            MongoClient mongoClient = new MongoClient(new MongoClientURI(URI));

            MongoDatabase mongoDatabase = mongoClient.getDatabase("network");
            this.tourneyCollection = mongoDatabase.getCollection("tourneyGames");
            Core.get().getLogManager().log(Logger.LogType.SERVER, "Successfully connected to Mongo DB!");

            return true;
        });
    }

    public MongoCollection<Document> getTourneyCollection() {
        return tourneyCollection;
    }

    private void setupHOF(){
        this.hofInventoryItems = Lists.newArrayList();
        getServer().getScheduler().runTaskAsynchronously(this, () ->{
            ArrayList<CachedGame> cachedGames = new ArrayList<>();
            FindIterable<Document> documents = this.getTourneyCollection().find();
            MongoCursor<Document> cursor = documents.iterator();
            try {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    CachedGame tempGame = new CachedGame();
                    tempGame.setHost(doc.getString("host"));
                    tempGame.setWinners((List<String>) doc.get("winners"));
                    tempGame.setKit(doc.getString("kit"));
                    tempGame.setFill(doc.getInteger("players"));
                    tempGame.setBracketLink(doc.getString("bracket"));
                    tempGame.setTeamType(doc.getString("teamType"));
                    tempGame.setStart(doc.getLong("startTime"));
                    tempGame.setEnd(doc.getLong("endTime"));
                    tempGame.setServer(doc.getString("server"));
                    tempGame.setTourneyID(doc.getLong("matchID"));
//                    if (tempGame.getFill() > 10)
//                        cachedGames.add(tempGame);
                }
            } finally {
                cursor.close();
            }


            cachedGames.sort(Comparator.comparing(CachedGame::getEnd));
            ArrayList<ItemStack> items = new ArrayList<>();
            SkullMeta skullMeta;
            ItemStack head;
            ItemBuilder itemBuilder;
            TPlayer host;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss aa MM/dd/YYYY");
            for (CachedGame cachedGame : cachedGames) {
                host = TPlayer.get(UUID.fromString(cachedGame.getHost()));
                head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                skullMeta = (SkullMeta) head.getItemMeta();
                skullMeta.setOwner(host.getName());
                head.setItemMeta(skullMeta);
                itemBuilder = new ItemBuilder(head);
                itemBuilder.name("&6" + host.getName() + "&8»");
                itemBuilder.lore("&eStart Time&8» &b" + simpleDateFormat.format(new Date(cachedGame.getStart())));
                itemBuilder.lore("&eEnd Time&8» &b" + simpleDateFormat.format(new Date(cachedGame.getEnd())));
                itemBuilder.lore(" ");
                itemBuilder.lore("&eFill&8» &b" + cachedGame.getFill());
                itemBuilder.lore("&eTeam Type&8» &b" + cachedGame.getTeamType());
                itemBuilder.lore(" ");
                itemBuilder.lore("&eKit8» &b" + cachedGame.getKit());
                String winnerName;
                itemBuilder.lore("&eWinner&7(&es&7)&8»");
                //itemBuilder.lore("&eWinner&7(&es&7)&8» &b" + winnerName + " &8[&3" + cachedGame.getWinnerKills().get(cachedGame.getWinners().get(0)) + "&8]");
                for (String w : cachedGame.getWinners()) {
                    winnerName = TPlayer.get(UUID.fromString(w)).getName();
                    itemBuilder.lore("  &8→ &b" + winnerName);
                }
                itemBuilder.lore(" ");
                itemBuilder.lore("&eBracket Link8» &b" + cachedGame.getBracketLink());
                itemBuilder.lore("&eRounds8» &b" + cachedGame.getRounds());
                items.add(itemBuilder.make());
            }
            Core.get().getLogManager().log(Logger.LogType.INFO, "HOF Successfully setup!");
        });
    }

    public ArrayList<ItemStack> getHofInventoryItems() {
        return hofInventoryItems;
    }
}
