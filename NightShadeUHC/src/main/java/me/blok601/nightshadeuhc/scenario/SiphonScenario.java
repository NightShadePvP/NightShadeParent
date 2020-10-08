package me.blok601.nightshadeuhc.scenario;

import com.google.common.collect.ImmutableList;
import com.nightshadepvp.core.utils.PlayerUtils;
import me.blok601.nightshadeuhc.event.CustomDeathEvent;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class SiphonScenario extends Scenario{

    private Random random;
    private List<Enchantment> enchants;
    public SiphonScenario() {
        super("Siphon", "Whenever you get a kill, you will regenerate 2 hearts, gain 2 levels and get a random tier 1 enchanted book", new ItemBuilder(Material.POTION).durability(16385).name("Siphon").make());
        random = new Random();
        enchants = ImmutableList.of(Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.PROTECTION_PROJECTILE, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.FIRE_ASPECT, Enchantment.DAMAGE_ALL);
    }

    @EventHandler
    public void onDeath(CustomDeathEvent event){
        if(!isEnabled()) return;
        Player dead = event.getKilled();
        Player killer = dead.getKiller();
        if(killer == null) return;

        //Add regen 2 hearts
        killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1, true, true));

        //Give book
        ItemBuilder itemBuilder = new ItemBuilder(Material.ENCHANTED_BOOK).enchantment(enchants.get(random.nextInt(enchants.size())));
        PlayerUtils.giveItem(itemBuilder.make(), killer);

        //Give xp
        killer.setLevel(killer.getLevel() + 2);
        PlayerUtils.playSound(Sound.ORB_PICKUP, killer);
    }


}
