package brigade.killbill.entities.enemies;

import java.util.Random;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.entities.EntityAttributes;
import brigade.killbill.misc.PerspectiveRenderer;

/**
 * This is basically a regular Entity.
 */
public class MicrosoftEmployee extends Entity {
    
    private PerspectiveRenderer perspectiveRenderer;

    public MicrosoftEmployee(KillBillGame game, int x, int y, int xSize, int ySize) {
        super(game, x, y, xSize, ySize, game.textureStore.getTexture("enemies_msemployee"), true, 1, 5);
        setReach(2 * KillBillGame.GRID_SIZE);

        Random rand = new Random();
        int pType = rand.nextInt(9) + 1;

        // Create a perspective renderer
        setTextureSet(pType);

        setSound("main_microsoft");
    }

    public void setTextureSet(int pType) {
        this.perspectiveRenderer = new PerspectiveRenderer(
            game, 
            game.textureStore.getTexture(String.format("msemployee_%d_stand", pType)), 
            game.textureStore.getTexture(String.format("msemployee_%d_hold", pType)), 
            game.textureStore.getTexture(String.format("msemployee_%d_walk1", pType)), 
            game.textureStore.getTexture(String.format("msemployee_%d_walk2", pType)), 
            game.textureStore.getTexture(String.format("msemployee_%d_hold_walk1", pType)), 
            game.textureStore.getTexture(String.format("msemployee_%d_hold_walk2", pType)), 
            getSpeed(), 
            this
        );
    }

    @Override
    public void preRender(float delta) {
        perspectiveRenderer.changeTexture(delta, isWalking(), getItem() != null);

        super.preRender(delta);
    }
}
