package com.mahghuuuls.elusiveinfusions.rules;

import com.mahghuuuls.elusiveinfusions.roster.InfusionSpec;
import com.mahghuuuls.elusiveinfusions.roster.Tier;

/**
 * Which two infusions of this mod may share one flask.
 *
 * <p>Two members of one family conflict when they are different tiers (a standard and a
 * Concentrated version are never placed together) or when the family is Unique (one member at
 * most, so a second copy conflicts too). Members of different families never conflict here;
 * Stackable copies of one family are allowed.
 */
public final class FamilyRules {

    private FamilyRules() {
    }

    public static boolean conflict(InfusionSpec first, Tier firstTier, InfusionSpec second, Tier secondTier) {
        if (!first.family().equals(second.family())) {
            return false;
        }
        return firstTier != secondTier || first.unique();
    }
}
