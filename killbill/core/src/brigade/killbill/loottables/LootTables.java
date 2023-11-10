package brigade.killbill.loottables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;

/**
 * Helper class which loads all loottables.
 * @author csenneff
 */
public class LootTables {
    /**
     * Font generators loaded from disk.
     */
    private HashMap<String, LootTable> tables;

    /**
     * Default font type.
     */
    private LootTable defaultTable;

    private KillBillGame game;

    /**
     * Constructs a new FontManager.
     */
    public LootTables(KillBillGame game) {
        this.game = game;
        this.tables = new HashMap<String, LootTable>();
        try {
            defaultTable = LootTable.fromFile(game, "loottables/default.txt");
        } catch (FileNotFoundException e) {
            throw new GdxRuntimeException("[lootTables] Failed to load loot tables: Default table does not exist");
        }
    }

    public LootTable getDefaultTable() {
        return defaultTable;
    }

    /**
     * Retrieves a font from the font manager.
     * @param fontName      Name of the font (filename, no extensions)
     * @param parameters    Parameters to apply to the font (size, color, border, etc)
     * @return              Generated BitmapFont with the specified parameters
     */
    public LootTable getTable(String name) {
        LootTable table = tables.get(name);

        if (table == null) {
            return defaultTable;
        }
        
        return table;
    }

    /**
     * Registers all loottables found in a directory.
     * @param directory     Directory to search in (probably "loottables/")
     */
    public void registerAll(String directory) {
        try {
            Files.list(new File(directory).toPath())
                .forEach(path -> {
                    try {
                        registerFromFile(path.toString().replace("\\", "/"));
                    } catch (Exception e) {
                        System.err.println("[lootTables] Failed to register file " + path.toString() + ".");
                        System.err.println(e);
                    } 
                });
        } catch (IOException e) {
            System.err.println("[lootTables] Failed to read files from assets/.");
        }
    }

    /**
     * Registers one font from a file. Name is automatically chosen to be the file's name.
     * @param path          Path to the freetype font file
     * @throws Exception    Font couldn't be loaded
     */
    public void registerFromFile(String path) throws Exception {
        String[] pathSplit = path.split("/");

        // First element is our target
        if (pathSplit.length == 0) {
            throw new Exception(String.format("Unable to parse filename %s.", path));
        }

        String fileName = pathSplit[pathSplit.length - 1];
        // Get the last item for the file name.
        String[] nameSplit = fileName.split("\\.");

        // First element is our target
        if (nameSplit.length == 0) {
            throw new Exception(String.format("Unable to parse filename %s.", fileName));
        }


        String fontName = nameSplit[0];
        registerTable(fontName, path);
    }

    /**
     * Registers a font manually.
     * @param fontName  Name to use for retrieval
     * @param path      Path to the font
     */
    public void registerTable(String tableName, String path) {
        try {
            tables.put(tableName, LootTable.fromFile(game, path));
        } catch (FileNotFoundException e) {
            throw new GdxRuntimeException("[lootTables] Failed to load: File at " + path + " not found");
        }
    }
}
