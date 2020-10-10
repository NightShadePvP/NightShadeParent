package me.blok601.nightshadeuhc.listener.gui;

import com.google.common.base.Joiner;
import com.google.gson.JsonObject;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.fanciful.FancyMessage;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.component.ComponentHandler;
import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.entity.object.PregenQueue;
import me.blok601.nightshadeuhc.entity.object.SetupStage;
import me.blok601.nightshadeuhc.event.PostUHCEvent;
import me.blok601.nightshadeuhc.gui.setup.ComponentGUI;
import me.blok601.nightshadeuhc.gui.setup.HostGUI;
import me.blok601.nightshadeuhc.gui.setup.TimerGUI;
import me.blok601.nightshadeuhc.gui.setup.world.BorderConfigGUI;
import me.blok601.nightshadeuhc.gui.setup.world.NetherGUI;
import me.blok601.nightshadeuhc.gui.setup.world.OverWorldGUI;
import me.blok601.nightshadeuhc.gui.setup.world.WorldGUI;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.task.GameCountdownTask;
import me.blok601.nightshadeuhc.task.PregenTask;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.PagedInventory;
import me.blok601.nightshadeuhc.util.Util;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import twitter4j.TwitterException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Created by Blok on 12/13/2018.
 */
public class GameSetupInventoryClick implements Listener {

    private GameManager gameManager;
    private UHC uhc;
    private ScenarioManager scenarioManager;
    private ComponentHandler componentHandler;


    public GameSetupInventoryClick(GameManager gameManager, UHC uhc, ScenarioManager scenarioManager, ComponentHandler componentHandler) {
        this.gameManager = gameManager;
        this.uhc = uhc;
        this.scenarioManager = scenarioManager;
        this.componentHandler = componentHandler;
    }

