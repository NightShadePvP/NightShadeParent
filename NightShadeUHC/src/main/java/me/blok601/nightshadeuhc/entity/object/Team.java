package me.blok601.nightshadeuhc.entity.object;

import com.google.common.collect.Lists;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.scoreboard.PlayerBoard;
import me.blok601.nightshadeuhc.scoreboard.ScoreboardHandler;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Blok on 9/24/2017.
 */
public class Team {

    private String name;
    private ArrayList<String> members;
    private String prefix;

    private UUID mole;

    private UUID melee;
    private UUID bow;
    private String color;

    public Team(String name, Player player, String color) {
        this.name = name;
        this.members = new ArrayList<>();
        this.color = color;
        if(player != null){
            this.members.add(player.getName());
        }

    }

    public Team(String name, String color){
        this.color = color;

        this.name = name;
        this.members = new ArrayList<>();
    }

    public Team(String name, ArrayList<String> members, String color){
        this.color = color;

        this.name = name;
        this.members = members;
    }


    public String getName() {
        return name;
    }

    public String getColor() {return color;}

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeader() {
        return this.members.get(0);
    }

    public void message(String msg){
        Player player;
        for (String string : this.members){
            player = Bukkit.getPlayer(string);
            if(player == null) continue;

            player.sendMessage(ChatUtils.format("&3Team&8 » &e" + msg));
        }
    }

    public void removeMember(Player player){
        if(this.members.contains(player.getName())) {
            this.removeColor(player);
            this.getMembers().remove(player.getName());
        }

    }

    public void addMember(Player player){
        this.members.add(player.getName());
    }

    public void addMember(String name) {
        this.members.add(name);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public UUID getMole() {
        return mole;
    }

    public void setMole(UUID mole) {
        this.mole = mole;
    }

    public UUID getMelee() {
        return melee;
    }

    public void setMelee(UUID melee) {
        this.melee = melee;
    }

    public UUID getBow() {
        return bow;
    }

    public void setBow(UUID bow) {
        this.bow = bow;
    }

    public void color() {
        this.color = ChatUtils.generateTeamColor();
        ScoreboardHandler scoreboardManager = UHC.getScoreboardManager();
        Scoreboard scoreboard;
        for (Map.Entry<UUID, PlayerBoard> playerPlayerScoreboardEntry : scoreboardManager.getPlayerBoards().entrySet()) {
            if (playerPlayerScoreboardEntry.getValue() == null) continue;
            if (playerPlayerScoreboardEntry.getKey() == null) continue;

            scoreboard = playerPlayerScoreboardEntry.getValue().getScoreboard();
            if (scoreboard.getTeam(this.getName()) != null) {
                scoreboard.getTeam(this.getName()).unregister();
            }

            org.bukkit.scoreboard.Team t = scoreboard.registerNewTeam(this.getName());
            t.setPrefix(color);
            for (String mem : getMembers()) {
                t.addEntry(mem);
            }
        }
    }


    public void setColor() {
        if(this.color == null) {
            getMembers().forEach(player -> {
                Player onlinePlayer = Bukkit.getPlayer(player);

                onlinePlayer.setPlayerListName(this.color + onlinePlayer.getName());
            });

        } else {
            getMembers().forEach(player -> {
                Player onlinePlayer = Bukkit.getPlayer(player);

                onlinePlayer.setPlayerListName(this.color + onlinePlayer.getName());
            });
        }
    }

    public void removeColor() {
        if(this.color == null) {
            getMembers().forEach(player -> {
                Player onlinePlayer = Bukkit.getPlayer(player);

                onlinePlayer.setPlayerListName("§f" + onlinePlayer.getName());
            });

        } else {
            getMembers().forEach(player -> {
                Player onlinePlayer = Bukkit.getPlayer(player);

                onlinePlayer.setPlayerListName("§f" + onlinePlayer.getName());
            });
        }
    }
    public void removeColor(Player p) {
        ScoreboardHandler scoreboardManager = UHC.getScoreboardManager();
        Scoreboard scoreboard;

        for (Map.Entry<UUID, PlayerBoard> playerPlayerScoreboardEntry : scoreboardManager.getPlayerBoards().entrySet()) {
            if (playerPlayerScoreboardEntry.getKey() == null) continue;
            if (playerPlayerScoreboardEntry.getValue() == null) continue;
            scoreboard = playerPlayerScoreboardEntry.getValue().getScoreboard();
            org.bukkit.scoreboard.Team team = scoreboard.getPlayerTeam(p);
            if(team == null) continue;
            team.removePlayer(p);
        }
    }

    public void scheduleRemoval(String name) {
        for (String n : this.getMembers()) {
            if (n.equalsIgnoreCase(name)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        getMembers().remove(n);
                    }
                }.runTaskLater(UHC.get(), 7);
            }
        }
    }

    public ArrayList<Player> getOnlineMembers(){
        ArrayList<Player> toReturn = Lists.newArrayList();
        Player player;
        for (String name : this.members){
            player = Bukkit.getPlayer(name);
            if(player == null){
                continue;
            }

            toReturn.add(player);
        }

        return toReturn;
    }

    public ArrayList<Player> getOnlineAliveMembers(){
        ArrayList<Player> toReturn = Lists.newArrayList();
        for (Player player : getOnlineMembers()){
            if(PlayerUtils.isPlaying(player)){
                toReturn.add(player);
            }
        }

        return toReturn;
    }

}
