package brigade.killbill.ui.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import brigade.killbill.KillBillGame;
import brigade.killbill.ui.Element;

/**
 * An element which renders an image to the screen.
 */
public class Image extends Element {
    /**
     * X location
     */
    private int x;

    /**
     * Y location
     */
    private int y;

    /**
     * Width
     */
    private int xSize;

    /**
     * Height
     */
    private int ySize;

    /**
     * Texture
     */
    private Texture texture;
    
    /**
     * Constructs a new Image.
     * @param game      Parent Game object
     * @param x         X location
     * @param y         Y location
     * @param xSize     Width
     * @param ySize     Height
     * @param texture   Texture
     */
    public Image(KillBillGame game, int x, int y, int xSize, int ySize, Texture texture) {
        super(game, true);
        this.x = x;
        this.y = y;
        this.xSize = xSize;
        this.ySize = ySize;
        this.texture = texture;
    }

    @Override
    public void renderTo(SpriteBatch batch, float delta) {
        batch.draw(texture, x, y, xSize, ySize);
    }
}
