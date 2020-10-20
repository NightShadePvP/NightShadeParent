package me.blok601.nightshadeuhc.scenario.cmd;

import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.scenario.OPsVsWorldScenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OPsVsWorldCommand implements UHCCommand {

    private ScenarioManager scenarioManager;

    public OPsVsWorldCommand(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "opsvsworld"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player player = (Player) s;
        OPsVsWorldScenario scenario = (OPsVsWorldScenario) scenarioManager.getScen("OPs vs World");
        if (!scenario.isEnabled()) {
            scenario.sendMessage(player, "&cOPs vs World is not enabled!");
            return;
        }


        if (args.length == 0) {
            scenario.sendMessage(player, "&eCommands for OPs vs World:");
            scenario.sendMessage(player, "&e/opvsworld addop <player>");
            scenario.sendMessage(player, "&e/opsvsworld setinv");
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("setinv")) {
                scenario.setArmor(player.getInventory().getArmorContents());
                scenario.setItems(player.getInventory().getContents());
                scenario.sendMessage(player, "&bUpdated the inventory!");
                return;
            } else {
                scenario.sendMessage(player, "&eCommands for OPs vs World:");
                scenario.sendMessage(player, "&e/opvsworld addop <player>");
                scenario.sendMessage(player, "&e/opsvsworld setinv");
                return;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("addop")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    //Online player
                    scenario.getOps().add(target.getUniqueId());
                    scenario.sendMessage(player, "&bAdded &f" + target.getName() + " &bto the ops list!");
                    return;
                }

                //offline player
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                if (offlinePlayer == null) {
                    scenario.sendMessage(player, "&cThat player couldn't be found!");
                    return;
                }

                scenario.getOps().add(offlinePlayer.getUniqueId());
                scenario.sendMessage(player, "&bAdded &f" + offlinePlayer.getName() + " &bto the ops list!");
                return;
            } else if (args[0].equalsIgnoreCase("removeop")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    //Online player
                    if (scenario.getOps().remove(target.getUniqueId())) {
                        scenario.sendMessage(player, "&bRemoved &f" + target.getName() + " &bfrom the ops list!");
                        return;
                    }

                    scenario.sendMessage(player, "&cThat player is not on the ops list!");
                    return;
                }
                //offline player
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                if (offlinePlayer == null) {
                    scenario.sendMessage(player, "&cThat player couldn't be found!");
                    return;
                }

                if (scenario.getOps().remove(offlinePlayer.getUniqueId())) {
                    scenario.sendMessage(player, "&bRemoved &f" + offlinePlayer.getName() + " &bfrom the ops list!");
                    return;
                }

                scenario.sendMessage(player, "&cThat player is not on the ops list!");
                return;

            } else {
                scenario.sendMessage(player, "&eCommands for OPs vs World:");
                scenario.sendMessage(player, "&e/opvsworld addop <player>");
                scenario.sendMessage(player, "&e/opsvsworld setinv");
                return;
            }
        }
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public Rank getRequiredRank() {
        return Rank.TRIAL;
    }

    @Override
    public boolean hasRequiredRank() {
        return true;
    }
}
