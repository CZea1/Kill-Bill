package brigade.killbill.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * Helper class which loads all freetype fonts and provides methods to generate BitmapFonts from them.
 * @author csenneff
 */
public class FontManager {
    /**
     * Font generators loaded from disk.
     */
    private HashMap<String, FreeTypeFontGenerator> fonts;

    /**
     * Default font type.
     */
    private FreeTypeFontGenerator defaultFont;

    /**
     * Constructs a new FontManager.
     */
    public FontManager() {
        this.fonts = new HashMap<String, FreeTypeFontGenerator>();
        //FreeTypeFontGenerator.setMaxTextureSize(1024);
        this.defaultFont = new FreeTypeFontGenerator(Gdx.files.internal("fonts/default.ttf"));
    }

    /**
     * Retrieves a font from the font manager.
     * @param fontName      Name of the font (filename, no extensions)
     * @param parameters    Parameters to apply to the font (size, color, border, etc)
     * @return              Generated BitmapFont with the specified parameters
     */
    public BitmapFont getFont(String fontName, FreeTypeFontParameter parameters) {
        FreeTypeFontGenerator retFont = this.fonts.get(fontName);

        if (retFont == null) {
            retFont = defaultFont;
        }
        
        if (parameters == null) {
            parameters = new FreeTypeFontParameter();
            parameters.size = 16;
            parameters.color = Color.BLACK;
            parameters.borderColor = Color.WHITE;
            parameters.borderWidth = 1;
        }

        BitmapFont bmpFont = retFont.generateFont(parameters);
        return bmpFont;
    }

    /**
     * Registers all freetype fonts found in a directory to the font manager.
     * @param directory     Directory to search in (probably "fonts/")
     */
    public void registerAllFonts(String directory) {
        try {
            Files.list(new File(directory).toPath())
                .forEach(path -> {
                    try {
                        registerFromFile(path.toString().replace("\\", "/"));
                    } catch (Exception e) {
                        System.err.println("Failed to register file " + path.toString() + ".");
                        System.err.println(e);
                    } 
                });
        } catch (IOException e) {
            System.err.println("Failed to read files from assets/.");
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
        registerFont(fontName, path);
    }

    /**
     * Registers a font manually.
     * @param fontName  Name to use for retrieval
     * @param path      Path to the font
     */
    public void registerFont(String fontName, String path) {
        FreeTypeFontGenerator newFont = new FreeTypeFontGenerator(Gdx.files.internal(path));

        this.fonts.put(fontName, newFont);
    }
}
