package brigade.killbill.ui;

import javax.imageio.spi.ImageInputStreamSpi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;

import brigade.killbill.KillBillGame;
import brigade.killbill.player.Player;
import brigade.killbill.screens.MapScreen;

/**
 * Renders the player's health to the screen.
 * @author calebzea
 */
public class HealthRenderer {
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Whether or not rendering is disabled
     */
    private boolean disabled;

    /**
     * Time in ms until rendering will be reenabled
     */
    private long disabledUntil;

    /**
     * Heart texture
     */
    private Texture heart;

    /**
     * Constructs a new HealthRenderer.
     * @param game  Parent Game object
     */
    public HealthRenderer(KillBillGame game) {
        this.game = game;
        disabled = false;
        heart = game.textureStore.getTexture("ui_heart");
    }

    /**
     * Pauses rendering for a specific period of time.
     * @param duration  Time to pause for (in seconds)
     */
    public void pauseFor(long duration) {
        disabledUntil = TimeUtils.nanoTime() + (duration * 1000);
        disabled = true;
    }

    /**
     * Pauses rendering indefinitely.
     */
    public void pauseIndefinitely() {
        disabledUntil = -1;
        disabled = true;
    }

    /**
     * Unpauses rendering.
     */
    public void unpause() {
        disabled = false;
    }

    /**
     * Draws the player's health to the screen.
     * @param delta     Delta time
     */
    public void draw(float delta) {
        if (!(game.getScreen() instanceof MapScreen)) return; // Only runs if we're in a map screen
        if (disabled) {
            if (disabledUntil < 0) return;
            else if (TimeUtils.nanoTime() < disabledUntil) disabled = false;
            else return;
        }
        if (!game.getExternRender()) return;

        int gridSize = Gdx.graphics.getWidth() / 25;
        int heartSize = gridSize / 2;
        int itemSpacing = gridSize / 16;
        Player player = game.getPlayer();

        // Find starting location
        //gives whitespace from the left of the screen
        int startingX = Gdx.graphics.getWidth() / 2 - ((heartSize + itemSpacing) * player.getHealth()) / 2;
        int currentX = startingX;

        for (int i = 0; i < player.getHealth(); i++) {    
            game.getScreen().fixedBatch.draw(heart, currentX, gridSize + itemSpacing * 3, heartSize, heartSize);
            currentX += heartSize + itemSpacing;
        }
    }
}