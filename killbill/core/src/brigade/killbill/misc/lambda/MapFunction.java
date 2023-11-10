package brigade.killbill.misc.lambda;

import brigade.killbill.map.MapObject;

/**
 * Lambda:
 * (object) -> void
 * 
 * Written before I knew that you could nest classes
 */
public interface MapFunction {
    void run(MapObject object);
}
