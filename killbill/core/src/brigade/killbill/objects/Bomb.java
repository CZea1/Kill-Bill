package brigade.killbill.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.entities.EntityAttributes;
import brigade.killbill.map.MapObject;
import brigade.killbill.misc.lambda.TaskFunction;
import brigade.killbill.player.Player;

/**
 * Go boom :)
 * @author csenneff
 */
public class Bomb extends MapObject {
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Texture of bomb
     */
    private Texture texture;

    /**
     * Alternate texture (when flashing)
     */
    private Texture altTexture;

    /**
     * Explosion texture to draw on boom
     */
    private Texture explosionTexture;

    /**
     * Duration of bomb delay
     */
    private int duration;

    /**
     * Range of bomb (in tiles)
     */
    private int range;

    /**
     * Damage bomb does
     */
    private int damage;

    /**
     * Current time since ignition
     */
    private int timer;

    /**
     * Time since last flash
     */
    private int flashTimer;

    /**
     * Whether or not the object is on the altTexture (flashing)
     */
    private boolean isFlashing;

    /**
     * Maximum explosion size in pixels
     */
    private int maxSize;

    /**
     * Whether or not the bomb has exploded
     */
    private boolean blownUp;

    /**
     * Whether or not the bomb has been ignited
     */
    private boolean started;

    /**
     * Whether or not the bomb has done damage to things
     */
    private boolean damaged;

    /**
     * Called on explosion.
     */
    private TaskFunction explosionFunc;

    public ArrayList<MapObject> immune;


    /**
     * Constructs a new Bomb.
     * @param game          Parent Game object
     * @param x             X location
     * @param y             Y location
     * @param xSize         Width
     * @param ySize         Height
     * @param texture       Texture
     * @param altTexture    Texture when flashing
     * @param duration      Duration of timer
     * @param range         Range (in tiles)
     * @param damage        Damage dealt
     */
    public Bomb(KillBillGame game, float x, float y, int xSize, int ySize, Texture texture, Texture altTexture, int duration, int range, int damage) {
        super(game, x, y, xSize, ySize, texture, false);
        setSolid(false);

        this.game = game;
        this.texture = texture;
        this.altTexture = altTexture;
        this.duration = duration;
        this.range = range;
        this.damage = damage;

        this.explosionTexture = game.textureStore.getTexture("items_explosion");

        blownUp = false;
        started = false;
        damaged = false;
        this.immune = new ArrayList<MapObject>();
    }

    /**
     * Makes an object immune to impact with this projectile.
     * @param obj
     */
    public void addImmune(MapObject obj) {
        immune.add(obj);
    }

    /**
     * Sets off the bomb.
     */
    public void start() {
        timer = duration;
        flashTimer = 0;
        isFlashing = false;
        maxSize = (range) * KillBillGame.GRID_SIZE;
        started = true;
    }

    /**
     * Runs an action on explosion.
     * @param func  Function to run
     */
    public void onExplosion(TaskFunction func) {
        explosionFunc = func;
    }

    /**
     * Handles flashing
     */
    @Override
    public void preRender(float delta) {
        if (!started) return;

        int ms = (int) (delta * 1000);
        timer -= ms;

        if (blownUp) {
            game.soundStore.pickOne("main_explosion").play(game.getVolume());
            if (timer <= 0) {
                game.getMapScreen().map.removeObject(this);
                if (explosionFunc != null) {
                    explosionFunc.run();
                }
            }

            return;
        }
        flashTimer -= ms;

        // Set texture depending on time (or blow up)
          
        if (flashTimer <= 0) {
            // Find next flash time
            isFlashing = !isFlashing;
            flashTimer = (int) (250 * ((float) timer / duration));
            if (flashTimer < 100 && isFlashing) flashTimer = 100;
            else if (flashTimer < 25) flashTimer = 25;

            setTexture(isFlashing ? altTexture : texture);
        }

        if (timer <= 0) {
            setVisible(false);
            blownUp = true;
            timer = 800;

        }
    }

    /**
     * Handles flashing and explosion
     */
    @Override
    public void onRender(float delta) {
        // Hijack to draw in explosion
        if (blownUp) {
            // Calculate size
            float scale = timer / 400f;
            if (scale >= 1f) scale = 2 - scale;

            int size = (int) (maxSize * scale);

            if (!damaged && timer < 450) {
                damaged = true;
                // Iterate over items
                game.getMapScreen().map.forEach(object -> {
                    if (object instanceof Entity || object instanceof Player) {
                        if (immune.contains(object)) return;

                        // Get distance from this
                        double distance = Math.sqrt(Math.pow(getXCenter() - object.getXCenter(), 2) + Math.pow(getYCenter() - object.getYCenter(), 2));

                        // Is it within range?
                        if (distance <= (range * KillBillGame.GRID_SIZE) / 2) {
                            if (object instanceof Entity) {
                                Entity ent = (Entity) object;
                                if (ent.hasAttr(EntityAttributes.INVINCIBLE)) return;
                                ent.doDamage(damage);
                            } else if (object instanceof Player) {
                                Player player = (Player) object;
                                player.doDamage(1);
                            }
                        }
                    }
                });
            }

            game.getScreen().batch.draw(explosionTexture, getX() - size / 2, getY() - size / 2, size, size);
        }
    }
}
