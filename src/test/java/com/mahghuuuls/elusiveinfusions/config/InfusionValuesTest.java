package com.mahghuuuls.elusiveinfusions.config;

import com.mahghuuuls.elusiveinfusions.roster.Channel;
import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Roster;
import com.mahghuuuls.elusiveinfusions.roster.Tier;
import com.mahghuuuls.elusiveinfusions.roster.TierValues;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the real parsing and clamping over the shipped roster. It cannot show what Forge
 * writes into the file or reads back from it; the generated file is inspected in the game.
 */
class InfusionValuesTest {

    /** With nothing configured, every item carries exactly its roster numbers and every recipe is on. */
    @Test
    void defaultsEqualTheRosterForEveryItem() {
        Roster roster = Roster.bundled();
        List<String> warnings = new ArrayList<String>();
        InfusionValues values = InfusionValues.parse(roster, new HashMap<String, String>(), warnings);
        int items = 0;
        for (InfusionSpec spec : roster.specs()) {
            for (Tier tier : Tier.values()) {
                if (!spec.hasTier(tier)) {
                    continue;
                }
                TierValues shipped = spec.values(tier);
                TierValues configured = values.of(spec, tier);
                assertEquals(shipped.potencyCost(), configured.potencyCost(), spec.itemPath(tier));
                assertEquals(shipped.level(), configured.level(), spec.itemPath(tier));
                assertEquals(shipped.seconds(), configured.seconds(), spec.itemPath(tier));
                assertEquals(shipped.bonuses(), configured.bonuses(), spec.itemPath(tier));
                assertEquals(shipped.plants(), configured.plants(), spec.itemPath(tier));
                assertTrue(values.recipeEnabled(spec.itemPath(tier)));
                items++;
            }
        }
        assertEquals(53, items);
        assertTrue(values.recipeEnabled(InfusionValues.BLANK_INFUSOR));
        assertEquals(2.0F, values.effectPowerMaximum(), 0.0F);
        assertEquals(2.0, InfusionValues.MAXIMUM_DEFAULT, 0.0);
        assertTrue(warnings.isEmpty(), warnings.toString());
    }

    /** The file layer declares exactly the options parse reads: same paths, same defaults. */
    @Test
    void optionsNameEveryValueParseReadsWithTheRosterDefault() {
        Roster roster = Roster.bundled();
        Map<String, String> raw = new HashMap<String, String>();
        for (ConfigOption option : InfusionValues.options(roster)) {
            assertTrue(raw.put(option.path(), option.defaultText()) == null, "duplicate " + option.path());
        }
        assertEquals(2 + 53 * 2 + 28 * 2 + 5 + 40, raw.size(), "general, blank recipe, cost and switch per item, level and seconds per effect item, one bonus per stackable, two per trade-off tier");
        List<String> warnings = new ArrayList<String>();
        InfusionValues fromDefaults = InfusionValues.parse(roster, raw, warnings);
        assertTrue(warnings.isEmpty(), warnings.toString());
        InfusionValues untouched = InfusionValues.defaults(roster);
        for (InfusionSpec spec : roster.specs()) {
            for (Tier tier : Tier.values()) {
                if (spec.hasTier(tier)) {
                    assertEquals(untouched.of(spec, tier).bonuses(), fromDefaults.of(spec, tier).bonuses());
                    assertEquals(untouched.of(spec, tier).potencyCost(), fromDefaults.of(spec, tier).potencyCost());
                    assertEquals(untouched.of(spec, tier).seconds(), fromDefaults.of(spec, tier).seconds());
                }
            }
        }
        // Every raw key was consumed: a typo in a path would leave a default unread and a warning-free miss.
        Map<String, String> changed = new HashMap<String, String>();
        for (ConfigOption option : InfusionValues.options(roster)) {
            if (option.type() != ConfigOption.Type.SWITCH) {
                changed.put(option.path(), "-1000000");
            }
        }
        List<String> clampWarnings = new ArrayList<String>();
        InfusionValues.parse(roster, changed, clampWarnings);
        assertEquals(changed.size(), clampWarnings.size(), "every numeric option must reach its range check");
    }

    @Test
    void configuredValuesReplaceTheDefaults() {
        Roster roster = Roster.bundled();
        Map<String, String> raw = new HashMap<String, String>();
        raw.put("infusions.heartroot_infusion.healing", "0.5");
        raw.put("infusions.heartroot_infusion.cost", "4");
        raw.put("infusions.swiftness_infusion.level", "2");
        raw.put("infusions.swiftness_infusion.seconds", "25");
        raw.put("infusions.concentrated_swiftness_infusion.seconds", "40");
        raw.put("recipes.swiftness_infusion", "false");
        raw.put("recipes.blank_infusor", "false");
        raw.put("general.effectPowerMaximum", "3.5");
        List<String> warnings = new ArrayList<String>();

        InfusionValues values = InfusionValues.parse(roster, raw, warnings);

        TierValues heartroot = values.of(roster.family("heartroot"), Tier.STANDARD);
        assertEquals(0.5, heartroot.bonuses().get(Channel.HEALING), 1e-9);
        assertEquals(-0.30, heartroot.bonuses().get(Channel.DRINK_SPEED), 1e-9);
        assertEquals(4, heartroot.potencyCost());
        TierValues swiftness = values.of(roster.family("swiftness"), Tier.STANDARD);
        assertEquals(2, swiftness.level());
        assertEquals(25, swiftness.seconds());
        assertEquals(40, values.of(roster.family("swiftness"), Tier.CONCENTRATED).seconds());
        assertFalse(values.recipeEnabled("swiftness_infusion"));
        assertTrue(values.recipeEnabled("concentrated_swiftness_infusion"));
        assertFalse(values.recipeEnabled("blank_infusor"));
        assertEquals(3.5F, values.effectPowerMaximum(), 1e-6F);
        assertTrue(warnings.isEmpty(), warnings.toString());
    }

