package brigade.killbill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.map.Map;
import brigade.killbill.map.Tile;

/**
 * Basic testing playground screen.
 * @author csenneff
 */
public class TestScreen extends GameScreen {
    public TestScreen(KillBillGame game) {
        super(game, "test_screen", 32, 14, 32);
		map = new Map(this.game);

        for (int i = 0; i < getXTiles(); i++) {
            map.addObject(new Tile(this.game, i, 0, 32, game.textureStore.getTexture("house")));
            map.addObject(new Tile(this.game, i, getYTiles() - 1, 32, game.textureStore.getTexture("house")));
        }

        for (int i = 1; i < getYTiles() - 1; i++) {
            map.addObject(new Tile(this.game, 0, i, 32, game.textureStore.getTexture("house")));
            map.addObject(new Tile(this.game, getXTiles() - 1, i, 32, game.textureStore.getTexture("house")));
        }

        map.addObject(new Entity(game, 500, 300, 32, 32, game.textureStore.getTexture("licensed-image"), true, 2, 5));
        map.addObject(new Entity(game, 600, 100, 32, 32, game.textureStore.getTexture("shroud"), true, 3, 5));

        game.keyChecker.registerKeyChecker(game.getPlayer().keyChecker);
        map.addObject(game.getPlayer());
        game.getPlayer().setLocation(100, 100);
    }

    @Override
    public void beforeRender(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        map.doPreRender(delta);
    }

    @Override
    public void renderMain(float delta) {
		map.drawAll(delta);
    }

    @Override
    public void renderFixed(float delta) {}

    @Override
    public void afterRender(float delta) {}
}
