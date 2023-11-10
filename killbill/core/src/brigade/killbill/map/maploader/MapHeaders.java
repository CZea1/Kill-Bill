package brigade.killbill.map.maploader;

/**
 * Enum representing the various headers present in a Map file.
 * @author csenneff
 */
public enum MapHeaders {
    /**
     * Name of the map
     */
    NAME, 

    /**
     * Integer level ID
     */
    LEVEL, 

    /**
     * Size of the map (grid)
     */
    SIZE, 

    /**
     * Objects to add to the map
     */
    OBJECTS, 

    /**
     * Where the player spawns
     */
    PLAYER_SPAWN, 

    /**
     * Wall info
     */
    WALLS, 

    /**
     * Background texture
     */
    BACKGROUND, 

    /**
     * Last-enemy drops
     */
    DROPS,

    /**
     * No header
     */
    NONE, 

    /**
     * Last header done reading
     */
    DONE,

    /**
     * Actions to take when the player enters the room
     */
    ACTIONS
}
