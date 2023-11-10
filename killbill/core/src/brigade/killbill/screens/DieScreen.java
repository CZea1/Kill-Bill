package brigade.killbill.screens;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import brigade.killbill.KillBillGame;
import brigade.killbill.ui.elements.Button;
import brigade.killbill.ui.elements.Image;

/**
 * Death screen.
 * @author czea
 */
public class DieScreen extends GameScreen {
    /**
     * Parent Game object.
     */
    public KillBillGame game;

    private Sprite gameUnderlay;

    private float timer;

    private final float FADE_TIME = 1f;

    /**
     * Constructs a new GameScreen.
     * @param game          Parent Game object.
     * @param name          A short string (use_snake_case) naming the screen.
     */
    public DieScreen(KillBillGame game, String name) {
        super(game, name);
        this.game = game;

        // Add stuff to the element renderer
        int gridSize = Gdx.graphics.getWidth() / 16;
        int width = gridSize * 4; // 64 x 16
        int height = gridSize;

        int packageHeight = height + gridSize + gridSize;

        int x = Gdx.graphics.getWidth() / 2 - width / 2;
        int y = Gdx.graphics.getHeight() / 2 + packageHeight / 2;

        int logoWidth = gridSize * 8;
        int logoHeight = gridSize * 2;

        /*elementRenderer.addElement(
            new Image(game, Gdx.graphics.getWidth() / 2 - Gdx.graphics.getHeight() / 2, 0, Gdx.graphics.getHeight(), Gdx.graphics.getHeight(), game.textureStore.getTexture("ui_you_loosed"))
        );*/

        elementRenderer.addElement(
            new Image(
                game, Gdx.graphics.getWidth() / 2 - logoWidth / 2, y, logoWidth, logoHeight, 
                game.textureStore.getTexture("ui_dead") 
            )
        );

        y -= gridSize + height;

        elementRenderer.addElement(
            new Button(
                game, x, y, width, height, 
                game.textureStore.getTexture("ui_restart_button"), 
                game.textureStore.getTexture("ui_restart_button_pressed"), 
                () -> {
                    // Runs when the button is pressed
                    game.debug.setRender(true);
                    game.setScreen(game.getMapScreen());
                }
            )
        );
        
        y -= gridSize + height / 2;

        elementRenderer.addElement(
            new Button(
                game, x, y, width, height, 
                game.textureStore.getTexture("ui_menu_button"), 
                game.textureStore.getTexture("ui_menu_button_pressed"), 
                () -> {
                    game.setScreen(game.menuScreen);
                }
            )
        );
    }

    public void die() {
        game.reset();

        game.debug.setRender(false);
        
        // Screnshot the current screen
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        ByteBuffer pixels = pixmap.getPixels();

        // This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
        int size = Gdx.graphics.getBackBufferWidth() * Gdx.graphics.getBackBufferHeight() * 4;
        for (int i = 3; i < size; i += 4) {
            pixels.put(i, (byte) 180);
        }

        // Store this
        this.gameUnderlay = new Sprite(new Texture(pixmap), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameUnderlay.setPosition(0, 0);
        gameUnderlay.setAlpha(0.3f);
        gameUnderlay.flip(false, true);
        gameUnderlay.setColor(1f, 1f, 1f, 1f);
        game.setScreen(this);

        timer = 0;

    }

    /**
     * Runs before anything else in the render cycle.
     * @param delta     Delta time
     */
    @Override
    public void beforeRender(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Runs while the secondary (fixed) batch is active.
     * @param delta     Delta time
     */
    @Override
    public void renderFixed(float delta) {
        if (gameUnderlay != null) {
            if (timer <= FADE_TIME) {
                timer += delta;

                float colorFactor = 1f - timer / FADE_TIME;
                if (colorFactor < 0) {
                    colorFactor = 0;
                    timer = FADE_TIME + 1;
                }

                gameUnderlay.setColor(1f, colorFactor, colorFactor, 1f);
            }
            gameUnderlay.draw(fixedBatch);
        }
    }

    /**
     * Runs after everything else in the render cycle.
     * @param delta     Delta time
     */
    @Override
    public void afterRender(float delta) {}
}