package com.nightshadepvp.tournament.engine;

import com.massivecraft.massivecore.Engine;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.nightshadepvp.tournament.Tournament;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.enums.MatchState;
import com.nightshadepvp.tournament.entity.enums.PlayerStatus;
import com.nightshadepvp.tournament.entity.handler.ArenaHandler;
import com.nightshadepvp.tournament.entity.handler.GameHandler;
import com.nightshadepvp.tournament.entity.handler.MatchHandler;
import com.nightshadepvp.tournament.entity.objects.data.Arena;
import com.nightshadepvp.tournament.entity.objects.game.SoloMatch;
import com.nightshadepvp.tournament.entity.objects.game.iMatch;
import com.nightshadepvp.tournament.event.PlayerSpectateMatchEvent;
import com.nightshadepvp.tournament.event.PlayerUnSpectateMatchEvent;
import com.nightshadepvp.tournament.utils.ChatUtils;
import net.sf.cglib.asm.$ByteVector;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;

/**
 * Created by Blok on 7/25/2018.
 */
@SuppressWarnings("Duplicates")
public class EngineMatch extends Engine {

    private static EngineMatch i = new EngineMatch();

    public static EngineMatch get() {
        return i;
    }

    private final BlockFace[] faces = new BlockFace[]{
            BlockFace.SELF,
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
    };

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        TPlayer tPlayer = TPlayer.get(p);

        iMatch soloMatch = MatchHandler.getInstance().getActiveMatch(tPlayer);
        if (soloMatch == null) return; //Not in game, not in spawn..where else are they ?

        if (!GameHandler.getInstance().getKit().isBuild()) {
            e.setCancelled(true);
        }

        if (e.getBlock().getType() == Material.LAVA || e.getBlock().getType() == Material.WATER) {
            e.getBlock().setMetadata("toRemove", new FixedMetadataValue(Tournament.get(), true));
            return;
        }

