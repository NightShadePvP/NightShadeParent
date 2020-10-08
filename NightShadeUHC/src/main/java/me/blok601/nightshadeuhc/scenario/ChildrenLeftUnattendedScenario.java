package me.blok601.nightshadeuhc.scenario;

import com.nightshadepvp.core.utils.PlayerUtils;
import me.blok601.nightshadeuhc.entity.object.Team;
import me.blok601.nightshadeuhc.event.CustomDeathEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ChildrenLeftUnattendedScenario extends Scenario {

    GameManager gameManager;

    public ChildrenLeftUnattendedScenario(GameManager gameManager) {
        super("Children Left Unattended", "When a teammate dies, the other teammate(s) receive speed I and tamed wolf", new ItemBuilder(Material.BONE).name("Children Left Unattended").make());
        this.gameManager = gameManager;
    }

    @Override
    public void onToggle(boolean newState, Player player) {
        if (newState) {
            if (!gameManager.isIsTeam()) {
                sendMessage(player, "&cThis scenario can't be enabled in a non-teams game!");
                this.setEnabled(false);
            }
        }
    }

    @EventHandler
    public void onDeath(CustomDeathEvent event) {
        if (!isEnabled()) return;
        Player died = event.getKilled();
        Team team = TeamManager.getInstance().getTeam(died);
        if (team == null || team.getMembers().size() == 1) return;


        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.setMainEffect(PotionEffectType.SPEED);
        PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, 180*20, 0);
        potionMeta.addCustomEffect(effect, true);
        potion.setItemMeta(potionMeta);
        for (Player player : team.getOnlineAliveMembers()) {
            if (player.getName().equalsIgnoreCase(died.getName())) continue;
            Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
            wolf.setAngry(false);
            wolf.setSitting(true);
            wolf.setTamed(true);
            wolf.setOwner(player);
            PlayerUtils.giveItem(potion, player);
            sendMessage(player, "&bYou have received a tamed wolf and a speed potion because of the death of &f" + died.getName());

        }
    }


}
