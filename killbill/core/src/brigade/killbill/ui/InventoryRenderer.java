package brigade.killbill.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;
import brigade.killbill.items.Weapon;
import brigade.killbill.player.Player;
import brigade.killbill.screens.MapScreen;

/**
 * Renders the inventory to the screen.
 * @author csenneff
 */
public class InventoryRenderer {
    private static final int DISPLAY_TIME = 2000;
    private static final int FADE_TIME = 200;

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
     * Texture every item is rendered over
     */
    private Texture backgroundTexture;

    /**
     * Background texture when the item is selected
     */
    private Texture selectedBackgroundTexture;

    /**
     * Overlay texture for the cooldown animation
     */
    private Texture cooldownOverlay;

    /**
     * Controls drawing the name of an item on switch.
     */
    private int nameTimer;

    /**
     * Used to render item names.
     */
    private BitmapFont font;

    /**
     * Item which is having its name displayed.
     */
    private Item textItem;

    /**
     * Width and height of item name.
     */
    private int msgWidth, msgHeight;

    /**
     * Constructs a new InventoryRenderer.
     * @param game      Game object
     */
    public InventoryRenderer(KillBillGame game) {
        this.game = game;
        disabled = false;
        backgroundTexture = game.textureStore.getTexture("ui_inventory_bg");
        selectedBackgroundTexture = game.textureStore.getTexture("ui_inventory_bg_selected");
        cooldownOverlay = game.textureStore.getTexture("ui_cooldown");

        FreeTypeFontParameter parameters = new FreeTypeFontParameter();
        parameters.size = Gdx.graphics.getHeight() / 25;
        parameters.borderWidth = 2;
        parameters.color = Color.BLACK;
        parameters.borderColor = Color.WHITE;
        parameters.shadowColor = new Color(0, 0, 0, 0.65f);
        parameters.shadowOffsetX = 3;
        parameters.shadowOffsetY = 3;
		font = game.fontManager.getFont("regular", parameters);

        textItem = null;
        msgWidth = -1;
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
     * Makes the renderer show the item name for a bit.
     */
    public void itemSwitched() {
        nameTimer = DISPLAY_TIME;
        Item item = game.getPlayer().inventory.getCurrentItem();
        if (item == null) return;
        if (item == textItem) return;

        textItem = item;
        updateLayout(item.getName());
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
        int itemSize = gridSize - itemSpacing * 2;

        Player player = game.getPlayer();

        // Find starting location
        int startingX = Gdx.graphics.getWidth() / 2 - (gridSize * player.inventory.getSize() / 2);
        int currentX = startingX;

        Texture usedBg;
        Item currentItem;
        for (int i = 0; i < player.inventory.getSize(); i++) {
            if (i == player.inventory.getIndex()) usedBg = selectedBackgroundTexture;
            else usedBg = backgroundTexture;

            game.getScreen().fixedBatch.draw(usedBg, currentX, itemSpacing, gridSize, gridSize);
            currentItem = player.inventory.getItem(i);
            if (currentItem != null) {
                currentItem.renderIntoUI(currentX + itemSpacing + itemSpacing / 2, itemSpacing * 2 + itemSpacing / 2, itemSize - itemSpacing, itemSize - itemSpacing);
            }

            // Check for cooldown
            if (currentItem instanceof Weapon) {
                Weapon currentWeapon = (Weapon) currentItem;

                long cooldownTime = currentWeapon.getCooldownTime();
                if (cooldownTime != -1) {
                    // Find cooldown time remaining
                    long msRemaining = cooldownTime - TimeUtils.millis();

                    double frac = (double) msRemaining / (double) currentWeapon.getCooldown();
                    int pixels = (int) (frac * gridSize);

                    if (frac < 0) currentWeapon.stopCooldown();

                    // Render stretched image here
                    game.getScreen().fixedBatch.draw(cooldownOverlay, currentX, itemSpacing, gridSize, pixels);
                }
            }

            currentX += gridSize;
        }

        if (textItem != null) {
            nameTimer -= (int) (1000 * delta);

            if (nameTimer < 0) {
                textItem = null;
            } else {
                Item item = player.inventory.getCurrentItem();

                if (item == null) {
                    nameTimer = 0;
                } else {
                    // Fade if < 200ms
                    float opacity = -1;
                    if (nameTimer < FADE_TIME) {
                        opacity = ((float) nameTimer) / FADE_TIME;
                    } else if (nameTimer > DISPLAY_TIME - FADE_TIME) {
                        opacity = ((float) (DISPLAY_TIME - nameTimer) / FADE_TIME);
                    }

                    if (opacity > 0) {
                        font.setColor(1, 1, 1, opacity);
                    }


                    font.draw(game.getScreen().fixedBatch, item.getName(), Gdx.graphics.getWidth() / 2 - msgWidth / 2, gridSize * 2 + itemSpacing);
                }
            }
        }
    }

    private void updateLayout(String text) {
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text, Color.BLACK, Gdx.graphics.getWidth() / 2, Align.left, true);
        
        msgWidth = (int) layout.width;
        msgHeight = (int) layout.height;
    }
}