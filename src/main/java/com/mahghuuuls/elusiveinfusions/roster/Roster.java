package com.mahghuuuls.elusiveinfusions.roster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Every infusion this mod ships, read from the one roster file inside the jar.
 *
 * <p>The roster file is the single source of what exists and of every shipped number. This class
 * is the only place that knows its columns and notation; everything else works with
 * {@link InfusionSpec}. A malformed file is a build defect, so reading fails loudly with the row
 * and the reason instead of skipping the row.
 */
public final class Roster {

    /** Classpath location of the bundled roster. */
    public static final String RESOURCE = "/assets/elusiveinfusions/infusion-roster.csv";

    private static final String[] COLUMNS = {
        "family", "display_name", "kind", "unique", "requires_mod", "effect_id",
        "std_plants", "std_cost", "std_bonuses", "std_level", "std_seconds",
        "conc_plants", "conc_cost", "conc_bonuses", "conc_level", "conc_seconds"
    };

    private static Roster bundled;

    private final List<InfusionSpec> specs;
    private final Map<String, InfusionSpec> byFamily;

    private Roster(List<InfusionSpec> specs) {
        this.specs = Collections.unmodifiableList(specs);
        Map<String, InfusionSpec> index = new HashMap<String, InfusionSpec>();
        for (InfusionSpec spec : specs) {
            index.put(spec.family(), spec);
        }
        this.byFamily = index;
    }

    /** The roster shipped in this jar. Read once, on first use. */
    public static synchronized Roster bundled() {
        if (bundled == null) {
            InputStream in = Roster.class.getResourceAsStream(RESOURCE);
            if (in == null) {
                throw new RosterFormatException("Roster resource missing: " + RESOURCE);
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                bundled = parse(reader);
            } catch (IOException e) {
                throw new RosterFormatException("Roster resource unreadable: " + e.getMessage());
            }
        }
        return bundled;
    }

    /** Every family, in file order. */
    public List<InfusionSpec> specs() {
        return specs;
    }

    /** The family with this id, or null. */
    public InfusionSpec family(String family) {
        return byFamily.get(family);
    }

