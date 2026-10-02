package com.mahghuuuls.elusiveinfusions.integration;

import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import net.minecraftforge.fml.common.Loader;

/**
 * Decides which families exist in this installation. A family that names another mod exists only
 * while that mod is installed, so a pack without it gets no dead items. Other mods are reached by
 * id alone; no class of theirs is ever referenced.
 */
public final class OptionalEffects {

    private OptionalEffects() {
    }

    public static boolean exists(InfusionSpec spec) {
        return spec.requiresMod() == null || Loader.isModLoaded(spec.requiresMod());
    }
}
