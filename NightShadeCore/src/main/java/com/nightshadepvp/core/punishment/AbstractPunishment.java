package com.nightshadepvp.core.punishment;

import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Blok on 8/24/2018.
 */
public abstract class AbstractPunishment {

    private String name;
    private Material material;
    private OffenseType offenseType;
    private HashMap<Integer, Punishment> children;
    private ItemStack itemStack;


    public AbstractPunishment(String name, Material material, OffenseType offenseType) {
        this.name = name;
        this.material = material;
        this.offenseType = offenseType;
        this.children = new HashMap<>();
        this.itemStack = null;
    }

    public AbstractPunishment(String name, ItemStack itemStack, OffenseType offenseType) {
        this.name = name;
        this.itemStack = itemStack;
        this.offenseType = offenseType;
        this.children = new HashMap<>();
        this.material = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    ItemStack getItemStack() {
        if(this.itemStack == null){
            return new ItemBuilder(material).name("&5" + this.name).lore("&eClick to view the punishment options for " + this.name).make();
        }

        return new ItemBuilder(itemStack).name("&5" + this.name).lore("&eClick to view the punishment options for " + this.name).make();
    }

    public OffenseType getOffenseType() {
        return offenseType;
    }

    public void addChild(Punishment child, int slot) {
        this.children.put(slot, child);
    }

    void click(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getType() == Material.AIR) return;

        Inventory childInventory = Bukkit.createInventory(null, 54, getName());
        for (Map.Entry<Integer, Punishment> entry : this.children.entrySet()) {
            childInventory.setItem(entry.getKey(), new ItemBuilder(entry.getValue().getItemStack()).make());
        }

        childInventory.setItem(45, PunishmentHandler.getInstance().getBackButton());

        p.openInventory(childInventory);
    }

    public Punishment getChild(int slot) {
        return this.children.getOrDefault(slot, null);
    }

    public Punishment getChild(ItemStack stack) {
        return this.children.values().stream().filter(punishment -> punishment.getItemStack().equals(stack)).findAny().orElse(null);
    }


}
