package me.blok601.nightshadeuhc.scenario.cmd;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import me.blok601.nightshadeuhc.event.FinalHealEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DonateHealthCommand implements UHCCommand, Listener {
    private boolean a = false;

    private ScenarioManager scenarioManager;

    public DonateHealthCommand(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "donatehealth"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;

        if (!scenarioManager.getScen("Health Donor").isEnabled()) {
            p.sendMessage(ChatUtils.message("&cHealth donor is not enabled!"));
            return;
        }
        if (!a) {
            p.sendMessage(ChatUtils.message("&cYou cannot donate health until after the final heal!"));
            return;
        }
        if (args.length == 0) {
            p.sendMessage(ChatUtils.message("&cYou must specify a player!"));
            return;

        }
        Player target = Bukkit.getPlayer(args[0]);

        if (p.getWorld() == GameManager.get().getWorld() && p.getGameMode() == GameMode.SURVIVAL) {

            if (target == p) {
                p.sendMessage(ChatUtils.message("&cYou cannot donate health to yourself!"));
                return;

            }

            if (target == null) {
                p.sendMessage(ChatUtils.message("&cYou must specify a valid online player!"));
                return;
            }
            if (args.length == 1) {
                p.sendMessage(ChatUtils.message("&cYou must specify an amount of health to donate!"));
                return;

            }
            try {
                int donate = Integer.parseInt(args[1]);
                if (p.getHealth() <= donate) {
                    p.sendMessage(ChatUtils.message("&cYou cannot donate more health than you have!"));
                    return;
                }
                if (donate < 1) {
                    p.sendMessage(ChatUtils.message("&cYou cannot donate negative health!"));
                    return;
                }
                double healthdiff = target.getMaxHealth() - target.getHealth();

                p.setHealth(p.getHealth() - donate);
                p.sendMessage(ChatUtils.message("&3Successfully Donated Health to " + target.getName() + "!"));


                if (target.getHealth() + (double) donate > target.getMaxHealth()) {
                    double addhealth = donate - healthdiff;
                    target.setMaxHealth(target.getMaxHealth() + addhealth);
                    target.setHealth(target.getMaxHealth());
                    target.sendMessage(ChatUtils.message("&3 Someone donated you " + donate + " health, and thus your max health has increased!!"));

                } else {
                    target.setHealth(target.getHealth() + donate);
                    target.sendMessage(ChatUtils.message("&3 Someone donated you " + donate + " health!"));

                }


            } catch (NumberFormatException e) {
                p.sendMessage(ChatUtils.message("&cAmount of health must be an integer!"));
                return;

            }

        } else {
            p.sendMessage(ChatUtils.message("&cYou must be playing the UHC to donate!"));
            return;

        }
        return;


    }

    @EventHandler
    public void onEvent(FinalHealEvent e) {
        this.a = true;
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public Rank getRequiredRank() {
        return null;
    }

    @Override
    public boolean hasRequiredRank() {
        return false;
    }
}

