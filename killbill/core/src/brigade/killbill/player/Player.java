package brigade.killbill.player;

import java.util.ArrayList;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.input.PlayerKeyChecker;
import brigade.killbill.map.MapObject;
import brigade.killbill.misc.PerspectiveRenderer;
import brigade.killbill.items.Item;
import brigade.killbill.items.Weapon;
import brigade.killbill.items.weapons.PenguinLauncher;

/**
 * MapObject representing the player. Access via game.player.
 * @author csenneff, calebzea
 */
public class Player extends MapObject {
    public static final int HIT_TIME = 2000;

    public static final float FAST_STEP_TIME = 0.450f;
    public static final float SLOW_STEP_TIME = 0.310f;

    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Handles player keypresses (movement, weapons, interactions, etc)
     */
    public PlayerKeyChecker keyChecker;

    /**
     * Player's inventory.
     */
    public Inventory inventory;

    /**
     * Player's HP
     */
    private int health;

    /**
     * Cooldown timer between hits
     */
    private int hitTimer;
    
    /**
     * Whether or not the player has been hit recently
     */
    private boolean isHit;

    /**
     * He longs for the sweet release that death brings
     */
    private boolean wantToDie;

    private PerspectiveRenderer perspectiveRenderer;

    private boolean walking;
    private boolean running;

    private int currentSpeed;

    public static class Effect {
        public EffectType type;
        public int msRemaining;
        public int totalDuration;
    }

    private ArrayList<Effect> effects;

    private int shockwaveTimer;
    private int shockwaveSpeed;
    private double shockwaveDirection;

    private float soundTimer;

    private Sound fastStepSound;
    private Sound slowStepSound;

    /**
     * Constructs a new Player.
     * @param game      Parent Game object
     * @param x         Starting X location (pixels, scaled)
     * @param y         Starting Y location (pixels, scaled)
     * @param xSize     Width of the player
     * @param ySize     Height of the player
     * @param texture   Texture to render with
     */
    public Player(KillBillGame game, int x, int y, int xSize, int ySize, Texture texture) {
        super(game, x, y, xSize, ySize, texture, true);
        setReach(2 * KillBillGame.GRID_SIZE);
        setSolid(false);

        health = 3;
        this.hitTimer = 0;
        this.isHit = false;
        wantToDie = false;

        this.game = game;
        this.keyChecker = new PlayerKeyChecker(game);
        this.inventory = new Inventory(game, 8);
        this.walking = false;
        this.running = false;
        this.currentSpeed = 1;

        this.perspectiveRenderer = new PerspectiveRenderer(
            game,
            game.textureStore.getTexture("player_melinda_top"),
            game.textureStore.getTexture("player_melinda_top_hold"),
            game.textureStore.getTexture("player_melinda_top_walk1"),
            game.textureStore.getTexture("player_melinda_top_walk2"),
            game.textureStore.getTexture("player_melinda_top_walk1_hold"),
            game.textureStore.getTexture("player_melinda_top_walk2_hold"),
            currentSpeed,
            this
        );

        fastStepSound = game.soundStore.getSound("main_stepFast");
        slowStepSound = game.soundStore.getSound("main_stepSlow");

        //inventory.appendItem(new PenguinLauncher(game, this, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE));
        //setItem(inventory.getCurrentItem());

        shockwaveTimer = -1;

        effects = new ArrayList<Effect>();

        soundTimer = -1;
    }

    public boolean isHit() {
        return isHit;
    }

    public ArrayList<Effect> getEffects() {
        return effects;
    }

    public boolean hasEffect(EffectType type) {
        for (Effect eff: effects) {
            if (eff.type == type) return true;
        }
        return false;
    }


    public void setEffect(EffectType type, int msDuration) {
        for (Effect eff: effects) {
            if (eff.type == type) {
                if (eff.msRemaining < msDuration) {
                    eff.msRemaining = msDuration;
                    eff.totalDuration = msDuration;
                    return;
                }
            }
        }

        Effect eff = new Effect();
        eff.msRemaining = msDuration;
        eff.totalDuration = msDuration;
        eff.type = type;
        effects.add(eff);

        if (type == EffectType.INVINCIBLE) {
            setColor(1, 1, 1, 1);
        }
    }

    public void shockwave(int msDuration, int speedPerSecond, int direction) {
        shockwaveTimer = msDuration;
        shockwaveSpeed = speedPerSecond;
        shockwaveDirection = Math.toRadians(direction);
    }

    public boolean canControl() {
        return shockwaveTimer < 0;
    }

