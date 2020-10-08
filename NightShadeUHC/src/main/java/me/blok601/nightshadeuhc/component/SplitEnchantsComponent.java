package me.blok601.nightshadeuhc.component;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.utils.PlayerUtils;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

public class SplitEnchantsComponent extends Component implements UHCCommand {


    public SplitEnchantsComponent() {
        super("Split Enchants", Material.BOOK, false, "Toggle whether players can do /splitenchants to split their enchanted books");
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "splitenchants"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player player = (Player) s;
        if(!this.isEnabled()){
            player.sendMessage(ChatUtils.message("&cSplitting enchants is currently disabled!"));
            return;
        }

        ItemStack hand = player.getItemInHand();

        if (hand == null || !hand.getType().equals(Material.ENCHANTED_BOOK)) {
            player.sendMessage(ChatUtils.message("&cYou are not holding an enchanted book."));
            return;
        }

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) hand.getItemMeta();
        Map<Enchantment, Integer> storedEnchants = meta.getStoredEnchants();

        if (storedEnchants.isEmpty()) {
            player.sendMessage(ChatUtils.message("&cYou are not holding an enchanted book."));
            return;
        }

        int size = storedEnchants.size();

        if (size == 1) {
           player.sendMessage(ChatUtils.message("&cThat book only has one enchantment."));
           return;
        }

        if (player.getLevel() < size) {
            player.sendMessage(ChatUtils.message("&cYou require &b" + size + " &clevel(s)"));
        }

        player.setLevel(player.getLevel() - size);
        player.setItemInHand(null);

        for (Map.Entry<Enchantment, Integer> entry : storedEnchants.entrySet()) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) book.getItemMeta();

            bookMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            book.setItemMeta(bookMeta);

            PlayerUtils.giveItem(book, player);
        }

        player.sendMessage(ChatUtils.message("&bEnchants have been split"));
    }

    @Override
    public boolean playerOnly() {
        return true;
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
