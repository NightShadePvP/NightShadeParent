package me.blok601.nightshadeuhc.gui.setup;

import me.blok601.nightshadeuhc.utils.ChatUtils;
import me.blok601.nightshadeuhc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Created by Blok on 12/9/2018.
 */
public class WorldGUI {

    public WorldGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatUtils.format("&5World Setup"));

        ItemBuilder normal = new ItemBuilder(Material.GRASS)
                .name("&6Overworld Creation")
                .lore("&7&o(&6i&7) &6Click to start the game world creation process");

        ItemBuilder nether = new ItemBuilder(Material.NETHERRACK)
                .name("&6Nether Creation")
                .lore("&7&o(&6i&7) &6Click to start the nether world creation process");
    }
}
