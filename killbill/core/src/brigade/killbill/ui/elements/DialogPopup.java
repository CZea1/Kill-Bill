package brigade.killbill.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import brigade.killbill.KillBillGame;
import brigade.killbill.ui.Element;

/**
 * Element which shows a dynamically-sized, animated dialog box to the user.
 */
public class DialogPopup extends Element {
    /**
     * X location
     */
    private int x;

    /**
     * Y location
     */
    private int y;

    /**
     * Maximum width in pixels
     */
    private int maxWidth;

    /** 
     * Message rendered to the screen
    */
    private String message;

    /**
     * Font to render with
     */
    private BitmapFont font;
    
    /**
     * Whether or not a border will be rendered
     */
    private boolean useBorder;

    /**
     * Alignment
     */
    private int align;

    /**
     * Width of screen
     */
    private int xSize;

    /**
     * Height of screen
     */
    private int ySize;

    /**
     * Message width
     */
    private int msgWidth;

    /**
     * Message height
     */
    private int msgHeight;

    // Animation
    /**
     * Current letter index
     */
    private int currentIndex;

    /**
     * MS between each letter
     */
    private int msPerIndex;

    /**
     * Whether or not to animate
     */
    private boolean useAnim;

    /**
     * Current time in animation
     */
    private int currentTime;

    // Box
    /**
     * Texture for top of box
     */
    private Texture boxTop;

    /**
     * Texture for middle of box
     */
    private Texture boxMid;

    /**
     * Texture for bottom of box
     */
    private Texture boxBot;

    /**
     * GlyphLayout used to calculate size
     */
    private GlyphLayout layout;

    /**
     * Self destruction time
     */
    private long destructAt;

    /**
     * Constructs a new DialogPopup.
     * @param game                  Parent Game object
     * @param rendersToFixed        Whether or not to render to fixed
     * @param x                     X location
     * @param y                     Y location
     * @param fontHeight            Font height
     * @param maxWidth              Maximum width in pixels
     * @param center                Whether or not to center the message
     * @param useBorder             Whether or not to render a border
     * @param message               Message to display
     */
    public DialogPopup(KillBillGame game, boolean rendersToFixed, int x, int y, int fontHeight, int maxWidth, boolean center, boolean useBorder, String message) {
        super(game, rendersToFixed);
        this.x = x;
        this.y = y;
        this.maxWidth = maxWidth;
        this.message = message;
        this.useBorder = useBorder;
        this.align = center ? Align.center : Align.left;

        // Get textures
        if (useBorder) {
            boxTop = game.textureStore.getTexture("ui_msgbox_top");
            boxMid = game.textureStore.getTexture("ui_msgbox_mid");
            boxBot = game.textureStore.getTexture("ui_msgbox_bot");
            if (rendersToFixed()) {
                this.maxWidth = (int) ((Gdx.graphics.getWidth()) / 25 * 12.5);
            } else {
                this.maxWidth = (int) ((game.getMapScreen().getXTiles() * game.getMapScreen().getGridSize()) / 25 * 12.5);
            }
        }

        FreeTypeFontParameter parameters = new FreeTypeFontParameter();
        parameters.size = fontHeight;
        parameters.color = new Color(0.25f, 0.25f, 0.25f, 0.85f);
        parameters.shadowColor = new Color(0.55f, 0.55f, 0.55f, 1f);
        parameters.shadowOffsetX = 2;
        parameters.shadowOffsetY = 2;
        this.font = game.fontManager.getFont("regular", parameters);
        useAnim = false;

        this.destructAt = -1;

        this.layout = new GlyphLayout();
    }

    /**
     * Starts an animation.
     * @param msTime        Duration of the animation in ms
     */
    public void setAnim(int msTime) {
        currentIndex = 0;
        currentTime = 0;
        if (message.length() == 0) {
            msPerIndex = msTime;
        }
        else {
            msPerIndex = msTime / message.length();
        }
        useAnim = true;
    }

    /**
     * Self destructs this popup.
     * @param msTime    Time until self destruction
     */
    public void setSelfDestruct(int msTime) {
        destructAt = TimeUtils.millis() + msTime;
    }

    /**
     * Draws a border around the message.
     * @param batch     Batch to draw to
     * @param xLoc      X location of message
     * @param yLoc      Y location of message
     */
    private void drawBorder(SpriteBatch batch, int xLoc, int yLoc) {
        int gridSize = xSize / 50;
        int padding = gridSize * 2;
        yLoc -= msgHeight;

        // Get actual sizes
        int boxWidth = gridSize * 2 * 13, boxHeight = gridSize;

        // Find the smallest number of box lines needed for this
        // Height is gridSize * 16
        int boxLines = (int) Math.ceil((msgHeight + padding) / (float) gridSize);
        if (boxLines < 4) boxLines = 4;

        // Find difference between start of message and start of box
        xLoc -= (boxWidth - msgWidth) / 2;
        yLoc -= (boxHeight * boxLines - msgHeight) / 2;
        int startingY = yLoc;
        
        Texture currentTexture;
        int offset = 1;
        for (int i = 0; i < boxLines; i++) {
            if (i == 0) {
                currentTexture = boxBot;
                offset = 2;
            }
            else if (i == boxLines - 2) {
                currentTexture = boxTop;
                offset = 2;
            }
            else {
                currentTexture = boxMid;
                offset = 1;
            }

            batch.draw(currentTexture, xLoc, startingY, boxWidth, gridSize * offset);

            startingY += gridSize * offset;
            if (offset == 2) i++;
        }

    }

    /**
     * Updates the sizes of both the display and message internally.
     */
    private void updateSizes() {
        // Get an x size and y size for the parent renderer
        if (rendersToFixed()) {
            xSize = Gdx.graphics.getWidth();
            ySize = Gdx.graphics.getHeight();
        } else {
            xSize = game.getMapScreen().getXTiles() * game.getMapScreen().getGridSize();
            ySize = game.getMapScreen().getYTiles() * game.getMapScreen().getGridSize();
        }

        // Update sizes of message
        layout.setText(font, message, Color.BLACK, maxWidth, align, true);
        
        msgWidth = (int) layout.width;
        msgHeight = (int) layout.height;
    }

    /**
     * Renders the message to the batch.
     * @param batch     Batch to render to
     * @param delta     Delta time
     */
    @Override
    public void renderTo(SpriteBatch batch, float delta) {
        // Check self destruction
        if (destructAt != -1) {
            if (TimeUtils.millis() > destructAt) {
                game.getScreen().elementRenderer.removeAfterRender(this);
                return;
            }
        }

        updateSizes();
        int actualX = x, actualY = y;
        // Change coordinates if X or Y is -1.
        if (x < 0) {
            actualX = xSize / 2 - msgWidth / 2;
        }
        if (y < 0) {
            actualY = ySize / 2 + msgHeight / 2;
        }

        if (useBorder) drawBorder(batch, actualX, actualY);

        // Check if animating
        if (!useAnim) {
            font.draw(batch, message, actualX, actualY, maxWidth, align, true);
        } else {
            if (!game.paused) currentTime += (int) (delta * 1000);

            currentIndex = currentTime / msPerIndex;

            if (currentIndex >= message.length()) {
                useAnim = false;
                currentIndex = message.length();
            }

            font.draw(batch, message.substring(0, currentIndex), actualX, actualY, maxWidth, align, true);
        }
    }
}
