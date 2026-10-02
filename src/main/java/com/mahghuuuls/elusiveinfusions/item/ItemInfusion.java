package com.mahghuuuls.elusiveinfusions.item;

import com.mahghuuuls.elusiveinfusions.Tags;
import com.mahghuuuls.elusiveinfusions.infusion.InfusionIdentity;
import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Kind;
import com.mahghuuuls.elusiveinfusions.roster.Tier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * One tier of one infusion family. The item carries its family and tier; it has no state.
 *
 * <p>Everfilling Flasks writes the header, the potency cost, and the effect or bonus lines of the
 * tooltip. This item adds only what the parent cannot know: whether the family is Unique or
 * Stackable, and that effect power lengthens an effect.
 */
public final class ItemInfusion extends Item implements InfusionIdentity {

    private static final String TOOLTIP = Tags.MOD_ID + ".tooltip.";

    private final InfusionSpec spec;
    private final Tier tier;
    private final boolean lengthensEffects;

    /** {@code lengthensEffects}: the configured values give effect power, so the tooltip says so. */
    ItemInfusion(InfusionSpec spec, Tier tier, boolean lengthensEffects) {
        this.spec = spec;
        this.tier = tier;
        this.lengthensEffects = lengthensEffects;
        setRegistryName(spec.itemPath(tier));
        setTranslationKey(Tags.MOD_ID + "." + spec.itemPath(tier));
    }

    @Override
    public InfusionSpec spec() {
        return spec;
    }

    @Override
    public Tier tier() {
        return tier;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip,
                               ITooltipFlag flag) {
        tooltip.add(line(spec.unique() ? "unique" : "stackable"));
        if (spec.kind() == Kind.EFFECT) {
            tooltip.add(line("lastsLonger"));
        } else if (lengthensEffects) {
            tooltip.add(line("lengthensEffects"));
        }
    }

    private static String line(String key) {
        return TextFormatting.GRAY + new TextComponentTranslation(TOOLTIP + key).getFormattedText();
    }
}
