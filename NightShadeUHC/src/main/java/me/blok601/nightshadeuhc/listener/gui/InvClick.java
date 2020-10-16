package me.blok601.nightshadeuhc.listener.gui;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.fanciful.FancyMessage;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.gui.leaderboards.LeaderBoardMainGUI;
import me.blok601.nightshadeuhc.gui.leaderboards.StatsGUI;
import me.blok601.nightshadeuhc.gui.leaderboards.TopKillsGUI;
import me.blok601.nightshadeuhc.gui.leaderboards.TopWinnersGUI;
import me.blok601.nightshadeuhc.scenario.MolesScenario;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class InvClick implements Listener {

    private ScenarioManager scenarioManager;
    private UHC uhc;

    public InvClick(ScenarioManager scenarioManager, UHC uhc) {
        this.scenarioManager = scenarioManager;
        this.uhc = uhc;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null) {
            return;
        }

        if (e.getInventory() == null) {
            return;
        }

        if(e.getInventory().getName().equalsIgnoreCase(ChatColor.YELLOW + "Leaderboards")){
            e.setCancelled(true);
            if(e.getSlot() == 11){
                p.sendMessage(ChatUtils.message("&eOpening the winner leaderboard!"));
               new TopWinnersGUI(p);
            }else if(e.getSlot() == 13){
                p.sendMessage(ChatUtils.message("&eOpening the winner leaderboard!"));
                new TopKillsGUI(p);
            }else if(e.getSlot() == 15){
                //open all around
            }
        }

        if(e.getInventory().getName().equalsIgnoreCase(ChatColor.YELLOW + "Top 10 Winners")){
            e.setCancelled(true);

            if (e.getSlot() == 45) {
                new LeaderBoardMainGUI(p);
                return;
            }

            ItemStack item = e.getCurrentItem();
            if(item.getType() != Material.SKULL_ITEM){
                return;
            }

            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            String name = skullMeta.getOwner();
            UHCPlayer gamePlayer = UHCPlayer.get(ChatColor.stripColor(name));
            if(gamePlayer == null){
                p.sendMessage(ChatUtils.message("&cThere was an error loading that player's stats! Please try again later!"));
                p.closeInventory();
                return;
            }


            new StatsGUI(gamePlayer, p);
        }

        if(e.getInventory().getName().equalsIgnoreCase(ChatColor.YELLOW + "Top 10 Killers")){
            e.setCancelled(true);

            if (e.getSlot() == 45) {
                new LeaderBoardMainGUI(p);
                return;
            }

            ItemStack item = e.getCurrentItem();
            if(item.getType() != Material.SKULL_ITEM){
                return;
            }

            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            String name = skullMeta.getOwner();
            UHCPlayer gamePlayer = UHCPlayer.get(ChatColor.stripColor(name));
            if(gamePlayer == null){
                p.sendMessage(ChatUtils.message("&cThere was an error loading that player's stats! Please try again later!"));
                p.closeInventory();
                return;
            }

            new StatsGUI(gamePlayer, p);
        }

        if(e.getInventory().getName().toLowerCase().contains("stats")){
            e.setCancelled(true);
            NSPlayer nsPlayer = NSPlayer.get(p);
            if(!nsPlayer.hasRank(Rank.TRIAL)){
                return;
            }
            int slot = e.getSlot();
            if(slot == 0){
                //
            }else if(slot == 8){
                if (!nsPlayer.hasRank(Rank.ADMIN)) {
                    p.sendMessage(ChatUtils.message("&cOnly players with the " + Rank.ADMIN.getPrefix() + " can wipe player's stats!"));
                    return;
                }

                String[] strings = e.getInventory().getName().split(" "); //Stats,Of, NAME
                String name = strings[2];
                Player target = Bukkit.getPlayer(name);
                UUID query;
                if (target == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                    if (offlinePlayer == null) {
                        p.sendMessage(ChatUtils.message("&cThere was an issue looking up that player's stats. Please try again later..."));
                        return;
                    }

                    query = offlinePlayer.getUniqueId();
                } else {
                    query = target.getUniqueId();
                }

                UHCPlayer targetUHCPlayer = UHCPlayer.get(query);
                FancyMessage fancyMessage = new FancyMessage("Please confirm within 10 seconds by doing /confirm or clicking this message");
                fancyMessage.color(ChatColor.YELLOW).command("/confirm");
                fancyMessage.send(p);
                com.nightshadepvp.core.utils.PlayerUtils.getToConfirm().put(p.getUniqueId(), () -> {
                    targetUHCPlayer.resetStats();
                    p.sendMessage(ChatUtils.message("&b" + targetUHCPlayer.getName() + "'s &estats have been cleared!"));
                });

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        com.nightshadepvp.core.utils.PlayerUtils.getToConfirm().remove(p.getUniqueId());
                    }
                }.runTaskLater(uhc, 20 * 10);

            }


        }

        if(e.getInventory().getName().equalsIgnoreCase(ChatColor.DARK_AQUA + "Mole Kit Selector")){
            Scenario scenario = scenarioManager.getScen("Moles");
            if(scenario == null || !scenario.isEnabled()){
                p.sendMessage(ChatUtils.message("&cMoles isn't enabled!"));
                return;
            }

            if(!MolesScenario.moles.containsKey(p.getUniqueId())){
                p.sendMessage(ChatUtils.format(scenario.getPrefix() + "&cYou are not a mole!"));
            }
            
            if(MolesScenario.moles.get(p.getUniqueId())){
                p.sendMessage(ChatUtils.format(scenarioManager.getScen("Moles").getPrefix() + "&cYou have already gotten your mole kit!"));
                return;
            }

            e.setCancelled(true);
            int slot = e.getSlot();

            if(slot == 0){
                p.getInventory().addItem(new ItemStack(Material.WEB, 16));
                p.getInventory().addItem(new ItemStack(Material.TNT, 5));
                p.getInventory().addItem(new ItemStack(Material.FLINT_AND_STEEL, 1));
                p.sendMessage(ChatUtils.format(scenario.getPrefix() + "&eYou selected the &6Troll Kit&e!"));

                MolesScenario.moles.replace(p.getUniqueId(), true);
            }else if(slot == 1) {
                //Potter later
                Potion speed = new Potion(PotionType.SPEED);
                speed.setLevel(2);

                Potion weakness = new Potion(PotionType.WEAKNESS);
                weakness.setSplash(true);

                Potion poison = new Potion(PotionType.POISON);
                poison.setSplash(true);
                poison.setLevel(2);

                p.getInventory().addItem(speed.toItemStack(1), weakness.toItemStack(1), poison.toItemStack(1));
                p.sendMessage(ChatUtils.format(scenario.getPrefix() + "&eYou selected the &6Potter Kit&e!"));
                MolesScenario.moles.replace(p.getUniqueId(), true);
            }else if(slot == 2){
                p.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
                p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
                p.getInventory().addItem(new ItemStack(Material.FISHING_ROD, 1));
                p.sendMessage(ChatUtils.format(scenario.getPrefix() + "&eYou selected the &6Fighter Kit&e!"));

                MolesScenario.moles.replace(p.getUniqueId(), true);
            }else if(slot == 3){
                p.getInventory().addItem(new ItemStack(Material.TNT, 16));
                p.getInventory().addItem(new ItemStack(Material.PISTON_STICKY_BASE), new ItemStack(Material.PISTON_BASE));
                p.getInventory().addItem(new ItemStack(Material.FLINT_AND_STEEL, 1));
                p.sendMessage(ChatUtils.format(scenario.getPrefix() + "&eYou selected the &6Trapper Kit&e!"));

                MolesScenario.moles.replace(p.getUniqueId(), true);
            }else if(slot == 4){
                ItemBuilder helmet = new ItemBuilder(Material.DIAMOND_HELMET).durability(358);
                ItemBuilder chestplate = new ItemBuilder(Material.DIAMOND_CHESTPLATE).durability(523);
                ItemBuilder leggings = new ItemBuilder(Material.DIAMOND_LEGGINGS).durability(490);
                ItemBuilder boots = new ItemBuilder(Material.DIAMOND_BOOTS).durability(424);

                p.getInventory().setHelmet(helmet.make());
                p.getInventory().setChestplate(chestplate.make());
                p.getInventory().setLeggings(leggings.make());
                p.getInventory().setBoots(boots.make());

                p.sendMessage(ChatUtils.format(scenario.getPrefix() + "&eYou selected the &6Tank Kit&e!"));

                MolesScenario.moles.replace(p.getUniqueId(), true);
            }else if(slot == 5){
                p.getInventory().addItem(new ItemStack(Material.ENCHANTMENT_TABLE));
                p.getInventory().addItem(new ItemStack(Material.EXP_BOTTLE, 64));
                p.getInventory().addItem(new ItemStack(Material.LAPIS_BLOCK, 8));
                p.sendMessage(ChatUtils.format(scenario.getPrefix() + "&eYou selected the &6Enchanter Kit&e!"));

                MolesScenario.moles.replace(p.getUniqueId(), true);
            }else if(slot == 6){
                Potion potion = new Potion(PotionType.INSTANT_HEAL);
                potion.setLevel(2);
                potion.setSplash(true);
                p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
                p.getInventory().addItem(new ItemStack(potion.toItemStack(1)));
                p.sendMessage(ChatUtils.format(scenario.getPrefix() + "&eYou selected the &6Healer Kit&e!"));

                MolesScenario.moles.replace(p.getUniqueId(), true);
            }else if(slot == 7){
                p.getInventory().addItem(new ItemStack(Material.BOW));
                p.getInventory().addItem(new ItemStack(Material.ARROW, 64));
                p.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
                p.sendMessage(ChatUtils.format(scenario.getPrefix() + "&eYou selected the &6Projectile Kit&e!"));

                MolesScenario.moles.replace(p.getUniqueId(), true);
            }

        }

    }

}
