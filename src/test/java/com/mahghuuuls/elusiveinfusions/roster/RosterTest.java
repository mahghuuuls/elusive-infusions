package com.mahghuuuls.elusiveinfusions.roster;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RosterTest {

    private static final String HEADER = "family,display_name,kind,unique,requires_mod,effect_id,"
            + "std_plants,std_cost,std_bonuses,std_level,std_seconds,"
            + "conc_plants,conc_cost,conc_bonuses,conc_level,conc_seconds\n";

    private static Roster parse(String rows) throws IOException {
        return Roster.parse(new StringReader(HEADER + rows));
    }

    @Test
    void readsAnEffectRowWithBothTiers() throws IOException {
        InfusionSpec spec = parse("swift,Sunspire,effect,yes,,minecraft:speed,"
                + "3xsunspire+3xskyclover,3,,1,10,2xsunspire+1xskyclover,5,,2,12\n").family("swift");

        assertEquals("Sunspire", spec.displayName());
        assertEquals(Kind.EFFECT, spec.kind());
        assertTrue(spec.unique());
        assertNull(spec.requiresMod());
        assertEquals("minecraft:speed", spec.effectId());
        assertEquals("swift_infusion", spec.itemPath(Tier.STANDARD));
        assertEquals("concentrated_swift_infusion", spec.itemPath(Tier.CONCENTRATED));

        TierValues standard = spec.values(Tier.STANDARD);
        assertEquals(3, standard.potencyCost());
        assertEquals(1, standard.level());
        assertEquals(10, standard.seconds());
        assertEquals(2, standard.plants().size());
        assertEquals("sunspire", standard.plants().get(0).plant());
        assertEquals(3, standard.plants().get(0).count());

        TierValues concentrated = spec.values(Tier.CONCENTRATED);
        assertEquals(5, concentrated.potencyCost());
        assertEquals(2, concentrated.level());
        assertEquals(12, concentrated.seconds());
        assertEquals(1, concentrated.plants().get(1).count());
    }

    @Test
    void readsAModifierRowWithABonusAndAPenalty() throws IOException {
        InfusionSpec spec = parse("quick,Quickwater,modifier,no,somemod,,"
                + "2xstreamreed+2xrainthistle,3,drink_speed:+0.50;healing:-0.20,,,,,,,\n").family("quick");

        assertEquals(Kind.MODIFIER, spec.kind());
        assertFalse(spec.unique());
        assertEquals("somemod", spec.requiresMod());
        assertNull(spec.effectId());
        assertFalse(spec.hasTier(Tier.CONCENTRATED));
        Map<Channel, Double> bonuses = spec.values(Tier.STANDARD).bonuses();
        assertEquals(0.50, bonuses.get(Channel.DRINK_SPEED), 1e-9);
        assertEquals(-0.20, bonuses.get(Channel.HEALING), 1e-9);
        assertEquals(2, bonuses.size());
        assertThrows(IllegalArgumentException.class, () -> spec.values(Tier.CONCENTRATED));
    }

    @Test
    void rejectsMalformedRows() {
        assertRejected("a,A,effect,yes,,,1xp,1,,1,10,,,,,\n", "effect without an effect id");
        assertRejected("a,A,modifier,yes,,minecraft:speed,1xp,1,healing:+0.1,,,,,,,\n", "modifier with an effect id");
        assertRejected("a,A,modifier,yes,,,1xp,1,charges:+1,,,,,,,\n", "unknown channel");
        assertRejected("a,A,modifier,yes,,,1xp,1,healing:+0.1;healing:+0.2,,,,,,,\n", "repeated channel");
        assertRejected("a,A,modifier,yes,,,pearl,1,healing:+0.1,,,,,,,\n", "plant without a count");
        assertRejected("a,A,modifier,maybe,,,1xp,1,healing:+0.1,,,,,,,\n", "unique not yes or no");
        assertRejected("a,A,effect,yes,,minecraft:speed,1xp,1,,0,10,,,,,\n", "level below 1");
        assertRejected("a,A,effect,yes,,minecraft:speed,1xp,1,,1,10,,5,,2,10\n", "Concentrated values without plants");
        assertRejected("a,A,effect,yes,,minecraft:speed,1xp,1,,1,10\n", "too few cells");
        assertRejected("a,A,effect,yes,,minecraft:speed,1xp,1,,1,10,,,,,\n"
                + "a,B,effect,yes,,minecraft:haste,1xq,1,,1,10,,,,,\n", "repeated family");
        assertRejected("a,A,effect,yes,,speed:,1xp,1,,1,10,,,,,\n", "effect id without a name");
        assertRejected("a,A,effect,yes,,minecraft:speed,1xp,1,healing:+0.1,1,10,,,,,\n", "effect with bonuses");
        assertRejected("a,A,modifier,yes,,,1xp,1,healing:+0.1,1,,,,,,\n", "modifier with a level");
        assertRejected("a,A,modifier,yes,,,1xp,1,,,,,,,,\n", "modifier without bonuses");
        assertRejected("a,A,modifier,yes,,,1xp,1,healing:0,,,,,,,\n", "zero bonus");
        assertRejected("a,A,modifier,yes,,,1xp,1,healing:NaN,,,,,,,\n", "bonus that is not a number");
        assertRejected("a,A,modifier,yes,,,1xp,1,healing:+0.1;,,,,,,,\n", "trailing bonus separator");
        assertRejected("a,A,modifier,yes,,,1xp+,1,healing:+0.1,,,,,,,\n", "trailing plant separator");
        assertRejected("a,A,modifier,yes,,,0xp,1,healing:+0.1,,,,,,,\n", "plant count below 1");
        assertRejected("a,A,modifier,yes,,,1xp,-1,healing:+0.1,,,,,,,\n", "negative cost");
        assertRejected("a,\"A\",modifier,yes,,,1xp,1,healing:+0.1,,,,,,,\n", "quoted cell");
    }

    @Test
    void acceptsWindowsLineEndingsAndBlankLines() throws IOException {
        Roster roster = Roster.parse(new StringReader(HEADER.replace("\n", "\r\n")
                + "a,A,modifier,no,,,1xp,1,healing:+0.1,,,,,,,\r\n\r\n"
                + "b,B,modifier,no,,,1xq,1,healing:+0.2,,,,,,,\r\n"));
        assertEquals(2, roster.specs().size());
        assertEquals(0.2, roster.family("b").values(Tier.STANDARD).bonuses().get(Channel.HEALING), 1e-9);
    }

    @Test
    void rejectsARenamedColumn() {
        assertThrows(RosterFormatException.class,
                () -> Roster.parse(new StringReader(HEADER.replace("std_cost", "cost"))));
    }

    @Test
    void rejectsAnUnexpectedHeader() {
        assertThrows(RosterFormatException.class,
                () -> Roster.parse(new StringReader("family,name\n")));
    }

    /**
     * Shipped defaults, as approved. These numbers are a second copy of roster cells on purpose:
     * they fail when the bundled file changes, so a change is always a deliberate edit of both.
     */
    @Test
    void bundledRosterCarriesTheApprovedDefaults() {
        Roster roster = Roster.bundled();
        assertEquals(29, roster.specs().size());

        InfusionSpec heartroot = roster.family("heartroot");
        assertTrue(heartroot.unique());
        TierValues heartrootStandard = heartroot.values(Tier.STANDARD);
        assertEquals(3, heartrootStandard.potencyCost());
        assertEquals(0.35, heartrootStandard.bonuses().get(Channel.HEALING), 1e-9);
        assertEquals(-0.30, heartrootStandard.bonuses().get(Channel.DRINK_SPEED), 1e-9);
        assertEquals("[3xdunerose, 3xamberleaf]", describe(heartrootStandard.plants()));
        TierValues heartrootConcentrated = heartroot.values(Tier.CONCENTRATED);
        assertEquals(6, heartrootConcentrated.potencyCost());
        assertEquals(0.55, heartrootConcentrated.bonuses().get(Channel.HEALING), 1e-9);
        assertEquals(-0.35, heartrootConcentrated.bonuses().get(Channel.DRINK_SPEED), 1e-9);

        InfusionSpec tideheart = roster.family("tideheart");
        assertFalse(tideheart.unique());
        assertEquals(2, tideheart.values(Tier.STANDARD).potencyCost());
        assertEquals(0.10, tideheart.values(Tier.STANDARD).bonuses().get(Channel.HEALING), 1e-9);
        assertEquals("[2xtideheart, 2xfrostbell]", describe(tideheart.values(Tier.STANDARD).plants()));

        assertEquals(0.08, roster.family("thawbud").values(Tier.STANDARD).bonuses()
                .get(Channel.RECHARGE_SPEED), 1e-9);
        assertEquals(3, roster.family("thawbud").values(Tier.STANDARD).potencyCost());
        assertEquals(0.15, roster.family("starpetal").values(Tier.STANDARD).bonuses()
                .get(Channel.EFFECT_POWER), 1e-9);
        assertEquals(0.80, roster.family("stillwater").values(Tier.STANDARD).bonuses()
                .get(Channel.HIT_RESISTANCE), 1e-9);
        assertEquals("[3xmoonveil, 2xumbrabud, 1xduskwisp]",
                describe(roster.family("moonveil").values(Tier.STANDARD).plants()));

        InfusionSpec swiftness = roster.family("swiftness");
        assertEquals("minecraft:speed", swiftness.effectId());
        assertEquals(3, swiftness.values(Tier.STANDARD).potencyCost());
        assertEquals(1, swiftness.values(Tier.STANDARD).level());
        assertEquals(10, swiftness.values(Tier.STANDARD).seconds());
        assertEquals(5, swiftness.values(Tier.CONCENTRATED).potencyCost());
        assertEquals(2, swiftness.values(Tier.CONCENTRATED).level());

        InfusionSpec deepwater = roster.family("deepwater");
        assertEquals(30, deepwater.values(Tier.STANDARD).seconds());
        assertEquals(60, deepwater.values(Tier.CONCENTRATED).seconds());

        assertEquals("potioncore", roster.family("canopy").requiresMod());
        assertEquals("jawmsadditions", roster.family("manabloom").requiresMod());
        assertNull(roster.family("frostguard").requiresMod());
        assertEquals(4, roster.family("frostguard").values(Tier.STANDARD).potencyCost());
    }

    private static String describe(List<PlantCount> plants) {
        List<String> parts = new ArrayList<String>();
        for (PlantCount plant : plants) {
            parts.add(plant.count() + "x" + plant.plant());
        }
        return parts.toString();
    }

    /** No shipped infusion uses lower hit resistance or lower effect power as its penalty. */
    @Test
    void bundledPenaltiesAreOnlyOnHealingDrinkSpeedOrRechargeSpeed() {
        for (InfusionSpec spec : Roster.bundled().specs()) {
            for (Tier tier : Tier.values()) {
                if (!spec.hasTier(tier)) {
                    continue;
                }
                Map<Channel, Double> bonuses = spec.values(tier).bonuses();
                for (Map.Entry<Channel, Double> bonus : bonuses.entrySet()) {
                    if (bonus.getValue() < 0) {
                        assertTrue(bonus.getKey() == Channel.HEALING
                                        || bonus.getKey() == Channel.DRINK_SPEED
                                        || bonus.getKey() == Channel.RECHARGE_SPEED,
                                spec.family() + " " + tier + " penalizes " + bonus.getKey());
                        assertTrue(bonus.getValue() > -1.0,
                                spec.family() + " " + tier + " switches a channel off entirely");
                    }
                }
            }
        }
    }

    /** Every modifier gives at least one real bonus, and a Stackable one has no Concentrated tier. */
    @Test
    void bundledModifiersAreWellFormed() {
        for (InfusionSpec spec : Roster.bundled().specs()) {
            if (spec.kind() != Kind.MODIFIER) {
                continue;
            }
            boolean positive = false;
            for (double value : spec.values(Tier.STANDARD).bonuses().values()) {
                positive |= value > 0;
            }
            assertTrue(positive, spec.family() + " gives no bonus");
            if (!spec.unique()) {
                assertFalse(spec.hasTier(Tier.CONCENTRATED), spec.family() + " is Stackable and Concentrated");
            }
        }
    }

    /** A copy of an effect cannot stack, so every effect family is Unique. */
    @Test
    void bundledEffectsAreUnique() {
        for (InfusionSpec spec : Roster.bundled().specs()) {
            if (spec.kind() == Kind.EFFECT) {
                assertTrue(spec.unique(), spec.family());
            }
        }
    }

    /** A Concentrated version costs more potency than its standard version. */
    @Test
    void bundledConcentratedCostsMoreThanStandard() {
        for (InfusionSpec spec : Roster.bundled().specs()) {
            if (spec.hasTier(Tier.CONCENTRATED)) {
                assertTrue(spec.values(Tier.CONCENTRATED).potencyCost()
                        > spec.values(Tier.STANDARD).potencyCost(), spec.family());
            }
        }
    }

    /** Shapeless recipes are told apart by their ingredients, so no two standard recipes may match. */
    @Test
    void bundledStandardRecipesHaveDistinctIngredients() {
        Set<String> seen = new HashSet<String>();
        for (InfusionSpec spec : Roster.bundled().specs()) {
            List<String> plants = new ArrayList<String>();
            for (PlantCount plant : spec.values(Tier.STANDARD).plants()) {
                for (int i = 0; i < plant.count(); i++) {
                    plants.add(plant.plant());
                }
            }
            Collections.sort(plants);
            assertTrue(seen.add(plants.toString()), spec.family() + " repeats the recipe " + plants);
        }
    }

    /** Every recipe is one base item plus its plants, and must fit the nine slots of a crafting grid. */
    @Test
    void bundledRecipesFitACraftingGrid() {
        for (InfusionSpec spec : Roster.bundled().specs()) {
            for (Tier tier : Tier.values()) {
                if (!spec.hasTier(tier)) {
                    continue;
                }
                int plants = 0;
                for (PlantCount plant : spec.values(tier).plants()) {
                    plants += plant.count();
                }
                assertTrue(plants + 1 <= 9, spec.family() + " " + tier + " needs " + (plants + 1) + " slots");
            }
        }
    }

    /** The lang file names every item the roster creates, with the family's display name. */
    @Test
    void langFileNamesEveryRosterItem() throws IOException {
        java.util.Properties lang = new java.util.Properties();
        try (java.io.InputStream in = RosterTest.class.getResourceAsStream(
                "/assets/elusiveinfusions/lang/en_us.lang")) {
            assertNotNull(in, "lang file is on the classpath");
            lang.load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
        }
        assertEquals("Blank Infusor", lang.getProperty("item.elusiveinfusions.blank_infusor.name"));
        assertNotNull(lang.getProperty("elusiveinfusions.journal.blank_infusor"));
        assertNotNull(lang.getProperty("itemGroup.elusiveinfusions"));
        for (String key : new String[] {"unique", "stackable", "lastsLonger", "lengthensEffects"}) {
            assertNotNull(lang.getProperty("elusiveinfusions.tooltip." + key), key);
        }
        assertNotNull(lang.getProperty("elusiveinfusions.effect.description"));
        assertNotNull(lang.getProperty("elusiveinfusions.effect.unavailable"));
        for (InfusionSpec spec : Roster.bundled().specs()) {
            assertEquals(spec.displayName() + " Infusion",
                    lang.getProperty("item.elusiveinfusions." + spec.itemPath(Tier.STANDARD) + ".name"));
            for (Tier tier : Tier.values()) {
                if (spec.hasTier(tier)) {
                    String journal = lang.getProperty("elusiveinfusions.journal." + spec.itemPath(tier));
                    assertNotNull(journal, spec.itemPath(tier));
                    assertTrue(journal.contains(spec.unique() ? "Unique" : "Stackable") || tier == Tier.CONCENTRATED, spec.itemPath(tier));
                    assertFalse(journal.contains(" a A") || journal.contains(" a I") || journal.contains(" a E") || journal.contains(" a O") || journal.contains(" a U"), "article in " + spec.itemPath(tier));
                    assertTrue(journal.matches("[\\x20-\\x7E]+"), "ASCII only in " + spec.itemPath(tier));
                }
            }
            if (spec.hasTier(Tier.CONCENTRATED)) {
                assertEquals("Concentrated " + spec.displayName() + " Infusion",
                        lang.getProperty("item.elusiveinfusions." + spec.itemPath(Tier.CONCENTRATED) + ".name"));
            }
        }
    }

    private static void assertRejected(String rows, String why) {
        assertThrows(RosterFormatException.class, () -> parse(rows), why);
    }
}
