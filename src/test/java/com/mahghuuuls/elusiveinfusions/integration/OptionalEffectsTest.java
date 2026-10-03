package com.mahghuuuls.elusiveinfusions.integration;

import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Roster;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the availability decision with a fake registry. It cannot show the registry order in
 * the game or the warning line; Campaign C observes those.
 */
class OptionalEffectsTest {

    private static final String HEADER = "family,display_name,kind,unique,requires_mod,effect_id,"
            + "std_plants,std_cost,std_bonuses,std_level,std_seconds,"
            + "conc_plants,conc_cost,conc_bonuses,conc_level,conc_seconds,color\n";

    @Test
    void onlyEffectFamiliesWithAnUnregisteredEffectAreMissing() throws IOException {
        Roster roster = Roster.parse(new StringReader(HEADER
                + "speedy,Speedy,effect,yes,,minecraft:speed,1xp,3,,1,10,,,,,,#808080\n"
                + "climb,Climb,effect,yes,potioncore,potioncore:climb,1xq,2,,1,30,,,,,,#808080\n"
                + "heart,Heart,modifier,yes,,,1xr,3,healing:+0.3,,,,,,,,#808080\n"));
        final Set<String> registered = new HashSet<String>(Arrays.asList("minecraft:speed"));

        List<InfusionSpec> missing = OptionalEffects.missingEffects(roster.specs(), new OptionalEffects.EffectLookup() {
            @Override
            public boolean isRegistered(String effectId) {
                if (effectId == null) {
                    throw new IllegalArgumentException("a modifier reached the lookup");
                }
                return registered.contains(effectId);
            }
        });

        assertEquals(1, missing.size());
        assertEquals("climb", missing.get(0).family());
    }

    /** Every bundled effect family reaches the lookup with its id, and none of the modifiers does. */
    @Test
    void everyBundledEffectFamilyIsCheckedOnce() {
        final List<String> asked = new java.util.ArrayList<String>();
        List<InfusionSpec> missing = OptionalEffects.missingEffects(Roster.bundled().specs(), new OptionalEffects.EffectLookup() {
            @Override
            public boolean isRegistered(String effectId) {
                asked.add(effectId);
                return !effectId.startsWith("potioncore:");
            }
        });
        assertEquals(14, asked.size());
        assertEquals(4, missing.size());
        assertEquals("canopy", missing.get(0).family());
    }

    /** Every optional family's effect id names its own mod, so an absent mod never leaves a dangling id. */
    @Test
    void bundledOptionalFamiliesNameTheirOwnMod() {
        for (InfusionSpec spec : Roster.bundled().specs()) {
            if (spec.requiresMod() != null) {
                assertTrue(spec.effectId().startsWith(spec.requiresMod() + ":"),
                        spec.family() + " needs " + spec.requiresMod() + " but uses " + spec.effectId());
            } else if (spec.effectId() != null) {
                assertTrue(spec.effectId().startsWith("minecraft:"), spec.family());
            }
        }
    }
}
