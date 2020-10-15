package me.blok601.nightshadeuhc.scenario;

import com.nightshadepvp.core.utils.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeistyBoysScenario extends Scenario {


    public FeistyBoysScenario() {
        super("FeistyBoys", "Pickaxes are enchanted with Sharpness 3 and Swords and Axes are uncraftable", new ItemBuilder(Material.IRON_PICKAXE).name(ChatUtils.format("FeistyBoys")).make());
    }


    List<Material> types = new ArrayList<>(Arrays.asList(Material.WOOD_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE));
    List<Material> nocraft = new ArrayList<>(Arrays.asList(Material.WOOD_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE, Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.WOOD_SWORD, Material.STONE_SWORD));



    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {

        if (!isEnabled()) return;

        if (types.contains(e.getRecipe().getResult().getType())) {
            e.getInventory().setResult(new ItemBuilder(e.getRecipe().getResult()).enchantment(Enchantment.DAMAGE_ALL, 3).enchantment(Enchantment.DURABILITY, 3).
                    make());
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (!isEnabled()) return;
        if (e.getWhoClicked() == null) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getInventory() == null || e.getClickedInventory() == null) return;
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        Player p = (Player) e.getWhoClicked();

        if (nocraft.contains(e.getRecipe().getResult().getType())) {
            e.setCancelled(true);
            p.closeInventory();
            p.sendMessage(ChatUtils.format(getPrefix() + "&fYou cannot craft &cSwords or Axes&f, did you read the matchpost?"));
        }
    }
}