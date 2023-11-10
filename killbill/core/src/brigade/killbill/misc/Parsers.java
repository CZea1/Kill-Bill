package brigade.killbill.misc;

import java.util.HashMap;

import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.EntityAttributes;

/**
 * Static class which has some shorthand string->other parsing functions.
 * @author csenneff
 */
public class Parsers {
    /**
     * True and false aliases.
     */
    private static final String[] TRUE_VALUES = {"true", "t", "yes", "y", "on", "enabled"};
    private static final String[] FALSE_VALUES = {"false", "f", "no", "n", "off", "disabled"};

    /**
     * Parses a string into an integer. Throws a runtime exception if it fails.
     * @param value     String value
     * @return          Integer value
     */
    public static int toInt(String value) {
        try {
            return Integer.parseInt(value);           
        } catch (NumberFormatException e) {
            throw new GdxRuntimeException("Failed to parse integer.");
        }
    }

    /**
     * Parses a string into a boolean. Throws a runtime exception if it fails.
     * @param value     String value
     * @return          Boolean value
     */
    public static Boolean toBool(String value) {
        value = value.toLowerCase();

        Boolean res = null;
        for (int i = 0; i < TRUE_VALUES.length; i++) {
            if (value.equals(TRUE_VALUES[i])) res = true;
        }

        
        for (int i = 0; i < FALSE_VALUES.length; i++) {
            if (value.equals(FALSE_VALUES[i])) res = false;
        }

        if (res == null) throw new GdxRuntimeException(String.format("Failed to parse '%s' as a boolean.", value));
        return res;
    }

    /**
     * Parses a string into an entity attribute. Throws a runtime exception if it fails.
     * @param value     String value
     * @return          Entity attribute value
     */
    public static EntityAttributes toEntityAttributes(String value) {
        value = value.toLowerCase();

        switch (value) {
            case "move_towards_player":
                return EntityAttributes.MOVE_TOWARDS_PLAYER;
            case "invincible":
                return EntityAttributes.INVINCIBLE;
            default:
                throw new GdxRuntimeException(String.format("Failed to parse %s as an EntityAtribute.", value));
        }
    }

    /**
     * Parses some arguments in format "key=value,key2=value2,..."
     * @param args  Argument string
     * @return      Parsed HashMap
     */
    public static HashMap<String, String> argsToHashMap(String args) {
        HashMap<String, String> argsMap = new HashMap<String, String>();
        String[] objArgs = args.split(",");
        String arg;
        for (int i = 0; i < objArgs.length; i++) {
            arg = objArgs[i].strip();
            if (arg.length() == 0) continue;
            // Find equals, split there
            if (!arg.contains("=")) {
                throw new GdxRuntimeException("Failed to parse arg hash map. Args must be key=value.");
            }
            String[] argSplit = arg.split("\\=", 2);

            argsMap.put(argSplit[0].strip(), argSplit[1].strip());
        }
        return argsMap;
    }

    /**
     * Parses a grid scale into a pixel size.
     * A grid scale value is formatted as 'g[numerator]/[denominator]'.
     * This effectively means grid size * some fraction.
     * For example, if gridSize is 32 and 'g1/4' is the fraction,m
     * this will return 8.
     * @param args  Grid scale value
     * @return      Pixels
     */
    public static int gridScaleToPixels(String args) {
        // Remove first 'g'
        if (args.charAt(0) == 'g') args = args.substring(1, args.length()).strip();
        if (args.length() == 0) return KillBillGame.GRID_SIZE;

        String[] frac = args.split("/", 2);

        if (frac.length != 2) {
            for (String f: frac) System.err.println(f);
            throw new GdxRuntimeException("Failed to parse grid scale: Must be formatted g[num]/[den]");
        }

        int num = Parsers.toInt(frac[0]);
        int den = Parsers.toInt(frac[1]);

        float factor = (float) num / den;
        return (int) (factor * KillBillGame.GRID_SIZE);
    }

    /**
     * Parses a gridscale or regular value to an integer.
     * @param args  Gridscale, regular, or both integer
     * @return      Pixels
     */
    public static int toPixels(String args) {
        args = args.strip();

        if (args.charAt(0) == 'g') {
            return gridScaleToPixels(args);
        } else if (args.contains("g")) {
            String[] coordArgs = {};
            int factor = 0;
            if (args.contains("+g")) {
                // Split into gridscale and main arg
                coordArgs = args.split("\\+", 2);
                factor = 1;
            } else if (args.contains("-g")) {
                coordArgs = args.split("\\-", 2);
                factor = -1;
            }

            return (KillBillGame.GRID_SIZE * toInt(coordArgs[0])) + (factor * gridScaleToPixels(coordArgs[1]));
        }
        else {
            return KillBillGame.GRID_SIZE * toInt(args);
        }
    }
}
