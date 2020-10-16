package me.blok601.nightshadeuhc.command.player;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.utils.fanciful.FancyMessage;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Created by Blok on 3/7/2018.
 */
public class ScenariosCommand implements UHCCommand{

    private ScenarioManager scenarioManager;

    public ScenariosCommand(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "scenarios"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        Collection<Scenario> scenarios = scenarioManager.getEnabledScenarios();

        if (scenarios.size() == 0) {
            p.sendMessage(ChatUtils.message("&cThere are no enabled scenarios"));
            return;
        }

        int index = 0;
        int last = scenarios.size() - 1;
        FancyMessage fancyMessage = new FancyMessage(ChatUtils.format("&eEnabled Scenarios&8» "));
        for (Scenario scenario : scenarios){
            //builder.append("&3").append(scenario.getName()).append("&8,");
            fancyMessage.then(scenario.getName() + (index == last ? "" : ", ")).color(ChatColor.AQUA).tooltip(scenario.getDesc());
            index++;
        }


        //p.sendMessage(ChatUtils.format("&eEnabled Scenarios&8» " + builder.toString().substring(0, builder.toString().length())));
        fancyMessage.send(p);
    }

    @Override
    public boolean playerOnly() {
        return false;
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
