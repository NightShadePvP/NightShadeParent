package com.nightshadepvp.core.punishment.type.mute;

import com.nightshadepvp.core.punishment.AbstractPunishment;
import com.nightshadepvp.core.punishment.Punishment;
import com.nightshadepvp.core.punishment.PunishmentHandler;
import com.nightshadepvp.core.punishment.PunishmentType;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class EncouragingSuicidePunishment extends AbstractPunishment {

    public EncouragingSuicidePunishment() {
        super("Encouraging Suicide", new ItemStack(Material.SKULL_ITEM, 1, (short) 0), PunishmentType.MUTE);

        this.addChild(new Punishment("Encouraging Suicide (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Encouraging Suicide (1st Offense)")
                .lore("&eClick to mute the player for 1d for Encouraging Suicide").make(),
                this, Collections.singletonList("mute %player% 1d Encouraging Suicide (1st Offense)"), PunishmentType.MUTE
        ), 20);

        this.addChild(new Punishment("Encouraging Suicide (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Encouraging Suicide (2nd Offense)")
                .amount(2)
                .lore("&eClick to mute the player for 7d for Encouraging Suicide").make(),
                this, Collections.singletonList("mute %player% 7d Encouraging Suicide (2nd Offense)"), PunishmentType.MUTE
        ), 20);
    }
}
