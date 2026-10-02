package com.mahghuuuls.elusiveinfusions.roster;

/**
 * The flask values a modifier infusion can change. Charges are deliberately absent: no infusion
 * of this mod raises or lowers them.
 */
public enum Channel {
    HEALING("healing"),
    DRINK_SPEED("drink_speed"),
    HIT_RESISTANCE("hit_resistance"),
    RECHARGE_SPEED("recharge_speed"),
    EFFECT_POWER("effect_power");

    private final String key;

    Channel(String key) {
        this.key = key;
    }

    /** The name the roster and the configuration use for this channel. */
    public String key() {
        return key;
    }

    /** The channel the roster names with this word, or null. */
    static Channel fromKey(String key) {
        for (Channel channel : values()) {
            if (channel.key.equals(key)) {
                return channel;
            }
        }
        return null;
    }
}
