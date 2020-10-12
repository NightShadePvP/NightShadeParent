package com.nightshadepvp.core.punishment;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by Blok on 8/25/2018.
 */
public class Punishment extends AbstractPunishment {

    private List<String> commands;
    private AbstractPunishment parent;
    private PunishmentType type;
    private Rank neededRank;

    private ItemStack childStack;

    public Punishment(String name, ItemStack itemStack, AbstractPunishment parent, List<String> commands, PunishmentType type, OffenseType offenseType, Rank neededRank) {
        super(name, itemStack, offenseType);
        this.commands = commands;
        this.type = type;
        this.parent = parent;
        this.neededRank = neededRank;
        this.childStack = itemStack;
    }
    public Punishment(String name, ItemStack itemStack, AbstractPunishment parent, List<String> commands, PunishmentType type, OffenseType offenseType) {
        super(name, itemStack, offenseType);
        this.commands = commands;
        this.type = type;
        this.parent = parent;
        this.childStack = itemStack;
        this.neededRank = Rank.TRIAL;
    }

    public void execute(Player staff){
        if(!PunishmentHandler.getInstance().getPunishing().containsKey(staff)){
            return;
        }

        if(!NSPlayer.get(staff).hasRank(this.neededRank)){
            staff.sendMessage(ChatUtils.message("&cYou require the " + this.neededRank.getPrefix() + "&crank to punish for &e" + this.getName()));
            return;
        }

        String name = PunishmentHandler.getInstance().getPunishing().get(staff);
        Player target = Bukkit.getPlayer(name);
        if(target == null){
            staff.sendMessage(ChatUtils.message("&eSuccessfully executed punishment."));
            for (String cmd : this.commands) {
                staff.chat("/" + cmd.replaceAll("%player%", name));
                staff.closeInventory();
            }
        }else{
            staff.sendMessage(ChatUtils.message("&eSuccessfully punished&8: " + target.getName()));
            if (type == PunishmentType.DQ || type == PunishmentType.BAN) {
                target.damage(0); //Damage effect
                target.setHealth(0);
                if(type == PunishmentType.DQ){
                    target.sendMessage(ChatUtils.message("&4You have been DQed for " + this.parent.getName()));
                }
            }

            for (String cmd : this.commands) {
                staff.chat("/" + cmd.replaceAll("%player%", name));
                staff.closeInventory();

            }
        }

        PunishmentHandler.getInstance().getPunishing().remove(staff);
    }

    public Rank getNeededRank() {
        return neededRank;
    }

    @Override
    ItemStack getItemStack() {
        return this.childStack;
    }
}
