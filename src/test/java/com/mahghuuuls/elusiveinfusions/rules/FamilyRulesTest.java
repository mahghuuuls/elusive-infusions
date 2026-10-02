package com.mahghuuuls.elusiveinfusions.rules;

import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Roster;
import com.mahghuuuls.elusiveinfusions.roster.Tier;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FamilyRulesTest {

    private static final String HEADER = "family,display_name,kind,unique,requires_mod,effect_id,"
            + "std_plants,std_cost,std_bonuses,std_level,std_seconds,"
            + "conc_plants,conc_cost,conc_bonuses,conc_level,conc_seconds\n";

    private static Roster roster() throws IOException {
        return Roster.parse(new StringReader(HEADER
                + "uniq,Uniq,modifier,yes,,,1xp,3,healing:+0.3,,,1xp,5,healing:+0.5,,\n"
                + "stack,Stack,modifier,no,,,1xq,2,healing:+0.1,,,1xq,4,healing:+0.2,,\n"
                + "other,Other,effect,yes,,minecraft:speed,1xr,3,,1,10,1xr,5,,2,10\n"));
    }

    @Test
    void aUniqueFamilyRefusesACopyOfItself() throws IOException {
        InfusionSpec uniq = roster().family("uniq");
        assertTrue(FamilyRules.conflict(uniq, Tier.STANDARD, uniq, Tier.STANDARD));
        assertTrue(FamilyRules.conflict(uniq, Tier.CONCENTRATED, uniq, Tier.CONCENTRATED));
    }

    @Test
    void standardAndConcentratedOfOneFamilyNeverShare() throws IOException {
        Roster roster = roster();
        InfusionSpec uniq = roster.family("uniq");
        assertTrue(FamilyRules.conflict(uniq, Tier.STANDARD, uniq, Tier.CONCENTRATED));
        assertTrue(FamilyRules.conflict(uniq, Tier.CONCENTRATED, uniq, Tier.STANDARD));
        InfusionSpec other = roster.family("other");
        assertTrue(FamilyRules.conflict(other, Tier.STANDARD, other, Tier.CONCENTRATED));
    }

    @Test
    void aStackableFamilyAcceptsCopiesButNotTheOtherTier() throws IOException {
        InfusionSpec stack = roster().family("stack");
        assertFalse(FamilyRules.conflict(stack, Tier.STANDARD, stack, Tier.STANDARD));
        assertFalse(FamilyRules.conflict(stack, Tier.CONCENTRATED, stack, Tier.CONCENTRATED));
        assertTrue(FamilyRules.conflict(stack, Tier.STANDARD, stack, Tier.CONCENTRATED));
    }

    @Test
    void differentFamiliesNeverConflict() throws IOException {
        Roster roster = roster();
        assertFalse(FamilyRules.conflict(roster.family("uniq"), Tier.STANDARD, roster.family("other"), Tier.STANDARD));
        assertFalse(FamilyRules.conflict(roster.family("uniq"), Tier.CONCENTRATED, roster.family("stack"), Tier.STANDARD));
        assertFalse(FamilyRules.conflict(roster.family("stack"), Tier.STANDARD, roster.family("other"), Tier.CONCENTRATED));
    }

    /** The full table over the shipped roster: the answer depends only on family, tier, and the Unique flag. */
    @Test
    void bundledRosterFollowsTheTable() {
        Roster roster = Roster.bundled();
        int conflicts = 0;
        for (InfusionSpec a : roster.specs()) {
            for (Tier ta : Tier.values()) {
                if (!a.hasTier(ta)) {
                    continue;
                }
                for (InfusionSpec b : roster.specs()) {
                    for (Tier tb : Tier.values()) {
                        if (!b.hasTier(tb)) {
                            continue;
                        }
                        boolean expected = a == b && (ta != tb || a.unique());
                        assertEquals(expected, FamilyRules.conflict(a, ta, b, tb),
                                a.family() + " " + ta + " vs " + b.family() + " " + tb);
                        if (expected) {
                            conflicts++;
                        }
                    }
                }
            }
        }
        // 24 Unique families with two tiers give 4 conflicting ordered pairs each; the 5 Stackable
        // families have one tier and give none.
        int uniqueTwoTier = 0;
        int stackable = 0;
        for (InfusionSpec spec : roster.specs()) {
            if (spec.unique() && spec.hasTier(Tier.CONCENTRATED)) {
                uniqueTwoTier++;
            } else if (!spec.unique()) {
                stackable++;
            }
        }
        assertEquals(24, uniqueTwoTier);
        assertEquals(5, stackable);
        assertEquals(24 * 4, conflicts);
    }
}
