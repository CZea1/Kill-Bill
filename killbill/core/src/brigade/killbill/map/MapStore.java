package brigade.killbill.map;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;

/**
 * Helper class which registers all maps from our map directory, and provides methods to retrieve them.
 * @author csenneff
 */
public class MapStore {
    /**
     * All registered maps.
     */
    private ArrayList<Map> maps;

    /**
     * Parent Game object.
     */
    private KillBillGame game;

    /**
     * Constructs a new MapStore object.
     * @param game      Parent Game object
     */
    public MapStore(KillBillGame game) {
        this.game = game;
        this.maps = new ArrayList<Map>();
    }

    /**
     * Gets a map from the map store. Throws a runtime exception if not found.
     * @param name      Name of map (!name in .map file)
     * @return          Map object found
     */
    public Map getMap(String name) {
        Map currentMap;
        for (int i = 0; i < maps.size(); i++) {
            currentMap = maps.get(i);

            if (currentMap == null) throw new GdxRuntimeException("HOW");
            if (currentMap.getName().equals(name)) return currentMap;
        }

        throw new GdxRuntimeException(String.format("Map %s could not be found", name));
    }

    /**
     * Removes all maps.
     */
    public void clearAllMaps() {
        maps.clear();
    }

    /**
     * Registers a map to the map store.
     * @param map       Map to add
     */
    public void registerMap(Map map) {
        this.maps.add(map);
    }

    /**
     * Loads a map from a file, then registers it to the map store.
     * @param path          Path to the map file
     * @throws Exception    File could not be loaded
     */
    public void registerFromFile(String path) throws Exception {
        Map map = new Map(game);

        game.mapLoader.loadTo(path, map);
        registerMap(map);
        //map.generatePaths();
    }

    /**
     * Registers all available maps in the specified directory.
     * @param directory     Directory to search in -- probably "maps/"
     */
    public void registerAllMaps(String directory) {
        registerFromDirectory(directory);
    }

    public void registerFromDirectory(String directory) {
        try {
            Files.list(new File(directory).toPath())
                .forEach(path -> {
                    try {
                        if (Files.isDirectory(path)) {
                            // Get directory name and recurse
                            System.out.println("[mapStore] Registering directory " + path.toString());
                            registerFromDirectory(path.toString());
                        }
                        else {
                            registerFromFile(path.toString().replace("\\", "/"));
                        }
                    } catch (Exception e) {
                        System.err.println("[mapStore] Failed to register map " + path.toString() + ".");
                        System.err.println(e);
                        e.printStackTrace();
                        throw new GdxRuntimeException("[mapStore] Failed to load. See stack trace above.");
                    } 
                });
        } catch (IOException e) {
            System.err.println("[mapStore] Failed to read maps from " + directory + ".");
        }
    }
}
