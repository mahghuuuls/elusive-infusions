package com.mahghuuuls.elusiveinfusions.item;

import com.mahghuuuls.elusiveinfusions.Tags;
import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Tier;
import net.minecraft.item.Item;

/** One tier of one infusion family. The item carries its family and tier; it has no state. */
public final class ItemInfusion extends Item {

    private final InfusionSpec spec;
    private final Tier tier;

    ItemInfusion(InfusionSpec spec, Tier tier) {
        this.spec = spec;
        this.tier = tier;
        setRegistryName(spec.itemPath(tier));
        setTranslationKey(Tags.MOD_ID + "." + spec.itemPath(tier));
    }

    public InfusionSpec spec() {
        return spec;
    }

    public Tier tier() {
        return tier;
    }
}
