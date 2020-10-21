package com.nightshadepvp.hub;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;
import com.nightshadepvp.hub.command.CommandHandler;
import com.nightshadepvp.hub.command.HubCommand;
import com.nightshadepvp.hub.listener.PlayerListener;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPubSub;

import java.util.List;

public final class Hub extends JavaPlugin {

    private CommandHandler commandHandler;
    private Core core;

    private int recentUHCPlayerCount;
    private boolean uhcOnline;
    private int recentTournamentPlayerCount;
    private boolean tournamentOnline;

    @Override
    public void onEnable() {
        this.core = Core.get();
        core.getLogManager().log(Logger.LogType.INFO, "Starting Hub....");
        long now = System.currentTimeMillis();
        commands();
        listeners();
        this.recentTournamentPlayerCount = 0;
        this.recentUHCPlayerCount = 0;
        this.uhcOnline = false;
        this.tournamentOnline = false;
        setupPubSub();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (this.commandHandler == null) {
            sender.sendMessage("Hub is not currently accepting commands! Please wait a few moments and try again!");
            return false;
        }

        for (HubCommand hubCommand : this.commandHandler.getCommands()) {
            List<String> possibleNames = Lists.newArrayList();
            possibleNames.add(hubCommand.getLabel());
            possibleNames.addAll(hubCommand.getAliases());

            //Got all possible names
            for (String cmdName : possibleNames) {
                if (command.getName().equalsIgnoreCase(cmdName)) {

                    if (!hubCommand.isAllowConsole()) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(ChatUtils.message("&cThis is a player only command!"));
                            return false;
                        }
                    }

                    if (sender instanceof ConsoleCommandSender && hubCommand.isAllowConsole()) {
                        try {
                            hubCommand.run(sender, args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }

                    Player p = (Player) sender;
                    NSPlayer user = NSPlayer.get(p.getUniqueId());
                    if (!(user.hasRank(hubCommand.getRequiredRank()))) {
                        p.sendMessage(com.nightshadepvp.core.utils.ChatUtils.message("&cYou require the " + hubCommand.getRequiredRank().getPrefix() + "&crank to do this command!"));
                        return false;
                    }

                    try {
                        hubCommand.run(sender, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;

                }
            }
        }

        return false;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    private void commands() {
        this.commandHandler = new CommandHandler(this);
        this.commandHandler.setup();
        this.commandHandler.getCommands().forEach(hubCommand -> core.getLogManager().log(Logger.LogType.INFO, "Registered command: " + hubCommand.getLabel()));
    }

    private void listeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    private void setupPubSub() {
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            core.getJedis().subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    if (channel.equalsIgnoreCase("serverData")) {
                        JsonParser parser = new JsonParser();
                        JsonObject object = parser.parse(message).getAsJsonObject();

                        //name, count, online

                        String name = object.get("serverName").getAsString();
                        boolean status = object.get("online").getAsBoolean();
                        int playercount = object.get("playerCount").getAsInt();

                        if(name.equalsIgnoreCase("UHC")){
                            uhcOnline = status;
                            recentUHCPlayerCount = playercount;
                        }else if(name.equalsIgnoreCase("Tournament")){
                            tournamentOnline = status;
                            recentTournamentPlayerCount = playercount;
                        }
                    }
                }
            }, "serverData");
        });
    }

    public int getRecentUHCPlayerCount() {
        return recentUHCPlayerCount;
    }

    public void setRecentUHCPlayerCount(int recentUHCPlayerCount) {
        this.recentUHCPlayerCount = recentUHCPlayerCount;
    }

    public boolean isUhcOnline() {
        return uhcOnline;
    }

    public void setUhcOnline(boolean uhcOnline) {
        this.uhcOnline = uhcOnline;
    }

    public int getRecentTournamentPlayerCount() {
        return recentTournamentPlayerCount;
    }

    public void setRecentTournamentPlayerCount(int recentTournamentPlayerCount) {
        this.recentTournamentPlayerCount = recentTournamentPlayerCount;
    }

    public boolean isTournamentOnline() {
        return tournamentOnline;
    }

    public void setTournamentOnline(boolean tournamentOnline) {
        this.tournamentOnline = tournamentOnline;
    }
}
