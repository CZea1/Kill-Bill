package brigade.killbill.misc.lambda;

import brigade.killbill.map.MapObject;

/**
 * Lambda:
 * (object) -> boolean
 */
public interface SearchFunction {
    boolean run(MapObject object);
}
