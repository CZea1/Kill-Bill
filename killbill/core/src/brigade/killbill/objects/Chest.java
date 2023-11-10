package brigade.killbill.objects;

import com.badlogic.gdx.graphics.Texture;

import brigade.killbill.KillBillGame;
import brigade.killbill.map.MapObject;
import brigade.killbill.map.Tile;
import brigade.killbill.items.Item;
import brigade.killbill.items.Weapon;
import brigade.killbill.loottables.LootTable;

/**
 * Represents a chest which can be opened.
 * @author Brayden
 */
public class Chest extends MapObject {
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Whether or not the chest is opened
     */
    private boolean isOpened;

    /**
     * Texture to change to when opened
     */
    private Texture openedTexture;

    private LootTable lootTable;

    /**
     * Constructs a new Door object.
     * @param game          Parent Game object
     * @param x             X location
     * @param y             Y location
     * @param gridSize      Grid size
     * @param texture       Texture to render with
     * @param openedTexture Texture to use when opened
     */
    public Chest(KillBillGame game, int x, int y, int gridSize, Texture texture, Texture openedTexture) {
        super(game, x, y, gridSize, gridSize, texture, true);
        setSolid(false);
        this.game = game;
        this.isOpened = false;
        this.openedTexture = openedTexture;
        this.lootTable = game.lootTables.getDefaultTable();
    }

    public void setLootTable(LootTable table) {
        this.lootTable = table;
    }

    /**
     * Checks for interactions.
     */
    @Override
    public void onInteraction() {
        // Check if in range
        if (!isOpened && game.player.canInteract(this, KillBillGame.INTERACTION_ANGLES, 2 * KillBillGame.GRID_SIZE)) {
            // Give stuff
            Item item = lootTable.getItem();

            // Check if inventory is full
            if (game.player.inventory.getFirstEmptySpace() == -1) {
                // Drop the item
                item.drop((int) game.player.getX(), (int) game.player.getY());
            } else {
                game.player.inventory.appendItem(item);
                game.player.setItem(game.player.inventory.getCurrentItem());
            }
            isOpened = true;
            setTexture(openedTexture);
        }
    }
}
