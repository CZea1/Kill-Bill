package brigade.killbill.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;
import brigade.killbill.items.Weapon;
import brigade.killbill.player.Player;
import brigade.killbill.screens.MapScreen;

/**
 * Renders the player's effects to the screen.
 * @author csenneff
 */
public class EffectRenderer {
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Whether or not rendering is disabled
     */
    private boolean disabled;

    /**
     * Time (in ms) when rendering will be re-enabled
     */
    private long disabledUntil;

    /**
     * Overlay texture for the cooldown animation
     */
    private Texture cooldownOverlay;

    private Texture invincibleTexture;
    private Texture speedTexture;
    private Texture instakillTexture;

    /**
     * Constructs a new InventoryRenderer.
     * @param game      Game object
     */
    public EffectRenderer(KillBillGame game) {
        this.game = game;
        disabled = false;
        cooldownOverlay = game.textureStore.getTexture("ui_cooldown");
        invincibleTexture = game.textureStore.getTexture("ui_effect_invincible");
        speedTexture = game.textureStore.getTexture("ui_effect_speed");
        instakillTexture = game.textureStore.getTexture("ui_effect_instakill");
    }

    /**
     * Pauses rendering for a specific period of time.
     * @param duration      Time to pause for
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
     * Draws the inventory to the active map screen.
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
        int itemSpacing = gridSize / 16;

        // Find starting location
        int currentY = itemSpacing;

        for (Player.Effect eff: game.getPlayer().getEffects()) {
            Texture texture;

            switch (eff.type) {
                case INVINCIBLE:
                    texture = invincibleTexture;
                    break;
                case SPEED:
                    texture = speedTexture;
                    break;
                case INSTA_KILL:
                    texture = instakillTexture;
                    break;
                default:
                    texture = game.textureStore.getDefaultTexture();
                    break;
            }

            game.getScreen().fixedBatch.draw(texture, itemSpacing, currentY, gridSize, gridSize);

            double frac = (double) eff.msRemaining / (double) eff.totalDuration;
            int pixels = (int) (frac * gridSize);

            // Render stretched image here
            game.getScreen().fixedBatch.draw(cooldownOverlay, itemSpacing, currentY, gridSize, pixels);

            currentY += gridSize + itemSpacing;
        }
    }
}