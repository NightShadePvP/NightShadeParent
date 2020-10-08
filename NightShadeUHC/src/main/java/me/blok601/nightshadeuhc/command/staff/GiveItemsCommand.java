package me.blok601.nightshadeuhc.command.staff;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.utils.PlayerUtils;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItemsCommand implements UHCCommand {
    private ScenarioManager scenarioManager;

    public GiveItemsCommand(ScenarioManager s) {
        this.scenarioManager = s;
    }


    @Override
    public String[] getNames() {
        return new String[]{
                "giveitems"
        };
    }
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(ChatUtils.message("&cThat player couldn't be found!"));
            return;
        }
        else {
            Player player = target;
            p.sendMessage(ChatUtils.message("&eSuccesfully administered starter items to target player!"));
            for (Scenario scenario : scenarioManager.getEnabledScenarios()) {
                if (scenario instanceof StarterItems) {

                    StarterItems starterItems = (StarterItems) scenario;
                    if (scenario.getName().equalsIgnoreCase("Infinite Enchanter") ||scenario.getName().equalsIgnoreCase("GoneFishin") ) {
                        player.setLevel(27000);
                    }

                    //UHCPlayerColl.get().getAllPlaying().forEach(uhcPlayer -> PlayerUtils.giveBulkItems(uhcPlayer.getPlayer(), starterItems.getStarterItems
                    for (ItemStack stack : starterItems.getStarterItems()) {
                        PlayerUtils.giveItem(stack, player);
                    }
                }
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
