package brigade.killbill.ui;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.TimeUtils;

import brigade.killbill.KillBillGame;

/**
 * Class which displays a debugging menu when the user presses 'F3'.
 * Any other class can add values to this on the fly -- see '.setValue()'.
 * @author csenneff
 */
public class DebugDisplay {
    /**
     * Parent Game object.
     */
    private KillBillGame game;

    /**
     * Whether or not the menu is displayed.
     */
    private Boolean enabled;

    /**
     * Whether or not the menu can be rendered.
     */
    private Boolean render;

    /**
     * Font to render with.
     */
    private BitmapFont font;

    /**
     * Extra values to render at the bottom of the display.
     * name: message
     */
    private LinkedHashMap<String, String> extras;

    /**
     * Time (in ms) since FPS last updated.
     */
    private long fpsTime;
    
    /**
     * Number of frames counted since last update.
     */
	private int frames;

    /**
     * Most current FPS value.
     */
	private int fps;

    /**
     * Height of the font, scaled to the display size.
     */
    private int fontHeight;

    /**
     * Current RAM usage in bytes.
     */
    private long ramUsage;

    /**
     * Constructs a new debug display.
     * @param game      Parent Game class.
     * @param enabled   Whether or not the menu is enabled by default.
     */
    public DebugDisplay(KillBillGame game, Boolean enabled) {
        this.game = game;
        this.enabled = enabled;
        this.fontHeight = Gdx.graphics.getHeight() / 50;
        this.ramUsage = 0;
        this.render = true;
        
        FreeTypeFontParameter parameters = new FreeTypeFontParameter();
        parameters.size = fontHeight;
        parameters.borderWidth = 2;
        parameters.color = Color.BLACK;
        parameters.borderColor = Color.WHITE;
        parameters.shadowColor = new Color(0, 0, 0, 0.65f);
        parameters.shadowOffsetX = 3;
        parameters.shadowOffsetY = 3;
		font = game.fontManager.getFont("mono-black", parameters);

        this.extras = new LinkedHashMap<String, String>();
        
        fpsTime = TimeUtils.millis();
    }
    
    /**
     * Adds or updates a message in the debug display.
     * @param refId     An internal string, used to update the value later.
     * @param message   The string that's actually displayed to the user.
     */
    public void setValue(String refId, String message) {
        extras.put(refId, message);
    } 

    /**
     * Removes a refId from the debug display.
     * @param refId     Reference ID (name) of value to remove.
     */
    public void removeValue(String refId) {
        extras.remove(refId);
    }

    /**
     * Checks if the debug menu is enabled.
     * @return  Whether or not the menu is enabled.
     */
    public Boolean isEnabled() {
        return enabled;
    }

    /**
     * Changes whether or not the display is enabled or disabled.
     * @param enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Checks if the debug menu is able to be rendered.
     * @return  Whether or not the menu is rendering.
     */
    public Boolean isRendering() {
        return render;
    }

    /**
     * Changes whether or not the display can be rendered or not.
     * @param state
     */
    public void setRender(Boolean state) {
        this.render = state;
    }

    /**
     * Call this when a frame is rendered. Used for FPS calculations.
     */
    public void frameDone() {
        frames++;
		if (TimeUtils.timeSinceMillis(fpsTime) >= 1000) {
			fpsTime = TimeUtils.millis();
			fps = frames;
			frames = 0;

            // Hijacking this to implement RAM usage
            ramUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		}
	}

    /**
     * Draws the debug menu to the screen.
     * Make sure the fixed batch has begun first.
     */
    public void draw() {
        if (!enabled) return;
        if (!render) return;
        if (!game.getExternRender()) return;

        int x_loc = 10;
        int y_loc = Gdx.graphics.getHeight() - 10;

        int y_diff = (int) (fontHeight * 1.2);

        // Title
        font.draw(game.getScreen().fixedBatch, String.format("Kill Bill v%s", KillBillGame.VERSION), x_loc, y_loc);
        y_loc -= y_diff;

        // Toggle
        font.draw(game.getScreen().fixedBatch, "press 'F3' to toggle debug info", x_loc, y_loc);
        y_loc -= y_diff * 1.5;

        // FPS
        font.draw(game.getScreen().fixedBatch, String.format("FPS: %d", fps), x_loc, y_loc);
        y_loc -= y_diff;

        // RAM
        String res = "";
        // This lovely function stolen from https://stackoverflow.com/questions/3758606/how-can-i-convert-byte-size-into-a-human-readable-format-in-java
        if (-1000 < ramUsage && ramUsage < 1000) {
            res = new String(ramUsage + " B");
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (ramUsage <= -999_950 || ramUsage >= 999_950) {
            ramUsage /= 1000;
            ci.next();
        }
        res = String.format("RAM: %.1f %cB", ramUsage / 1000.0, ci.current());
        font.draw(game.getScreen().fixedBatch, res, x_loc, y_loc);
        y_loc -= y_diff * 1.5;

        // User defined values
        for (Map.Entry<String, String> mapElement : extras.entrySet()) {
            font.draw(game.getScreen().fixedBatch, mapElement.getValue(), x_loc, y_loc);
            y_loc -= y_diff;
        }
    }
}