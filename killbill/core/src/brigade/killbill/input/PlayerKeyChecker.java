package brigade.killbill.input;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.entities.enemies.Bill;
import brigade.killbill.items.Item;
import brigade.killbill.map.MapObject;
import brigade.killbill.misc.MiscUtils;
import brigade.killbill.player.EffectType;
import brigade.killbill.player.Player;
import brigade.killbill.screens.MapScreen;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;

/**
 * Registers keypresses related to the player.
 * @author calebzea, csenneff
 */
public class PlayerKeyChecker extends ExtensionKeyChecker {

    /**
     * Stores the num keys for reading inventory position changes.
     */
    public static final int[] NUM_KEYS = {
        Input.Keys.NUM_1,
        Input.Keys.NUM_2,
        Input.Keys.NUM_3,
        Input.Keys.NUM_4,
        Input.Keys.NUM_5,
        Input.Keys.NUM_6,
        Input.Keys.NUM_7,
        Input.Keys.NUM_8,
        Input.Keys.NUM_9,
        Input.Keys.NUM_0
    };

    /**
     * Number of inventory keys to check for.
     * This is probably useless? I dunno, not checking. This equals NUM_KEYS.length.
     */
    public static final int KEY_COUNT = 10;

    /**
     * Constructs a new PlayerKeyChecker.
     * @param game      Parent Game object
     */
    public PlayerKeyChecker(KillBillGame game) {
        super(game);
        enableTimeKeeper(false);
    }

    /**
     * Checks all key presses.
     * @param delta     Delta time
     */
    @Override
    public void checkKeys(float delta) {
        doMovement(delta);
        switchItems(delta);
        checkUse(delta);
    }

