package me.blok601.nightshadeuhc.entity.object;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class HelpOP {

    private static ArrayList<HelpOP> helpOPS = Lists.newArrayList();

    private int id;
    private UUID player;
    private String message;

    public HelpOP() {
    }

    public HelpOP(UUID player, String message) {
        this.id = helpOPS.size() + 1;
        this.player = player;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ArrayList<HelpOP> getHelpOPS() {
        return helpOPS;
    }

    public static HelpOP getHelpOP(int check) {
        for (HelpOP helpOP : helpOPS) {
            if (helpOP.getId() == check) {
                return helpOP;
            }
        }
        return null;
    }
}
