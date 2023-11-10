package brigade.killbill.objects;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;
import brigade.killbill.map.MapObject;

/**
 * Object which represents an item the player dropped and can pick up.
 * @author csenneff
 */
public class DroppedItem extends MapObject {
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Item this is representing
     */
    private Item item;

    /**
     * Constructs a new DroppedItem.
     * @param game      Parent Game object
     * @param x         X location
     * @param y         Y location
     * @param xSize     Width
     * @param ySize     Height
     * @param item      Item to drop
     */
    public DroppedItem(KillBillGame game, float x, float y, int xSize, int ySize, Item item) {
        super(game, x, y, xSize, ySize, item.getUITexture(), false);
        setSolid(false);
        this.item = item;
        this.game = game;
    }

    /**
     * Handles picking up the item.
     */
    @Override
    public void onInteraction() {
        if (game.player.canInteract(this, KillBillGame.INTERACTION_ANGLES, game.player.getReach())) {
            // Check if inventory full
            int nextIndex = game.player.inventory.getFirstEmptySpace();
            if (nextIndex != -1) {
                // Add to inventory
                item.setUser(game.player);
                game.player.inventory.setItem(nextIndex, this.item);
                game.getMapScreen().map.removeObject(this);
                item.clearDroppedItem();
                game.player.setItem(game.player.inventory.getCurrentItem());
            }
        }
    }
}