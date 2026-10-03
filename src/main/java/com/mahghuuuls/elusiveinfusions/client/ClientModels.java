package com.mahghuuuls.elusiveinfusions.client;

import com.mahghuuuls.elusiveinfusions.Tags;
import com.mahghuuuls.elusiveinfusions.item.ItemInfusion;
import com.mahghuuuls.elusiveinfusions.item.ModItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Every infusion uses one layered model: the infuser frame, then a grey fill tinted with the
 * family's fixed color. A Concentrated infusion is the same model with the enchantment glint,
 * which the item itself declares.
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Tags.MOD_ID)
public final class ClientModels {

    private static final ModelResourceLocation INFUSION =
            new ModelResourceLocation(new ResourceLocation(Tags.MOD_ID, "infusion"), "inventory");
    private static final int FILL_LAYER = 1;
    private static final int UNTINTED = -1;

    private ClientModels() {
    }

    @SubscribeEvent
    public static void onRegisterModels(ModelRegistryEvent event) {
        Item blank = ModItems.blankInfusor();
        ModelLoader.setCustomModelResourceLocation(blank, 0,
                new ModelResourceLocation(blank.getRegistryName(), "inventory"));
        for (ItemInfusion item : ModItems.infusions()) {
            ModelLoader.setCustomModelResourceLocation(item, 0, INFUSION);
        }
    }

    @SubscribeEvent
    public static void onItemColors(ColorHandlerEvent.Item event) {
        IItemColor familyColor = new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex != FILL_LAYER || !(stack.getItem() instanceof ItemInfusion)) {
                    return UNTINTED;
                }
                return ((ItemInfusion) stack.getItem()).spec().color();
            }
        };
        for (ItemInfusion item : ModItems.infusions()) {
            event.getItemColors().registerItemColorHandler(familyColor, item);
        }
    }
}
