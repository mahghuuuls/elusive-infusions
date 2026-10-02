package com.mahghuuuls.elusiveinfusions;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point for Elusive Infusions, which turns Elusive Flora plants into infusions for
 * Everfilling Flasks.
 *
 * <p>Loads on both sides: the server decides every drink and the client only draws. Everfilling
 * Flasks and Elusive Flora are required. Mods that only provide a potion effect are optional and
 * are never named here, because their infusions are looked up by registry id.
 *
 * <p>Dependency ranges are minimum-only on purpose: the versions named are the ones this mod was
 * built and tested against, and nothing newer is excluded.
 */
@Mod(
        modid = Tags.MOD_ID,
        name = Tags.MOD_NAME,
        version = Tags.VERSION,
        dependencies = "required-after:forge@[14.23.5.2847,);"
                + "required-after:everfillingflasks@[1.2.0,);"
                + "required-after:elusiveflora@[1.0.0,)")
public class ElusiveInfusionsMod {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
}
