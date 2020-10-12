package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

public class RandomTeamRulesPunishment extends AbstractPunishment {

    public RandomTeamRulesPunishment() {
        super("Breaking Random Teams Rules", Material.BONE, OffenseType.GAMEPLAY);

        this.addChild(new Punishment("Breaking Random Teams Rules (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                        .name("&5Breaking Random Teams Rules (1st Offense)")
                        .loreWithNuke("&eClick to ban the player for 7d for breaking random teams rules (1st Offense)").make(), this,
                        Collections.singletonList("banip %player% 7d Breaking Random Teams Rules (1st Offense)"), PunishmentType.BAN,
                        this.getOffenseType()),
                20);
    }
}
