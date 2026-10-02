package com.mahghuuuls.elusiveinfusions.infusion;

import com.mahghuuuls.elusiveinfusions.config.InfusionValues;
import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Kind;
import com.mahghuuuls.elusiveinfusions.roster.Tier;
import com.mahghuuuls.everfillingflasks.api.InfusionDefinition;

/** Builds the Everfilling Flasks definition for one tier of one family. */
public final class InfusionDefinitions {

    private InfusionDefinitions() {
    }

    public static InfusionDefinition create(InfusionSpec spec, Tier tier, InfusionValues values) {
        if (spec.kind() == Kind.MODIFIER) {
            return new ModifierInfusion(spec, tier, values.of(spec, tier));
        }
        return new EffectInfusion(spec, tier, values.of(spec, tier), values.effectPowerMaximum());
    }
}
