package me.blok601.nightshadeuhc.command.player;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

public class HelpopCommand implements UHCCommand{

	private HashSet<UUID> helpopCooldown = new HashSet<>();

	@Override
	public String[] getNames() {
		return new String[]{
				"helpop", "request"
		};
	}

	@Override
	public void onCommand(CommandSender sender, Command cmd, String l, String[] args) {
		Player p = (Player) sender;
		NSPlayer user = NSPlayer.get(p.getUniqueId());
		if(user.getRank().getValue() >= Rank.TRIAL.getValue()){
			p.sendMessage(ChatColor.DARK_AQUA + "[UHC] " + ChatColor.RED + "Staff members can't speak in helpop! Use /sc!");
			return;
		}else{

		    if(helpopCooldown.contains(p.getUniqueId())){
		        p.sendMessage(ChatUtils.message("&cYou can only use helpop every 3 seconds!"));
		        return;
            }



			if(args.length == 0){
				p.sendMessage(ChatColor.DARK_AQUA + "[UHC] " + ChatColor.RED + "Usage: /helpop <message>");
				return;
			}else{

				if(GameManager.get().getHelpOpMutes().containsKey(p.getUniqueId())){
					if(!GameManager.get().getHelpopMuteReasons().containsKey(p.getUniqueId())){
						//Check if they both have it
						//GameManager.get().getHelpopMuteReasons().remove(p.getUniqueId());
						GameManager.get().getHelpOpMutes().remove(p.getUniqueId());
						return;
					}

					//They both have
					p.sendMessage(ChatUtils.message("&cYou are currently helpop muted for&8: &3" + TimeUtils.formatSeconds(GameManager.get().getHelpOpMutes().get(p.getUniqueId())) + " &efor&8: &3" + GameManager.get().getHelpopMuteReasons().get(p.getUniqueId())));
					return;
				}

				StringBuilder message = new StringBuilder();

				for(int i = 0; i < args.length; i++){
					message.append(args[i]).append(" ");
				}

				String reason = message.toString().trim();


				Bukkit.getOnlinePlayers().stream().filter(o -> NSPlayer.get(o.getUniqueId()).hasRank(Rank.TRIAL)).filter(o -> UHCPlayer.get(o.getUniqueId()).isReceiveHelpop()).forEach(o -> o.sendMessage(ChatUtils.format( "&8[&cHelpOP&8] &c"+  p.getName() + ": "+ ChatColor.YELLOW + reason)));
                this.helpopCooldown.add(p.getUniqueId());
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        helpopCooldown.remove(p.getUniqueId());
                    }
                }.runTaskLater(UHC.get(), 20*5);
				p.sendMessage(ChatColor.DARK_AQUA + "[UHC] " + ChatColor.YELLOW + "Your message was sent to the online moderators");
				return;
			}
		}
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

	@Override
	public Rank getRequiredRank() {
		return null;
	}

	@Override
	public boolean hasRequiredRank() {
		return false;
	}
}
