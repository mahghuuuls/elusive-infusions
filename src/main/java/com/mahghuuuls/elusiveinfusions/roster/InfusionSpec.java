package com.mahghuuuls.elusiveinfusions.roster;

/**
 * One infusion family as the roster describes it: what it is, when it exists, and the shipped
 * values of its standard and, when it has one, its Concentrated version.
 */
public final class InfusionSpec {

    private final String family;
    private final String displayName;
    private final Kind kind;
    private final boolean unique;
    private final String requiresMod;
    private final String effectId;
    private final TierValues standard;
    private final TierValues concentrated;

    InfusionSpec(String family, String displayName, Kind kind, boolean unique, String requiresMod,
                 String effectId, TierValues standard, TierValues concentrated) {
        this.family = family;
        this.displayName = displayName;
        this.kind = kind;
        this.unique = unique;
        this.requiresMod = requiresMod;
        this.effectId = effectId;
        this.standard = standard;
        this.concentrated = concentrated;
    }

    /** Stable id of the family; registry names are derived from it. */
    public String family() {
        return family;
    }

    /** English name of the family, without the word "Infusion". */
    public String displayName() {
        return displayName;
    }

    public Kind kind() {
        return kind;
    }

    /** True when only one member of this family may sit in a flask. */
    public boolean unique() {
        return unique;
    }

    /** Mod id that must be installed for this family to exist, or null when it always exists. */
    public String requiresMod() {
        return requiresMod;
    }

    /** Registry id of the potion effect, for an effect infusion; null for a modifier. */
    public String effectId() {
        return effectId;
    }

    public boolean hasTier(Tier tier) {
        return tier == Tier.STANDARD || concentrated != null;
    }

    /** Shipped values of the tier. Throws when the family has no such tier. */
    public TierValues values(Tier tier) {
        if (tier == Tier.STANDARD) {
            return standard;
        }
        if (concentrated == null) {
            throw new IllegalArgumentException("Family " + family + " has no Concentrated version");
        }
        return concentrated;
    }

    /** Registry path of the item for this tier, for example {@code concentrated_heartroot_infusion}. */
    public String itemPath(Tier tier) {
        return tier == Tier.STANDARD ? family + "_infusion" : "concentrated_" + family + "_infusion";
    }
}
