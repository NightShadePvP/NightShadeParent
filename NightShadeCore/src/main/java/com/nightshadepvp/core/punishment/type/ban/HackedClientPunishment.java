package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.punishment.AbstractPunishment;
import com.nightshadepvp.core.punishment.Punishment;
import com.nightshadepvp.core.punishment.PunishmentHandler;
import com.nightshadepvp.core.punishment.PunishmentType;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

/**
 * Created by Blok on 8/25/2018.
 */
public class HackedClientPunishment extends AbstractPunishment {

    public HackedClientPunishment() {
        super("Hacked Client", Material.DIAMOND_SWORD, PunishmentType.BAN);

        this.addChild(new Punishment("Hacked Client (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Hacked Client &8(&51st Offense&8)")
                .lore("&eBan the player for 2 months for Hacked Client").make(),
                this, Collections.singletonList("banip %player% 2mo Hacked Client (1st Offense)"), PunishmentType.BAN), 20);

        this.addChild(new Punishment("Hacked Client (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .amount(2)
                .name("&5Hacked Client &8(&52nd Offense&8)")
                .lore("&eBan the player for 3 months for Hacked Client").make(),
                this, Collections.singletonList("banip %player% 3mo Hacked Client (2nd Offense)"), PunishmentType.BAN), 21);

        this.addChild(new Punishment("Hacked Client (3rd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .amount(3)
                .name("&5Hacked Client &8(&53rd Offense&8)")
                .lore("&eBan the player permanently for Hacked Client").make(),
                this, Collections.singletonList("banip %player% 1mo Hacked Client (3rd Offense)"), PunishmentType.BAN
        ), 22);
    }
}