    @Test
    void outOfRangeValuesAreClampedWithOneWarningEach() {
        Roster roster = Roster.bundled();
        Map<String, String> raw = new HashMap<String, String>();
        raw.put("infusions.swiftness_infusion.seconds", "-5");
        raw.put("infusions.heartroot_infusion.healing", "-1.5");
        raw.put("infusions.heartroot_infusion.cost", "500");
        raw.put("general.effectPowerMaximum", "0.5");
        List<String> warnings = new ArrayList<String>();

        InfusionValues values = InfusionValues.parse(roster, raw, warnings);

        assertEquals(1, values.of(roster.family("swiftness"), Tier.STANDARD).seconds());
        assertEquals(-0.95, values.of(roster.family("heartroot"), Tier.STANDARD).bonuses().get(Channel.HEALING), 1e-9);
        assertEquals(100, values.of(roster.family("heartroot"), Tier.STANDARD).potencyCost());
        assertEquals(1.0F, values.effectPowerMaximum(), 0.0F);
        assertEquals(4, warnings.size(), warnings.toString());
        boolean secondsWarned = false;
        for (String warning : warnings) {
            secondsWarned |= warning.contains("infusions.swiftness_infusion.seconds")
                    && warning.contains("-5") && warning.contains("using 1");
        }
        assertTrue(secondsWarned, warnings.toString());
    }

    @Test
    void upperBoundsClampAndExactBoundsDoNot() {
        Roster roster = Roster.bundled();
        Map<String, String> raw = new HashMap<String, String>();
        raw.put("infusions.swiftness_infusion.level", "11");
        raw.put("infusions.swiftness_infusion.seconds", "3601");
        raw.put("general.effectPowerMaximum", "11");
        raw.put("infusions.heartroot_infusion.cost", "100");
        raw.put("infusions.heartroot_infusion.healing", "-0.95");
        List<String> warnings = new ArrayList<String>();

        InfusionValues values = InfusionValues.parse(roster, raw, warnings);

        assertEquals(10, values.of(roster.family("swiftness"), Tier.STANDARD).level());
        assertEquals(3600, values.of(roster.family("swiftness"), Tier.STANDARD).seconds());
        assertEquals(10.0F, values.effectPowerMaximum(), 0.0F);
        assertEquals(100, values.of(roster.family("heartroot"), Tier.STANDARD).potencyCost());
        assertEquals(-0.95, values.of(roster.family("heartroot"), Tier.STANDARD).bonuses().get(Channel.HEALING), 1e-9);
        assertEquals(3, warnings.size(), warnings.toString());
    }

    @Test
    void textThatIsNotANumberFallsBackToTheDefaultWithAWarning() {
        Roster roster = Roster.bundled();
        Map<String, String> raw = new HashMap<String, String>();
        raw.put("infusions.heartroot_infusion.cost", "three");
        raw.put("infusions.heartroot_infusion.healing", "NaN");
        raw.put("recipes.heartroot_infusion", "maybe");
        List<String> warnings = new ArrayList<String>();

        InfusionValues values = InfusionValues.parse(roster, raw, warnings);

        assertEquals(3, values.of(roster.family("heartroot"), Tier.STANDARD).potencyCost());
        assertEquals(0.35, values.of(roster.family("heartroot"), Tier.STANDARD).bonuses().get(Channel.HEALING), 1e-9);
        assertTrue(values.recipeEnabled("heartroot_infusion"));
        assertEquals(3, warnings.size(), warnings.toString());
    }

    /** A pack author may turn a penalty into a bonus; the shipped defaults, not the range, hold the penalty rule. */
    @Test
    void aPenaltyMayBeConfiguredPositive() {
        Roster roster = Roster.bundled();
        Map<String, String> raw = new HashMap<String, String>();
        raw.put("infusions.heartroot_infusion.drink_speed", "0.1");
        List<String> warnings = new ArrayList<String>();
        InfusionValues values = InfusionValues.parse(roster, raw, warnings);
        assertEquals(0.1, values.of(roster.family("heartroot"), Tier.STANDARD).bonuses().get(Channel.DRINK_SPEED), 1e-9);
        assertTrue(warnings.isEmpty());
    }
}
