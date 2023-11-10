package brigade.killbill.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import brigade.killbill.KillBillGame;

/**
 * Base class which can render something to some part of the screen using the ElementRenderer.
 * @author csenneff
 */
public class Element {
    /**
     * Parent Game object
     */
    protected KillBillGame game;

    /**
     * Whether or not the element renders to the fixed batch
     */
    private boolean rendersToFixed;

    /**
     * Constructs a new element.
     * @param game              Parent Game object
     * @param rendersToFixed    Whether or not the element renders to the fixed batch. If false, renders to the fitted batch.
     */
    public Element(KillBillGame game, boolean rendersToFixed) {
        this.game = game;
        this.rendersToFixed = rendersToFixed;
    }

    /**
     * Returns whether or not the element renders to the fixed batch.
     */
    public boolean rendersToFixed() {
        return rendersToFixed;
    }

    /**
     * OVERRIDE THIS with actions to take on batch render.
     */
    public void renderTo(SpriteBatch batch, float delta) { }
    
    /**
     * OVERRIDE THIS with actions to be taken on mouse click.
     */
    public void onMouseClick() { }
}
