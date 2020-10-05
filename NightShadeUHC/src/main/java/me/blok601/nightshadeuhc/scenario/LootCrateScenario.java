package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Blok on 6/29/2017.
 */
public class LootCrateScenario extends Scenario{

    List<ItemStack> chests = new ArrayList<ItemStack>();
    private static  int timer;
    private ItemStack[] tier1;
    private ItemStack[] tier2;

    private BukkitTask task;

    public LootCrateScenario() {
        super("LootCrates", "Every 10 minute, everyone gets a lootcrate filled with goodies", new ItemBuilder(Material.CHEST).name("LootCrates").make());
        chests.add(new ItemBuilder(Material.CHEST).name("&6Tier 1 LootCrate").lore("&eRight click to redeem tier 1 items").make());
        chests.add(new ItemBuilder(Material.ENDER_CHEST).name("&bTier 2 LootCrate").lore("&eRight click to redeem tier 2 items").make());
        timer = 600;
        tier1 = new ItemStack[]{
                new ItemBuilder(Material.IRON_PICKAXE).make(), new ItemBuilder(Material.APPLE).amount(2).make(), new ItemBuilder(Material.COOKED_BEEF).amount(8).make(), new ItemBuilder(Material.CAKE).make(),
                new ItemBuilder(Material.RAW_FISH).amount(64).make(), new ItemBuilder(Material.BOW).make(), new ItemBuilder(Material.GOLD_CHESTPLATE).make(), new ItemBuilder(Material.FISHING_ROD).make(),
                new ItemBuilder(Material.IRON_SWORD).make(), new ItemBuilder(Material.DIAMOND_SPADE).make(), new ItemBuilder(Material.IRON_LEGGINGS).make(), new ItemBuilder(Material.SNOW_BALL).amount(16).make(), new ItemBuilder(Material.BOOK).amount(3).make()
        };

        tier2 = new ItemStack[]{
                new ItemBuilder(Material.DIAMOND).make(), new ItemBuilder(Material.GOLD_INGOT).amount(3).make(), new ItemBuilder(Material.IRON_INGOT).amount(10).make(), new ItemBuilder(Material.ENCHANTMENT_TABLE).make(),
                new ItemBuilder(Material.DIAMOND_SWORD).make(), new ItemStack(Material.DIAMOND_HELMET), new ItemBuilder(Material.ARROW).amount(32).make(), new ItemBuilder(Material.GOLDEN_APPLE).make()
        };
    }

    @Override
    public void onToggle(boolean newState, Player toggler) {
        if (newState) return;

        if (this.task != null) {
            task.cancel();
            sendMessage(toggler, "&bLootCrate tasks have been disabled!");
        }

        this.task = null;
    }

    @EventHandler
    public void onStart(GameStartEvent e){
        if(!isEnabled()){
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()){
            Random r = new Random();
            ItemStack stack = chests.get(r.nextInt(chests.size()));
            player.getInventory().addItem(stack);
            player.sendMessage(ChatUtils.message(getPrefix() + "&eYou have been given your lootcrate!"));
        }


        this.task = new BukkitRunnable() {
            @Override
            public void run(){
                if(!isEnabled()){
                    cancel();
                    return;
                }
                timer-=30;
                if(timer == 0){
                    timer = 600;
                    for (Player player : Bukkit.getOnlinePlayers()){
                        Random r = new Random();
                        if (player.getInventory().firstEmpty() == -1) {
                            Location loc = player.getLocation();
                            player.getWorld().dropItem(loc, chests.get(r.nextInt(chests.size())));
                        }
                        else {
                            player.getInventory().addItem(chests.get(r.nextInt(chests.size())));
                        }
                        PlayerUtils.playSound(Sound.NOTE_BASS_DRUM, player);
                        player.sendMessage(ChatUtils.format(getPrefix() + "&eYou have been given your lootcrate!"));
                    }
                }
            }
        }.runTaskTimer(UHC.get(), 0, 20*30);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        if(!isEnabled()){
            return;
        }

        if(e.getItem() == null){
            return;
        }

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
            Player p = e.getPlayer();
            ItemStack itemStack = p.getItemInHand();
            if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) return;
            if(itemStack.getType() == Material.CHEST){
                if(!itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatUtils.format("&6Tier 1 LootCrate"))) return;
                if(itemStack.getAmount() > 1){
                    p.sendMessage(ChatUtils.message("&cYou can only open one lootcrate at a time!"));
                    return;
                }


                Random r = ThreadLocalRandom.current();
                ItemStack stack = tier1[r.nextInt(tier1.length)];
                p.getInventory().setItemInHand(null);
                p.getInventory().addItem(stack);
                p.sendMessage(ChatUtils.format(getPrefix() + "&eYou have gotten " + stack.getAmount() + " &b" + stack.getType().name() + " &efrom your lootcrate!"));
            }else if(itemStack.getType() == Material.ENDER_CHEST){
                if(!itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatUtils.format("&bTier 2 LootCrate"))) return;
                if(itemStack.getAmount() > 1){
                    p.sendMessage(ChatUtils.message("&cYou can only open one lootcrate at a time!"));
                    return;
                }
                Random r = ThreadLocalRandom.current();
                ItemStack stack = tier2[r.nextInt(tier2.length)];
                p.getInventory().setItemInHand(null);
                p.getInventory().addItem(stack);
                p.sendMessage(ChatUtils.format(getPrefix() + "&eYou have gotten " + stack.getAmount() + " &b" + stack.getType().name() + " &efrom your lootcrate!"));
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if(!isEnabled()){
            return;
        }

        if(e.getBlock().getType() == Material.CHEST || e.getBlock().getType() == Material.ENDER_CHEST){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatUtils.message("&cYou can't place chests in LootCrates!"));
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent e){
        if(!isEnabled()){
            return;
        }
        if(e.getCurrentItem().getType() == Material.ENDER_CHEST || e.getCurrentItem().getType() == Material.CHEST){
            e.setCancelled(true);
            e.getWhoClicked().sendMessage(ChatUtils.message("&cYou can't craft chests in LootCrates!"));
        }
    }

    public static int getTimer() {
        return timer;
    }


}
