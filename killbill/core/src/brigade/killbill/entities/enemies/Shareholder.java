package brigade.killbill.entities.enemies;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.entities.EntityAttributes;
import brigade.killbill.items.Item;
import brigade.killbill.misc.PerspectiveRenderer;
import brigade.killbill.objects.Projectile;

/**
 * Throws money at you.
 *
 * It will run until it's a certain distance away from the player, then launch money at them, and repeat.
 */
public class Shareholder extends Entity { 
    public static final int RANGE = 5 * KillBillGame.GRID_SIZE;
    public static final int COOLDOWN_TIME = 3000;
    public static final int SHOOT_TIME = 1000;

    public static final int DAMAGE = 3;
    public static final int SPEED = 4 * KillBillGame.GRID_SIZE;
    public static final int SPLASH = 2;

    private Texture moneyTexture;
    private int cooldown;
    private boolean moving;
    private Item money;
    private PerspectiveRenderer perspectiveRenderer;

    public Shareholder(KillBillGame game, int x, int y, int xSize, int ySize) {
        super(game, x, y, xSize, ySize, game.textureStore.getTexture("enemies_shareholder"), true, 1, 5);

        this.moneyTexture = game.textureStore.getTexture("projectiles_money");
        this.cooldown = -1;

        this.moving = true;
        setAttr(EntityAttributes.MOVE_TOWARDS_PLAYER);

        money = new Item(game, this, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE, moneyTexture, moneyTexture);

        Random rand = new Random();
        int pType = rand.nextInt(7) + 1;

        // Create a perspective renderer
        setTextureSet(pType);

        setSound("main_shareholder");
    }

    public void setTextureSet(int pType) {
        this.perspectiveRenderer = new PerspectiveRenderer(
            game, 
            game.textureStore.getTexture(String.format("shareholder_%d_stand", pType)), 
            game.textureStore.getTexture(String.format("shareholder_%d_hold", pType)), 
            game.textureStore.getTexture(String.format("shareholder_%d_walk1", pType)), 
            game.textureStore.getTexture(String.format("shareholder_%d_walk2", pType)), 
            game.textureStore.getTexture(String.format("shareholder_%d_walk1", pType)), 
            game.textureStore.getTexture(String.format("shareholder_%d_walk2", pType)), 
            getSpeed(), 
            this
        );
    }

    // Hijacking the moveTowardsPlayer method to do this.
    // Check the distance. If within range:
        // If on cooldown, do nothing
        // Remove attr moveTowardsPlayer
        // Launch
        // Set cooldown
    // Otherwise:
        // Set attr moveTowardsPlayer
    @Override
    public void preRender(float delta) {
        // Calculate distance between this and player
        double distance = Math.sqrt(Math.pow(getX() - game.player.getX(), 2) + Math.pow(getY() - game.player.getY(), 2));

        if (distance < RANGE) {
            if (moving) {
                moving = false;
                unsetAttr(EntityAttributes.MOVE_TOWARDS_PLAYER);
                cooldown = SHOOT_TIME;
                setItem(money);
                walking = false;
            }

            cooldown -= (int) (delta * 1000);
            lookAtPlayer();
            // Check cooldown
            if (cooldown < 0) {
                setItem(null);
                // Launch a projectile in the direction we're looking
                Rectangle origin = getOrigin();
                Projectile projectile = new Projectile(game, origin.getX(), origin.getY(), KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE, moneyTexture, SPEED, DAMAGE, SPLASH, getRotation());
                projectile.addImmune(this);
                game.getScreen().map.addObject(projectile);
                cooldown = COOLDOWN_TIME;
            }
        } else {
            if (!moving) { // Used instead of hasAttr to reduce calculations per frame
                moving = true;
                setAttr(EntityAttributes.MOVE_TOWARDS_PLAYER);
            }
        }

        perspectiveRenderer.changeTexture(delta, isWalking(), getItem() != null);
        super.preRender(delta);
    }
}
