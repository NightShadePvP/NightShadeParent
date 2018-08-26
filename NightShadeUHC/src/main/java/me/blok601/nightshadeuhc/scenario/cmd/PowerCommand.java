package me.blok601.nightshadeuhc.scenario.cmd;

import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.GameState;
import me.blok601.nightshadeuhc.commands.CmdInterface;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scenario.SuperheroesScenario;
import me.blok601.nightshadeuhc.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Blok on 7/17/2018.
 */
public class PowerCommand implements CmdInterface{
    @Override
    public String[] getNames() {
        return new String[]{
                "power"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        if(!ScenarioManager.getScen("Superheroes").isEnabled()){
            p.sendMessage(ChatUtils.message("&cSuperheroes isn't enabled!"));
            return;
        }

        if(!GameState.gameHasStarted()){
            p.sendMessage(ChatUtils.message("&cThe game hasn't started yet!"));
            return;
        }

        if(!SuperheroesScenario.powers.containsKey(p.getUniqueId())) {
            p.sendMessage(ChatUtils.message("&cYou haven't been assigned a power! Ask the host to assign you one!"));
            return;
        }

        SuperheroesScenario.SuperHeroType type = SuperheroesScenario.powers.get(p.getUniqueId());
        p.sendMessage(ChatUtils.message("&eYour super power is: &3" + type.getName()));
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
