package com.mahghuuuls.elusiveinfusions.infusion;

import com.mahghuuuls.elusiveinfusions.Tags;
import com.mahghuuuls.elusiveinfusions.integration.OptionalEffects;
import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.TierValues;
import com.mahghuuuls.elusiveinfusions.rules.EffectDuration;
import com.mahghuuuls.everfillingflasks.api.DrinkOutcome;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * An infusion that gives the drinker a potion effect once per completed drink.
 *
 * <p>The effect is applied through the player's normal potion method, so a repeated drink follows
 * the vanilla rule: a higher level replaces, an equal level keeps the longer time, a lower level
 * changes nothing. Effect power lengthens the duration only (see {@link EffectDuration}).
 */
final class EffectInfusion extends RosterInfusion {

    private static final String DESCRIPTION_KEY = Tags.MOD_ID + ".effect.description";
    private static final String UNAVAILABLE_KEY = Tags.MOD_ID + ".effect.unavailable";

    private final InfusionSpec spec;
    private final float maximumMultiplier;

    EffectInfusion(InfusionSpec spec, TierValues values, float maximumMultiplier) {
        super(values);
        this.spec = spec;
        this.maximumMultiplier = maximumMultiplier;
    }

    @Override
    public void onDrinkCompleted(ItemStack infusion, ItemStack flask, EntityPlayer player,
                                 DrinkOutcome outcome) {
        Potion potion = OptionalEffects.potion(spec);
        if (potion == null) {
            return;
        }
        int ticks = EffectDuration.ticks(values().seconds(), outcome.effectPower(), maximumMultiplier);
        if (ticks <= 0) {
            return;
        }
        player.addPotionEffect(new PotionEffect(potion, ticks, values().level() - 1, false, true));
    }

    @Override
    public ITextComponent effectDescription(ItemStack infusion) {
        Potion potion = OptionalEffects.potion(spec);
        if (potion == null) {
            return new TextComponentTranslation(UNAVAILABLE_KEY, new TextComponentString(spec.effectId()));
        }
        return new TextComponentTranslation(DESCRIPTION_KEY,
                new TextComponentTranslation(potion.getName()),
                new TextComponentTranslation("enchantment.level." + values().level()),
                values().seconds());
    }
}
