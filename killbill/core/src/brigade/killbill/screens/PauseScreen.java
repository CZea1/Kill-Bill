package brigade.killbill.screens;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import brigade.killbill.KillBillGame;
import brigade.killbill.ui.elements.Button;
import brigade.killbill.ui.elements.Image;

/**
 * Main menu screen.
 * @author Brayden, csenneff
 */
public class PauseScreen extends GameScreen {
    /**
     * Parent Game object.
     */
    public KillBillGame game;

    public Sprite gameUnderlay;

    /**
     * Constructs a new GameScreen.
     * @param game          Parent Game object.
     * @param name          A short string (use_snake_case) naming the screen.
     */
    public PauseScreen(KillBillGame game, String name) {
        super(game, name);
        this.game = game;

        // Add stuff to the element renderer
        int gridSize = Gdx.graphics.getWidth() / 16;
        int width = gridSize * 4; // 64 x 16
        int height = gridSize;

        int logoWidth = gridSize * 8;
        int logoHeight = gridSize * 2;

        int packageHeight = height + gridSize + gridSize;

        int x = Gdx.graphics.getWidth() / 2 - width / 2;
        int y = Gdx.graphics.getHeight() / 2 + packageHeight / 2;

        elementRenderer.addElement(
            new Image(
                game, Gdx.graphics.getWidth() / 2 - logoWidth / 2, y, logoWidth, logoHeight, 
                game.textureStore.getTexture("ui_pause") 
            )
        );

        y -= gridSize + height;

        elementRenderer.addElement(
            new Button(
                game, x, y, width, height, 
                game.textureStore.getTexture("ui_resume_button"), 
                game.textureStore.getTexture("ui_resume_button_pressed"), 
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

    public void pause() {
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
        game.setScreen(this);
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
        // Render the background image with transparecny
        if (gameUnderlay != null) {
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