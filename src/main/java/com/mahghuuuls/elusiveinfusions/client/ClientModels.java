package com.mahghuuuls.elusiveinfusions.client;

import com.mahghuuuls.elusiveinfusions.Tags;
import com.mahghuuuls.elusiveinfusions.item.ItemInfusion;
import com.mahghuuuls.elusiveinfusions.item.ModItems;
import com.mahghuuuls.elusiveinfusions.roster.Tier;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/** Points every infusion at one of two shared models: standard or Concentrated. */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Tags.MOD_ID)
public final class ClientModels {

    private static final ModelResourceLocation STANDARD = shared("infusion");
    private static final ModelResourceLocation CONCENTRATED = shared("concentrated_infusion");

    private ClientModels() {
    }

    @SubscribeEvent
    public static void onRegisterModels(ModelRegistryEvent event) {
        Item blank = ModItems.blankInfusor();
        ModelLoader.setCustomModelResourceLocation(blank, 0,
                new ModelResourceLocation(blank.getRegistryName(), "inventory"));
        for (ItemInfusion item : ModItems.infusions()) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                    item.tier() == Tier.STANDARD ? STANDARD : CONCENTRATED);
        }
    }

    private static ModelResourceLocation shared(String path) {
        return new ModelResourceLocation(new ResourceLocation(Tags.MOD_ID, path), "inventory");
    }
}
