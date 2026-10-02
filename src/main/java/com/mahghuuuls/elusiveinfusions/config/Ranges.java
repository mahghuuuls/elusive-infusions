package com.mahghuuuls.elusiveinfusions.config;

import java.util.List;

/**
 * Reads one raw option string into a number or switch within its allowed range. A bad value never
 * stops the game: the nearest allowed value (or the default, when the text is not a number) is
 * used for the session and one warning says so. The file itself is left as written.
 */
final class Ranges {

    private Ranges() {
    }

    static int intValue(String option, String raw, int fallback, int min, int max, List<String> warnings) {
        if (raw == null) {
            return fallback;
        }
        int value;
        try {
            value = Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            warnings.add(option + " is '" + raw + "', not a whole number; using " + fallback);
            return fallback;
        }
        int used = Math.max(min, Math.min(max, value));
        if (used != value) {
            warnings.add(option + " is " + value + ", outside " + min + " to " + max + "; using " + used);
        }
        return used;
    }

    static double doubleValue(String option, String raw, double fallback, double min, double max,
                              List<String> warnings) {
        if (raw == null) {
            return fallback;
        }
        double value;
        try {
            value = Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            value = Double.NaN;
        }
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            warnings.add(option + " is '" + raw + "', not a number; using " + fallback);
            return fallback;
        }
        double used = Math.max(min, Math.min(max, value));
        if (used != value) {
            warnings.add(option + " is " + value + ", outside " + min + " to " + max + "; using " + used);
        }
        return used;
    }

    static boolean booleanValue(String option, String raw, boolean fallback, List<String> warnings) {
        if (raw == null) {
            return fallback;
        }
        String text = raw.trim();
        if ("true".equalsIgnoreCase(text)) {
            return true;
        }
        if ("false".equalsIgnoreCase(text)) {
            return false;
        }
        warnings.add(option + " is '" + raw + "', not true or false; using " + fallback);
        return fallback;
    }
}
