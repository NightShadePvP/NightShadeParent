package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

public class IllegalTeamSizePunishment extends AbstractPunishment {

    public IllegalTeamSizePunishment() {
        super("Illegal Team Size", Material.IRON_BLOCK, OffenseType.GAMEPLAY);

        this.addChild(new Punishment("Illegal Team Size (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                        .name("&5Illegal Team Size (1st Offense)")
                        .lore("&eClick to warn the player for Illegal Team Size").make(),
                        this, Collections.singletonList("warn %player% Illegal Team Size (1st Offense)"), PunishmentType.WARNING,
                        this.getOffenseType()),
                20);

        this.addChild(new Punishment("Illegal Team Size (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                        .name("&5Illegal Team Size (2nd Offense)")
                        .lore("&eClick to ban the player for 7d for Illegal Team Size").make(),
                        this, Collections.singletonList("banip %player% 7d Illegal Team Size (2nd Offense)"), PunishmentType.BAN,
                        this.getOffenseType()),
                21);
    }
}
