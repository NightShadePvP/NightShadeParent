package me.blok601.nightshadeuhc.component;

import com.nightshadepvp.core.utils.fanciful.FancyMessage;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.entity.object.SpecInfoData;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class SpectatorInfoComponent extends Component {

    private UHC plugin;
    public SpectatorInfoComponent(UHC plugin) {
        super("Spec Info", Material.NAME_TAG, true, "Spectator Info");
        this.lock();
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent e) {
        if(!isEnabled()) return;
        if (!GameState.gameHasStarted()) return;
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();

        if (e.getFinalDamage() == 0) return;
        if (e.isCancelled()) return;
        if (p.getHealth() - e.getFinalDamage() <= 0) return; //They died

        FancyMessage fancyMessage = new FancyMessage();
        fancyMessage.command("/tp " + p.getName());
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isSpectator).forEach(uhcPlayer -> fancyMessage.text(SpecInfoData.translate(p, p.getHealth(), null, SpecInfoData.DAMAGE_FALL)).send(uhcPlayer.getPlayer()));
        } else if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || e.getCause() == EntityDamageEvent.DamageCause.LAVA || e.getCause() == EntityDamageEvent.DamageCause.MELTING) {
            if (!plugin.getScenarioManager().getScen("Fireless").isEnabled()) {
                UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isSpectator).forEach(uhcPlayer -> fancyMessage.text(SpecInfoData.translate(p, p.getHealth(), null, SpecInfoData.DAMAGE_BURN)).send(uhcPlayer.getPlayer()));
            }
        } else if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            return;
        } else {
            UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isSpectator).forEach(uhcPlayer -> fancyMessage.text(SpecInfoData.translate(p, p.getHealth(), null, SpecInfoData.DAMAGE_OTHER)).send(uhcPlayer.getPlayer()));
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHit(EntityDamageByEntityEvent e) {
        if(!isEnabled()) return;
        if (!GameState.gameHasStarted()) return;
        if (!(e.getEntity() instanceof Player)) return;

        Player p = (Player) e.getEntity();
        if (e.getFinalDamage() == 0) return;
        if (p.getHealth() - e.getFinalDamage() <= 0) return; //They died

        FancyMessage fancyMessage = new FancyMessage();
        fancyMessage.command("/tp " + p.getName());
        if (e.getEntity() instanceof Monster) {
            UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isSpectator).filter(UHCPlayer::isReceivingSpectatorInfo).forEach(uhcPlayer -> fancyMessage.text(SpecInfoData.translate(p, p.getHealth() - e.getFinalDamage(), null, SpecInfoData.DAMAGE_MOB)).send(uhcPlayer.getPlayer()));
        }


        if (e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isSpectator).filter(UHCPlayer::isReceivingSpectatorInfo).forEach(uhcPlayer -> fancyMessage.text(SpecInfoData.translate(p, p.getHealth() - e.getFinalDamage(), damager, SpecInfoData.DAMAGE_PLAYER)).send(uhcPlayer.getPlayer()));
        } else if (e.getDamager() instanceof Arrow) {
            Arrow a = (Arrow) e.getDamager();
            if (a.getShooter() instanceof Player) {
                Player damager = (Player) a.getShooter();
                UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isSpectator).filter(UHCPlayer::isReceivingSpectatorInfo).forEach(uhcPlayer -> fancyMessage.text(SpecInfoData.translate(p, p.getHealth() - e.getFinalDamage(), damager, SpecInfoData.DAMAGE_PLAYER_BOW)).send(uhcPlayer.getPlayer()));
            }
        }
    }


    HashMap<UUID, HashSet<Block>> brokenBlocks = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if(!isEnabled()) return;
        Player p = e.getPlayer();
        if (brokenBlocks.containsKey(p.getUniqueId())) {
            if (brokenBlocks.get(p.getUniqueId()).contains(e.getBlock())) return;
        }

        if (e.getBlock().getType() == Material.DIAMOND_ORE) {
            int diamonds = 0;
            for (int x = -2; x < 2; x++) {
                for (int y = -2; y < 2; y++) {
                    for (int z = -2; z < 2; z++) {
                        Block block = e.getBlock().getLocation().add(x, y, z).getBlock();
                        if (block.getType() == Material.DIAMOND_ORE) {
                            diamonds++;
                            if (brokenBlocks.containsKey(p.getUniqueId())) {
                                HashSet<Block> blocks = brokenBlocks.get(p.getUniqueId());
                                blocks.add(block);
                                brokenBlocks.put(p.getUniqueId(), blocks);
                            } else {
                                HashSet<Block> blocks = new HashSet<>();
                                blocks.add(block);
                                brokenBlocks.put(p.getUniqueId(), blocks);
                            }
                        }
                    }
                }
            }

            ChatUtils.sendMiningMessage(true, p, diamonds);

        } else if (e.getBlock().getType() == Material.GOLD_ORE) {
            int gold = 0;
            for (int x = -2; x < 2; x++) {
                for (int y = -2; y < 2; y++) {
                    for (int z = -2; z < 2; z++) {
                        Block block = e.getBlock().getLocation().add(x, y, z).getBlock();
                        if (block.getType() == Material.GOLD_ORE) {
                            gold++;
                            if (brokenBlocks.containsKey(p.getUniqueId())) {
                                HashSet<Block> blocks = brokenBlocks.get(p.getUniqueId());
                                blocks.add(block);
                                brokenBlocks.put(p.getUniqueId(), blocks);
                            } else {
                                HashSet<Block> blocks = new HashSet<>();
                                blocks.add(block);
                                brokenBlocks.put(p.getUniqueId(), blocks);
                            }
                        }
                    }
                }
            }

            ChatUtils.sendMiningMessage(false, p, gold);

        }
    }


}
