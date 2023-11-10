package brigade.killbill.screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import brigade.killbill.KillBillGame;
import brigade.killbill.ui.elements.Button;
import brigade.killbill.ui.elements.Image;

/**
 * Main menu screen.
 * @author Brayden, csenneff
 */
public class MenuScreen extends GameScreen {
    /**
     * Parent Game object.
     */
    public KillBillGame game;

    /**
     * Constructs a new GameScreen.
     * @param game          Parent Game object.
     * @param name          A short string (use_snake_case) naming the screen.
     */
    public MenuScreen(KillBillGame game, String name) {
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

        Random rand = new Random();
        int imgId = rand.nextInt(5) + 2;

        String img = "ui_title_bg" + imgId;
        if (rand.nextInt(100) == 0) {
            img = "ui_title_bg_crazyfrog";
        }
        else if (rand.nextInt(100) == 0) {
            img = "ui_title_bghouse";
        }
        else if (rand.nextInt(100) == 0) {
            img = "ui_title_bghekimoglu";
        }

        elementRenderer.addElement(
            new Image(
                game, 0, 0, Gdx.graphics.getHeight(), Gdx.graphics.getHeight(),
                game.textureStore.getTexture(img)
            )
        );

        elementRenderer.addElement(
            new Image(
                game, Gdx.graphics.getWidth() / 2 - logoWidth / 2, y, logoWidth, logoHeight, 
                game.textureStore.getTexture("ui_logo") 
            )
        );

        y -= gridSize + height;

        elementRenderer.addElement(
            new Button(
                game, x, y, width, height, 
                game.textureStore.getTexture("ui_start_button"), 
                game.textureStore.getTexture("ui_start_button_pressed"), 
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
                game.textureStore.getTexture("ui_quit_button"), 
                game.textureStore.getTexture("ui_quit_button_pressed"), 
                () -> {
                    Gdx.app.exit();
                    System.exit(69);
                }
            )
        );
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
    }

    /**
     * Runs after everything else in the render cycle.
     * @param delta     Delta time
     */
    @Override
    public void afterRender(float delta) {}
}