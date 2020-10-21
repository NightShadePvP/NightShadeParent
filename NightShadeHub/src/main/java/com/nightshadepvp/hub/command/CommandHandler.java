package com.nightshadepvp.hub.command;

import com.google.common.collect.Sets;
import com.nightshadepvp.hub.Hub;

import java.util.HashSet;

/**
 * Created by Blok on 2/5/2019.
 */
public class CommandHandler {

    private Hub plugin;

    private static HashSet<HubCommand> commands;

    public CommandHandler(Hub plugin) {
        this.plugin = plugin;

        commands = Sets.newHashSet();
    }

    public HashSet<HubCommand> getCommands() {
        return commands;
    }

    public HubCommand getCommand(String name) {
        return commands.stream().filter(hubCommand -> hubCommand.getLabel().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public HubCommand getCommandByAlias(String alias) {
        for (HubCommand hubCommand : commands) {
            if (hubCommand.getAliases().contains(alias.toLowerCase())) {
                return hubCommand;
            }
        }

        return null;
    }

    public void register(HubCommand hubCommand) {
        hubCommand.setup(plugin);
        commands.add(hubCommand);
    }

    public void setup(){
        register(new HelpCommand());
    }


}
