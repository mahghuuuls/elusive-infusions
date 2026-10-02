package com.mahghuuuls.elusiveinfusions.integration;

import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Decides which families exist in this installation and finds their potion effects. A family that
 * names another mod exists only while that mod is installed, so a pack without it gets no dead
 * items. Other mods are reached by registry id alone; no class of theirs is ever referenced.
 */
public final class OptionalEffects {

    private OptionalEffects() {
    }

    public static boolean exists(InfusionSpec spec) {
        return spec.requiresMod() == null || Loader.isModLoaded(spec.requiresMod());
    }

    /**
     * The potion effect of an effect infusion, or null when it is not registered. Looked up by
     * registry id, so it is valid only once potions have been registered.
     */
    public static Potion potion(InfusionSpec spec) {
        String id = spec.effectId();
        return id == null ? null : ForgeRegistries.POTIONS.getValue(new ResourceLocation(id));
    }
}
