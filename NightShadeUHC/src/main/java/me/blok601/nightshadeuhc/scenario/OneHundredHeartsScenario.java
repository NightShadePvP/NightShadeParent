package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;

public class OneHundredHeartsScenario extends Scenario {
    public OneHundredHeartsScenario() {
        super("OneHundredHearts", "Players start with 100 hearts", new ItemBuilder(Material.GOLDEN_APPLE).name("One Hundred Hearts").make());
    }

    public void onStart(GameStartEvent event) {
        if (!isEnabled()) return;

        if (getScenarioManager().getScen("Chicken").isEnabled()) return;

        UHCPlayerColl.get().getAllPlaying().forEach(uhcPlayer -> {
            uhcPlayer.getPlayer().setMaxHealth(200D);
            uhcPlayer.getPlayer().setHealth(uhcPlayer.getPlayer().getMaxHealth());
        });
    }

}
