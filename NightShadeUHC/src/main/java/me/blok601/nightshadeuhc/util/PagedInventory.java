package me.blok601.nightshadeuhc.util;

import me.blok601.nightshadeuhc.listener.gui.ScenarioClick;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class PagedInventory {

    public ArrayList<Inventory> pages = new ArrayList<Inventory>();
    public UUID id;
    public String name;
    public int currpage = 0;

    //Running this will open a paged inventory for the specified player, with the items in the arraylist specified.
    public PagedInventory(ArrayList<ItemStack> items, String name, Player p) {
        this.id = UUID.randomUUID();
        this.name = name;
//create new blank page
        Inventory page = getBlankPage(name);
        //According to the items in the arraylist, add items to the ScrollerInventory
        for (int i = 0; i < items.size(); i++) {
            //If the current page is full, add the page to the inventory's pages arraylist, and create a new page to add the items.
            if (page.firstEmpty() == 46) {
                pages.add(page);
                page = getBlankPage(name);
                page.addItem(new ItemBuilder(items.get(i)).make());
            } else {
//Add the item to the current page as per normal
                page.addItem(new ItemBuilder(items.get(i)).make());
            }
        }
        pages.add(page);

        //ChestGui chestGui = ChestGui.getCreative(pages.get(currpage));

        //open page 0 for the specified player
        p.openInventory(pages.get(currpage));
        ScenarioClick.getUsers().put(p.getUniqueId(), this);
    }


    public static final String nextPageName = ChatColor.AQUA + "Next Page";
    public static final String previousPageName = ChatColor.AQUA + "Previous Page";

    //This creates a blank page with the next and prev buttons
    private Inventory getBlankPage(String name) {
        Inventory page = Bukkit.createInventory(null, 54, name);

        ItemStack nextpage = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
        ItemMeta meta = nextpage.getItemMeta();
        meta.setDisplayName(nextPageName);
        nextpage.setItemMeta(meta);

        ItemStack prevpage = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 2);
        meta = prevpage.getItemMeta();
        meta.setDisplayName(previousPageName);
        prevpage.setItemMeta(meta);


        page.setItem(53, nextpage);
        page.setItem(45, prevpage);
        return page;
    }
}