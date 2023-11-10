package brigade.killbill.misc;

/**
 * Miscellaneous helper functions.
 * @author csenneff
 */
public class MiscUtils {
    /**
     * Epsilon value for float comparisons.
     */
    public static final float EPSILON = 0.0005f;

    /**
     * Checks if two floats are sufficiently equal (+- epsilon).
     * @param value     Float 1
     * @param target    Float 2
     * @return          Whether or not value and target are equal
     */
    public static boolean areFloatsEqual(float value, float target) {
        return (value > target - EPSILON) && (value < target + EPSILON);
    }
}
