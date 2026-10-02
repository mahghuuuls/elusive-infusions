package com.mahghuuuls.elusiveinfusions.recipe;

import com.mahghuuuls.elusiveinfusions.ElusiveInfusionsMod;
import com.mahghuuuls.elusiveinfusions.Tags;
import com.mahghuuuls.elusiveinfusions.item.ItemInfusion;
import com.mahghuuuls.elusiveinfusions.item.ModItems;
import com.mahghuuuls.elusiveinfusions.roster.PlantCount;
import com.mahghuuuls.elusiveinfusions.roster.Tier;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds every crafting recipe from the roster, so the plants and counts are written in one place.
 *
 * <p>A standard infusion is a Blank Infusor plus its plants. A Concentrated infusion is the
 * standard infusion of the same family plus its plants. Each recipe is registered under the
 * registry name of the item it makes; those names must stay stable across releases.
 */
@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public final class RecipeRegistrar {

    private static final String PLANT_MOD_ID = "elusiveflora";

    private RecipeRegistrar() {
    }

    @SubscribeEvent
    public static void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {
        Item blank = ModItems.blankInfusor();
        event.getRegistry().register(new ShapedOreRecipe(null, new ItemStack(blank),
                " N ", "NBN", " N ", 'N', "nuggetIron", 'B', Blocks.IRON_BARS)
                .setRegistryName(blank.getRegistryName()));

        int registered = 0;
        for (ItemInfusion item : ModItems.infusions()) {
            Item base = item.tier() == Tier.STANDARD
                    ? blank
                    : ModItems.infusion(item.spec(), Tier.STANDARD);
            List<Object> inputs = new ArrayList<Object>();
            inputs.add(new ItemStack(base));
            if (!addPlants(inputs, item)) {
                continue;
            }
            event.getRegistry().register(new ShapelessOreRecipe(null, new ItemStack(item),
                    inputs.toArray())
                    .setRegistryName(item.getRegistryName()));
            registered++;
        }
        ElusiveInfusionsMod.LOGGER.info("Registered {} infusion recipes for {} infusion items",
                registered, ModItems.infusions().size());
    }

    /** Adds one stack per plant needed. False when a plant item is not registered. */
    private static boolean addPlants(List<Object> inputs, ItemInfusion item) {
        for (PlantCount plant : item.spec().values(item.tier()).plants()) {
            Item plantItem = ForgeRegistries.ITEMS.getValue(
                    new ResourceLocation(PLANT_MOD_ID, plant.plant()));
            if (plantItem == null) {
                ElusiveInfusionsMod.LOGGER.warn("No recipe for {}: plant {}:{} is not registered",
                        item.getRegistryName(), PLANT_MOD_ID, plant.plant());
                return false;
            }
            for (int i = 0; i < plant.count(); i++) {
                inputs.add(new ItemStack(plantItem));
            }
        }
        return true;
    }
}
