package me.blok601.nightshadeuhc.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nightshadepvp.core.utils.PacketUtils;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.CachedColor;
import me.blok601.nightshadeuhc.entity.object.Team;
import me.blok601.nightshadeuhc.scoreboard.PlayerBoard;
import me.blok601.nightshadeuhc.scoreboard.ScoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Blok on 9/24/2017.
 */
public class TeamManager {

    private boolean teamManagement = false;
    private int teamSize = 2;
    private boolean teamFriendlyFire = true;
    private boolean rvb = false;
    private int rvbScatterType = 1; //1 - Teams, 0 is solo
    private boolean randomTeams = false;
    private static HashSet<CachedColor> colors;//storing player names because teams don't store UUIDs ;(
    private List<String> possibleColors;

    private static TeamManager ourInstance = new TeamManager();

    public static TeamManager getInstance() {
        return ourInstance;
    }

    private TeamManager() {

    }

    public void setup() {
        colors = Sets.newHashSet();
        this.teamManagement = false;
        this.teamSize = 2;
        this.teamFriendlyFire = true;
        this.rvb = false;
        this.rvbScatterType = 1;
        this.randomTeams = false;

        this.possibleColors = Lists.newArrayList("§1", "§2", "§3", "§4", "§5", "§6", "§a", "§b", "§c", "§d", "§e", "§9");

        List<String> temp = Lists.newArrayList();
        temp.addAll(possibleColors.stream().map(lColor -> lColor + "§o").collect(Collectors.toList()));
        temp.addAll(possibleColors.stream().map(lColor -> lColor + "§n").collect(Collectors.toList()));
        temp.addAll(possibleColors.stream().map(lColor -> lColor + "§m").collect(Collectors.toList()));
        temp.addAll(possibleColors.stream().map(lColor -> lColor + "§l").collect(Collectors.toList()));
        temp.addAll(possibleColors.stream().map(lColor -> lColor + "§m§n").collect(Collectors.toList()));
        temp.addAll(possibleColors.stream().map(lColor -> lColor + "§o§n").collect(Collectors.toList()));
        temp.addAll(possibleColors.stream().map(lColor -> lColor + "§o§m").collect(Collectors.toList()));
        temp.addAll(possibleColors.stream().map(lColor -> lColor + "§o§n§m").collect(Collectors.toList()));
        temp.addAll(possibleColors.stream().map(lColor -> lColor + "§l§n").collect(Collectors.toList()));
        temp.addAll(possibleColors.stream().map(lColor -> lColor + "§l§m").collect(Collectors.toList()));
        temp.addAll(possibleColors.stream().map(lColor -> lColor + "§l§o").collect(Collectors.toList()));
        temp.addAll(possibleColors.stream().map(lColor -> lColor + "§l§o§n").collect(Collectors.toList()));
        this.possibleColors = Lists.newArrayList();

        possibleColors.addAll(temp);

        possibleColors.remove("§7§o");

        possibleColors.addAll(temp);
        Collections.shuffle(possibleColors); //Have all good color combos
    }

    private ArrayList<Team> teams = new ArrayList<>();

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void addTeam(Team team) {
        this.teams.add(team);
    }

    public void removeTeam(Team team) {
        for (String sl : team.getMembers()) {
            Player p1 = Bukkit.getPlayer(sl);
            team.removeColor(p1);
        }
        this.teams.remove(team);
    }

    public boolean isTeam(String string) {
        return getTeam(string) != null;
    }

    public Team getTeam(String name) {
        for (Team team : this.teams) {
            if (team.getName().equalsIgnoreCase(name)) return team;
        }

        return null;
    }

    public Team getTeam(Player player) {
        return getTeambyPlayerOnTeam(player.getName());
    }


    public Team getTeambyPlayerOnTeam(String name){
        for (Team team : this.teams){
            for (String member : team.getMembers()){
                if(member.toLowerCase().equalsIgnoreCase(name.toLowerCase())){
                    return team;
                }
            }
        }

        return null;
    }

    public Team getTeamByPlayerUUIDOnTeam(UUID uuid) {
        return getTeambyPlayerOnTeam(PacketUtils.getNameFromUUID(uuid));
    }

    public boolean isTeamManagement() {
        return teamManagement;
    }

