package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.component.ComponentHandler;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class KrenzinatorScenario extends Scenario{

    private UHC plugin;
    ComponentHandler componentHandler;
    public KrenzinatorScenario(UHC plugin, ComponentHandler componentHandler) {
        super("Krenzinator", "9 redstone blocks can be crafted into a diamond, horses are disabled, crafting a diamond sword damages you 0.5 hearts, eggs do 0.5 hearts of damage, nether is off", new ItemBuilder(Material.REDSTONE).name("Krenzinator").make());
        this.plugin = plugin;
        this.componentHandler = componentHandler;
    }

    @Override
    public void onToggle(boolean newState) {
        if(newState){
            //Register crafts
            ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.DIAMOND));
            recipe.addIngredient(9, Material.REDSTONE_BLOCK);
            plugin.getServer().addRecipe(recipe); //Added recipe

            componentHandler.getComponent("Horses").setEnabled(false); //Disabled horses
            componentHandler.getComponent("Nether").setEnabled(false); //Disabled nether

        }else{
            plugin.getServer().clearRecipes();
            componentHandler.getComponent("Horses").setEnabled(true);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        if(!isEnabled()) return;
        Player player = (Player) event.getWhoClicked();
        if(event.getClickedInventory() == null || event.getCurrentItem() == null) return;

       if(event.getCurrentItem().getType() == Material.DIAMOND_SWORD){
           if(event.getResult() == Event.Result.ALLOW){
               player.damage(2);
               sendMessage(player, "&eYou have been damaged for crafting a Diamond Sword!");
               return;
           }
       }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if(!isEnabled()) return;

        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();
        if(!(damager instanceof Egg)) return;
        if(!(damaged instanceof Player)) return;

        event.setDamage(1);
    }
}
