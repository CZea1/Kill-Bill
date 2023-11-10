package brigade.killbill.entities.enemies;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.entities.EntityAttributes;
import brigade.killbill.misc.PerspectiveRenderer;
import brigade.killbill.objects.Bomb;

/**
 * Blows up.
 */
public class SecurityRobot extends Entity {
    private final int RANGE = 2 * KillBillGame.GRID_SIZE;

    private PerspectiveRenderer perspectiveRenderer;

    public SecurityRobot(KillBillGame game, int x, int y, int xSize, int ySize) {
        super(game, x, y, xSize, ySize, game.textureStore.getTexture("enemies_securityrobot"), true, 1, 5);
        setReach(2 * KillBillGame.GRID_SIZE);

        // Create a perspective renderer
        this.perspectiveRenderer = new PerspectiveRenderer(
            game, 
            game.textureStore.getTexture("securityrobot_stand"), 
            game.textureStore.getTexture("default"), 
            game.textureStore.getTexture("securityrobot_walk1"), 
            game.textureStore.getTexture("securityrobot_walk2"), 
            game.textureStore.getTexture("default"), 
            game.textureStore.getTexture("default"), 
            getSpeed(), 
            this
        );

        setSound("main_robot");
    }

    @Override
    public void preRender(float delta) {
        perspectiveRenderer.changeTexture(delta, isWalking(), getItem() != null);

        if (hasAttr(EntityAttributes.MOVE_TOWARDS_PLAYER)) {
            // Check if we're near the player. If so, unset the moveTowardsPlayer attr and blow up
            double distance = Math.sqrt(Math.pow(getX() - game.player.getX(), 2) + Math.pow(getY() - game.player.getY(), 2));
    
            if (distance < RANGE) {
                // Summon a bomb version of robot
                Bomb bomb = new Bomb(game, getX(), getY(), getXSize(), getYSize(), game.textureStore.getTexture("securityrobot_explode1"), game.textureStore.getTexture("securityrobot_explode2"), 1000, 5, 5);
                bomb.setCollidable(true);
                bomb.onExplosion(() -> {
                    onDeath();
                });
                bomb.start();
                bomb.setRotation(getRotation());
                game.getMapScreen().map.addObject(bomb);
                game.getMapScreen().map.removeObject(this);
                return;
            }
        }

        super.preRender(delta);
    }
}