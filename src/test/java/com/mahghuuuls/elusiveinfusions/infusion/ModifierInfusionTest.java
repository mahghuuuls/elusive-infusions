package com.mahghuuuls.elusiveinfusions.infusion;

import com.mahghuuuls.elusiveinfusions.roster.Roster;
import com.mahghuuuls.elusiveinfusions.roster.Tier;
import com.mahghuuuls.everfillingflasks.api.FlaskBonuses;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Runs the real contribution code against the real Everfilling Flasks bonus accumulator. It
 * cannot show what the flask then does with the totals; that is observed in the game.
 */
class ModifierInfusionTest {

    private static final String HEADER = "family,display_name,kind,unique,requires_mod,effect_id,"
            + "std_plants,std_cost,std_bonuses,std_level,std_seconds,"
            + "conc_plants,conc_cost,conc_bonuses,conc_level,conc_seconds,color\n";

    @Test
    void eachChannelReachesItsOwnTotal() throws IOException {
        Roster roster = Roster.parse(new StringReader(HEADER
                + "all,All,modifier,yes,,,1xp,4,healing:+0.11;drink_speed:-0.22;hit_resistance:+0.33;"
                + "recharge_speed:-0.44;effect_power:+0.55,,,,,,,,#808080\n"));
        ModifierInfusion infusion = new ModifierInfusion(roster.family("all"), Tier.STANDARD,
                roster.family("all").values(Tier.STANDARD));
        FlaskBonuses bonuses = new FlaskBonuses();

        infusion.contribute(null, null, bonuses);

        assertEquals(0.11F, bonuses.healingSum(), 1e-6F);
        assertEquals(-0.22F, bonuses.drinkSpeedSum(), 1e-6F);
        assertEquals(0.33F, bonuses.hitResistanceSum(), 1e-6F);
        assertEquals(-0.44F, bonuses.rechargeSpeedSum(), 1e-6F);
        assertEquals(0.55F, bonuses.effectPowerSum(), 1e-6F);
        assertEquals(0, bonuses.maxChargesFlat());
        assertEquals(4, infusion.potencyCost(null));
    }

    @Test
    void twoCopiesAddUp() throws IOException {
        Roster roster = Roster.parse(new StringReader(HEADER
                + "tide,Tide,modifier,no,,,1xp,2,healing:+0.10,,,,,,,,#808080\n"));
        ModifierInfusion infusion = new ModifierInfusion(roster.family("tide"), Tier.STANDARD,
                roster.family("tide").values(Tier.STANDARD));
        FlaskBonuses bonuses = new FlaskBonuses();

        infusion.contribute(null, null, bonuses);
        infusion.contribute(null, null, bonuses);

        assertEquals(0.20F, bonuses.healingSum(), 1e-6F);
    }
}
