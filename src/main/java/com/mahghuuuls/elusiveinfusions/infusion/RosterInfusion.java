package com.mahghuuuls.elusiveinfusions.infusion;

import com.mahghuuuls.elusiveinfusions.roster.TierValues;
import com.mahghuuuls.everfillingflasks.api.InfusionDefinition;
import net.minecraft.item.ItemStack;

/**
 * What every infusion of this mod tells Everfilling Flasks: its potency cost. The two kinds add
 * what they do on top of this.
 *
 * <p>The values object is the one source of this infusion's numbers. The methods are also called
 * on the client with a bare stack and no player, so they answer from the values alone.
 */
abstract class RosterInfusion implements InfusionDefinition {

    private final TierValues values;

    RosterInfusion(TierValues values) {
        this.values = values;
    }

    protected final TierValues values() {
        return values;
    }

    @Override
    public int potencyCost(ItemStack infusion) {
        return values.potencyCost();
    }
}
