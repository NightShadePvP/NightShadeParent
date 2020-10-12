package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

public class ToggleSneakPunishment extends AbstractPunishment {

    public ToggleSneakPunishment() {
        super("ToggleSneak", Material.IRON_BOOTS, OffenseType.GAMEPLAY);

        this.addChild(new Punishment("ToggleSneak (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                        .name("&5ToggleSneak (1st Offense)")
                        .loreWithNuke("&eClick to warn the player for ToggleSneak (1st Offense)").make(), this,
                        Collections.singletonList("warn %player% ToggleSneak (1st Offense)"), PunishmentType.WARNING, this.getOffenseType()),
                20);

        this.addChild(new Punishment("ToggleSneak (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                        .name("&5ToggleSneak (2nd Offense)")
                        .amount(2)
                        .loreWithNuke("&eClick to ban the player for 30d for ToggleSneak (2nd Offense)").make(), this,
                        Collections.singletonList("warn %player% ToggleSneak (2nd Offense)"), PunishmentType.BAN, this.getOffenseType()),
                21);
    }
}
