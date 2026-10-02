package com.mahghuuuls.elusiveinfusions.integration;

import com.mahghuuuls.elusiveinfusions.ElusiveInfusionsMod;
import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Kind;
import com.mahghuuuls.elusiveinfusions.roster.Roster;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Decides which families exist in this installation and which effects they can apply.
 *
 * <p>A family that names another mod exists only while that mod is installed, so a pack without
 * it gets no dead items. Whether the effect itself is registered is known only after potions
 * register, which is after items: {@link #resolve} is called once at that point, warns once per
 * missing effect, and caches every found potion, so a drink or a tooltip never searches the
 * registry. Other mods are reached by registry id alone; no class of theirs is ever referenced.
 */
public final class OptionalEffects {

    /** What the registry answers about an effect id; separate so the decision can be tested. */
    interface EffectLookup {
        boolean isRegistered(String effectId);
    }

    private static final EffectLookup REGISTRY = new EffectLookup() {
        @Override
        public boolean isRegistered(String effectId) {
            return ForgeRegistries.POTIONS.containsKey(new ResourceLocation(effectId));
        }
    };

    /** Family id to its potion; a family whose effect is missing maps to null. Empty before resolve. */
    private static final Map<String, Potion> POTIONS = new HashMap<String, Potion>();
    private static boolean resolved;

    private OptionalEffects() {
    }

    public static boolean exists(InfusionSpec spec) {
        return spec.requiresMod() == null || Loader.isModLoaded(spec.requiresMod());
    }

    /**
     * Checks every existing effect family of the roster against the potion registry, once
     * potions are registered. A family whose effect is missing (for example disabled in its
     * mod's own configuration) is logged once, applies nothing, and gets no recipe.
     */
    public static void resolve(Roster roster) {
        List<InfusionSpec> existing = new ArrayList<InfusionSpec>();
        for (InfusionSpec spec : roster.specs()) {
            if (exists(spec)) {
                existing.add(spec);
            }
        }
        List<InfusionSpec> gone = missingEffects(existing, REGISTRY);
        POTIONS.clear();
        for (InfusionSpec spec : existing) {
            if (spec.kind() == Kind.EFFECT) {
                POTIONS.put(spec.family(), gone.contains(spec)
                        ? null
                        : ForgeRegistries.POTIONS.getValue(new ResourceLocation(spec.effectId())));
            }
        }
        for (InfusionSpec spec : gone) {
            ElusiveInfusionsMod.LOGGER.warn(
                    "Infusion {} applies nothing and has no recipe: its effect {} is not registered",
                    spec.family(), spec.effectId());
        }
        resolved = true;
    }

    /** The effect families whose effect the lookup does not know. Pure: the Forge part is the lookup. */
    static List<InfusionSpec> missingEffects(Collection<InfusionSpec> existing, EffectLookup lookup) {
        List<InfusionSpec> gone = new ArrayList<InfusionSpec>();
        for (InfusionSpec spec : existing) {
            if (spec.kind() == Kind.EFFECT && !lookup.isRegistered(spec.effectId())) {
                gone.add(spec);
            }
        }
        return gone;
    }

    /** Whether this family's recipes may exist: always for a modifier, for an effect only once its potion was found. */
    public static boolean recipeAllowed(InfusionSpec spec) {
        return spec.kind() == Kind.MODIFIER || potion(spec) != null;
    }

    /**
     * The potion effect of an effect infusion, or null when it is missing or not yet resolved.
     * Answered from the cache filled by {@link #resolve}.
     */
    public static Potion potion(InfusionSpec spec) {
        return resolved ? POTIONS.get(spec.family()) : null;
    }
}
