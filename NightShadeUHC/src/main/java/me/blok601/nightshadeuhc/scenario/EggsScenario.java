    package me.blok601.nightshadeuhc.scenario;

    import com.massivecraft.massivecore.util.MUtil;
    import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
    import me.blok601.nightshadeuhc.util.ChatUtils;
    import me.blok601.nightshadeuhc.util.ItemBuilder;
    import org.bukkit.Location;
    import org.bukkit.Material;
    import org.bukkit.World;
    import org.bukkit.entity.Chicken;
    import org.bukkit.entity.Egg;
    import org.bukkit.entity.EntityType;
    import org.bukkit.entity.Projectile;
    import org.bukkit.event.EventHandler;
    import org.bukkit.event.entity.EntityDeathEvent;
    import org.bukkit.event.entity.ProjectileHitEvent;
    import org.bukkit.inventory.ItemStack;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;
    import java.util.concurrent.ThreadLocalRandom;


    public class EggsScenario extends Scenario implements StarterItems {
        public EggsScenario() {
            super("Eggs", "Throwing an Egg Spawns a random mob, chickens have a change of dropping eggs on death.", new ItemBuilder(Material.EGG).name(ChatUtils.format("Eggs")).make());
        }

        @EventHandler
        public void on(ProjectileHitEvent e) {
            Projectile pr = e.getEntity();
            if (!isEnabled()) return;
            if (!(pr instanceof Egg)) {
                return;
            }
            Random rand = ThreadLocalRandom.current();
            ArrayList<EntityType> types = new ArrayList<EntityType>();

            for (EntityType type : EntityType.values()) {
                if (!type.isAlive() || !type.isSpawnable()) {
                    continue;
                }

                types.add(type);
            }
            EntityType type = types.get(rand.nextInt(types.size()));

            // get the location and world the projectile hit in.
            Location loc = pr.getLocation();
            World world = pr.getWorld();

            world.spawnEntity(loc, type);
        }

        @EventHandler
        public void onDeath(EntityDeathEvent e) {
            if (!isEnabled()) return;

            if (!(e.getEntity() instanceof Chicken)) {
                return;
            }

            Random rand = new Random();
            double chance = 0.05;

            if (rand.nextDouble() > chance) {
                return;
            }

            e.getDrops().add(new ItemStack(Material.EGG));

        }
        @Override
        public List<ItemStack> getStarterItems() {
            return MUtil.list(new ItemBuilder(Material.EGG).amount(16).make());
        }

        @Override
        public int getStartingLevels() {
            return 0;
        }
    }

