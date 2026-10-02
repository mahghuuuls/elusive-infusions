package com.mahghuuuls.elusiveinfusions.roster;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The values of one tier of one family: what it costs to craft and to place, and what it does.
 *
 * <p>A modifier tier carries bonuses per channel (a negative value is a penalty). An effect tier
 * carries the potion level, counted from 1, and the base duration in seconds.
 */
public final class TierValues {

    private final List<PlantCount> plants;
    private final int potencyCost;
    private final Map<Channel, Double> bonuses;
    private final int level;
    private final int seconds;

    private TierValues(List<PlantCount> plants, int potencyCost, Map<Channel, Double> bonuses,
                       int level, int seconds) {
        this.plants = Collections.unmodifiableList(plants);
        this.potencyCost = potencyCost;
        this.bonuses = Collections.unmodifiableMap(bonuses);
        this.level = level;
        this.seconds = seconds;
    }

    public static TierValues modifier(List<PlantCount> plants, int potencyCost,
                                      Map<Channel, Double> bonuses) {
        return new TierValues(plants, potencyCost, new EnumMap<Channel, Double>(bonuses), 0, 0);
    }

    public static TierValues effect(List<PlantCount> plants, int potencyCost, int level, int seconds) {
        return new TierValues(plants, potencyCost, new EnumMap<Channel, Double>(Channel.class),
                level, seconds);
    }

    /** Plants the recipe consumes, beside the Blank Infusor or the standard infusion. */
    public List<PlantCount> plants() {
        return plants;
    }

    public int potencyCost() {
        return potencyCost;
    }

    /** Bonus per channel as a fraction: 0.35 is 35 percent more, -0.30 is 30 percent less. */
    public Map<Channel, Double> bonuses() {
        return bonuses;
    }

    /** Potion level counted from 1; 0 for a modifier tier. */
    public int level() {
        return level;
    }

    /** Base effect duration in seconds; 0 for a modifier tier. */
    public int seconds() {
        return seconds;
    }
}
