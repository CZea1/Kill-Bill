package brigade.killbill.input;

import com.badlogic.gdx.InputProcessor;

import brigade.killbill.KillBillGame;
import brigade.killbill.misc.MiscUtils;
import brigade.killbill.screens.GameScreen;

/**
 * Handles input from the mouse (scroll and click).
 * @author csenneff
 */
public class MouseInputProcessor implements InputProcessor {
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Constructs a new MouseInputProcessor.
     * @param game  Parent Game object
     */
    public MouseInputProcessor(KillBillGame game) {
        super();
        this.game = game;
    }

    /**
     * Called when a key is pressed.
     * @param keycode   Code of key pressed
     */
    public boolean keyDown(int keycode) {
        return false;
    }

    /**
     * Called when a key is released.
     * @param keycode   Code of key released
     */
    public boolean keyUp(int keycode) {
        return false;
    }

    /**
     * Called when a key is typed.
     * @param character     Character typed
     */
    public boolean keyTyped(char character) {
        return false;
    }

    /**
     * Called when the screen is clicked/a button is pressed.
     * @param x             X location
     * @param y             Y location
     * @param pointer       Ignore this
     * @param button        L/R mouse
     */
    public boolean touchDown(int x, int y, int pointer, int button) {
        return false;
    }

    public boolean touchUp(int x, int y, int pointer, int button) {
        game.getScreen().elementRenderer.onMouseClick();
        return false;
    }

    public boolean touchDragged(int x, int y, int pointer) {
        return false;
    }

    public boolean mouseMoved(int x, int y) {
        return false;
    }

    public boolean scrolled(float amountX, float amountY) {
        if (!(game.getScreen() instanceof GameScreen)) return false;

        int shift = 0;
        if (MiscUtils.areFloatsEqual(1, amountY)) {
            shift = 1;
        } else if (MiscUtils.areFloatsEqual(-1, amountY)) {
            shift = -1;
        }

        if (!MiscUtils.areFloatsEqual(0, shift)) {
            game.getPlayer().inventory.shiftIndex(shift);
            game.getPlayer().setItem(game.getPlayer().inventory.getCurrentItem());
        }

        return false;
    }
}
