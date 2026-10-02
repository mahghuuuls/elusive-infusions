package com.mahghuuuls.elusiveinfusions.rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EffectDurationTest {

    /** The shipped maximum: an effect lasts at most twice its base duration (REQ-007). */
    @Test
    void shippedMaximumIsTwice() {
        assertEquals(2.0F, EffectDuration.DEFAULT_MAXIMUM, 0.0F);
        assertEquals(400, EffectDuration.ticks(10, 3.0F, EffectDuration.DEFAULT_MAXIMUM));
    }

    @Test
    void aMaximumOfZeroSwitchesEffectsOff() {
        assertEquals(0, EffectDuration.ticks(10, 1.5F, 0.0F));
    }

    @Test
    void plainMultiplierGivesTheBaseDuration() {
        assertEquals(200, EffectDuration.ticks(10, 1.0F, 2.0F));
    }

    @Test
    void effectPowerLengthensTheDuration() {
        assertEquals(300, EffectDuration.ticks(10, 1.5F, 2.0F));
        assertEquals(260, EffectDuration.ticks(10, 1.3F, 2.0F));
    }

    @Test
    void theMaximumHoldsTheMultiplier() {
        assertEquals(400, EffectDuration.ticks(10, 2.8F, 2.0F));
        assertEquals(400, EffectDuration.ticks(10, 2.0F, 2.0F));
        assertEquals(1000, EffectDuration.ticks(10, 7.0F, 5.0F));
    }

    @Test
    void cancelledEffectPowerGivesNothing() {
        assertEquals(0, EffectDuration.ticks(10, 0.0F, 2.0F));
        assertEquals(0, EffectDuration.ticks(10, -0.5F, 2.0F));
    }

    @Test
    void nothingForANonPositiveBase() {
        assertEquals(0, EffectDuration.ticks(0, 1.0F, 2.0F));
    }

    @Test
    void fractionsRoundToTheNearestTick() {
        assertEquals(161, EffectDuration.ticks(7, 1.15F, 2.0F));
        assertEquals(165, EffectDuration.ticks(15, 0.55F, 2.0F));
    }
}
