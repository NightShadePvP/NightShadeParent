package me.blok601.nightshadeuhc.component;

import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.gui.setup.HostGUI;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Blok on 12/10/2017.
 */
public class ComponentHandler {

    private GameManager gameManager;
    private ScenarioManager scenarioManager;
    private UHC plugin;

    public ComponentHandler(GameManager gameManager, ScenarioManager scenarioManager, UHC uhc) {
        this.gameManager = gameManager;
        this.scenarioManager = scenarioManager;
        this.plugin = uhc;
    }

    private ArrayList<Component> components;

    public void setup(){
        this.components = new ArrayList<>();
        this.gameManager = GameManager.get();

        addComponent(new AbsorptionComponent(plugin));
        addComponent(new CobbleComponent());
        addComponent(new DeathLightningComponent());
        addComponent(new EnderpearlDamageComponent());
        addComponent(new GodAppleComponent());
        addComponent(new HorseComponent());
        addComponent(new NetherComponent());
        addComponent(new NetherQuartzXPNerfFeature(gameManager));
        addComponent(new SaturationComponent());
        addComponent(new ShearsComponent(gameManager));
        addComponent(new SportsmanshipComponent());
        addComponent(new StripminingComponent());
        addComponent(new StrCompentent());
        addComponent(new HardEnchantComponent());
        addComponent(new CaneBuffComponent());
        addComponent(new FireAspectComponent());
        addComponent(new StatsComponent());
        addComponent(new RecordedRoundComponent(plugin, gameManager));
        addComponent(new SplitEnchantsComponent());
        addComponent(new SpectatorInfoComponent(plugin));
        addComponent(new GoldenHeadConsume());
        addComponent(new FriendlyFireComponent());

        this.components.sort(Comparator.comparing(Component::getName));
    }

    private void addComponent(Component component) {
        this.components.add(component);
        Bukkit.getPluginManager().registerEvents(component, UHC.get());
    }

    public Component getComponent(String name){
        for (Component component : this.components){
            if(component.getName().equalsIgnoreCase(name)){
                return component;
            }
        }

        return null;
    }

    public Component getComponent(Material material) {
        for (Component component : this.components) {
            if (component.getMaterial() == material) {
                return component;
            }
        }

        return null;
    }

    public boolean handleClick(ItemStack stack, InventoryClickEvent e, int slot){

        if (slot == 26) { //main menu slot
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> new HostGUI((Player) e.getWhoClicked(), gameManager, scenarioManager), 1L);

            return false;
        }

        if (getComponent(stack.getType()) != null) {
            Component c = getComponent(stack.getType());

            if(c.isLocked()){
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();
                player.closeInventory();
                player.sendMessage(ChatUtils.message("&cThis component is locked and can't be changed!"));
                return false;
            }

            c.click(e, slot);
        }else{
            return false;
        }

        return true;

    }

    public ArrayList<Component> getComponents() {
        return components;
    }
}