    public static Roster parse(Reader source) throws IOException {
        BufferedReader reader = new BufferedReader(source);
        String header = reader.readLine();
        if (header == null) {
            throw new RosterFormatException("Roster is empty");
        }
        List<String> names = splitLine(header, 1);
        if (names.size() != COLUMNS.length) {
            throw new RosterFormatException("Roster header has " + names.size()
                    + " columns, expected " + COLUMNS.length);
        }
        for (int i = 0; i < COLUMNS.length; i++) {
            if (!COLUMNS[i].equals(names.get(i))) {
                throw new RosterFormatException("Roster column " + (i + 1) + " is '" + names.get(i)
                        + "', expected '" + COLUMNS[i] + "'");
            }
        }

        List<InfusionSpec> specs = new ArrayList<InfusionSpec>();
        Set<String> families = new HashSet<String>();
        int lineNumber = 1;
        String line;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (line.trim().isEmpty()) {
                continue;
            }
            List<String> cells = splitLine(line, lineNumber);
            if (cells.size() != COLUMNS.length) {
                throw new RosterFormatException("Roster line " + lineNumber + " has " + cells.size()
                        + " cells, expected " + COLUMNS.length);
            }
            InfusionSpec spec = spec(cells, lineNumber);
            if (!families.add(spec.family())) {
                throw new RosterFormatException("Roster line " + lineNumber + " repeats family '"
                        + spec.family() + "'");
            }
            specs.add(spec);
        }
        return new Roster(specs);
    }

    private static InfusionSpec spec(List<String> c, int line) {
        String family = required(c.get(0), "family", line);
        if (!family.matches("[a-z][a-z0-9_]*")) {
            throw new RosterFormatException("Roster line " + line + ": family '" + family
                    + "' must be lowercase letters, digits, or underscores");
        }
        String displayName = required(c.get(1), "display_name", line);
        Kind kind = Kind.fromKey(required(c.get(2), "kind", line));
        if (kind == null) {
            throw new RosterFormatException("Roster line " + line + ": unknown kind '" + c.get(2) + "'");
        }
        boolean unique = yesNo(c.get(3), "unique", line);
        String requiresMod = emptyToNull(c.get(4));
        String effectId = emptyToNull(c.get(5));
        if (kind == Kind.EFFECT && (effectId == null || !effectId.matches("[a-z0-9_.-]+:[a-z0-9_./-]+"))) {
            throw new RosterFormatException("Roster line " + line + ": an effect infusion needs an "
                    + "effect_id of the form mod:name");
        }
        if (kind == Kind.MODIFIER && effectId != null) {
            throw new RosterFormatException("Roster line " + line + ": a modifier infusion has no effect_id");
        }

        TierValues standard = tier(kind, c.get(6), c.get(7), c.get(8), c.get(9), c.get(10), "std", line);
        TierValues concentrated = null;
        if (!c.get(11).isEmpty()) {
            concentrated = tier(kind, c.get(11), c.get(12), c.get(13), c.get(14), c.get(15), "conc", line);
        } else if (!(c.get(12) + c.get(13) + c.get(14) + c.get(15)).isEmpty()) {
            throw new RosterFormatException("Roster line " + line + ": Concentrated values without conc_plants");
        }
        return new InfusionSpec(family, displayName, kind, unique, requiresMod, effectId,
                standard, concentrated);
    }

    private static TierValues tier(Kind kind, String plants, String cost, String bonuses, String level,
                                   String seconds, String prefix, int line) {
        List<PlantCount> plantCounts = plants(plants, prefix + "_plants", line);
        int potencyCost = integer(cost, prefix + "_cost", line);
        if (potencyCost < 0) {
            throw new RosterFormatException("Roster line " + line + ": " + prefix + "_cost is negative");
        }
        if (kind == Kind.MODIFIER) {
            if (!level.isEmpty() || !seconds.isEmpty()) {
                throw new RosterFormatException("Roster line " + line + ": a modifier infusion has no "
                        + prefix + "_level or " + prefix + "_seconds");
            }
            return TierValues.modifier(plantCounts, potencyCost, bonuses(bonuses, prefix + "_bonuses", line));
        }
        if (!bonuses.isEmpty()) {
            throw new RosterFormatException("Roster line " + line + ": an effect infusion has no "
                    + prefix + "_bonuses");
        }
        int effectLevel = integer(level, prefix + "_level", line);
        int effectSeconds = integer(seconds, prefix + "_seconds", line);
        if (effectLevel < 1 || effectSeconds < 1) {
            throw new RosterFormatException("Roster line " + line + ": " + prefix
                    + "_level and " + prefix + "_seconds must be at least 1");
        }
        return TierValues.effect(plantCounts, potencyCost, effectLevel, effectSeconds);
    }

    /** {@code 3xpearlfrond+3xtideheart}: a count, an x, and a plant id, joined by plus signs. */
    private static List<PlantCount> plants(String cell, String column, int line) {
        required(cell, column, line);
        List<PlantCount> result = new ArrayList<PlantCount>();
        for (String part : cell.split("\\+", -1)) {
            int x = part.indexOf('x');
            if (x < 1 || x == part.length() - 1) {
                throw new RosterFormatException("Roster line " + line + ": " + column + " entry '"
                        + part + "' is not <count>x<plant>");
            }
            int count = integer(part.substring(0, x), column, line);
            if (count < 1) {
                throw new RosterFormatException("Roster line " + line + ": " + column
                        + " count must be at least 1");
            }
            result.add(new PlantCount(part.substring(x + 1), count));
        }
        return result;
    }

    /** {@code drink_speed:+0.50;healing:-0.20}: a channel and a signed fraction, joined by semicolons. */
    private static Map<Channel, Double> bonuses(String cell, String column, int line) {
        required(cell, column, line);
        Map<Channel, Double> result = new EnumMap<Channel, Double>(Channel.class);
        for (String part : cell.split(";", -1)) {
            int colon = part.indexOf(':');
            Channel channel = colon < 1 ? null : Channel.fromKey(part.substring(0, colon));
            if (channel == null) {
                throw new RosterFormatException("Roster line " + line + ": " + column + " entry '"
                        + part + "' does not name a known channel");
            }
            double value;
            try {
                value = Double.parseDouble(part.substring(colon + 1));
            } catch (NumberFormatException e) {
                throw new RosterFormatException("Roster line " + line + ": " + column + " entry '"
                        + part + "' has no number");
            }
            if (value == 0.0 || Double.isNaN(value) || Double.isInfinite(value)
                    || result.put(channel, value) != null) {
                throw new RosterFormatException("Roster line " + line + ": " + column + " entry '"
                        + part + "' is zero, not a finite number, or repeats a channel");
            }
        }
        return result;
    }

    private static String required(String cell, String column, int line) {
        if (cell.isEmpty()) {
            throw new RosterFormatException("Roster line " + line + ": " + column + " is empty");
        }
        return cell;
    }

    private static boolean yesNo(String cell, String column, int line) {
        if ("yes".equals(cell)) {
            return true;
        }
        if ("no".equals(cell)) {
            return false;
        }
        throw new RosterFormatException("Roster line " + line + ": " + column + " must be yes or no");
    }

    private static int integer(String cell, String column, int line) {
        try {
            return Integer.parseInt(cell);
        } catch (NumberFormatException e) {
            throw new RosterFormatException("Roster line " + line + ": " + column + " '" + cell
                    + "' is not a whole number");
        }
    }

    private static String emptyToNull(String cell) {
        return cell.isEmpty() ? null : cell;
    }

    /**
     * Splits one line on commas. The roster needs no quoting, so a quote is refused rather than
     * interpreted: a cell that seems to need one is a mistake in the file.
     */
    private static List<String> splitLine(String line, int lineNumber) {
        if (line.indexOf('"') >= 0) {
            throw new RosterFormatException("Roster line " + lineNumber + " contains a quote");
        }
        List<String> cells = new ArrayList<String>();
        for (String cell : line.split(",", -1)) {
            cells.add(cell.trim());
        }
        return cells;
    }
}
