package me.blok601.nightshadeuhc.scenario;

import com.google.common.collect.Sets;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.UUID;

public class OPsVsWorldScenario extends Scenario{

    private ItemStack[] armor;
    private ItemStack[] items;
    private HashSet<UUID> ops;

    public OPsVsWorldScenario() {
        super("OPs vs World", "There are set OPs who start with items who win by killing all the other players", new ItemBuilder(Material.BEDROCK).name("&eOPs vs World").make());
        ops = Sets.newHashSet();
    }

    @EventHandler
    public void onStart(GameStartEvent event){
        for (UUID uuid : ops){
            Player op = Bukkit.getPlayer(uuid);
            if(op == null) continue;

            op.getInventory().setArmorContents(armor);
            op.getInventory().setContents(items);
            sendMessage(op, "&bYou have received your OP kit!");
        }
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public void setItems(ItemStack[] items) {
        this.items = items;
    }

    public HashSet<UUID> getOps() {
        return ops;
    }
}
