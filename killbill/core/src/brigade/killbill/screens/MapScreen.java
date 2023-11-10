package brigade.killbill.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;

import brigade.killbill.KillBillGame;
import brigade.killbill.map.MapObject;
import brigade.killbill.player.Player;

/**
 * Screen which renders maps to the screen. 
 * @author csenneff
 */
public class MapScreen extends GameScreen {
    private Sprite windowTint;

    private String currentMap;

    /**
     * Constructs a new MapScreen.
     * @param game  Parent Game object
     */
    public MapScreen(KillBillGame game) {
        super(game, "map_screen", 5, 5, 32);
        // NOTE TO ANYONE CHANGING THE DEFAULT MAP:
        // To make it easier to access, default map is now a constant variable in KillBillGame.java.
        // Don't edit the line below this.
        setMap(KillBillGame.DEFAULT_MAP);

        windowTint = new Sprite(game.textureStore.getTexture("ui_color_block"), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        windowTint.setPosition(0, 0);
        windowTint.setColor(1f, 0f, 0f, 0.2f);
    }

    /**
     * Changes to a specified map.
     * @param mapName       Name of map to switch to
     */
    public void setMap(String mapName) {
        map = game.mapStore.getMap(mapName);
        setSize(map.getXSize(), map.getYSize());
        // Make sure player hasn't been added yet
        ArrayList<MapObject> objects = map.getObjects();
        boolean exists = false;
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i) instanceof Player) exists = true;
        }
        if (!exists) map.addObject(game.getPlayer());
        map.movePlayerSpawn();
        this.currentMap = mapName;
        if (map.getActionSet() != null) {
            map.getActionSet().start();
        }
    }

    /**
     * Actions taken before render
     * @param delta     Delta time
     */
    @Override
    public void beforeRender(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.debug.setValue("active_map", String.format("Map: %s", map.getName()));
        map.doPreRender(delta);
        if (map.getActionSet() != null) {
            map.getActionSet().tick(delta);
        }
    }

    /**
     * Actions taken during main batch render
     * @param delta     Delta time
     */
    @Override
    public void renderMain(float delta) {
        map.drawAll(delta);
    }

    /**
     * Actions taken during fixed batch render
     * @param delta     Delta time
     */
    @Override
    public void renderFixedFirst(float delta) {
        if (game.getPlayer().isHit()) {
            windowTint.draw(fixedBatch);
        }
    }

    /**
     * Actions taken during fixed batch render
     * @param delta     Delta time
     */
    @Override
    public void renderFixed(float delta) {
    }

    /**
     * Actions taken after render
     * @param delta     Delta time
     */
    @Override
    public void afterRender(float delta) {
        map.doPostRender(delta);
    }

    public String getMapName() {
        return currentMap;
    }
}
