package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blok on 12/3/2017.
 */
public class InfiniteEnchanterScenario extends Scenario implements StarterItems {


    public InfiniteEnchanterScenario() {
        super("Infinite Enchanter", "All players start with 128 Bookshelves, Infinite XP Levels, 64 Anvils and 64 Enchantment Tables", new ItemBuilder(Material.ENCHANTMENT_TABLE).name("Infinite Enchanter").make());
    }

//    @EventHandler
//    public void onStart(GameStartEvent e){
//        if(!isEnabled()){
//            return;
//        }
//        //UHCPlayerColl.get().getAllPlaying().forEach(uhcPlayer -> uhcPlayer.getPlayer().setLevel(27000));
//    }

    @Override
    public List<ItemStack> getStarterItems() {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(new ItemBuilder(Material.BOOKSHELF).amount(64).make());
        itemStacks.add(new ItemBuilder(Material.LAPIS_BLOCK).amount(64).make());
        for (int i = 0; i < 3; i++) {
            itemStacks.add(new ItemBuilder(Material.ENCHANTMENT_TABLE).amount(64).make());
            itemStacks.add(new ItemBuilder(Material.ANVIL).amount(64).make());
        }
        return itemStacks;
    }

    @Override
    public int getStartingLevels() {
        return 27000;
    }
}
