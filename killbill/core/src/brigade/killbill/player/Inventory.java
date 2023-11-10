package brigade.killbill.player;

import java.util.ArrayList;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;

/**
 * Represents a player/entity's inventory.
 * @author csenneff
 */
public class Inventory {
    public interface ItemFunction {
        void run(Item item);
    }
    

    /**
     * Number of objects to store
     */
    private int size;

    /**
     * Index of the currently held item
     */
    private int index;

    private ArrayList<Item> items;

    /**
     * Initializes a new Inventory.
     * @param game      Parent Game object
     * @param size      Maximum number of items that can be held
     */
    public Inventory(KillBillGame game, int size) {
        this.size = size;
        this.index = 0;
        this.items = new ArrayList<Item>();
        for (int i = 0; i < size; i++) {
            items.add(null);
        }
    }

    /**
     * Gets the size of this inventory.
     * @return      Size of inventory
     */
    public int getSize() {
        return size;
    }

    /**
     * Gets the current index of the inventory.
     * @return      Current index (held item)
     */
    public int getIndex() {
        return index;
    }

    /**
     * Shifts the index over by a certain amount (1, -1, etc). Wraps around if out of bounds.
     * @param by    Amount to shift by.
     */
    public void shiftIndex(int by) {
        index += by;
        if (index >= size) index = 0;
        else if (index < 0) index = size - 1;
    }

    /**
     * Changes index to the specified index. Wraps around if out of bounds.
     * @param to    New index
     */
    public void setIndex(int to) {
        index = to;
        if (index >= size) index = 0;
        else if (index < 0) index = size - 1;
    }

    /**
     * Gets an item at a specific index.
     * @param index     Index of item to grab
     * @return          Item at that index (or null)
     */
    public Item getItem(int index) {
        try {
            return this.items.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Appends an item to the inventory.
     * @param item      Item to add
     * @return          Whether or not the item was actually added (could be full)
     */
    public boolean appendItem(Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == null) {
                this.items.set(i, item);
                return true;
            }
        }
        return false;
    }

    /**
     * Changes the item at an index.
     * @param index     Index to store item to
     * @param item      Item to set
     */
    public void setItem(int index, Item item) {
        this.items.set(index, item);
    }

    /**
     * Removes the item at a specified index from the inventory.
     * @param index     Index to remove
     */
    public void removeItem(int index) {
        this.items.set(index, null);
    }

    /**
     * Removes an item from the inventory.
     * @param item      Item to remove
     */
    public void removeItem(Item item) {
        for (int i = 0; i < size; i++) {
            if (items.get(i) == item) items.set(i, null);
        }
    }

    /**
     * Finds the index of the first empty space.
     * @return      First empty space. -1 if full.
     */
    public int getFirstEmptySpace() {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the currently held item.
     * @return      Item being held, null if not holding one.
     */
    public Item getCurrentItem() {
        try {
            return this.items.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Runs a lambda expression on each object this inventory.
     * Example:
     * {@code
     * inventory.forEach( (item) -> item.drop() )
     * }
     * would drop every item.
     * @param func      Lambda expression to run
     */
    public void forEach(ItemFunction func) {
        for (Item item: items) {
            func.run(item);
        }
    }
}
