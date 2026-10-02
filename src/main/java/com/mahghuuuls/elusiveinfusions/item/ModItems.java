package com.mahghuuuls.elusiveinfusions.item;

import com.mahghuuuls.elusiveinfusions.Tags;
import com.mahghuuuls.elusiveinfusions.infusion.InfusionDefinitions;
import com.mahghuuuls.elusiveinfusions.integration.OptionalEffects;
import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Roster;
import com.mahghuuuls.elusiveinfusions.roster.Tier;
import com.mahghuuuls.everfillingflasks.api.FlaskApi;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registers the Blank Infusor and one item per tier of every family that exists in this
 * installation, and registers each infusion with Everfilling Flasks beside it.
 */
@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public final class ModItems {

    public static final String BLANK_INFUSOR_PATH = "blank_infusor";

    private static final List<ItemInfusion> INFUSIONS = new ArrayList<ItemInfusion>();
    private static Item blankInfusor;

    private ModItems() {
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        blankInfusor = new Item()
                .setRegistryName(BLANK_INFUSOR_PATH)
                .setTranslationKey(Tags.MOD_ID + "." + BLANK_INFUSOR_PATH)
                .setCreativeTab(CreativeTabs.MISC);
        event.getRegistry().register(blankInfusor);

        for (InfusionSpec spec : Roster.bundled().specs()) {
            if (!OptionalEffects.exists(spec)) {
                continue;
            }
            for (Tier tier : Tier.values()) {
                if (!spec.hasTier(tier)) {
                    continue;
                }
                ItemInfusion item = new ItemInfusion(spec, tier);
                item.setCreativeTab(CreativeTabs.MISC);
                INFUSIONS.add(item);
                event.getRegistry().register(item);
                FlaskApi.registerInfusion(item, InfusionDefinitions.create(spec, tier));
            }
        }
    }

    /** The Blank Infusor. Null before the item registry event. */
    public static Item blankInfusor() {
        return blankInfusor;
    }

    /** The registered infusion items, in roster order. Empty before the item registry event. */
    public static List<ItemInfusion> infusions() {
        return Collections.unmodifiableList(INFUSIONS);
    }

    /** The registered item of this family and tier, or null. */
    public static ItemInfusion infusion(InfusionSpec spec, Tier tier) {
        for (ItemInfusion item : INFUSIONS) {
            if (item.spec() == spec && item.tier() == tier) {
                return item;
            }
        }
        return null;
    }
}
