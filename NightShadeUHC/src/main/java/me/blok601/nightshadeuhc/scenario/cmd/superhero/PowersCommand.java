package me.blok601.nightshadeuhc.scenario.cmd.superhero;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scenario.SuperHeroesTeamScenario;
import me.blok601.nightshadeuhc.scenario.SuperheroesScenario;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Set;
import java.util.UUID;

public class PowersCommand implements UHCCommand {

    private ScenarioManager scenarioManager;

    public PowersCommand(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "powers"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        UHCPlayer uhcPlayer = UHCPlayer.get(s);
        if(!uhcPlayer.isSpectator()){
            uhcPlayer.msg(ChatUtils.message("&cYou must be a spectator to view everyone's power"));
            return;
        }
        Scenario scenario = scenarioManager.getScen("SuperHeroes");
        if(scenario.isEnabled()){
            // normal powers
            Set<UUID> users = SuperheroesScenario.powers.keySet();
            uhcPlayer.msg(ChatUtils.message("&b&m---------------------------"));
            users.forEach(uuid -> uhcPlayer.msg("&b" + Bukkit.getOfflinePlayer(uuid).getName() + ": " + SuperheroesScenario.powers.get(uuid).getName()));
            uhcPlayer.msg(ChatUtils.message("&b&m---------------------------"));
        }else if(scenarioManager.getScen("Superheroes Teams").isEnabled()){
            scenario = (scenarioManager.getScen("Superheroes Teams"));
            Set<UUID> users = SuperheroesScenario.powers.keySet();
            uhcPlayer.msg(ChatUtils.message("&b&m---------------------------"));
            users.forEach(uuid -> uhcPlayer.msg("&b" + Bukkit.getOfflinePlayer(uuid).getName() + ": " + SuperheroesScenario.powers.get(uuid).getName()));
            uhcPlayer.msg(ChatUtils.message("&b&m---------------------------"));
        }else{
            uhcPlayer.msg(ChatUtils.message("&cSuperheroes or Superheroes Teams must be enabled to view powers!"));
            return;
        }
    }

    @Override
    public boolean playerOnly() {
        return false;
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
