package brigade.killbill.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;

/**
 * Helper class containing references to all of our textures by name.
 * @author csenneff
 */
public class TextureStore {
    /**
     * HashMap containing a string naming a texture and the actual Texture object.
     */
    private HashMap<String, Texture> textures;

    /**
     * Default texture. Used if texture could not be found.
     */
    private Texture defaultTexture;

    /**
     * Texture load errors logged, so they aren't repeated
     */
    private ArrayList<String> loggedErrors;

    /**
     * Constructs a new TextureStore.
     */
    public TextureStore() {
        this.textures = new HashMap<String, Texture>();
        defaultTexture = new Texture("images/default.png");
        loggedErrors = new ArrayList<String>();
    }

    /**
     * Gets a texture from the texture store.
     * @param textureName   Name of the texture to look up.
     * @return              Texture object found.
     */
    public Texture getTexture(String textureName) {
        Texture retTexture = this.textures.get(textureName);

        if (retTexture == null) {
            if (!loggedErrors.contains(textureName)) {
                loggedErrors.add(textureName);
                System.err.printf("[textureStore] Unable to load texture '%s'. Using default.\n", textureName);
            }
            return defaultTexture;
        }
        return retTexture;
    }

    /**
     * Gets the default texture (gmod error).
     * @return      Default texture
     */
    public Texture getDefaultTexture() {
        return defaultTexture;
    }

    /**
     * Registers all textures available in a directory recursively.
     * @param directory     Directory to register from
     * @param prefix        Prefix to put in front of all texture names
     */
    public void registerFromDirectory(String directory, String prefix) {
        try {
            Files.list(new File(directory).toPath())
                .forEach(path -> {
                    try {
                        if (Files.isDirectory(path)) {
                            // Get directory name and recurse
                            System.out.println("[textureStore] Registering directory " + path.toString());
                            registerFromDirectory(path.toString(), path.getFileName() + "_");
                        }
                        else {
                            registerFromFile(path.toString(), prefix);
                        }
                    } catch (Exception e) {
                        System.err.println("[textureStore] Failed to register file " + path.toString() + ".");
                        System.err.println("[textureStore] " + e);
                    } 
                });
        } catch (IOException e) {
            System.err.println("[textureStore] Failed to read files from assets/.");
        }
    }

    /**
     * Registers textures automatically from an array of filenames.
     * @param fileNames     Array of filenames to register.
     * @throws Exception    One or more textures couldn't be registered.
     */
    public void registerMany(String[] fileNames) throws Exception {
        for (int i = 0; i < fileNames.length; i++) {
            registerFromFile(fileNames[i], "");
        }
        
    }

    /**
     * Registers a texture, generating a name automatically based on its filename.
     * For example, if you call registerFromFile("house.jpg"), the textureName will be "house".
     * @param path              Path to file
     * @param prefix            Prefix to start all texture names with
     * @throws Exception        File couldn't be registered
     */
    public void registerFromFile(String path, String prefix) throws Exception {
        path = path.replace("\\", "/");
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

        String textureName = prefix + nameSplit[0];
        registerTexture(textureName, path);
    }

    /**
     * Registers a texture with a specific texture name.
     * @param textureName   Name of texture.
     * @param fileName      Path to texture file.
     */
    public void registerTexture(String textureName, String fileName) {
        System.out.printf("[textureStore] Registered texture %s.\n", textureName);
        this.textures.put(textureName, new Texture(fileName));
    }

    /**
     * Gets the HashMap containing all textures.
     * @return  All textures.
     */
    public HashMap<String, Texture> getAllTextures() {
        return this.textures;
    }

    public void clearAll() {
        textures.clear();
    }
}
