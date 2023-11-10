package brigade.killbill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Rectangle;

import brigade.killbill.KillBillGame;
import brigade.killbill.map.Map;
import brigade.killbill.ui.ElementRenderer;

/**
 * Extensible class which represents a basic game screen (as in, actually playing -- not the title screen).
 * @author csenneff
 */
public class GameScreen implements Screen {
    /**
     * Number of tiles in the X axis.
     */
    private int xTiles;

    /**
     * Number of tiles in the Y axis.
     */
    private int yTiles;

    /**
     * Grid size (pixels per tile).
     */
    private int gridSize;

    /**
     * X axis size in pixels.
     */
    private int xSize;

    /**
     * Y axis size in pixels.
     */
    private int ySize;

    /**
     * Screen's name.
     */
    private String name;

    /**
     * Fitted batch. Generated using xTiles and yTiles.
     */
	public SpriteBatch batch;

    /**
     * Fixed batch. Same size as the display -- use it for the HUD.
     */
    public SpriteBatch fixedBatch;

    /**
     * Fitted camera. Renders 'batch'.
     */
    public OrthographicCamera camera;

    /**
     * Fixed camera. Renders 'fixedBatch'.
     */
    public OrthographicCamera fixedCamera;
    
    /**
     * Fitted viewport. Applied to 'camera'.
     */
    public ExtendViewport viewport;

    /**
     * Fixed viewport. Applied to 'fixedCamera'.
     */
    public Viewport fixedViewport;

    /**
     * Parent Game object.
     */
    public KillBillGame game;

    /**
     * Parent Map object.
     */
    public Map map;

    /**
     * Enables or disables the fitted batch.
     */
    public boolean useFittedBatch;

    /**
     * Renders UI elements to the screen.
     */
    public ElementRenderer elementRenderer;

    /**
     * Rectangle representing the current bounds of the fitted batch.
     */
    public Rectangle fittedRectangle;

