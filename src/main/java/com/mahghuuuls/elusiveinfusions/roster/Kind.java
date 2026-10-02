package com.mahghuuuls.elusiveinfusions.roster;

/** What an infusion does: apply a potion effect after a drink, or change the flask itself. */
public enum Kind {
    EFFECT("effect"),
    MODIFIER("modifier");

    private final String key;

    Kind(String key) {
        this.key = key;
    }

    /** The kind the roster names with this word, or null. */
    static Kind fromKey(String key) {
        for (Kind kind : values()) {
            if (kind.key.equals(key)) {
                return kind;
            }
        }
        return null;
    }
}
