package me.blok601.nightshadeuhc.listener.gui;

import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.event.ScenarioDisableEvent;
import me.blok601.nightshadeuhc.event.ScenarioEnableEvent;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.PagedInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Blok on 7/18/2018.
 */
public class ScenarioClick implements Listener  {

//    private static ScenarioClick i = new ScenarioClick();
//    public static ScenarioClick get() { return i; }

    private ScenarioManager scenarioManager;

    public ScenarioClick(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
    }

    private static HashMap<UUID, PagedInventory> users = new HashMap<>();

    public static HashMap<UUID, PagedInventory> getUsers() {
        return users;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        //Get the current scroller inventory the player is looking at, if the player is looking at one.
        if (!users.containsKey(p.getUniqueId())) return;

        if (event.getInventory().getName() == null) return;
        if (event.getInventory().getName().equalsIgnoreCase(ChatColor.stripColor(event.getInventory().getName())))
            return;

        PagedInventory inv = users.get(p.getUniqueId());

        if (!event.getInventory().getName().equalsIgnoreCase(inv.name)) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if (!event.getCurrentItem().hasItemMeta()) return;
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        //If the pressed item was a nextpage button
        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(PagedInventory.nextPageName)) {

            //If there is no next page, don't do anything
            if (inv.currpage >= inv.pages.size() - 1) {
                return;
            } else {
                //Next page exists, flip the page
                inv.currpage += 1;
                p.openInventory(inv.pages.get(inv.currpage));
            }
            //if the pressed item was a previous page button
        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(PagedInventory.previousPageName)) {
            //If the page number is more than 0 (So a previous page exists)
            if (inv.currpage > 0) {
                //Flip to previous page
                inv.currpage -= 1;
                p.openInventory(inv.pages.get(inv.currpage));
            }
        } else {
            ItemStack is = event.getCurrentItem();
            if(event.getInventory().getName().equalsIgnoreCase(ChatColor.GOLD + "Scenarios")) {
                if (is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                    Scenario scenario = scenarioManager.getScen(is);

                    if (scenario == null) return;

                    if (scenario.isEnabled()) {
                        ScenarioDisableEvent scenarioDisableEvent = new ScenarioDisableEvent(scenario, p);
                        UHC.get().getServer().getPluginManager().callEvent(scenarioDisableEvent);
                        if(!scenarioDisableEvent.isCancelled()){
                            scenario.setEnabled(false);
                            scenario.onToggle(false, p);
                            p.sendMessage(ChatUtils.message("&cDisabled &e" + scenario.getName() + "!"));
                        }

                    } else {
                        ScenarioEnableEvent ev = new ScenarioEnableEvent(scenario, p);
                        Bukkit.getServer().getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            scenario.setEnabled(true);
                            scenario.onToggle(true, p);
                            p.sendMessage(ChatUtils.message("&aEnabled &e" + scenario.getName() + "!"));
                        }
                    }
                }
            }

        }

        if (!users.containsKey(p.getUniqueId())) {
            users.put(p.getUniqueId(), inv);
        }

    }

}