    @SuppressWarnings("Duplicates")
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) {
            return;
        }

        if (e.getInventory() == null) {
            return;
        }

        if (e.getCurrentItem().getType() == Material.AIR) return;

        Player p = (Player) e.getWhoClicked();

        ItemStack item = e.getCurrentItem();
        NSPlayer user = NSPlayer.get(p.getUniqueId());
        Inventory inventory = e.getInventory();
        int slot = e.getSlot();

        if (e.getInventory().getName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "Game Settings")) {
            e.setCancelled(true);
        }


        if (inventory.getName().equalsIgnoreCase(ChatColor.YELLOW + "Toggleable Options")) {
            e.setCancelled(true);
            componentHandler.handleClick(e.getCurrentItem(), e, e.getSlot());
        }

        if (inventory.getName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "UHC Game Setup")) {
            e.setCancelled(true);
            ClickType clickType = e.getClick();
            if (slot == 0) { //matchpost
                p.closeInventory();
                p.sendMessage(ChatUtils.message("&eChat your matchpost now, or say 'cancel' to leave the setup process"));
                gameManager.getSetupStageHashMap().put(p, SetupStage.MATCHPOST);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        gameManager.getSetupStageHashMap().remove(p);
                    }
                }.runTaskLater(UHC.get(), 2 * 20 * 60);
                return;
            } else if (slot == 2) {
                //Open world GUI
                p.sendMessage(ChatUtils.message("&eOpening the world setup gui..."));
                new WorldGUI(p);
            } else if (slot == 4) {
                p.sendMessage(ChatUtils.message("&eOpening the border configuration..."));
                new BorderConfigGUI(p);
            } else if (slot == 6) {
                new ComponentGUI(p, componentHandler);
            } else if (slot == 8) {
                new TimerGUI(p);
            } else if (slot == 12) {
                p.closeInventory();
                if(gameManager.getWorld() == null){
                    p.sendMessage(ChatUtils.message("&cYou have not set the game world!"));
                    return;
                }
                p.sendMessage(ChatUtils.message("&eThe game will start in 3 minutes..."));
                p.sendMessage(ChatUtils.message("&eDo /cancelgame at any time within those 3 minutes to cancel the start timer!"));
                GameCountdownTask gameCountdownTask = new GameCountdownTask(gameManager, scenarioManager);
                gameCountdownTask.runTaskTimer(uhc, 0, Util.TICKS);
                gameManager.setGameCountdownTask(gameCountdownTask);
                GameState.setState(GameState.PRE_SCATTER);
                p.chat("/claimhost");
            } else if (slot == 14) {
                //Post to Discord
                //Access Jedis
                p.sendMessage(ChatUtils.message("&eYour UHC will now be posted"));
                Jedis jedis = Core.get().getJedis();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("host", gameManager.getHost().getName());
                        //jsonObject.addProperty("time");

                        StringBuilder stringBuilder = new StringBuilder();
                        int index = 0;
                        int last = scenarioManager.getEnabledScenarios().size() - 1;

                        for (Scenario scenario : scenarioManager.getEnabledScenarios()) {
                            if (scenario.getName().equalsIgnoreCase("Mystery Scenarios")) {
                                stringBuilder.setLength(0);
                                stringBuilder.append("Mystery Scenarios");
                                break;
                            }
                            stringBuilder.append(scenario.getName()).append(index == last ? "" : ", ");
                            index++;
                        }
                        jsonObject.addProperty("scenarios", stringBuilder.toString().trim());

                        if (gameManager.isIsTeam()) {
                            jsonObject.addProperty("teamsize", (TeamManager.getInstance().isRandomTeams() ? "r" : "c") + "To" + TeamManager.getInstance().getTeamSize());
                        } else {
                            jsonObject.addProperty("teamsize", "FFA");
                        }
                        jsonObject.addProperty("ip", (UHC.getServerType().equalsIgnoreCase("uhc1") ? "uhc1" : "uhc2") + ".nightshadepvp.com");
                        jsonObject.addProperty("matchpost", Core.get().getMatchpost());

                        jedis.publish("matches", jsonObject.toString());
                        p.sendMessage(ChatUtils.message("&bYour UHC was posted to &fDiscord!"));

                        try {
                            PostUHCEvent event = new PostUHCEvent();
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                p.sendMessage(ChatUtils.message("&cYour game was not posted to Twitter because: &c" + event.getCancelReason()));
                                return;
                            }
                            if(gameManager.isIsTeam()){
                                Core.get().getTwitter().updateStatus("NightShadePvP UHC» \nTeamsize: " + ((TeamManager.getInstance().isRandomTeams() ? "r" : "c") + "To" + TeamManager.getInstance().getTeamSize()) + "\n " +
                                        "Scenarios: " + stringBuilder.toString().trim() + "\nMatchpost: " + Core.get().getMatchpost() + "\nIP: uhc1.nightshadepvp.com");
                            }else{
                                Core.get().getTwitter().updateStatus("NightShadePvP UHC» \nTeamsize: FFA" + "\n " +
                                        "Scenarios: " + stringBuilder.toString().trim() + "\nMatchpost: " + Core.get().getMatchpost() + "\nIP: uhc1.nightshadepvp.com");
                            }
                        } catch (TwitterException twitterException) {
                            twitterException.printStackTrace();
                            p.sendMessage(ChatUtils.message("&cThere was a problem posting your game to Twitter!"));
                        }
                    }
                }.runTaskAsynchronously(uhc);


            } else if (slot == 27) { //Starter food
                if (clickType == ClickType.LEFT) {
                    //Increase
                    gameManager.setStarterFood(gameManager.getStarterFood() + 1);
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);

                    ItemBuilder starterFood = new ItemBuilder(Material.COOKED_BEEF)
                            .name("&5&lStarter Food")
                            .lore("&7(&6i&7) &6Right click to increase by 1, left click to decrease by 1")
                            .amount(gameManager.getStarterFood());
                    inventory.setItem(slot, starterFood.make());
                    p.updateInventory();
                    return;
                } else if (clickType == ClickType.RIGHT) {
                    gameManager.setStarterFood(gameManager.getStarterFood() - 1);
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);

                    ItemBuilder starterFood = new ItemBuilder(Material.COOKED_BEEF)
                            .name("&5&lStarter Food")
                            .lore("&7(&6i&7) &6Right click to increase by 1, left click to decrease by 1")
                            .amount(gameManager.getStarterFood());
                    inventory.setItem(slot, starterFood.make());
                    p.updateInventory();
                    return;
                } else {
                    return;
                }
            } else if (slot == 28) {
                if (gameManager.getHost() == null) {
                    //Host not set -> Claim host
                    gameManager.setHost(p);
                    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    ItemBuilder newSkull = new ItemBuilder(skull)
                            .name("&5&lHost")
                            .skullOwner(gameManager.getHost().getName())
                            .lore("&6" + gameManager.getHost().getName());
                    inventory.setItem(slot, newSkull.make());
                    p.updateInventory();
                    p.sendMessage(ChatUtils.message("&eYou are now the host!"));
                    Util.staffLog("&b" + p.getName() + " is now the host!");
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);
                } else {
                    return;
                }
            } else if (slot == 29) {
                if (clickType == ClickType.LEFT) {
                    //Increase by 5
                    gameManager.setMaxPlayers(gameManager.getMaxPlayers() + 5);
                    ItemBuilder maxPlayers = new ItemBuilder(Material.NAME_TAG)
                            .name("&5&lMax Players")
                            .lore("&eCurrent: " + gameManager.getMaxPlayers())
                            .lore("&7&o(&6&oi&7&o) &6&oClick to increase by 5, left click to decrease by 5")
                            .amount(gameManager.getMaxPlayers());
                    inventory.setItem(slot, maxPlayers.make());
                    p.updateInventory();
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);
                    return;
                } else if (clickType == ClickType.RIGHT) {
                    gameManager.setMaxPlayers(gameManager.getMaxPlayers() - 5);
                    ItemBuilder maxPlayers = new ItemBuilder(Material.NAME_TAG)
                            .name("&5&lMax Players")
                            .lore("&eCurrent: " + gameManager.getMaxPlayers())
                            .lore("&7&o(&6&oi&7&o) &6&oClick to increase by 5, left click to decrease by 5")
                            .amount(gameManager.getMaxPlayers());
                    inventory.setItem(slot, maxPlayers.make());
                    p.updateInventory();
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);
                    return;
                } else {
                    return;
                }
            } else if (slot == 30) {
                ArrayList<ItemStack> items = new ArrayList<>();

                for (Scenario scenario : scenarioManager.getScenarios()) {
                    ItemStack i = new ItemBuilder(scenario.getItem()).name(scenario.isEnabled() ? ChatUtils.format("&a" + scenario.getName()) : ChatUtils.format("&c" + scenario.getName())).make();
                    items.add(i);
                }

                new PagedInventory(items, ChatColor.translateAlternateColorCodes('&', "&6Scenarios"), p);
            } else if (slot == 31) {
                if (clickType == ClickType.LEFT) {
                    //Increase by .5
                    gameManager.setAppleRates(gameManager.getAppleRates() + 0.5);
                    ItemBuilder appleRates = new ItemBuilder(Material.APPLE)
                            .name("&5&lApple Rates")
                            .lore("&eCurrent Apple Rates: " + gameManager.getAppleRates())
                            .lore("&7&o(&6&oi&7&o) &6&oClick to increase by 0.5%, left click to decrease by 0.5%");

                    inventory.setItem(slot, appleRates.make());
                    p.updateInventory();
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);
                    return;
                } else if (clickType == ClickType.RIGHT) {
                    if ((gameManager.getAppleRates() - 0.5) <= 0) {
                        return;
                    }
                    gameManager.setAppleRates(gameManager.getAppleRates() - 0.5);
                    ItemBuilder appleRates = new ItemBuilder(Material.APPLE)
                            .name("&5&lApple Rates")
                            .lore("&eCurrent Apple Rates: " + gameManager.getAppleRates())
                            .lore("&7&o(&6&oi&7&o) &6&oClick to increase by 0.5%, left click to decrease by 0.5%");
                    inventory.setItem(slot, appleRates.make());
                    p.updateInventory();
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);
                    return;
                }
            } else if (slot == 32) {
                if (clickType == ClickType.LEFT) {
                    gameManager.setFlintRates(gameManager.getFlintRates() + 0.5);
                    ItemBuilder flintRates = new ItemBuilder(Material.FLINT)
                            .name("&5&lFlint Rates")
                            .lore("&eCurrent Flint Rates: " + gameManager.getFlintRates())
                            .lore("&7&o(&6&oi&7&o) &6&oClick to increase by 0.5%, left click to decrease by 0.5%")
                            .lore("&cWarning: Flint Rates will be overridden by CutClean if enabled!");
                    inventory.setItem(slot, flintRates.make());
                    p.updateInventory();
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);
                    return;
                } else if (clickType == ClickType.RIGHT) {
                    if ((gameManager.getFlintRates() - 0.5) <= 0) {
                        return;
                    }
                    gameManager.setFlintRates(gameManager.getFlintRates() - 0.5);
                    ItemBuilder flintRates = new ItemBuilder(Material.FLINT)
                            .name("&5&lFlint Rates")
                            .lore("&eCurrent Flint Rates: " + gameManager.getFlintRates())
                            .lore("&7&o(&6&oi&7&o) &6&oClick to increase by 0.5%, left click to decrease by 0.5%")
                            .lore("&cWarning: Flint Rates will be overridden by CutClean if enabled!");
                    inventory.setItem(slot, flintRates.make());
                    p.updateInventory();
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);
                    return;
                } else {
                    return;
                }
            } else if (slot == 33) {
                gameManager.setIsTeam(!gameManager.isIsTeam());
                ItemBuilder teamGame = new ItemBuilder(Material.SIGN)
                        .name("&5&lTeam Game")
                        .lore("&eCurrent: " + (gameManager.isIsTeam() ? "&aYes" : "&cNo"))
                        .lore("&7&o(&6&oi&7&o) &6&oClick to toggle teams on and off");
                inventory.setItem(slot, teamGame.make());
                p.updateInventory();
                p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);
                return;
            } else if (slot == 34) {
                if (!gameManager.isIsTeam()) {
                    p.sendMessage(ChatUtils.message("&cIt must be a teams game to change this setting!"));
                    return;
                }
                TeamManager.getInstance().setTeamManagement(!TeamManager.getInstance().isTeamManagement());
                ItemBuilder teamMan = new ItemBuilder(Material.ARROW)
                        .name("&5&lTeam Management")
                        .lore("&eCurrent: " + (TeamManager.getInstance().isTeamManagement() ? "&aEnabled" : "&cDisabled"))
                        .lore("&7&o(&6&oi&7&o) &6&oClick to toggle team management");
                inventory.setItem(slot, teamMan.make());
                p.updateInventory();
                p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);
                return;
            } else if (slot == 35) {
                if (clickType == ClickType.LEFT) {
                    if (!gameManager.isIsTeam()) {
                        p.sendMessage(ChatUtils.message("&cIt must be a teams game to change this setting!"));
                        return;
                    }
                    TeamManager.getInstance().setTeamSize(TeamManager.getInstance().getTeamSize() + 1);
                    ItemBuilder teamSize = new ItemBuilder(Material.NETHER_STAR)
                            .name("&5&lTeam Size")
                            .lore("&eCurrent: " + TeamManager.getInstance().getTeamSize())
                            .lore("&7&o(&6&oi&7&o) &6&oClick to increase team size, right click to decrease team size")
                            .amount(TeamManager.getInstance().getTeamSize());
                    inventory.setItem(slot, teamSize.make());
                    p.updateInventory();
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);
                    return;
                } else if (clickType == ClickType.RIGHT) {
                    if (TeamManager.getInstance().getTeamSize() == 2) { //Lowest team size
                        return;
                    }
                    TeamManager.getInstance().setTeamSize(TeamManager.getInstance().getTeamSize() - 1);
                    ItemBuilder teamSize = new ItemBuilder(Material.NETHER_STAR)
                            .name("&5&lTeam Size")
                            .lore("&eCurrent: " + TeamManager.getInstance().getTeamSize())
                            .lore("&7&o(&6&oi&7&o) &6&oClick to increase team size, right click to decrease team size")
                            .amount(TeamManager.getInstance().getTeamSize());
                    inventory.setItem(slot, teamSize.make());
                    p.updateInventory();
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);
                    return;
                } else {
                    return;
                }
            }
        }

        if (inventory.getName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "World Setup")) {
            e.setCancelled(true);
            if (slot == 26) {
                //back
                new HostGUI(p, gameManager, scenarioManager);
                return;
            } else if (slot == 12) {
                //Over world shit
                new OverWorldGUI(p);
                return;
            } else if (slot == 14) {
                new NetherGUI(p);
            } else if (slot == 18) {
                if (com.nightshadepvp.core.utils.PlayerUtils.getToConfirm().containsKey(p.getUniqueId())) {
                    p.sendMessage(ChatUtils.message("&cYou are already in the process of deleting your worlds! Do /confirm to confirm your actions first!"));
                    return;
                }
                p.closeInventory();
                HashSet<World> toDelete = new HashSet<>();
                MVWorldManager mvWorldManager = UHC.getMultiverseCore().getMVWorldManager();
                for (World world : Bukkit.getWorlds()) {
                    if (mvWorldManager.isMVWorld(world.getName())) {
                        if (mvWorldManager.isMVWorld(world.getName())) {
                            if (world.getName().startsWith("UHC" + p.getName())) {
                                toDelete.add(world);
                            }
                        }
                    }
                }

                if (toDelete.isEmpty()) {
                    p.sendMessage(ChatUtils.message("&eYou have no current worlds to delete!"));
                    return;
                }

                p.sendMessage(ChatUtils.message("&eThe following world&8(&es&8)&e are about to be deleted: &b" + Joiner.on("&7, &b").join(toDelete.stream().map(World::getName).collect(Collectors.toList()))));
                FancyMessage fancyMessage = new FancyMessage("Please confirm within 10 seconds by doing /confirm or clicking this message");
                fancyMessage.color(ChatColor.YELLOW).command("/confirm");
                fancyMessage.send(p);
                com.nightshadepvp.core.utils.PlayerUtils.getToConfirm().put(p.getUniqueId(), () -> {
                    p.sendMessage(ChatUtils.message("&eThe worlds will now be deleted..."));
                    for (World world : toDelete) {
                        mvWorldManager.removeWorldFromConfig(world.getName());
                        Bukkit.unloadWorld(world, false);
                        Util.deleteWorldFolder(world);
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            com.nightshadepvp.core.utils.PlayerUtils.getToConfirm().remove(p.getUniqueId());
                        }
                    }.runTaskLater(UHC.get(), 10 * 20);
                    p.sendMessage(ChatUtils.message("&eYour worlds have been cleared!"));
                });
            }
        }

        if (inventory.getName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "Overworld Setup")) {
            e.setCancelled(true);
            if (slot == 3) {
                //Seed
                p.closeInventory();
                p.sendMessage(ChatUtils.message("&eChat your desired seed now, or type `cancel` to leave the setup process"));
                gameManager.getSetupStageHashMap().put(p, SetupStage.SEED);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        gameManager.getSetupStageHashMap().remove(p);
                    }
                }.runTaskLater(UHC.get(), 2 * 20 * 60);
            } else if (slot == 5) {
                //Create
                p.closeInventory();

                if (Bukkit.getWorld("UHC" + p.getName()) != null) {
                    p.sendMessage(ChatUtils.message("&cYou already have a game world!"));
                    p.sendMessage(ChatUtils.message("&cYou can delete it in the hosting gui!"));
                    return;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.sendMessage(ChatUtils.message("&eYou world is now being created and pregenned..."));
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv create UHC" + p.getName() + " NORMAL -s " + gameManager.getSetupSeed());
                    }
                }.runTaskLater(UHC.get(), 1);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.sendMessage(ChatUtils.message("&eYour world has been created!"));
                        p.sendMessage(ChatUtils.message("&ePregen will begin in 5 seconds..."));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (PregenTask.getPregenQueue().size() > 0) {
                                    p.sendMessage(ChatUtils.message("&eThere are other worlds being pregenning right now! This world will be added to the pregen queue!"));
                                }
                                World world = Bukkit.getWorld("UHC" + p.getName());
                                if (world == null) {
                                    p.sendMessage(ChatUtils.message("&cThere was an error trying to load your world..."));
                                    p.sendMessage(ChatUtils.message("&cTry recreating it to make sure!"));

                                    p.sendMessage(ChatUtils.message("&eAttempting to clear your world folders..."));
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            HashSet<File> files = Util.worldFoldersFromString("UHC" + p.getName());
                                            if (files == null || files.isEmpty()) {
                                                p.sendMessage(ChatUtils.message("&cYou don't have any world folders! You should be good to go!"));
                                                return;
                                            }

                                            files.stream().filter(File::exists).forEach(File::delete);
                                            p.sendMessage(ChatUtils.message("&eWorld folders have been cleared! Try again now..."));
                                        }
                                    }.runTaskAsynchronously(uhc);
                                    return;
                                }
                                gameManager.setWorld(world);
                                PregenQueue queue = new PregenQueue();
                                queue.setRunning(false);
                                queue.setStarter(p.getUniqueId());
                                queue.setRadius(gameManager.getSetupRadius());
                                queue.setWorld(world);
                                PregenTask.getPregenQueue().add(queue);
                            }
                        }.runTaskLater(UHC.get(), 5 * 20);
                    }
                }.runTaskLater(UHC.get(), 50);
            } else if (slot == 8) {
                new WorldGUI(p);
                return;
            } else if (slot == 18) {
                //- 100
                gameManager.setSetupRadius(gameManager.getSetupRadius() - 100);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 19) {
                gameManager.setSetupRadius(gameManager.getSetupRadius() - 50);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 20) {
                gameManager.setSetupRadius(gameManager.getSetupRadius() - 10);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 21) {
                gameManager.setSetupRadius(gameManager.getSetupRadius() - 5);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 23) {
                gameManager.setSetupRadius(gameManager.getSetupRadius() + 5);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 24) {
                gameManager.setSetupRadius(gameManager.getSetupRadius() + 10);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 25) {
                gameManager.setSetupRadius(gameManager.getSetupRadius() + 50);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 26) {
                gameManager.setSetupRadius(gameManager.getSetupRadius() + 100);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            }
        }

        if (inventory.getName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "Nether World Setup")) {
            e.setCancelled(true);
            if (slot == 3) {
                if (gameManager.isNetherEnabled()) {
                    gameManager.setNetherEnabled(false);
                } else {
                    gameManager.setNetherEnabled(true);
                }

                ItemBuilder toggler = new ItemBuilder(Material.PAPER)
                        .name("&c&lEnable/Disable Nether")
                        .lore("&eCurrently: " + (gameManager.isNetherEnabled() ? "&aEnabled" : "&cDisabled"));
                inventory.setItem(slot, toggler.make());
                p.updateInventory();
                return;
            } else if (slot == 5) {
                p.closeInventory();

                if (Bukkit.getWorld("UHC" + p.getName()) == null) {
                    p.sendMessage(ChatUtils.message("&cYour overworld does not exist! Make sure to create an overworld first!"));
                    return;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.sendMessage(ChatUtils.message("&eYour &cnether&e is now being created and pregenned..."));
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv create UHC" + p.getName() + "_nether NETHER");
                    }
                }.runTaskLater(UHC.get(), 1);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.sendMessage(ChatUtils.message("&eYour world has been created!"));
                        p.sendMessage(ChatUtils.message("&ePregen will begin in 5 seconds..."));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!PregenTask.getPregenQueue().isEmpty()) {
                                    p.sendMessage(ChatUtils.message("&cThe game world has not finished pregenning! The nether world will be created and pregenned after the game world is finished!"));
                                }
                                PregenQueue pregenQueue = new PregenQueue();
                                pregenQueue.setWorld(Bukkit.getWorld("UHC" + p.getName() + "_nether"));
                                pregenQueue.setRadius(gameManager.getSetupNetherRadius());
                                pregenQueue.setStarter(p.getUniqueId());
                                pregenQueue.setRunning(false);
                                PregenTask.getPregenQueue().add(pregenQueue);
                            }
                        }.runTaskLater(UHC.get(), 5 * 20);
                    }
                }.runTaskLater(UHC.get(), 50);
                return;
            } else if (slot == 8) {
                new WorldGUI(p);
                return;
            } else if (slot == 18) {
                //- 100
                gameManager.setSetupNetherRadius(gameManager.getSetupNetherRadius() - 100);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupNetherRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 19) {
                gameManager.setSetupNetherRadius(gameManager.getSetupNetherRadius() - 50);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupNetherRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 20) {
                gameManager.setSetupNetherRadius(gameManager.getSetupNetherRadius() - 10);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupNetherRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 21) {
                gameManager.setSetupNetherRadius(gameManager.getSetupNetherRadius() - 5);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupNetherRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 23) {
                gameManager.setSetupNetherRadius(gameManager.getSetupNetherRadius() + 5);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupNetherRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 24) {
                gameManager.setSetupNetherRadius(gameManager.getSetupNetherRadius() + 10);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupNetherRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 25) {
                gameManager.setSetupNetherRadius(gameManager.getSetupNetherRadius() + 50);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupNetherRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            } else if (slot == 26) {
                gameManager.setSetupNetherRadius(gameManager.getSetupNetherRadius() + 100);
                ItemBuilder currentBorder = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()))
                        .name("&6Current Border")
                        .lore("&eCurrent Border: " + gameManager.getSetupNetherRadius());
                inventory.setItem(22, currentBorder.make());
                p.updateInventory();
            }
            return;
        }

        if (inventory.getName().equalsIgnoreCase(ChatColor.YELLOW + "Border Config")) {
            e.setCancelled(true);

            if (GameState.getState() != GameState.WAITING) {
                p.sendMessage(ChatUtils.message("&cThe game has already started!"));
                return;
            }


            if (slot == 26) {
                new HostGUI(p, gameManager, scenarioManager);
                return;
            }

            int cBorder = gameManager.getShrinks()[slot];

            if (e.getClick() == ClickType.LEFT) {
                gameManager.getShrinks()[slot] = cBorder + 25; //Add 25
            } else if (e.getClick() == ClickType.RIGHT) {
                if (gameManager.getShrinks()[slot] - 25 < 0) return;
                gameManager.getShrinks()[slot] = cBorder - 25; //Minus 25
            } else if (e.getClick() == ClickType.MIDDLE) {
                p.closeInventory();
                gameManager.getSetupStageHashMap().put(p, SetupStage.BORDER);
                gameManager.setCurrentEditingBorderIndex(slot);
                p.sendMessage(ChatUtils.message("&eChat the custom border amount, or say cancel to cancel the process"));
                return;
            }

            ItemStack stack = e.getCurrentItem();
            ItemBuilder builder = new ItemBuilder(stack);

            int border = gameManager.getShrinks()[slot];
            if (border == 0 && GameState.getState() != GameState.WAITING) {
                return;
            } else {
                builder.lore(ChatUtils.format("&6" + border), true);
                builder.lore("&7(&6i&7) &6Right click to increase border by 25, left click to decrease by 25");
            }

            inventory.setItem(slot, builder.make());
            p.updateInventory();

        }

        if (inventory.getName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "Timer Setup")) {
            e.setCancelled(true);
            if (slot == 0) {

                if (GameState.getState() == GameState.INGAME) {
                    p.sendMessage(ChatUtils.message("&cYou can't change this setting while the game is running!"));
                    return;
                }

                if (e.getClick() == ClickType.LEFT) {
                    gameManager.setFinalHealTime(gameManager.getFinalHealTime() + 300); // Add 5 mins
                } else if (e.getClick() == ClickType.RIGHT) {
                    if (gameManager.getFinalHealTime() - 300 < 0) {
                        return;
                    }
                    gameManager.setFinalHealTime(gameManager.getFinalHealTime() - 300); // Add 5 mins
                } else {
                    return;
                }

                ItemStack itemStack = e.getCurrentItem(); // Know its the final heal one
                ItemBuilder builder = new ItemBuilder(itemStack);
                int finalHeal = gameManager.getFinalHealTime();
                if (finalHeal == 0 && GameState.getState() != GameState.WAITING) {
                    builder.lore(ChatUtils.format("&6Not set or already passed!"), true);
                } else {
                    builder.lore(ChatUtils.format("&6" + gameManager.getFinalHealTime() / 60 + " minutes"), true);
                }
                builder.lore("&7(&6&oi&r&7) &6&oTime until Final Heal is given");

                inventory.setItem(0, builder.make());
                p.updateInventory();
            } else if (slot == 1) {
                //This is pvp time
                if (GameState.getState() == GameState.INGAME) {
                    p.sendMessage(ChatUtils.message("&cYou can't change this setting while the game is running!"));
                    return;
                }

                if (e.getClick() == ClickType.LEFT) {
                    gameManager.setPvpTime(gameManager.getPvpTime() + 300); // Add 5 mins
                } else if (e.getClick() == ClickType.RIGHT) {
                    if (gameManager.getPvpTime() - 300 < 0) {
                        return;
                    }
                    gameManager.setPvpTime(gameManager.getPvpTime() - 300); // Add 5 mins
                } else {
                    return;
                }

                ItemStack itemStack = e.getCurrentItem(); // Know its the final heal one
                ItemBuilder builder = new ItemBuilder(itemStack);
                builder.removeLore(0);
                int pvpTime = gameManager.getPvpTime();
                if (pvpTime == 0 && GameState.getState() != GameState.WAITING) {
                    builder.lore(ChatUtils.format("&6Not set or already passed!"), true);
                } else {
                    builder.lore(ChatUtils.format("&6" + gameManager.getPvpTime() / 60 + " minutes"), true);
                }
                builder.lore("&7(&6&oi&r&7) &6&oTime until PvP is enabled");

                inventory.setItem(e.getSlot(), builder.make());
                p.updateInventory();
            } else if (slot == 2) {
                //This is border time

                if (GameState.getState() == GameState.INGAME) {
                    p.sendMessage(ChatUtils.message("&cYou can't change this setting while the game is running!"));
                    return;
                }

                if (e.getClick() == ClickType.LEFT) {
                    gameManager.setBorderTime(gameManager.getBorderTime() + 300); // Add 5 mins
                } else if (e.getClick() == ClickType.RIGHT) {
                    if (gameManager.getBorderTime() - 300 < 0) {
                        return;
                    }
                    gameManager.setBorderTime(gameManager.getBorderTime() - 300); // remove 5 mins
                } else {
                    return;
                }

                ItemStack itemStack = e.getCurrentItem(); // Know its the final heal one
                ItemBuilder builder = new ItemBuilder(itemStack);
                builder.removeLore(0);
                int borderTime = gameManager.getBorderTime();
                if (borderTime == 0 && GameState.getState() != GameState.WAITING) {
                    builder.lore(ChatUtils.format("&6Not set or already passed!"), true);
                } else {
                    builder.lore(ChatUtils.format("&6" + gameManager.getBorderTime() / 60 + " minutes"), true);
                }
                builder.lore("&7(&6&oi&r&7) &6&oTime until First Border Shrink");

                inventory.setItem(e.getSlot(), builder.make());
                p.updateInventory();
            } else if (slot == 3) {
                //Meetup Time

                if (GameState.getState() == GameState.INGAME) {
                    p.sendMessage(ChatUtils.message("&cYou can't change this setting while the game is running!"));
                    return;
                }

                if (e.getClick() == ClickType.LEFT) {
                    gameManager.setMeetupTime(gameManager.getMeetupTime() + 300); // Add 5 mins
                } else if (e.getClick() == ClickType.RIGHT) {
                    if (gameManager.getMeetupTime() - 300 < 0) {
                        return;
                    }
                    gameManager.setMeetupTime(gameManager.getMeetupTime() - 300); // remove 5 mins
                } else {
                    return;
                }

                ItemStack itemStack = e.getCurrentItem(); // Know its the final heal one
                ItemBuilder builder = new ItemBuilder(itemStack);
                builder.removeLore(0);
                int meetupTime = gameManager.getMeetupTime();
                if (meetupTime == 0 && GameState.getState() != GameState.WAITING) {
                    builder.lore(ChatUtils.format("&6Not set or already passed!"), true);
                } else {
                    builder.lore(ChatUtils.format("&6" + gameManager.getMeetupTime() / 60 + " minutes"), true);
                }
                builder.lore("&7(&6&oi&r&7) &6&oTime until \"Meetup\" begins");

                inventory.setItem(e.getSlot(), builder.make());
                p.updateInventory();
            } else if (slot == 8) { //back
                new HostGUI(p, gameManager, scenarioManager);
                return;
            } else if (slot == 4) {
                //RR Time

                if(!componentHandler.getComponent("Recorded Round").isEnabled()){
                    p.sendMessage(ChatUtils.message("&cThis is not a recorded round! Enable it in the toggleable options menu!"));
                    return;
                }
                if (GameState.getState() == GameState.INGAME) {
                    p.sendMessage(ChatUtils.message("&cYou can't change this setting while the game is running!"));
                    return;
                }

                if (e.getClick() == ClickType.LEFT) {
                    gameManager.setRrEpisodeLength(gameManager.getRrEpisodeLength() + 300); // Add 5 mins
                } else if (e.getClick() == ClickType.RIGHT) {
                    if (gameManager.getRrEpisodeLength() - 300 < 0) {
                        return;
                    }
                    gameManager.setRrEpisodeLength(gameManager.getRrEpisodeLength() - 300); // remove 5 mins
                } else {
                    return;
                }

                ItemStack itemStack = e.getCurrentItem(); // Know its the final heal one
                ItemBuilder builder = new ItemBuilder(itemStack);
                builder.removeLore(0);
                int meetupTime = gameManager.getMeetupTime();
                if (meetupTime == 0 && GameState.getState() != GameState.WAITING) {
                    builder.lore(ChatUtils.format("&6Not set or already passed!"), true);
                } else {
                    builder.lore(ChatUtils.format("&6" + gameManager.getRrEpisodeLength() / 60 + " minutes"), true);
                }
                builder.lore("&7(&6&oi&r&7) &6&oRR Episode length");

                inventory.setItem(e.getSlot(), builder.make());
                p.updateInventory();
            } else {
                return;
            }
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 3, 3);
        }
    }

}
