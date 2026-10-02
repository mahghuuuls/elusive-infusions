package com.mahghuuuls.elusiveinfusions.config;

/**
 * One option of the configuration file: where it lives, its shipped default as text, what it
 * means, and what kind of value it holds. {@link InfusionValues} lists them; the file layer
 * declares exactly that list, so the two can never name an option differently.
 */
public final class ConfigOption {

    public enum Type {
        INTEGER, DECIMAL, SWITCH
    }

    private final String category;
    private final String key;
    private final String defaultText;
    private final String comment;
    private final Type type;

    ConfigOption(String category, String key, String defaultText, String comment, Type type) {
        this.category = category;
        this.key = key;
        this.defaultText = defaultText;
        this.comment = comment;
        this.type = type;
    }

    public String category() {
        return category;
    }

    public String key() {
        return key;
    }

    /** The lookup name {@code category.key} used for raw values. */
    public String path() {
        return category + "." + key;
    }

    public String defaultText() {
        return defaultText;
    }

    public String comment() {
        return comment;
    }

    public Type type() {
        return type;
    }
}
