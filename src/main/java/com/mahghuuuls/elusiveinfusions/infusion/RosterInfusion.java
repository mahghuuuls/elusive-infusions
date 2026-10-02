package com.mahghuuuls.elusiveinfusions.infusion;

import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Tier;
import com.mahghuuuls.elusiveinfusions.roster.TierValues;
import com.mahghuuuls.elusiveinfusions.rules.FamilyRules;
import com.mahghuuuls.everfillingflasks.api.InfusionDefinition;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * What every infusion of this mod tells Everfilling Flasks: its potency cost and which other
 * pieces it refuses to share a grid with. The two kinds add what they do on top of this.
 *
 * <p>The values object is the one source of this infusion's numbers. The methods are also called
 * on the client with a bare stack and no player, so they answer from the values and the two
 * stacks alone.
 */
abstract class RosterInfusion implements InfusionDefinition {

    private final InfusionSpec spec;
    private final Tier tier;
    private final TierValues values;

    RosterInfusion(InfusionSpec spec, Tier tier, TierValues values) {
        this.spec = spec;
        this.tier = tier;
        this.values = values;
    }

    protected final InfusionSpec spec() {
        return spec;
    }

    protected final TierValues values() {
        return values;
    }

    @Override
    public int potencyCost(ItemStack infusion) {
        return values.potencyCost();
    }

    /** Conflicts are decided by {@link FamilyRules}; another mod's infusion never conflicts here. */
    @Override
    public boolean conflictsWith(ItemStack self, ItemStack other) {
        Item item = other.getItem();
        if (!(item instanceof InfusionIdentity)) {
            return false;
        }
        InfusionIdentity identity = (InfusionIdentity) item;
        return FamilyRules.conflict(spec, tier, identity.spec(), identity.tier());
    }
}
