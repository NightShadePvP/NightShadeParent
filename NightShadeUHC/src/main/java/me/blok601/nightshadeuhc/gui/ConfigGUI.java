package me.blok601.nightshadeuhc.gui;

import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.utils.ItemBuilder;
import me.blok601.nightshadeuhc.component.Component;
import me.blok601.nightshadeuhc.component.ComponentHandler;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Blok on 11/5/2017.
 */
public class ConfigGUI {

    public ConfigGUI(Player player, GameManager gameManager, ComponentHandler componentHandler) {
        GuiBuilder menu = new GuiBuilder();
        menu.name(ChatUtils.format("&5Game Settings"));
        menu.rows(5);

        ItemBuilder post = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.ORANGE.getWoolData()))
                .name("&6&lMatchpost");
        if (!Core.get().getMatchpost().equalsIgnoreCase("uhc.gg")) {//Its set
            post.lore("&bCurrent &8» &f" + Core.get().getMatchpost());
        }

        ItemBuilder timers = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getWoolData()))
                .name("&2&lTimeleft")
                .lore("&bFinal Heal &8» &f" + gameManager.getFinalHealTime() / 60 + " minutes")
                .lore("&bPvP Time &8» &f" + gameManager.getPvpTime() / 60 + " minutes")
                .lore("&bFirst Shrink Time &8» &f" + gameManager.getBorderTime() / 60 + " minutes")
                .lore("&bMeetup Time &8» &f" + gameManager.getMeetupTime() / 60 + " minutes");

        ItemStack mining = new ItemBuilder(Material.IRON_PICKAXE)
                .name(ChatUtils.format("&6Mining Information"))
                .lore(ChatUtils.format("&bRollercoastering &8» &aAllowed"))
                .make();

        ItemStack apples = new ItemBuilder(Material.APPLE)
                .name(ChatUtils.format("&6Apples"))
                .lore(ChatUtils.format("&bApple rates &8» &f" + GameManager.get().getAppleRates() + "%")).make();

        ItemStack healing = new ItemBuilder(Material.GOLDEN_APPLE)
                .name(ChatUtils.format("&6Healing"))
                .lore(ChatUtils.format("&bGolden Heads &8» &fHeal 4 hearts"))
                .lore(ChatUtils.format("&bHealth Potions &8» &aOff"))
                .make();

        ItemStack potions = new ItemBuilder(Material.POTION)
                .name(ChatUtils.format("&6Potions"))
                .lore(ChatUtils.format("&bNether &8» &cDisabled"))
                .lore(ChatUtils.format("&bTier 2 Potions» &cOff"))
                .make();

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        me.blok601.nightshadeuhc.util.ItemBuilder newSkull = new me.blok601.nightshadeuhc.util.ItemBuilder(skull);
        newSkull.name(ChatUtils.format("&b&lHost"))
                .lore(ChatUtils.format("&bHost &8» &e" + GameManager.get().getHost().getName()));
        newSkull.skullOwner(GameManager.get().getHost().getName());

        ItemStack server = new ItemBuilder(Material.PAPER).name(ChatUtils.format("&5&lServer Information"))
                .lore(ChatUtils.format("&bOwner &8» &fBlok"))
                .lore(ChatUtils.format("&bProvider &8» &fOVH"))
                .lore(ChatUtils.format("&bDevelopers &8» &fBL0K and RJ"))
                .lore(ChatUtils.format("&bWebsite &8» &fwww.nightshadepvp.com"))
                .lore(ChatUtils.format("&bTwitter &8» &f@NightShadePVPMC"))
                .lore(ChatUtils.format("&bDiscord &8» &fdiscord.nightshadepvp.com"))
                .make();

        menu.item(3, post.make());
        menu.item(4, newSkull.make());
        menu.item(5, timers.make());
        menu.item(11, mining);
        menu.item(12, apples);
        menu.item(14, healing);
        menu.item(15, potions);
        menu.item(13, server);


        int index = 18;

        for (Component component : componentHandler.getComponents()){
            menu.item(index, new ItemBuilder(component.getMaterial())
                    .name("&b" + component.getName())
                    .lore(component.isEnabled() ? "&aEnabled" : "&cDisabled").make());
            index++;
        } //Should add all the toggleable options to the gui

        player.openInventory(menu.make());
    }
}
