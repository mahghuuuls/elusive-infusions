package com.mahghuuuls.elusiveinfusions.rules;

/**
 * How long an effect infusion's potion effect lasts after a drink.
 *
 * <p>The base duration is multiplied by the drink's effect power multiplier (1 means no bonus,
 * 1.5 means fifty percent more), and that multiplier is held at a configured maximum so a flask
 * full of effect power cannot make a buff last as long as the recharge. The level never changes.
 */
public final class EffectDuration {

    /** The shipped maximum multiplier: an effect lasts at most twice its base duration. */
    public static final float DEFAULT_MAXIMUM = 2.0F;

    private static final int TICKS_PER_SECOND = 20;

    private EffectDuration() {
    }

    /**
     * Duration in ticks, or 0 when no effect should be applied at all. A multiplier of 0 or
     * below means effect power has been cancelled entirely, so nothing is given.
     */
    public static int ticks(int baseSeconds, float effectPowerMultiplier, float maximumMultiplier) {
        if (baseSeconds <= 0 || effectPowerMultiplier <= 0.0F) {
            return 0;
        }
        // A maximum of 0 or below switches effects off entirely; the configuration owns its range.
        float multiplier = Math.min(effectPowerMultiplier, maximumMultiplier);
        if (multiplier <= 0.0F) {
            return 0;
        }
        return Math.round(baseSeconds * TICKS_PER_SECOND * multiplier);
    }
}
