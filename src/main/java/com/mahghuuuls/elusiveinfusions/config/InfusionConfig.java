package com.mahghuuuls.elusiveinfusions.config;

import com.mahghuuuls.elusiveinfusions.ElusiveInfusionsMod;
import com.mahghuuuls.elusiveinfusions.roster.Roster;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The configuration file: every option {@link InfusionValues#options} lists, written with its
 * roster default and comment the first time, then read back as text and turned into
 * {@link InfusionValues}.
 *
 * <p>Every option is declared to Forge as text, never as a typed value. Forge's typed getters
 * replace a value they cannot parse with the default and rewrite the file; the text form hands
 * the raw value through untouched, so a bad value is reported by {@link Ranges} and replaced for
 * the session only, while the file keeps what the author wrote. Values are read once at
 * pre-initialization and take effect on the next game start.
 */
public final class InfusionConfig {

    private static InfusionValues current;

    private InfusionConfig() {
    }

    /** Reads (and on first use writes) the file, keeps the result as the current values. */
    public static InfusionValues load(File file, Roster roster) {
        Configuration config = new Configuration(file);
        config.load();
        config.setCategoryComment(InfusionValues.CATEGORY_GENERAL, "Options that apply to every infusion.");
        config.setCategoryComment(InfusionValues.CATEGORY_INFUSIONS,
                "One section per infusion item. Every value takes effect on the next game start.");
        config.setCategoryComment(InfusionValues.CATEGORY_RECIPES, "One switch per recipe.");
        Map<String, String> raw = new HashMap<String, String>();
        for (ConfigOption option : InfusionValues.options(roster)) {
            Property property = config.get(option.category(), option.key(), option.defaultText(),
                    option.comment(), forgeType(option.type()));
            raw.put(option.path(), property.getString());
        }
        List<String> warnings = new ArrayList<String>();
        current = InfusionValues.parse(roster, raw, warnings);
        for (String warning : warnings) {
            ElusiveInfusionsMod.LOGGER.warn("Configuration: {}", warning);
        }
        if (config.hasChanged()) {
            config.save();
        }
        return current;
    }

    /** The values in force; null before {@link #load}, which pre-initialization runs. */
    public static InfusionValues current() {
        return current;
    }

    /** The type Forge records for the option, which only shapes how it writes the file. */
    private static Property.Type forgeType(ConfigOption.Type type) {
        switch (type) {
            case INTEGER:
                return Property.Type.INTEGER;
            case DECIMAL:
                return Property.Type.DOUBLE;
            case SWITCH:
                return Property.Type.BOOLEAN;
            default:
                throw new IllegalStateException("Unmapped option type " + type);
        }
    }
}