    /**
     * Constructs a new GameScreen with a fitted batch.
     * @param game          Parent Game object.
     * @param name          A short string (use_snake_case) naming the screen.
     * @param xTiles        Number of tiles in the X axis.
     * @param yTiles        Number of tiles in the Y axis.
     * @param gridSize      Size of each tile.
     */
    public GameScreen(KillBillGame game, String name, int xTiles, int yTiles, int gridSize) {
        this.game = game;
        this.name = name;
        this.xTiles = xTiles;
        this.yTiles = yTiles;
        this.gridSize = gridSize;
        this.xSize = this.xTiles * this.gridSize;
        this.ySize = this.yTiles * this.gridSize;
        this.useFittedBatch = true;
		this.batch = new SpriteBatch();
        this.fixedBatch = new SpriteBatch();
        this.elementRenderer = new ElementRenderer(game);
        this.fittedRectangle = new Rectangle();

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.fixedCamera = new OrthographicCamera();

        viewport = new ExtendViewport(Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 5, Gdx.graphics.getWidth() * 3, Gdx.graphics.getHeight() * 3, camera);
        viewport.setScaling(Scaling.fit);
        fixedViewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), fixedCamera);

        setSize(xTiles, yTiles);
        updateCameraLocations();
        updateCameras();
    }

    /**
     * Constructs a new GameScreen without a fitted batch.
     * @param game      Parent Game object
     * @param name      Name of this screen
     */
    public GameScreen(KillBillGame game, String name) {
        this.game = game;
        this.name = name;
        this.useFittedBatch = false;
        this.fixedBatch = new SpriteBatch();
        this.elementRenderer = new ElementRenderer(game);
        this.fixedCamera = new OrthographicCamera();
        fixedViewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), fixedCamera);

        setSize(xTiles, yTiles);
        updateCameraLocations();
        updateCameras();
    }

    /**
     * Gets the size of the X axis in terms of number of tiles.
     * @return  Size of X axis (# tiles).
     */
    public int getXTiles() {
        return xTiles;
    }

    /**
     * Gets the size of the Y axis in terms of number of tiles.
     * @return  Size of Y axis (# tiles).
     */
    public int getYTiles() {
        return yTiles;
    }

    /**
     * Changes the size of this map's grid.
     * @param xTiles    Tiles in X direction (width)
     * @param yTiles    Tiles in Y direction (height)
     */
    public void setSize(int xTiles, int yTiles) {
        if (useFittedBatch) {
            this.xTiles = xTiles;
            this.yTiles = yTiles;
            this.xSize = this.xTiles * this.gridSize;
            this.ySize = this.yTiles * this.gridSize;
            viewport.setScaling(Scaling.fit);
            viewport.setWorldSize(xSize, ySize);
            viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        }
        updateCameraLocations();
        updateCameras();
    }

    /**
     * Gets the grid size.
     * @return  Grid size (in pixels per tile).
     */
    public int getGridSize() {
        return gridSize;
    }

    /**
     * Gets the name of this screen.
     * @return  Screen name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the camera after its position/other attributes have changed.
     */
    public void updateCameras() {
        if (useFittedBatch) {
            camera.update();
            batch.setProjectionMatrix(camera.combined);
        }

        fixedCamera.update();
        fixedBatch.setProjectionMatrix(fixedCamera.combined);
    }

    /**
     * Redefines the positions of each of the cameras.
     */
    public void updateCameraLocations() {
        if (useFittedBatch)
            camera.position.set(game.getPlayer().getX() + game.getPlayer().getXSize() / 2f, game.getPlayer().getY() + game.getPlayer().getYSize() / 2f, 0);
        fixedCamera.position.set(fixedCamera.viewportWidth / 2, fixedCamera.viewportHeight / 2 , 0);
    }

    public void updateRectangle() {
        if (fittedRectangle == null) return;
        this.fittedRectangle.setCenter(camera.position.x, camera.position.y);
        this.fittedRectangle.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    /**
     * Renders the screen.
     * DO NOT OVERRIDE. Use one of:
     *  beforeRender()
     *  renderMain()
     *  renderFixed()
     *  afterRender()
     */
    @Override
    public void render(float delta) {
        try {
            game.presence.core.runCallbacks();
        } catch (Exception e) {}
        updateCameraLocations();
        updateCameras();
        updateRectangle();
        beforeRender(delta);

        game.keyChecker.checkKeys(delta, useFittedBatch);

        // PROJECTION BATCH
        if (useFittedBatch) {
            viewport.apply();
            batch.enableBlending();
            batch.begin();
            renderMain(delta);
            map.doLastRender(delta);
            elementRenderer.render(delta);
            batch.end();
        }

        // FIXED BATCH
        fixedViewport.apply();
        fixedBatch.enableBlending();
        fixedBatch.begin();
        renderFixedFirst(delta);
		game.debug.draw(); 
        game.inventoryRenderer.draw(delta);
        game.healthRenderer.draw(delta);
        game.effectRenderer.draw(delta);
        renderFixed(delta);
        elementRenderer.renderFixed(delta);
        fixedBatch.end();

        // SHAPE RENDERER
        if (useFittedBatch) {
            game.shapeRenderer.setProjectionMatrix(game.getScreen().camera.combined);
            game.shapeRenderer.begin(ShapeType.Line);
            game.shapeRenderer.setColor(Color.RED);
            map.drawDebug(delta);
            game.shapeRenderer.end();

            map.forEach(object -> {
                object.afterRender(delta);
            });
        }

        // AFTER RENDER
        elementRenderer.finish(delta);
		game.debug.frameDone();
        afterRender(delta);
        game.keyChecker.checkKeysLast(delta);
    }

    /**
     * Runs before anything else in the render cycle. Override this.
     * @param delta     Delta time
     */
    public void beforeRender(float delta) {}

    /**
     * Runs while the main (fitted) batch is active. Override this.
     * @param delta     Delta time
     */
    public void renderMain(float delta) {}

    /**
     * Runs while the secondary (fixed) batch is active. Override this.
     * @param delta     Delta time
     */
    public void renderFixed(float delta) {}

    /**
     * Runs at the beginning of the secondary (fixed) batch. Override this.
     * @param delta     Delta time
     */
    public void renderFixedFirst(float delta) {}

    /**
     * Runs after everything else in the render cycle. Override this.
     * @param delta     Delta time
     */
    public void afterRender(float delta) {}

    @Override
    public void show() {}

    /**
     * Handles resizing. 
     * DO NOT OVERRIDE. Use atResize().
     */
    @Override
    public void resize(int width, int height) {
        if (useFittedBatch)
            viewport.update(width, height);
        fixedViewport.update(width, height);
        updateCameraLocations();
        updateCameras();
        updateRectangle();
        atResize(width, height);
    }

    /**
     * Run after a resize event. Override this.
     * @param width     New screen width
     * @param height    New screen height
     */
    public void atResize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}