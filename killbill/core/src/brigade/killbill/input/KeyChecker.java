package brigade.killbill.input;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import brigade.killbill.KillBillGame;
import brigade.killbill.screens.GameScreen;
import brigade.killbill.screens.IntroScreen;
import brigade.killbill.screens.MapScreen;
import brigade.killbill.screens.MenuScreen;
import brigade.killbill.screens.PauseScreen;


/**
 * The KeyChecker handles keypresses and directs them to various events.
 * For example, when the user presses 'ESC' the game exits, and when they press
 * 'F3' the debug menu opens.
 * @author csenneff
 */
public class KeyChecker {
    /**
     * Parent Game object.
     */
    private KillBillGame game;

    /**
     * Extension key checkers.
     */
    private ArrayList<ExtensionKeyChecker> extensions;

    /**
     * Fixes a bug where keys get held onto after restart.
     * Makes the user release all buttons first before exiting.
     */
    private boolean wantToExit;

    /**
     * Constructs a new KeyChecker.
     * @param game   Parent Game class.
     */
    public KeyChecker(KillBillGame game) {
        this.game = game;

        this.extensions = new ArrayList<ExtensionKeyChecker>();
        this.wantToExit = false;

        MouseInputProcessor inputProcessor = new MouseInputProcessor(game);
        Gdx.input.setInputProcessor(inputProcessor);
    }

    /**
     * Registers a new extension key checker.
     * @param keyChecker
     */
    public void registerKeyChecker(ExtensionKeyChecker keyChecker) {
        this.extensions.add(keyChecker);
    }

    /**
     * Checks if any keys are pressed.
     * Call this in render() after the batch has ended.
     */
    public void checkKeys(float delta, boolean checkExt) {
        // Run all extension keys
        if (checkExt) {
            for (int i = 0; i < this.extensions.size(); i++) {
                this.extensions.get(i).runKeyChecks(delta);
            }
        }

        if (Gdx.input.isKeyJustPressed(Keys.F3)) {
            game.debug.setEnabled(!game.debug.isEnabled());
        }

        if (Gdx.input.isKeyJustPressed(Keys.F2)) {
            game.textureStore.clearAll();
            game.textureStore.registerFromDirectory("images/", "");

            float pX = game.player.getX();
            float pY = game.player.getY();
            String currentMap = game.getMapScreen().getMapName();
            game.mapStore.clearAllMaps();
            game.mapStore.registerAllMaps("maps/");
            game.getMapScreen().setMap(currentMap);
            game.getPlayer().setLocation(pX, pY);
        }

        if (Gdx.input.isKeyJustPressed(Keys.F1)) {
            game.setExternRender(!game.getExternRender());
        }
    }

    public void checkKeysLast(float delta) {
        // Escape key
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            GameScreen currentScreen = game.getScreen();
            // Pause if in game
            if (currentScreen instanceof MapScreen) {
                game.pauseScreen.pause();
            }
            // Exit if on main menu 
            else if (currentScreen instanceof MenuScreen) {
                wantToExit = true;
            } 
            // Resume if paused
            else if (currentScreen instanceof PauseScreen) {
                game.setScreen(game.getMapScreen());
                game.debug.setRender(true);
            } 
            // Skip intro if we're there
            else if (currentScreen instanceof IntroScreen) {
                game.introScreen.cancelAll();
            }
        }

        if (wantToExit) {
            if (!Gdx.input.isKeyPressed(Keys.ANY_KEY)) {
                Gdx.app.exit();
                System.exit(69);
            }
        }
    }
}
