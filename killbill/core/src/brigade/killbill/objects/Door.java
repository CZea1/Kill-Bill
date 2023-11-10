package brigade.killbill.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.other.Key;
import brigade.killbill.map.MapObject;
import brigade.killbill.map.Tile;
import brigade.killbill.player.Player;
import brigade.killbill.ui.elements.DialogPopup;

/**
 * Transports the player between rooms.
 * @author csenneff
 */
public class Door extends Tile {
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Destination location
     */
    private String destination;

    private Texture lockedTexture;
    private Texture unlockedTexture;

    private boolean isLocked;
    private boolean requiresKey;

    private long lastNotified;

    private int xSpawn;
    private int ySpawn;

    /*
     * Lambda functions suck in this stupid language
     */
    private boolean doStuff;

    /**
     * Constructs a new Door object.
     * @param game          Parent Game object
     * @param gridX         X location (in tiles)
     * @param gridY         Y location (in tiles)
     * @param texture       Texture to render with
     * @param destination   Destination map name
     */
    public Door(KillBillGame game, int gridX, int gridY, int gridSize, Texture texture, Texture unlockedTexture, String destination, boolean requiresKey) {
        super(game, gridX, gridY, gridSize, texture);

        this.game = game;
        this.lockedTexture = texture;
        this.unlockedTexture = unlockedTexture;
        this.destination = destination;
        this.requiresKey = requiresKey;
        isLocked = requiresKey;

        lastNotified = -1;
        xSpawn = -1;
    }

    /**
     * Checks for collisions with the player.
     * @param collidedWith      Object collided with
     */
    @Override
    public void onInteraction() {
        // Check if player is nearby
        if (isLocked && requiresKey && game.player.canInteract(this, KillBillGame.INTERACTION_ANGLES, 2 * KillBillGame.GRID_SIZE)) {
            // Check if the player is holding the key
            if (game.player.inventory.getCurrentItem() instanceof Key) {
                Key currentKey = (Key) game.player.inventory.getCurrentItem();
                if(currentKey.getTo().equals("all")){
                    game.getScreen().map.forEach(object -> {
                        if (object instanceof Door) {
                            Door door = (Door) object;
                            if(door.isLocked){
                                door.unlock();
                            }
                        }
                    });

                    game.soundStore.pickOne("main_door").play(game.getVolume());
                }
                else if (currentKey.getTo().equals(getName())) {
                    // Remove key
                    game.player.inventory.removeItem(currentKey);
                    game.player.setItem(game.player.inventory.getCurrentItem());

                    game.getScreen().map.forEach(object -> {
                        if (object instanceof Door) {
                            Door door = (Door) object;

                            if (currentKey.getTo().equals(door.getName())) {
                                door.unlock();
                            }
                        }
                    });
                    
                    game.soundStore.pickOne("main_door").play(game.getVolume());
                }
                else if (lastNotified < TimeUtils.millis() - 10000) {
                    lastNotified = TimeUtils.millis();
                    DialogPopup dp = new DialogPopup(game, true, -1, Gdx.graphics.getHeight() / 4, 48, Gdx.graphics.getWidth() / 2, false, true, "That keycard doesn't work on this door!");
                    dp.setAnim(1000);
                    dp.setSelfDestruct(5000);
                    game.getScreen().elementRenderer.addElement(dp);
                }
            }
        }
    }

    /**
     * Changes where the player spawns when they go through the door.
     * @param xSpawn
     * @param ySpawn
     */
    public void setPlayerSpawn(int xSpawn, int ySpawn) {
        this.xSpawn = xSpawn;
        this.ySpawn = ySpawn;
    }

    /**
     * Unlocks the door.
     */
    public void unlock() {
        isLocked = false;
        setTexture(unlockedTexture);
    }

    /**
     * Sets locked state.
     */
    public void setLocked(boolean state) {
        isLocked = state;
        setTexture(state ? lockedTexture : unlockedTexture);
    }

    /**
     * Checks for collisions with the player.
     * @param collidedWith      Object collided with
     */
    @Override
    public void onCollision(MapObject collidedWith) {
        if (isLocked && requiresKey) {
            if (lastNotified < TimeUtils.millis() - 10000) {
                doStuff = true;
                game.getPlayer().inventory.forEach(item -> {
                    if (item instanceof Key) {
                        lastNotified = TimeUtils.millis();
                        DialogPopup dp = new DialogPopup(game, true, -1, Gdx.graphics.getHeight() / 4, 48, Gdx.graphics.getWidth() / 2, false, true, "Hold your keycard and press E to unlock this door.");
                        dp.setAnim(1000);
                        dp.setSelfDestruct(5000);
                        game.getScreen().elementRenderer.addElement(dp);
                        doStuff = false;
                    }
                });

                if (doStuff) {
                    lastNotified = TimeUtils.millis();
                    DialogPopup dp = new DialogPopup(game, true, -1, Gdx.graphics.getHeight() / 4, 48, Gdx.graphics.getWidth() / 2, false, true, "This door requires a keycard.");
                    dp.setAnim(1000);
                    dp.setSelfDestruct(5000);
                    game.getScreen().elementRenderer.addElement(dp);
                }
            }
            return;
        }

        // Check if the player is colliding
        if (!isLocked && collidedWith instanceof Player) {
            game.getMapScreen().setMap(destination);

            if (xSpawn != -1) game.player.setLocation(xSpawn, ySpawn);
        }
    }
}
