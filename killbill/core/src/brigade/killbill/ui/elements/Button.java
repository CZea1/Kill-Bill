package brigade.killbill.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import brigade.killbill.KillBillGame;
import brigade.killbill.misc.lambda.TaskFunction;
import brigade.killbill.ui.Element;

/**
 * Element which provides a clickable button.
 * @author Brayden, csenneff
 */
public class Button extends Element {
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
     * Regular texture
     */
    private Texture textureRegular;

    /**
     * Texture when hovering
     */
    private Texture textureHovered;

    /**
     * Function to call on press
     */
    private TaskFunction onPress;
    
    /**
     * Rectangle representing this button
     */
    private Rectangle rectangle;
    
    /**
     * Constructs a new Button.
     * @param game              Parent Game object
     * @param x                 X location
     * @param y                 Y location
     * @param xSize             Width
     * @param ySize             Height
     * @param textureRegular    Regular texture
     * @param textureHovered    Hovering texture
     * @param onPress           Action to take on press: <code>() -> {code; goes; here;}</code>
     */
    public Button(KillBillGame game, int x, int y, int xSize, int ySize, Texture textureRegular, Texture textureHovered, TaskFunction onPress) {
        super(game, true);
        this.x = x;
        this.y = y;
        this.xSize = xSize;
        this.ySize = ySize;
        this.textureRegular = textureRegular;
        this.textureHovered = textureHovered;
        this.onPress = onPress;

        // Generate a rectangle
        this.rectangle = new Rectangle(x, y, xSize, ySize);
    }

    /**
     * Runs actions when the mouse is clicked
     */
    @Override
    public void onMouseClick() {
        // Get X and Y
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (rectangle.contains(mouseX, mouseY)) {
            onPress.run();
        }
    }

    /**
     * Renders the button to the batch
     * @param batch     Batch to render to
     * @param delta     Delta time
     */
    @Override
    public void renderTo(SpriteBatch batch, float delta) {
        // Get texture depending on mouse location
        Texture texture;
        // Get X and Y
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (rectangle.contains(mouseX, mouseY)) texture = textureHovered;
        else texture = textureRegular;

        batch.draw(texture, x, y, xSize, ySize);
    }
}