    public void setWalking(boolean state) {
        this.walking = state;
    }

    public void setRunning(boolean state) {
        this.running = state;
    }

    public void setCurrentSpeed(int speed) {
        this.currentSpeed = speed;
        this.perspectiveRenderer.setSpeed(speed);
    }

    /**
     * Changes the player's currently held item.
     * @param item      Item to switch to
     */
    @Override
    public void setItem(Item item) {
        if (super.getItem() == item) return;

        super.setItem(item);
        if (item instanceof Weapon) {
            ((Weapon) item).startCooldown();
        }
        if (game.inventoryRenderer != null) game.inventoryRenderer.itemSwitched();
    }

    /**
     * Syncs the inventory's held item to the player.
     */
    public void updateItem() {
        Item item = inventory.getCurrentItem();
        
        super.setItem(item);
        if (item instanceof Weapon) {
            ((Weapon) item).startCooldown();
        }
    }

    /**
     * Does stuff before rendering.
     * @param delta     Delta time
     */
    @Override
    public void preRender(float delta) {
        game.debug.setValue("playerLoc", String.format("Player: %.1f, %.1f (%.1f, %.1f)", getX() / KillBillGame.GRID_SIZE, getY() / KillBillGame.GRID_SIZE, getX(), getY()));

        int msDelta = (int) (delta * 1000);

        // Check effects
        ArrayList<Effect> toRemove = new ArrayList<Effect>();
        for (Effect eff: effects) {
            eff.msRemaining -= msDelta;

            if (eff.msRemaining <= 0) {
                toRemove.add(eff);
            }
        }

        for (Effect eff: toRemove) {
            effects.remove(eff);
        }

        // Change texture back if done
        if (isHit) {
            hitTimer -= msDelta;

            if (hitTimer <= 0) {
                if (!hasEffect(EffectType.INVINCIBLE)) setColor(1, 1, 1, 1);
                isHit = false;
            }
        }

        if (shockwaveTimer > 0) {
            shockwaveTimer -= msDelta;
            
            float yDelta = (shockwaveSpeed * (float) Math.sin(shockwaveDirection)) * delta;
            float xDelta = (shockwaveSpeed * (float) Math.cos(shockwaveDirection)) * delta;
            translate(xDelta, yDelta);
        }

        perspectiveRenderer.changeTexture(delta, walking, getItem() != null);

        // Sounds
        soundTimer -= delta;
        if (soundTimer < 0 && walking) {
            if (running) {
                fastStepSound.play(game.getVolume() / 2);
                soundTimer = FAST_STEP_TIME;
            } else {
                slowStepSound.play(game.getVolume() / 2);
                soundTimer = SLOW_STEP_TIME;
            }
        }
    }

    /**
     * Checks for collision stuff.
     * @param collidedWith      Object hit
     */
    @Override
    public void onCollision(MapObject collidedWith) {
        //we collided
        if (collidedWith instanceof Entity) {
            doDamage(1);
        }

        if (health <= 0) {
            game.soundStore.getSound("main_death").play(game.getVolume());
            wantToDie = true;
        }
    }

    @Override
    public void afterRender(float delta) {
        if (wantToDie){
            game.death.die();
        } 
    }

    /**
     * Gets the player's health.
     * @return      Player health
     */
    public int getHealth() {
        return health;
    }

    /**
     * Sets the player's health. 
     * IF YOU'RE DOING DAMAGE TO THE PLAYER, USE <code>player.doDamage(amount)</code>
     * @param health    Health to change to
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Does damage to the player, makes them invincible for a bit, and turns them red
     * @param damage    Health to remove (positive)
     */
    public void doDamage(int damage) {
        if (isHit) return; // Do nothing if hit recently
        if (hasEffect(EffectType.INVINCIBLE)) return; // Invincibility

        game.soundStore.pickOne("main_damage").play(game.getVolume());

        this.health -= damage;

        // Make enemy turn red
        this.hitTimer = HIT_TIME;
        this.isHit = true;
        setColor(0.7f, 0.2f, 0.2f, 1f);

        if (health <= 0) wantToDie = true;
    }

    /**
     * Makes the player invincible for a bit.
     * @param timer     Time to be invincible for
     */
    public void setInvincible(int timer) {
        hitTimer = timer;
        isHit = true;
    }

    /**
     * Deletes an item from the player's inventory
     * @param index     Index of item to remove
     */
    public void deleteItem(int index) {
        inventory.removeItem(index);
        setItem(inventory.getCurrentItem());
    }
}