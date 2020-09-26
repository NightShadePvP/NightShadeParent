package me.blok601.nightshadeuhc.scoreboard.provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scoreboard.SidebarEntry;
import me.blok601.nightshadeuhc.scoreboard.SidebarProvider;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Blok on 10/14/2019.
 */
public class LobbyProvider implements SidebarProvider {

    List<String> colorsForScenarios;
    private ScenarioManager scenarioManager;
    public LobbyProvider(UHC uhc, GameManager gameManager, ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
        colorsForScenarios = ImmutableList.of(ChatColor.WHITE.toString(), ChatColor.DARK_BLUE.toString(), ChatColor.BLACK.toString(), ChatColor.RED.toString(), ChatColor.YELLOW.toString());
    }
    @Override
    public List<SidebarEntry> getLines(Player p) {
        List<SidebarEntry> lines = new ArrayList<>();
        lines.add(new SidebarEntry(ChatUtils.format("&f&m--------------------" + ChatColor.GOLD.toString())));
        if (GameManager.get().getHost() == null) {
            lines.add(new SidebarEntry(ChatUtils.format("&fHost: &bNone")));
        } else {
            lines.add(new SidebarEntry(ChatUtils.format("&fHost: &b" + GameManager.get().getHost().getName())));
        }
        if (GameManager.get().isIsTeam()) {
            lines.add(new SidebarEntry(ChatColor.LIGHT_PURPLE.toString(), ChatUtils.format("&fTeam Size: &b"), ChatColor.AQUA + "" + (TeamManager.getInstance().isRandomTeams() ? "rTo" + TeamManager.getInstance().getTeamSize() : "cTo" + TeamManager.getInstance().getTeamSize())));
        } else {
            lines.add(new SidebarEntry(ChatColor.GREEN.toString(), ChatUtils.format("&fTeam Size: &b"), ChatColor.AQUA + "FFA"));

        }
        lines.add(new SidebarEntry(ChatColor.BLUE.toString(), ChatUtils.format("&fPlayers: &b"), ChatColor.AQUA + "" + Bukkit.getServer().getOnlinePlayers().size()));
        lines.add(new SidebarEntry(""));
        lines.add(new SidebarEntry(ChatUtils.format("&fScenarios:")));
        lines.addAll(scenNames());
        lines.add(new SidebarEntry(ChatUtils.format("&f&m--------------------")));
        lines.add(new SidebarEntry(ChatUtils.format("&bdiscord.nightshadepvp.com")));
        lines.add(new SidebarEntry(ChatUtils.format("&b@NightShadePvPMC")));


        return lines;
    }

    private List<SidebarEntry> scenNames() {
        if (scenarioManager.getEnabledScenarios().size() == 0) return Lists.newArrayList();
        if (scenarioManager.getEnabledScenarios().contains(scenarioManager.getScen("Mystery Scenarios")))
            return Collections.singletonList(new SidebarEntry(this.colorsForScenarios.get(0), ChatUtils.format("&bMystery Scenarios"), ""));
        ArrayList<SidebarEntry> names = Lists.newArrayList();
        int i = 0;
        if (scenarioManager.getEnabledScenarios().size() <= 3) {
            //scenarioManager.getEnabledScenarios().forEach(scenario -> names.add(new SidebarEntry(ChatUtils.format("  &f↣ &b" + scenario.getName()))));
            for (int count = 0; count < scenarioManager.getEnabledScenarios().size(); count++){
                names.add(new SidebarEntry(this.colorsForScenarios.get(count), ChatUtils.format("  &f↣ &b"), ChatColor.AQUA + scenarioManager.getEnabledScenarios().get(count).getName()));
            }
            return names;
        }

        for (Scenario scen : scenarioManager.getEnabledScenarios()) {
            if (i < 3 && scen.getName().length() < 16) {
                names.add(new SidebarEntry(this.colorsForScenarios.get(i), ChatUtils.format("  &f↣ &b"), ChatColor.AQUA + scen.getName()));
                i++;
            } else if (i >= 3) {
                break;
            }
        }

        names.add(new SidebarEntry(ChatColor.DARK_AQUA.toString(), ChatUtils.format("  &f↣ &b&o" + (scenarioManager.getEnabledScenarios().size() - i) + " more..."), ChatColor.GOLD.toString()));
        return names;
    }
}