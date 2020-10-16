package me.blok601.nightshadeuhc.command.player.teams;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.Team;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Blok on 9/24/2017.
 */
public class TeamsCommand implements UHCCommand {

    private ScenarioManager scenarioManager;
    private GameManager gameManager;

    public TeamsCommand(ScenarioManager scenarioManager, GameManager gameManager) {
        this.scenarioManager = scenarioManager;
        this.gameManager = gameManager;
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "teams"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        UHCPlayer uhcPlayer = UHCPlayer.get(s);

        if (!gameManager.isIsTeam()) {
            uhcPlayer.msg(ChatUtils.message("&cIt is not a teams game!"));
            return;
        }

        Scenario scenario = scenarioManager.getScen("Secret Teams");
        if (scenario != null && scenario.isEnabled()) {
            if (!NSPlayer.get(s).hasRank(Rank.TRIAL) && !uhcPlayer.isSpectator()) {
                uhcPlayer.msg(ChatUtils.format(scenario.getPrefix() + "&cYou can't view the teams in Secret Teams!"));
                return;
            }
        }

        if (TeamManager.getInstance().getTeams().size() == 0) {
            uhcPlayer.msg(ChatUtils.message("&cThere are no teams currently!"));
            return;
        }

        uhcPlayer.msg(ChatUtils.format("&5&m-----------------------------------"));
        StringBuilder stringBuilder;
        for (Team team : TeamManager.getInstance().getTeams()) {
            stringBuilder = new StringBuilder();
            for (String string : team.getMembers()) {
                Player member = Bukkit.getPlayer(string);
                if (member == null) {
                    stringBuilder.append("&7" + string);
                } else {
                    stringBuilder.append("&a" + string);
                }

                stringBuilder.append(", ");
            }

            String f = stringBuilder.toString().trim();
            uhcPlayer.msg(ChatUtils.format("&e" + team.getName() + "&8: &5" + f.substring(0, stringBuilder.toString().length() - 1)));
        }
        uhcPlayer.msg(ChatUtils.format("&5&m-----------------------------------"));

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
