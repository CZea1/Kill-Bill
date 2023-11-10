package brigade.killbill.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;
import brigade.killbill.map.MapObject;
import brigade.killbill.objects.DroppedItem;

public class Item {
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * User which has this item
     */
    private MapObject user;

    /**
     * MapObject representing this object, as seen on the map
     */
    private MapObject object;

    /**
     * Texture rendered into the UI representing this item
     */
    private Texture uiTexture;

    /**
     * DroppedItem instance, if applicable
     */
    private DroppedItem droppedItem;

    /**
     * The user-facing name of this item.
     */
    private String name;

    /**
     * Constructs a new Item.
     * @param game          Parent Game object
     * @param user          MapObject that's holding the object
     * @param xSize         Width of object
     * @param ySize         Height of object
     * @param uiTexture     Texture to render into UI
     * @param heldTexture   Texture as held in hand
     */
    public Item(KillBillGame game, MapObject user, int xSize, int ySize, Texture uiTexture, Texture heldTexture) {
        this.game = game;
        this.user = user;
        this.uiTexture = uiTexture;
        this.droppedItem = null;
        this.object = new MapObject(game, 0, 0, xSize, ySize, heldTexture, false);
        this.name = "Item";

    }

    /**
     * If you didn't pass a user in the constructor, set one before doing anything with this item.
     * @param user      User to set
     */
    public void setUser(MapObject user) {
        this.user = user;
    }

    /**
     * Gets the X location where actions are to be taken (top right of item)
     * @return      
     */
    public int getXOrigin() {
        return (int) object.getX() + object.getXSize();
    }

    /**
     * Gets the Y location where actions are to be taken (top right of item)
     * @return      
     */
    public int getYOrigin() {
        return (int) object.getY() + object.getYSize();
    }

    /**
     * Gets the current rotation of the object
     * @return
     */
    public int getRotation() {
        return user.getRotation();
    }

    /**
     * Gets the current object, after updating its location.
     * @return      Object representing this item
     */
    public MapObject getObject() {
        refreshLocation();
        return object;
    }

    /**
     * Gets the user holding this item.
     * @return      User holding the item
     */
    /**
     * Gets the user holding this item.
     * @return      User of the object
     */
    public MapObject getUser() {
        return user;
    }

    /**
     * Gets the UI texture applied to the item.
     * @return  UI texture
     */
    public Texture getUITexture() {
        return uiTexture;
    }

    /**
     * Gets the held texture for this item.
     * @return  Held texture
     */
    public Texture getHeldTexture() {
        return object.getTexture();
    }

    /**
     * Gets a DroppedItem instance for this item.
     * @return  DroppedItem version of this item
     */
    public DroppedItem getDroppedItem() {
        return droppedItem;
    }

    /**
     * Clears the current dropped item.
     */
    public void clearDroppedItem() {
        droppedItem = null;
    }

    /**
     * Refreshes the object's location and rotation based on the user.
     */
    public void refreshLocation() {
        // Location of this object is at 45 degrees behind the user's angle.
        // Using some math to figure out where that is.
        float x0 = user.getXCenter(), y0 = user.getYCenter();

        int angle = getRotation() + 65;
        if (angle >= 360) angle -= 360;
        double angleRad = Math.toRadians((double) angle);

        float cosAngle = (float) Math.cos(angleRad);
        float sinAngle = (float) Math.sin(angleRad);
        // Equations:
        // x1 = x0 + (i * cosAngle)
        // y1 = y0 + (i * sinAngle)
        
        // Find the distance it needs to traverse using pythag.
        float distance = (float) Math.sqrt(Math.pow(user.getXSize() / 2, 2) + Math.pow(user.getYSize() / 2, 2));

        float x1 = x0 + (distance * cosAngle);
        float y1 = y0 + (distance * sinAngle);

        // This should be the center of the object.
        object.setX(x1 - object.getXSize() / 2);
        object.setY(y1 - object.getYSize() / 2);
        object.setRotation(user.getRotation());
    }

    /**
     * OVERRIDE THIS with actions to be taken when the user uses this object.
     */
    public void use() {}

    /**
     * Renders this item to the UI.
     * @param x             X location
     * @param y             Y location
     * @param xSize         Width
     * @param ySize         Height
     */
    public void renderIntoUI(int x, int y, int xSize, int ySize) {
        game.getScreen().fixedBatch.draw(uiTexture, x, y, xSize, ySize);
    }

    /**
     * Drops the item (and sets the droppedItem var).
     * @param x     X location to drop at
     * @param y     Y location to drop at
     */
    public void drop(int x, int y) {
        if (droppedItem != null) throw new GdxRuntimeException("Failed to drop item: Item is already dropped");

        // Generate a DroppedItem
        droppedItem = new DroppedItem(game, x, y, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE, this);
        // Add to map
        game.getMapScreen().map.addObject(droppedItem);
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
