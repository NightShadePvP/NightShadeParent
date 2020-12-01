package me.blok601.nightshadeuhc.listener.misc;

import com.massivecraft.massivecore.store.SenderEntity;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SpectatorListener implements Listener {

	@EventHandler
	public void pickup(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		UHCPlayer gamePlayer = UHCPlayer.get(p);
		if (gamePlayer.isSpectator()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void hit(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getEntity();

			UHCPlayer gamePlayer = UHCPlayer.get(p);
			if(gamePlayer.isInArena()){
				return;
			}
			if (gamePlayer.isSpectator()) {
				e.setCancelled(true);
			}

		}
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			UHCPlayer gamePlayer = UHCPlayer.get(e.getEntity().getUniqueId());
			if(gamePlayer.isInArena()){
				return;
			}
			if (gamePlayer.isSpectator()) {
				e.setCancelled(true);
			}
		}
		
		if(e.getDamager() instanceof Player){
			UHCPlayer gamePlayer = UHCPlayer.get(e.getDamager().getUniqueId());
			if(gamePlayer.isInArena()){
				return;
			}
			if(gamePlayer.isSpectator()){
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		UHCPlayer gamePlayer = UHCPlayer.get(e.getPlayer());
		if (gamePlayer.isSpectator()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		UHCPlayer gamePlayer = UHCPlayer.get(p);
		if (gamePlayer.isSpectator()) {
			e.setCancelled(true);
			p.sendMessage(ChatUtils.message("&cYou can't break items while in spectator mode!"));
		}
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		UHCPlayer gamePlayer = UHCPlayer.get(p);
		if (gamePlayer.isSpectator()){
			e.setCancelled(true);
			p.sendMessage(ChatUtils.message("&cYou can't place items while in spectator mode!"));
		}
	}

	@EventHandler (priority = EventPriority.NORMAL)
	public void leave(PlayerQuitEvent e) {
		UHCPlayer gamePlayer = UHCPlayer.get(e.getPlayer());
		if (gamePlayer.isSpectator()) {
			gamePlayer.unspec();
		}
	}

	@EventHandler
	public void interact(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		UHCPlayer gamePlayer = UHCPlayer.get(p);

		if (gamePlayer.isSpectator()) {
			e.setCancelled(true);
			Random r = ThreadLocalRandom.current();
			ArrayList<Player> players = UHCPlayerColl.get().getAllPlaying().stream().filter(uhcPlayer -> !uhcPlayer.getName().equalsIgnoreCase(p.getName())).map(SenderEntity::getPlayer).collect(Collectors.toCollection(ArrayList::new));
//			UHC.players.stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).forEach(uuid -> players.add(Bukkit.getPlayer(uuid)));
			if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
                    //If they have something in their hand
                    if (players.size() == 0) {
                        p.sendMessage(ChatUtils.message("&cThere are not enough players in the game to do this!"));
                        return;
                    }
                    Player loc = players.get(r.nextInt(players.size()));
                    if (loc == null) {
                        p.sendMessage(ChatUtils.message("&cThere was a problem randomly selecting a player!"));
                        return;
                    }

                    p.teleport(loc);
                    p.sendMessage(ChatUtils.message("&6Teleported to&8: &3" + loc.getName()));
                }else{
                	e.setCancelled(true);
				}
			}
		}
	}

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) {
            return;
        }

        Player p = e.getPlayer();
        UHCPlayer uhcPlayer = UHCPlayer.get(p);
        if (!uhcPlayer.isSpectator()) {
            return;
        }

        Rank rank = NSPlayer.get(p).getRank();
        if (!rank.isStaff(rank)) {
            if (e.getTo().getY() < 40) {
				e.setTo(e.getFrom());
				p.sendMessage(ChatUtils.message("&cYou can not go this low with your current rank!"));
				return;

			}            }

            double newX = Math.abs(e.getTo().getX());
            double newZ = Math.abs(e.getTo().getZ());
            if (rank == Rank.DRAGON) { // 100 x 100
                if (newX > 100 || newZ > 100) {
                    e.setTo(e.getFrom());
                    p.sendMessage(ChatUtils.message(rank.getPrefix() + " &cranks must stay within &b100 &8x&b 100&c!"));
                    return;
                }
            } else if (rank == Rank.COMET) {
                if (newX > 300 || newZ > 300) {
                    e.setTo(e.getFrom());
                    p.sendMessage(ChatUtils.message(rank.getPrefix() + " &cranks must stay within &b300 &8x&b 300&c!"));
                    return;
                }
            } else if (rank == Rank.GUARDIAN) {
                if (newX > 500 || newZ > 500) {
                    e.setTo(e.getFrom());
                    p.sendMessage(ChatUtils.message(rank.getPrefix() + " &cranks must stay within &b500 &8x&b 500&c!"));
                    return;
                }
            }
    }
}