    /**
     * Checks for item usage.
     * @param delta     Delta time
     */
    public void checkUse(float delta) {
        if (game.paused) return;

        // Health increment (temporary)
        if (Gdx.input.isKeyJustPressed(Keys.F5)) {
            if (game.player.getHealth() < 3) {
                game.player.setHealth(game.player.getHealth() + 3);
            }
        }
        if (Gdx.input.isKeyJustPressed(Keys.F6)) {
            if (game.getScreen() instanceof MapScreen) {
                game.getMapScreen().setMap("billroom");
            }
        }
        if (Gdx.input.isKeyJustPressed(Keys.F4)) {
            game.player.setEffect(EffectType.INVINCIBLE, 100000000);
        }

        if (Gdx.input.isKeyJustPressed(Keys.F7)) {
            ArrayList<Entity> toDmg = new ArrayList<Entity>();
            game.getMapScreen().map.forEach(obj -> {
                if (obj instanceof Entity && !(obj instanceof Bill)) {
                    toDmg.add((Entity) obj);
                }

                if (obj instanceof Bill) {
                    Bill bill = (Bill) obj;
                    bill.doDamage(bill.getHealth() / 2);
                }
            });

            for (Entity ent: toDmg) {
                ent.doDamage(ent.getHealth());
            }
        }

        // Interaction
        if (Gdx.input.isKeyJustPressed(Keys.E)) {
            // Call onInteraction checks
            game.getMapScreen().map.forEach((object) -> { object.onInteraction(); });
        }
        // Attack
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            // Get currently held item
            Item heldItem = game.player.getItem();

            if (heldItem == null) return;

            heldItem.use();
        }
        // Drop
        if (Gdx.input.isKeyJustPressed(Keys.Q)) {
            // Get currently held item
            Item heldItem = game.player.getItem();

            if (heldItem == null) return;

            heldItem.drop((int) game.player.getX(), (int) game.player.getY());
            game.player.deleteItem(game.player.inventory.getIndex());
        }
        // Skip voice line
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            if (game.getScreen() instanceof MapScreen) {
                game.getScreen().map.forEach(obj -> {
                    if (obj instanceof Entity) {
                        Entity ent = (Entity) obj;
                        ent.skipVoiceLine();
                    }
                });
            }
        }
    }

    /**
     * Checks for player movement.
     * @param delta     Delta time
     */
    public void doMovement(float delta) {
        if (game.paused) return;
       
        Player player = game.getPlayer();
        if (!player.canControl()) return;

        float baseSpeed = delta / (1f / 60f);
        int speedModifier = 1;
		
        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
            player.setRunning(true);
            speedModifier = 2;
        } else {
            player.setRunning(false);
        }

        if (player.hasEffect(EffectType.SPEED)) {
            speedModifier *= 2;
            player.setRunning(true);
        }

        float translateX = 0;
        float translateY = 0;

        ArrayList<Integer> rotations = new ArrayList<Integer>();
    
        if (Gdx.input.isKeyPressed(Keys.W)) {
            translateY = speedModifier;
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
            translateX = -1 * speedModifier;
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
            if (translateY != 0) translateY = 0;
            else translateY = -1 * speedModifier;
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
            if (translateX != 0) translateX = 0;
            else translateX = 1 * speedModifier;
		}
        
        if (translateX != 0 && translateY != 0) {
            translateX = (float) Math.sqrt(2) / 2 * translateX;
            translateY = (float) Math.sqrt(2) / 2 * translateY;
        }

        if (translateX == 0 && translateY == 0) {
            player.setWalking(false);
            return;
        }
        player.setWalking(true);
        player.setCurrentSpeed(speedModifier);

        translateX *= baseSpeed;
        translateY *= baseSpeed;

        if (translateY > 0) rotations.add(0);
        else if (translateY < 0) rotations.add(180);

        if (translateX > 0) rotations.add(270);
        else if (translateX < 0) rotations.add(90);

        if (rotations.size() != 0) {
            int total = 0;
            for (int i = 0; i < rotations.size(); i++) {
                if (i > 1) break;

                int rotation = rotations.get(i);

                if (total == 0 && rotation == 270 && i != 0) total = 360;
                total += rotation;
            }
            int avg = total / rotations.size();
            
            player.setRotation(avg);
        }
        
        float oldX, oldY;
        oldX = player.getX();
        oldY = player.getY();
        ArrayList<MapObject> collisions = new ArrayList<MapObject>();
        // No collisions should be happening before a move.
        MapObject collidedWith = game.getMapScreen().map.getAnyCollision(player);
        if (collidedWith == null) {
            // Call collision checks

            if (!MiscUtils.areFloatsEqual(translateX, 0f)) {
                player.setLocation(oldX + translateX, oldY);
                collidedWith = game.getMapScreen().map.getAnyCollision(player);
                if (collidedWith != null) {
                    if (!collisions.contains(collidedWith)) collisions.add(collidedWith);
                    player.setLocation(oldX, oldY);
                    player.translateUntilCollision(collidedWith, translateX, 0);
                }

                oldX = player.getX();
            }

            if (!MiscUtils.areFloatsEqual(translateY, 0f)) {
                player.setLocation(oldX, oldY + translateY);
                collidedWith = game.getMapScreen().map.getAnyCollision(player);
                if (collidedWith != null) {
                    if (!collisions.contains(collidedWith)) collisions.add(collidedWith);
                    player.setLocation(oldX, oldY);
                    player.translateUntilCollision(collidedWith, 0, translateY);
                }
            }
        } else {
            // Let them keep moving otherwise -- prevents them getting trapped.
            player.setLocation(oldX + translateX, oldY + translateY);
        }

        for (int i = 0; i < collisions.size(); i++) {
            collisions.get(i).onCollision(player);
        }
    }

    /**
     * Checks for item switching.
     * @param delta     Delta time
     */
    public void switchItems(float delta) {
        int index = -1;
        for (int i = 0; i < KEY_COUNT; i++) {
            if (Gdx.input.isKeyJustPressed(NUM_KEYS[i])) index = i;
        }

        if (index != -1) {
            if (game.getPlayer().inventory.getSize() <= index) return;

            game.getPlayer().inventory.setIndex(index);
            game.getPlayer().setItem(game.getPlayer().inventory.getCurrentItem());
        }
    }
}
