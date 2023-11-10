package brigade.killbill.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Helper class containing references to all of our sound effects by name.
 * @author csenneff
 */
public class SoundStore {
    /**
     * HashMap containing a string naming a sound and the actual Sound object.
     */
    private HashMap<String, Sound> sounds;

    /**
     * Default sound. Used if sound could not be found.
     */
    private Sound defaultSound;

    /**
     * Sound load errors logged, so they aren't repeated
     */
    private ArrayList<String> loggedErrors;

    private Random rand;

    /**
     * Constructs a new SoundStore.
     */
    public SoundStore() {
        this.sounds = new HashMap<String, Sound>();
        defaultSound = Gdx.audio.newSound(Gdx.files.internal("sounds/default.wav"));
        loggedErrors = new ArrayList<String>();
        rand = new Random();
    }

    /**
     * Gets a sound from the sound store.
     * @param soundName     Name of the sound to look up.
     * @return              Sound object found.
     */
    public Sound getSound(String soundName) {
        Sound retSound = this.sounds.get(soundName);

        if (retSound == null) {
            if (!loggedErrors.contains(soundName)) {
                loggedErrors.add(soundName);
                System.err.printf("Unable to load sound '%s'. Using default.\n", soundName);
            }
            return defaultSound;
        }
        return retSound;
    }

    public Sound pickOne(String prefix) {
        ArrayList<Sound> res = new ArrayList<Sound>();
        for (Map.Entry<String, Sound> entry : sounds.entrySet()) {
            if (entry.getKey().startsWith(prefix)) res.add(entry.getValue());
        }

        if (res.size() == 0) return defaultSound;
        return res.get(rand.nextInt(res.size()));
    }

    /**
     * Gets the default sound (bill gates laughing).
     * @return      Default sound
     */
    public Sound getDefaultSound() {
        return defaultSound;
    }

    /**
     * Registers all sounds available in a directory recursively.
     * @param directory     Directory to register from
     * @param prefix        Prefix to put in front of all sound names
     */
    public void registerFromDirectory(String directory, String prefix) {
        try {
            Files.list(new File(directory).toPath())
                .forEach(path -> {
                    try {
                        if (Files.isDirectory(path)) {
                            // Get directory name and recurse
                            System.out.println("[soundStore] Registering directory " + path.toString());
                            registerFromDirectory(path.toString(), path.getFileName() + "_");
                        }
                        else {
                            registerFromFile(path.toString(), prefix);
                        }
                    } catch (Exception e) {
                        System.err.println("[soundStore] Failed to register file " + path.toString() + ".");
                        System.err.println("[soundStore] " + e);
                    } 
                });
        } catch (IOException e) {
            System.err.println("[soundStore] Failed to read files from assets/.");
        }
    }

    /**
     * Registers sounds automatically from an array of filenames.
     * @param fileNames     Array of filenames to register.
     * @throws Exception    One or more sounds couldn't be registered.
     */
    public void registerMany(String[] fileNames) throws Exception {
        for (int i = 0; i < fileNames.length; i++) {
            registerFromFile(fileNames[i], "");
        }
        
    }

    /**
     * Registers a sound, generating a name automatically based on its filename.
     * For example, if you call registerFromFile("haha.wav"), the soundName will be "haha".
     * @param path              Path to file
     * @param prefix            Prefix to start all sound names with
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

        String soundName = prefix + nameSplit[0];
        registerSound(soundName, path);
    }

    /**
     * Registers a sound with a specific sound name.
     * @param soundName   Name of sound.
     * @param fileName      Path to sound file.
     */
    public void registerSound(String soundName, String fileName) {
        System.out.printf("[soundStore] Registered sound %s.\n", soundName);
        this.sounds.put(soundName, Gdx.audio.newSound(Gdx.files.internal(fileName)));
    }

    /**
     * Gets the HashMap containing all sounds.
     * @return  All sounds.
     */
    public HashMap<String, Sound> getAllSounds() {
        return this.sounds;
    }
}
