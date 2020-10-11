package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.punishment.AbstractPunishment;
import com.nightshadepvp.core.punishment.Punishment;
import com.nightshadepvp.core.punishment.PunishmentHandler;
import com.nightshadepvp.core.punishment.PunishmentType;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

public class IllegalTeamSizePunishment extends AbstractPunishment {

    public IllegalTeamSizePunishment() {
        super("Illegal Team Size", Material.IRON_BLOCK, PunishmentType.BAN);

        this.addChild(new Punishment("Illegal Team Size (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                        .name("&5Illegal Team Size (1st Offense)")
                        .lore("&eClick to warn the player for Illegal Team Size").make(),
                        this, Collections.singletonList("warn %player% Illegal Team Size (1st Offense)"), PunishmentType.WARNING),
                20);

        this.addChild(new Punishment("Illegal Team Size (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                        .name("&5Illegal Team Size (2nd Offense)")
                        .lore("&eClick to ban the player for 7d for Illegal Team Size").make(),
                        this, Collections.singletonList("banip %player% 7d Illegal Team Size (2nd Offense)"), PunishmentType.BAN),
                21);
    }
}