    public void setTeamManagement(boolean teamManagement) {
        this.teamManagement = teamManagement;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public boolean isTeamFriendlyFire() {
        return teamFriendlyFire;
    }

    public void setTeamFriendlyFire(boolean teamFriendlyFire) {
        this.teamFriendlyFire = teamFriendlyFire;
    }

    public boolean isRvB() {
        return rvb;
    }

    public void setRvB(boolean rvB) {
        rvB = rvB;
    }

    public int getRvBScatterType() {
        return rvbScatterType;
    }

    public void setRvBScatterType(int rvBScatterType) {
        rvBScatterType = rvBScatterType;
    }

    public boolean isRandomTeams() {
        return randomTeams;
    }

    public void setRandomTeams(boolean randomTeams) {
        this.randomTeams = randomTeams;
    }

    public void resetTeams(){
        ScoreboardHandler scoreboardManager = UHC.getScoreboardManager();
        for (Team t : TeamManager.getInstance().getTeams() ) {
            t.removeColor();
        }
        TeamManager.getInstance().getTeams().clear();
        Scoreboard scoreboard;

        for (Map.Entry<UUID, PlayerBoard> playerPlayerScoreboardEntry : scoreboardManager.getPlayerBoards().entrySet()) {
            if (playerPlayerScoreboardEntry.getKey() == null) continue;
            if (playerPlayerScoreboardEntry.getValue() == null) continue;
            scoreboard = playerPlayerScoreboardEntry.getValue().getScoreboard();
            for (org.bukkit.scoreboard.Team team : scoreboard.getTeams()){
                if(team == null) continue;

                if(team.getName().startsWith("UHC")){
                    team.unregister();
                }

                if(team.getScoreboard().getTeam("RED") == null && team.getScoreboard().getTeam("BLUE") == null) continue;
                if(team.getName().equalsIgnoreCase("RED") || team.getName().equalsIgnoreCase("BLUE")){
                    team.unregister();
                }
            }
        }
    }

    public void colorAllTeams() {
        for (Team team : this.teams) {
            team.color();
        }
    }

    public static HashSet<CachedColor> getCachedColors() {
        return colors;
    }

    public void updateSpectatorTeam() {
        ScoreboardHandler scoreboardManager = UHC.getScoreboardManager();
        UHCPlayerColl.get().getAllOnline().forEach(uhcPlayer -> {
            Player player = uhcPlayer.getPlayer();
            PlayerBoard playerScoreboard = scoreboardManager.getPlayerBoard(player.getUniqueId());
            if (playerScoreboard.getScoreboard().getTeam("spec") != null) {
                playerScoreboard.getScoreboard().getTeam("spec").unregister(); //Removed all of those
            }


            org.bukkit.scoreboard.Team specTeam = playerScoreboard.getScoreboard().registerNewTeam("spec");
            specTeam.setPrefix(ChatColor.GRAY + "" + ChatColor.ITALIC);

            UHCPlayerColl.get().getSpectators().forEach(uhcPlayer1 -> {
                specTeam.addEntry(uhcPlayer1.getName());
                CachedColor cachedColor = new CachedColor(uhcPlayer1.getName());
                cachedColor.setColor(ChatColor.GRAY + "" + ChatColor.ITALIC);
                cachedColor.setPlayer(uhcPlayer1.getName());
                getCachedColors().add(cachedColor);
            });
        });
    }

    public List<String> getPossibleColors() {
        return possibleColors;
    }

    public Collection<CachedColor> getCachedColors(Player player) {
        HashSet<CachedColor> temp = Sets.newHashSet();
        for (CachedColor cachedColor : this.getCachedColors()) {
            if (cachedColor.getId().equalsIgnoreCase(player.getName())) {
                temp.add(cachedColor);
            }

            if (cachedColor.getPlayer().equalsIgnoreCase(player.getName())) {
                temp.add(cachedColor);
            }
        }

        return temp;
    }

    public Collection<CachedColor> getCachedColors(String id) {
        HashSet<CachedColor> temp = Sets.newHashSet();
        for (CachedColor cachedColor : getCachedColors()) {
            if (cachedColor.getId().equalsIgnoreCase(id)) {
                temp.add(cachedColor);
            }
        }

        return temp;
    }
}
