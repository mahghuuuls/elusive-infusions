package com.mahghuuuls.elusiveinfusions.config;

import com.mahghuuuls.elusiveinfusions.roster.Channel;
import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Kind;
import com.mahghuuuls.elusiveinfusions.roster.Roster;
import com.mahghuuuls.elusiveinfusions.roster.Tier;
import com.mahghuuuls.elusiveinfusions.roster.TierValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The numbers in force this session: every infusion's cost and values, every recipe switch, and
 * the effect power maximum. Built once from the roster and the configuration file, then read
 * everywhere; nothing else parses or clamps.
 *
 * <p>This class owns the option names and ranges. {@link #options} lists every option with its
 * roster default and comment; the file layer declares that list and hands back the raw text, and
 * {@link #parse} reads the same list. Option names are what pack authors write, so they are frozen
 * at release: {@code infusions.<item>.cost}, {@code infusions.<item>.<channel>},
 * {@code infusions.<item>.level}, {@code infusions.<item>.seconds}, {@code recipes.<item>},
 * {@code general.effectPowerMaximum}.
 */
public final class InfusionValues {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_INFUSIONS = "infusions";
    public static final String CATEGORY_RECIPES = "recipes";
    public static final String OPTION_EFFECT_POWER_MAXIMUM = "effectPowerMaximum";
    public static final String OPTION_COST = "cost";
    public static final String OPTION_LEVEL = "level";
    public static final String OPTION_SECONDS = "seconds";
    /** Registry path of the Blank Infusor, which is also its recipe switch. */
    public static final String BLANK_INFUSOR = "blank_infusor";

    public static final int COST_MIN = 0;
    public static final int COST_MAX = 100;
    public static final int LEVEL_MIN = 1;
    public static final int LEVEL_MAX = 10;
    public static final int SECONDS_MIN = 1;
    public static final int SECONDS_MAX = 3600;
    public static final double BONUS_MIN = -0.95;
    public static final double BONUS_MAX = 10.0;
    public static final double MAXIMUM_MIN = 1.0;
    public static final double MAXIMUM_MAX = 10.0;
    /** The shipped effect power maximum: an effect lasts at most twice its base duration. */
    public static final double MAXIMUM_DEFAULT = 2.0;

    private static final String COST_COMMENT = "Potency this piece uses while placed in a flask. Whole number, "
            + COST_MIN + " to " + COST_MAX + ".";
    private static final String LEVEL_COMMENT = "Effect level given after a completed drink: 1 is I, 2 is II. "
            + LEVEL_MIN + " to " + LEVEL_MAX + ".";
    private static final String SECONDS_COMMENT = "Base duration of the effect in seconds, before effect power. "
            + SECONDS_MIN + " to " + SECONDS_MAX + ".";
    private static final String BONUS_COMMENT = "Change to the flask's %s as a fraction: 0.35 is 35 percent more, "
            + "-0.30 is 30 percent less. " + BONUS_MIN + " to " + BONUS_MAX + ".";
    private static final String RECIPE_COMMENT = "false removes the recipe that makes this item.";
    private static final String MAXIMUM_COMMENT = "Largest effect power multiplier that lengthens an effect "
            + "infusion's duration: 2.0 means at most twice the base duration. "
            + MAXIMUM_MIN + " to " + MAXIMUM_MAX + ".";

    private final Map<String, TierValues> values;
    private final Map<String, Boolean> recipes;
    private final float effectPowerMaximum;

    private InfusionValues(Map<String, TierValues> values, Map<String, Boolean> recipes,
                           float effectPowerMaximum) {
        this.values = Collections.unmodifiableMap(values);
        this.recipes = Collections.unmodifiableMap(recipes);
        this.effectPowerMaximum = effectPowerMaximum;
    }

    /** Every option the file holds for this roster, in file order, with its roster default. */
    public static List<ConfigOption> options(Roster roster) {
        List<ConfigOption> options = new ArrayList<ConfigOption>();
        options.add(new ConfigOption(CATEGORY_GENERAL, OPTION_EFFECT_POWER_MAXIMUM,
                Double.toString(MAXIMUM_DEFAULT), MAXIMUM_COMMENT, ConfigOption.Type.DECIMAL));
        options.add(new ConfigOption(CATEGORY_RECIPES, BLANK_INFUSOR, "true", RECIPE_COMMENT,
                ConfigOption.Type.SWITCH));
        for (InfusionSpec spec : roster.specs()) {
            for (Tier tier : Tier.values()) {
                if (!spec.hasTier(tier)) {
                    continue;
                }
                String item = spec.itemPath(tier);
                String category = CATEGORY_INFUSIONS + "." + item;
                TierValues shipped = spec.values(tier);
                options.add(new ConfigOption(category, OPTION_COST, Integer.toString(shipped.potencyCost()),
                        COST_COMMENT, ConfigOption.Type.INTEGER));
                if (spec.kind() == Kind.MODIFIER) {
                    for (Map.Entry<Channel, Double> bonus : shipped.bonuses().entrySet()) {
                        String key = bonus.getKey().key();
                        options.add(new ConfigOption(category, key, Double.toString(bonus.getValue()),
                                String.format(BONUS_COMMENT, key.replace('_', ' ')), ConfigOption.Type.DECIMAL));
                    }
                } else {
                    options.add(new ConfigOption(category, OPTION_LEVEL, Integer.toString(shipped.level()),
                            LEVEL_COMMENT, ConfigOption.Type.INTEGER));
                    options.add(new ConfigOption(category, OPTION_SECONDS, Integer.toString(shipped.seconds()),
                            SECONDS_COMMENT, ConfigOption.Type.INTEGER));
                }
                options.add(new ConfigOption(CATEGORY_RECIPES, item, "true", RECIPE_COMMENT,
                        ConfigOption.Type.SWITCH));
            }
        }
        return options;
    }

    /** The shipped numbers: the roster, untouched. */
    public static InfusionValues defaults(Roster roster) {
        return parse(roster, Collections.<String, String>emptyMap(), new ArrayList<String>());
    }

    /**
     * Builds the values from raw option text keyed by {@link ConfigOption#path()}. A missing
     * option keeps its roster default. A value outside its range is replaced by the nearest
     * allowed value, and text that is not a number (or not true or false) by the default; each
     * such case adds one line naming the option, the bad value, and the value used to
     * {@code warnings}.
     */
    public static InfusionValues parse(Roster roster, Map<String, String> raw, List<String> warnings) {
        Map<String, TierValues> values = new HashMap<String, TierValues>();
        Map<String, Boolean> recipes = new HashMap<String, Boolean>();
        for (InfusionSpec spec : roster.specs()) {
            for (Tier tier : Tier.values()) {
                if (!spec.hasTier(tier)) {
                    continue;
                }
                String item = spec.itemPath(tier);
                TierValues shipped = spec.values(tier);
                String prefix = CATEGORY_INFUSIONS + "." + item + ".";
                int cost = Ranges.intValue(prefix + OPTION_COST, raw.get(prefix + OPTION_COST),
                        shipped.potencyCost(), COST_MIN, COST_MAX, warnings);
                if (spec.kind() == Kind.MODIFIER) {
                    Map<Channel, Double> bonuses = new EnumMap<Channel, Double>(Channel.class);
                    for (Map.Entry<Channel, Double> bonus : shipped.bonuses().entrySet()) {
                        String key = prefix + bonus.getKey().key();
                        bonuses.put(bonus.getKey(), Ranges.doubleValue(key, raw.get(key),
                                bonus.getValue(), BONUS_MIN, BONUS_MAX, warnings));
                    }
                    values.put(item, TierValues.modifier(shipped.plants(), cost, bonuses));
                } else {
                    int level = Ranges.intValue(prefix + OPTION_LEVEL, raw.get(prefix + OPTION_LEVEL),
                            shipped.level(), LEVEL_MIN, LEVEL_MAX, warnings);
                    int seconds = Ranges.intValue(prefix + OPTION_SECONDS, raw.get(prefix + OPTION_SECONDS),
                            shipped.seconds(), SECONDS_MIN, SECONDS_MAX, warnings);
                    values.put(item, TierValues.effect(shipped.plants(), cost, level, seconds));
                }
                String recipeKey = CATEGORY_RECIPES + "." + item;
                recipes.put(item, Ranges.booleanValue(recipeKey, raw.get(recipeKey), true, warnings));
            }
        }
        String blankKey = CATEGORY_RECIPES + "." + BLANK_INFUSOR;
        recipes.put(BLANK_INFUSOR, Ranges.booleanValue(blankKey, raw.get(blankKey), true, warnings));
        String maximumKey = CATEGORY_GENERAL + "." + OPTION_EFFECT_POWER_MAXIMUM;
        double maximum = Ranges.doubleValue(maximumKey, raw.get(maximumKey), MAXIMUM_DEFAULT,
                MAXIMUM_MIN, MAXIMUM_MAX, warnings);
        return new InfusionValues(values, recipes, (float) maximum);
    }

    /** The configured values of one tier of one family. Throws for a tier the family lacks. */
    public TierValues of(InfusionSpec spec, Tier tier) {
        TierValues result = values.get(spec.itemPath(tier));
        if (result == null) {
            throw new IllegalArgumentException("No values for " + spec.itemPath(tier));
        }
        return result;
    }

    /** Whether the recipe that makes this item is switched on. Unknown items have no switch and are on. */
    public boolean recipeEnabled(String itemPath) {
        Boolean enabled = recipes.get(itemPath);
        return enabled == null || enabled;
    }

    /** The largest effect power multiplier that lengthens an effect; see the duration rule. */
    public float effectPowerMaximum() {
        return effectPowerMaximum;
    }
}
