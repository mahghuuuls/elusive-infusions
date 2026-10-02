package com.mahghuuuls.elusiveinfusions.infusion;

import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Tier;

/**
 * What an item of this mod reveals about the infusion it is, so a definition can answer the
 * conflict question from two stacks alone. The item class implements it; definitions never
 * depend on the item class itself.
 */
public interface InfusionIdentity {

    InfusionSpec spec();

    Tier tier();
}
