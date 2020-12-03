package me.blok601.nightshadeuhc.util;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.fanciful.FancyMessage;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.HelpOP;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scenario.Scenario;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;


/**
 * Created by Blok on 4/6/2017.
 */
public class ChatUtils {

    private static boolean chatFrozen;

    public static boolean isChatFrozen() {
        return chatFrozen;
    }

    public static void setChatFrozen(boolean chatFrozen) {
        ChatUtils.chatFrozen = chatFrozen;
    }

    public static void sendRules(){
        new BukkitRunnable(){
            int counter = 8;

            @Override
            public void run() {
                if(counter >= 0){
                    if(counter == 7){
                        Bukkit.broadcastMessage(ChatUtils.message("&51. Use of xray mods/cave finders/xray texture packs/hacked clients will result into a permanent ban + UBL from the Network. Benefiting from it (teammate xraying) will result into a 2-3 day ban. Lag machines (placing lava/water) to create server lag/player lag will result in a temporary ban/permanent ban from the Network."));
                    } else if (counter == 6){
                        Bukkit.broadcastMessage(ChatUtils.message("&32. Stalking is allowed but not excessivly. Camping allowed, but not during Meetup. iPvP is not allowed (hurting another player before actual PvP) & will result in a ban. Cross teaming is allowed in team games. Spoiling player's coords is not allowed. You may kill your teammate in non-friendly fire team games."));
                    } else if(counter == 5){
                        Bukkit.broadcastMessage(ChatUtils.message("&53. Skybases allowed below 100x100y. Strip mining/pokeholding below y32 is not allowed. Mining to sounds/player tags allowed. Rollercoastering allowed. Do not excessibly steal/trade. Do not stalk famous ranks (YouTube, Twitch, Staff, etc)."));
                    }else if(counter == 4){
                        Bukkit.broadcastMessage(ChatUtils.message("&34. Teaming in FFA games is not allowed. Please use /helpop or /report to report hackers or any help. Do not re log while scattering. If you re log, you have 5 minutes to get back into the game. Final heal: 15 minutes, PvP enabled: 20 minutes."));
                    }else if(counter == 3){
                        Bukkit.broadcastMessage(ChatUtils.message("&55. If you die due to your lag, not server lag, you will not get a heal. If you die to a hacker (that we know of)/server lag, you may ask for a heal/respawn and get it. Do not hackusate in chat. Use /helpop or /report. It is a random scatter."));
                    }else if(counter == 2){
                        Bukkit.broadcastMessage(ChatUtils.message("&36. Using F3 + A in order to xray is against the rules and bannable!"));
                    }else if(counter == 1){
                        Bukkit.broadcastMessage(ChatUtils.message("&57. Check the match post to see the border shrinks + gamemodes/scenarios. For any questions, ask the Host, other than that look at the match post. There is no final feed. Lying to Host will result in a ban/mute. Have fun & thank your Host."));
                    }else if(counter == 0){
                        counter = -10;
                        this.cancel();
                    }
                }
                counter--;
            }
        }.runTaskTimer(UHC.get(), 0, Util.TICKS);
    }

    public static void sendAll(String s){
        Bukkit.getOnlinePlayers().forEach(player ->{
          player.sendMessage(ChatUtils.message(s));
        });
    }

    public static String message(String s) {
        String prefix = format("&5UHC&8 » ");
        return format(prefix + "&b" + s);
    }

    public static String format(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }


    public static String helpBotMessage(String string){
        String prefix = ChatColor.BLUE + "[HelpBot] ";
        return ChatColor.translateAlternateColorCodes('&', prefix + string);
    }

    public static void sendMiningMessage(boolean diamonds, Player player, int amt) {
        if (diamonds) {
            FancyMessage fancyMessage = new FancyMessage(format("&5[M]&8» &3" + player.getName() + " &7found &6" + amt + " &bdiamonds"));
            fancyMessage.command("/tp " + player.getName());
            fancyMessage.tooltip(format("&eClick to teleport to the player"));
            UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isSpectator).filter(UHCPlayer::isReceivingMiningAlerts).forEach(uhcPlayer -> fancyMessage.send(uhcPlayer.getPlayer()));
        } else {
            FancyMessage fancyMessage = new FancyMessage((format("&5[M]&8» &3" + player.getName() + " &7found &6" + amt + " &6gold")));
            fancyMessage.command("/tp " + player.getName());
            fancyMessage.tooltip(format("&eClick to teleport to the player"));
            UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isSpectator).filter(UHCPlayer::isReceivingMiningAlerts).forEach(uhcPlayer -> fancyMessage.send(uhcPlayer.getPlayer()));
        }
    }

    public static void sendCommandSpyMessage(String cmd, Player player){
        UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isReceivingCommandSpy).forEach(uhcPlayer -> uhcPlayer.msg("&5CmdSpy&8» &7" + player.getName() + " ran &9" + cmd));
    }

   public static void broadcastArenaMessage(String message){
       UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isInArena).forEach(uhcPlayer -> uhcPlayer.msg(sendArenaMessage(message)));
   }

   public static String sendArenaMessage(String msg){
       return format("&5Arena &8» &b" + msg);
   }

    public static String generateTeamColor() {
        Random random = new Random();
        return TeamManager.getInstance().getPossibleColors().get(random.nextInt(TeamManager.getInstance().getPossibleColors().size()));
    }

    public static void sendAllNoPrefix(String msg){
        Bukkit.getOnlinePlayers().forEach(o -> o.sendMessage(ChatUtils.format(msg)));
    }

    public static void sendBorderMessage(String msg){
        String prefix = "&5Border&8 »&b";
        sendAllNoPrefix(prefix + msg);
    }

    public static void sendAllScenarioMessage(String message, Scenario scenario) {
        Bukkit.getOnlinePlayers().forEach(o -> scenario.sendMessage(o, message));
    }

    public static void sendHelpOPMessage(Player p, String message, HelpOP helpOP){
        Stream<? extends Player> players = Bukkit.getOnlinePlayers().stream().filter(o -> NSPlayer.get(o.getUniqueId()).hasRank(Rank.TRIAL)).filter(o -> UHCPlayer.get(o.getUniqueId()).isReceiveHelpop());
        FancyMessage fancyMessage = new FancyMessage(format( "&8[&cHelpOP&8] &c"+  p.getName() + ": "+ ChatColor.YELLOW + message));
        fancyMessage.suggest("/hr " + helpOP.getId() + " ");
        fancyMessage.tooltip(format("&eClick to respond to this HelpOP"));
        players.forEach(fancyMessage::send);
    }

}
