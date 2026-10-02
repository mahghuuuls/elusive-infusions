package com.mahghuuuls.elusiveinfusions.infusion;

import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Kind;
import com.mahghuuuls.elusiveinfusions.roster.Tier;
import com.mahghuuuls.elusiveinfusions.rules.EffectDuration;
import com.mahghuuuls.everfillingflasks.api.InfusionDefinition;

/** Builds the Everfilling Flasks definition for one tier of one family. */
public final class InfusionDefinitions {

    private InfusionDefinitions() {
    }

    public static InfusionDefinition create(InfusionSpec spec, Tier tier) {
        if (spec.kind() == Kind.MODIFIER) {
            return new ModifierInfusion(spec.values(tier));
        }
        return new EffectInfusion(spec, spec.values(tier), EffectDuration.DEFAULT_MAXIMUM);
    }
}