        Block block = e.getBlock();
        soloMatch.getBlocks().add(block.getLocation());
    }

    @EventHandler
    public void onPlaceFluid(PlayerInteractEvent event) {

        Player p = event.getPlayer();
        TPlayer tPlayer = TPlayer.get(p);

        iMatch soloMatch = MatchHandler.getInstance().getActiveMatch(tPlayer);
        if (soloMatch == null) return; //Not in game, not in spawn..where else are they ?

//        if (!GameHandler.getInstance().getKit().isBuild()) {
//            event.setCancelled(true);
//        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Material type;
        if (event.getItem() == null) return;
        if (event.getItem().getType() == Material.WATER_BUCKET) {
            type = Material.WATER;
        } else if (event.getItem().getType() == Material.LAVA_BUCKET) {
            type = Material.LAVA;
        } else return;
        Block block = event.getClickedBlock().getRelative(event.getBlockFace());
        if (generates(type, block)) {
            return;
        } else {
            BlockFace[] nesw = {BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
            for (BlockFace face : nesw) {
                if (generates(type, block.getRelative(face))) {
                    //System.out.println(face);
                    //event.setCancelled(true);
                    soloMatch.getBlocks().add(block.getRelative(face).getLocation());
                    return;
                }
            }
        }
    }


    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        TPlayer tPlayer = TPlayer.get(p);

        iMatch soloMatch = MatchHandler.getInstance().getActiveMatch(tPlayer);
        if (soloMatch == null) return;

        if (!GameHandler.getInstance().getKit().isBuild()) {
            e.setCancelled(true);
        }

        Block block = e.getBlock();

        if (!soloMatch.getBlocks().contains(block.getLocation())) {
            e.setCancelled(true);
            //They broke a map block - cancel
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player killed = (Player) e.getEntity();

        if (e.getFinalDamage() >= killed.getHealth()) {
            iMatch im = MatchHandler.getInstance().getActiveMatch(killed);
            if (im == null) return; //Not in game
            TPlayer tPlayer = TPlayer.get(killed);

            if (im.getPlayers().size() == 2) {
                //Solo game
                SoloMatch match = (SoloMatch) im;
                match.endMatch(Collections.singletonList(match.getOpponents(tPlayer).get(0)), e);
            }
        }

        if (e.getDamager() instanceof Arrow) {
            Arrow a = (Arrow) e.getDamager();
            if (!(a.getShooter() instanceof Player)) {
                return;
            }
            double damage = e.getFinalDamage();
            double health = (killed.getHealth() - damage);
            Player damager = (Player) a.getShooter();
            DecimalFormat format = new DecimalFormat("##.#");
            format.setRoundingMode(RoundingMode.HALF_UP);
            String f = format.format(health);
            damager.sendMessage(ChatUtils.message("&9" + killed.getName() + " &b now has &9" + f + "❤"));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();

        iMatch im = MatchHandler.getInstance().getActiveMatch(p);
        if (im == null) return; //Not in game
        TPlayer tPlayer = TPlayer.get(p);
        if (im instanceof SoloMatch) {
            //Solo game
            SoloMatch match = (SoloMatch) im;
            match.endMatch(Collections.singletonList(match.getOpponents(tPlayer).get(0)), event);
        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            TPlayer tPlayer = TPlayer.get(p);
            if (tPlayer.isFrozen()) {
                e.setCancelled(true);
            }

            iMatch match = MatchHandler.getInstance().getActiveMatch(p);
            if (match == null) return;
            if (match.getMatchState() == MatchState.RESETTING) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) {
            return;
        }
        Player p = e.getPlayer();

        TPlayer tPlayer = TPlayer.get(p);
        if (tPlayer.isFrozen()) {
            e.setTo(e.getFrom());
        }

        iMatch match = MatchHandler.getInstance().getActiveMatch(p);
        if (match == null || match.getMatchState() != MatchState.INGAME) return;
        if (!match.getArena().getSelection().contains(e.getTo())) e.setTo(e.getFrom());
    }

    @EventHandler
    public void onPearl(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        Player p = e.getPlayer();
        iMatch match = MatchHandler.getInstance().getActiveMatch(p);
        if (match == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        TPlayer tPlayer = TPlayer.get(p);
        if (tPlayer.isFrozen()) {
            e.setCancelled(true);
            return;
        }

        iMatch match = MatchHandler.getInstance().getActiveMatch(tPlayer);
        if (match == null) return;

        match.getDrops().add(e.getItemDrop());
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getEntity();
        iMatch match = MatchHandler.getInstance().getActiveMatch(p);
        if (match == null) return;

        if (!GameHandler.getInstance().getKit().isAllowRegen()) {
            if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        iMatch match = MatchHandler.getInstance().getActiveMatch(p);
        if (match == null) return;
        TPlayer tPlayer = TPlayer.get(p);

        if (match instanceof SoloMatch) {
            SoloMatch soloMatch = (SoloMatch) match;
            soloMatch.startLogOutTimer(tPlayer);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (GameHandler.getInstance().getTeleportQueue().containsKey(p.getUniqueId())) {
            p.teleport(GameHandler.getInstance().getTeleportQueue().get(p.getUniqueId())); //Teleport them to where they need to be
            GameHandler.getInstance().getTeleportQueue().remove(p.getUniqueId());
        }

        iMatch match = MatchHandler.getInstance().getActiveMatch(p);
        if (match == null) return;
        TPlayer tPlayer = TPlayer.get(p);
        if (match.getLogOutTimer(tPlayer) != null) {
            //They have a timer
            match.stopLogOutTimer(tPlayer);
            match.setupBoard();
            tPlayer.setStatus(PlayerStatus.PLAYING);
            p.setFlySpeed(0.1F);
            p.setFlying(false);
            p.setAllowFlight(false);
            //Continue play
        }
    }

    @EventHandler
    public void on(PlayerBucketEmptyEvent e) {
        iMatch match = MatchHandler.getInstance().getActiveMatch(e.getPlayer());
        if (match == null) return;
        Player p = e.getPlayer();
        if (!GameHandler.getInstance().getKit().isBuild()) {
            e.setCancelled(true);
            return;
        }
        BlockFace face = e.getBlockFace();
        Block block = e.getBlockClicked().getRelative(face); //Add source block to it

        match.getBlocks().add(block.getLocation());
    }

    @EventHandler
    public void onFill(PlayerBucketFillEvent e) {
        iMatch match = MatchHandler.getInstance().getActiveMatch(e.getPlayer());
        if (match == null) return;
        BlockFace face = e.getBlockFace();
        Block block = e.getBlockClicked().getRelative(face);
        if (!match.getBlocks().contains(block.getLocation())) {
            e.setCancelled(true); //Tried to steal from arena
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        if (MatchHandler.getInstance().getActiveMatch(e.getPlayer()) == null) return;

        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if (item.getType() == Material.GOLDEN_APPLE) {

            if (!item.hasItemMeta()) {
                return;
            }

            if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Golden Head")) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 25 * 8, 1));
            }
        }
    }

    @EventHandler
    public void onSpread(BlockFromToEvent e) {
        Block b = e.getToBlock();

        Arena arena = ArenaHandler.getInstance().getFromBlock(e.getBlock());
        if (arena == null) return;
        iMatch match = MatchHandler.getInstance().getMatchFromArena(arena);
        if (match == null) return;

        if (e.getToBlock().getType() == Material.COBBLESTONE || e.getToBlock().getType() == Material.OBSIDIAN) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    match.getBlocks().add(e.getToBlock().getLocation());
                    Core.get().getLogManager().log(Logger.LogType.DEBUG, "Generated (1): " + e.getToBlock().getType().name());

                }
            }.runTaskLater(Core.get(), 2);
        }

        Material type = e.getBlock().getType();
        if (type == Material.WATER || type == Material.STATIONARY_WATER || type == Material.LAVA || type == Material.STATIONARY_LAVA) {
            if (b.getType() == Material.AIR) {
                if (generatesCobble(type, b)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            match.getBlocks().add(e.getToBlock().getLocation());
                            Core.get().getLogManager().log(Logger.LogType.DEBUG, "Generated (2): " + e.getToBlock().getType().name());
                        }
                    }.runTaskLater(Core.get(), 2);

                }
            }
        }
    }

    @EventHandler
    public void onSpectate(PlayerSpectateMatchEvent e) {
        for (TPlayer tPlayer : e.getMatch().getPlayers()) {
            if (tPlayer.isSpecMatchAlert()) {
                tPlayer.msg(ChatUtils.format("&5" + e.getTPlayer().getName() + " &bis now spectating."));
            }
        }

        e.getMatch().getSpectators().forEach(tPlayer -> tPlayer.msg(ChatUtils.format("&5" + e.getTPlayer().getName() + " &bis now spectating.")));
    }

    @EventHandler
    public void onUnSpectate(PlayerUnSpectateMatchEvent e) {
        for (TPlayer tPlayer : e.getMatch().getPlayers()) {
            if (tPlayer.isSpecMatchAlert()) {
                tPlayer.msg(ChatUtils.format("&5" + e.getTPlayer().getName() + " &bis no longer spectating."));
            }
        }

        e.getMatch().getSpectators().forEach(tPlayer -> tPlayer.msg(ChatUtils.format("&5" + e.getTPlayer().getName() + " &bis no longer spectating.")));
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(ChunkUnloadEvent event) {
        World world = event.getWorld();

        if (!world.getName().equalsIgnoreCase(ArenaHandler.getInstance().getArenas().get(0).getWorld().getName())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onForm(BlockFormEvent event) {
        BlockState newState = event.getNewState();
        Arena arena = ArenaHandler.getInstance().getFromBlock(newState.getBlock());
        if (arena == null) return;

        iMatch match = MatchHandler.getInstance().getMatchFromArena(arena);
        if (match == null) return;

        match.getBlocks().add(newState.getLocation());
    }

    public boolean generatesCobble(Material type, Block b) {
        Material mirrorID1 = (type == Material.WATER || type == Material.STATIONARY_WATER ? Material.LAVA : Material.WATER);
        Material mirrorID2 = (type == Material.WATER || type == Material.STATIONARY_WATER ? Material.STATIONARY_LAVA : Material.STATIONARY_WATER);
        for (BlockFace face : faces) {
            Block r = b.getRelative(face, 1);
            if (r.getType() == mirrorID1 || r.getType() == mirrorID2) {
                return true;
            }
        }
        return false;
    }

    private boolean generates(Block from, Block to) {
        if (!to.isLiquid()) return false;
        return generates(from.getType(), to.getType());
    }

    private boolean generates(Material from, Block to) {
        if (!to.isLiquid()) return false;
        return generates(from, to.getType());
    }

    private boolean generates(Material from, Material to) {
        if (from == Material.STATIONARY_WATER) from = Material.WATER;
        else if (from == Material.STATIONARY_LAVA) from = Material.LAVA;

        if (to == Material.STATIONARY_WATER) to = Material.WATER;
        else if (to == Material.STATIONARY_LAVA) to = Material.LAVA;

        return from != to;
    }
}


