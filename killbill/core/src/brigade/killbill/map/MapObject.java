package brigade.killbill.map;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;

/**
 * Generic class representing an object on the map.
 * @author csenneff
 */
public class MapObject {
    /**
     * Parent Game object.
     */
    protected KillBillGame game;

    /**
     * Rectangle representing the object's location and size. Used for collision detection only.
     */
    private Rectangle rectangle;

    /**
     * Sprite object representing this MapObject.
     */
    private Sprite sprite;

    /**
     * Whether or not the object is visible.
     */
    private Boolean visible;

    /**
     * Texture to render. Can be null.
     */
    private Texture texture;

    /**
     * Whether or not the object is collidable.
     */
    private Boolean collidable;

    /**
     * X and Y locations of the object.
     */
    private float x, y;

    /**
     * Width and height of the object.
     */
    private int xSize, ySize;

    /**
     * Object's name.
     */
    private String name;

    /**
     * Item currently held by the object, if it exists.
     */
    private Item currentItem;

    /**
     * Distance this object can reach.
     */
    private int reach;

    /**
     * Whether or not the object blocks line-of-sight calculations.
     */
    private boolean solid;
    
    /**
     * Constructs a MapObject, which is the generic representation of any object
     *  rendered to the display.
     * @param game          Parent Game object.
     * @param x             X coordinate.   
     * @param y             Y coordinate.
     * @param xSize         X size (width).
     * @param ySize         Y size (height).
     * @param texture       Texture object. Can be null.
     * @param collidable    Whether or not the object can be collided with.
     */
    public MapObject(KillBillGame game, float x, float y, int xSize, int ySize, Texture texture, Boolean collidable) {
        visible = texture != null;

        this.texture = texture;
        this.game = game;
        this.x = x;
        this.y = y;
        this.xSize = xSize;
        this.ySize = ySize;
        this.collidable = collidable;
        this.name = "object";
        this.reach = 0;
        this.solid = true;

        this.rectangle = new Rectangle();
        this.sprite = new Sprite(this.texture);
        updateRectangle();
        
        this.currentItem = null;
    }

    /**
     * Gets the name of this object.
     * @return  Object name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this object.
     * @param name  Object name
     */
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    /**
     * Sets this object's reach.
     * @param reach     Reach (in tiles)
     */
    public void setReach(int reach) {
        this.reach = reach;
    }

    /**
     * Gets this object's reach.
     * @return      Reach (in tiles)
     */
    public int getReach() {
        return reach;
    }

    /**
     * Checks if the object is currently visible.
     * @return  Boolean value representing whether the object is visible
     */
    public Boolean isVisible() {
        return visible;
    }

    /**
     * Makes the object visible or invisible.
     * @param visible   Whether the object should be visible.
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    /**
     * Changes the object's texture.
     * @param newTexture    New texture to change to. Can be null.
     */
    public void setTexture(Texture newTexture) {
        this.texture = newTexture;
        this.sprite.setTexture(texture);

        if (newTexture == null) {
            this.visible = false;
        }
    }

    /**
     * Gets this object's texture.
     * @return      Object's current texture
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Checks if the object is collidable or not.
     * @return  Boolean representing whether or not the object is collidable.
     */
    public Boolean isCollidable() {
        return this.collidable;
    }

    /**
     * Changes whether or not the object is collidable.
     * @param collidable    New collidable state.
     */
    public void setCollidable(Boolean collidable) {
        this.collidable = collidable;
    }

    /**
     * Changes the location of the object.
     * @param x     New X coordinate.
     * @param y     New Y coordinate.
     */
    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
        updateRectangle();
    }

    /**
     * Changes the X coordinate of the object.
     * @param x     New X coordinate.
     */
    public void setX(float x) {
        this.x = x;
        updateRectangle();
    }

    /**
     * Changes the Y coordinate of the object.
     * @param y     New Y coordinate.
     */
    public void setY(float y) {
        this.y = y;
        updateRectangle();
    }

    /**
     * Gets the X coordinate of the object.
     * @return      Object's X coordinate.
     */
    public float getX() {
        return x;
    }

    /**
     * Gets the Y coordinate of the object.
     * @return      Object's Y coordinate.
     */
    public float getY() {
        return y;
    }

    public int getXTiled() {
        return (int) (getXCenter() / KillBillGame.GRID_SIZE);
    }

    public int getYTiled() {
        return (int) (getYCenter() / KillBillGame.GRID_SIZE);
    }

    
    /**
     * Changes the width and height of the object.
     * @param xSize New width
     * @param ySize New height
     */
    public void resize(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        updateRectangle();
    }

    /**
     * Gets the X size (width) of the object.
     * @return  X size (in pixels)
     */
    public int getXSize() {
        return xSize;
    }

    /**
     * Gets the Y size (height) of the object.
     * @return  Y size (in pixels)
     */
    public int getYSize() {
        return ySize;
    }

    /**
     * Gets the center of this object on the X axis.
     * @return      X center
     */
    public float getXCenter() {
        return x + xSize / 2f;
    }

    /**
     * Gets the center of this object on the Y axis.
     * @return      Y center
     */
    public float getYCenter() {
        return y + ySize / 2f;
    }

    /**
     * Changes the rotation of the object's sprite.
     * @param rotation      New rotation, 0-360
     */
    public void setRotation(int rotation) {
        this.sprite.setRotation(rotation);
    }

    /**
     * Gets the current rotation in degrees.
     * @return      Rotation, 0-360
     */
    public int getRotation() {
        return (int) this.sprite.getRotation();
    }

    /**
     * Changes the color of this object.
     * @param r     Red
     * @param g     Green
     * @param b     Blue
     * @param a     Alpha
     */
    public void setColor(float r, float g, float b, float a) {
        this.sprite.setColor(r, g, b, a);
    }

    /**
     * Updates the internal Rectangle object.
     */
    private void updateRectangle() {
        rectangle.x = (int) x;
        rectangle.y = (int) y;
        rectangle.width = xSize;
        rectangle.height = ySize;
        this.sprite.setPosition((int) this.x, (int) this.y);
        this.sprite.setOrigin(xSize / 2f, ySize / 2f);
        this.sprite.setSize(xSize, ySize);
    }

    /**
     * Gets a Rectangle representing this object's hitbox.
     * @return      Rectangle for this object's hitbox
     */
    public Rectangle getRectangle() {
        return rectangle;
    }

    /**
     * Gets a Sprite representing this object.
     * @return      Sprite for drawing the object to the screen.
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Draws the object to the parent Game's batch.
     * @param delta     Delta time since last run
     */
    public void drawToBatch(float delta) {
        if (visible) {
            this.sprite.draw(game.getScreen().batch);
    
            if (currentItem != null) {
                this.currentItem.getObject().drawToBatch(delta);
            }
        }
        onRender(delta);
    }

    /**
     * Checks if the object is currently colliding with another object.
     * @param compareTo     Object to compare to.
     * @return              Whether or not the object is colliding.
     */
    public Boolean isColliding(MapObject compareTo) {
        // Compare object's rectangles
        return this.rectangle.overlaps(compareTo.rectangle);
    }

    /**
     * Checks if this object would collide with compareTo if it (this) were moved to targetX/targetY.
     * @param compareTo     MapObject to check for collisions with
     * @param targetX       X location to check collision at
     * @param targetY       Y location to check collision at
     * @return              Whether or not there is a collision
     */
    public Boolean checkCollisionAt(MapObject compareTo, int targetX, int targetY) {
        // Make a rectangle at this size
        Rectangle targetRectangle = new Rectangle(targetX, targetY, this.rectangle.getWidth(), this.rectangle.getHeight());

        // Compare
        return targetRectangle.overlaps(compareTo.rectangle);
    }

    /**
     * Moves an object by translateX/translateY units, or until it collides with a specified object.
     * @param collidedWith      Object to check for collisions with
     * @param translateX        Units to translate in X direction
     * @param translateY        Units to translate in Y direction
     */
    public void translateUntilCollision(MapObject collidedWith, float translateX, float translateY) {
        int xIter = translateX < 0 ? -1 : 1;
        int yIter = translateY < 0 ? -1 : 1;

        Boolean stopped = false;
        //System.out.printf("X: %d, to: %d, iter: %d\n", (int) this.x, (int) (this.x + translateX + xIter), xIter);
        for (int currentX = (int) this.x; currentX != (int) (this.x + translateX + xIter); currentX += xIter) {
            if (this.checkCollisionAt(collidedWith, currentX, (int) this.y)) {
                //System.out.println("Stopping");                / Stop here
                this.x = currentX - xIter;
                stopped = true;
                break;
            }
        }
        if (!stopped) {
            //System.out.println("Never stopped, moving to end");
            this.x += translateX;
        }

        stopped = false;
        //System.out.printf("Y: %d, to: %d, iter: %d\n", (int) this.y, (int) (this.y + translateY + yIter), yIter);
        for (int currentY = (int) this.y; currentY != (int) (this.y + translateY + yIter); currentY += yIter) {
            if (this.checkCollisionAt(collidedWith, (int) this.x, currentY)) {
                // Stop here
                this.y = currentY - yIter;
                stopped = true;
                break;
            }
        }
        if (!stopped) {
            this.y += translateY;
        }
        updateRectangle();
    }

    /**
     * Sets the object's currently held item.
     * @param item      Item to use (null = no item)
     */
    public void setItem(Item item) {
        this.currentItem = item;
    }

    /**
     * Gets the object's currently held item, if it exists.
     * @return          Current item (can be null)
     */
    public Item getItem() {
        return currentItem;
    }

    /**
     * Override this with actions to take before render.
     * @param delta     Delta time since last render
     */
    public void preRender(float delta) {

    }

    /**
     * Override this with actions to take during render.
     * @param delta     Delta time since last render
     */
    public void onRender(float delta) {

    }

    /**
     * Override this with actions to take at the very end of render.
     * @param delta     Delta time since last render
     */
    public void onRenderLast(float delta) {

    }

    /**
     * Override this with actions to take after render.
     * @param delta     Delta time since last render
     */
    public void afterRender(float delta) {
    }


    /**
     * Override this with actions to take on collision with any other object.
     * If your objects call movement functions, you're responsible for calling this on
     * the object you collide with when collisions happen.
     * @param collidedWith      Object collided with
     */
    public void onCollision(MapObject collidedWith) {

    }

    /**
     * Draws a line (length = reach) in the direction that this object is looking.
     * @param batch     Batch to draw to
     */
    public void drawLookingLine(ShapeRenderer batch) {
        if (reach < 1) return;

        float x0 = getXCenter(), y0 = getYCenter();

        int angle = getRotation() + 90;
        if (angle >= 360) angle -= 360;
        double angleRad = Math.toRadians((double) angle);

        float cosAngle = (float) Math.cos(angleRad);
        float sinAngle = (float) Math.sin(angleRad);
        // Equations:
        // x1 = x0 + (i * cosAngle)
        // y1 = y0 + (i * sinAngle)

        float x1 = x0 + (reach * cosAngle);
        float y1 = y0 + (reach * sinAngle);

        batch.line(x0, y0, x1, y1);
    }

    /**
     * Checks if this object is within interaction range of the specified object.
     * Works by drawing a rectangle out of this object using the specified sizes and
     * checking if a collision occurs.
     * @param with          Object to check if an interaction is possible with
     * @param angles        Angles away from object to check at
     * @param distance      Distance the object can reach
     */
    public boolean canInteract(MapObject with, int[] angles, int distance) {
        if (reach <= 0.0005f) return false;

        float x0 = getXCenter(), y0 = getYCenter();

        for (int i = 0; i < angles.length; i++) {
            int userAngle = getRotation() + 90 + angles[i];
            if (userAngle >= 360) userAngle -= 360;
            double angleRad = Math.toRadians((double) userAngle);

            float cosAngle = (float) Math.cos(angleRad);
            float sinAngle = (float) Math.sin(angleRad);

            int actualDistance = 0;
            boolean hitObject = false;
            // Iterate over this line from getX() to with.getX()
            float x1, y1;
            for (int j = 0; j < distance + 1; j++) {
                // Find our X and Y
                x1 = x0 + (j * cosAngle);
                y1 = y0 + (j * sinAngle);

                // Check if this point is colliding with the user, nothing, or target
                if (with.getRectangle().contains(x1, y1)) {
                    hitObject = true;
                    break;
                } else if (!rectangle.contains(x1, y1)) {
                    actualDistance++;
                }
            }
            if (actualDistance < distance && hitObject) return true;
        }

        return false;
    }

    /**
     * Called when the player interacts with this object.
     * Override this.
     * You have to check if the player can interact before doing anything with this.
     */
    public void onInteraction() {
        
    }

    /**
     * Checks whether or not this object is currently on the screen.
     * @return
     */
    public boolean isOnScreen() {
        return game.getScreen().fittedRectangle.overlaps(this.rectangle);
    }

    /**
     * Checks if any solid objects are in the way.
     * @param target    Object to check visibility of
     * @return          Whether or not this object can see the other
     */
    public boolean canSee(MapObject target) {
        float xDiff = getXCenter() - target.getXCenter();
        float yDiff = getYCenter() - target.getYCenter();
        //System.out.printf("\n--- DIFF: %.1f %.1f\n", xDiff, yDiff);

        // Find our iterators
        float xIter, yIter;
        float iterCount;
        if (Math.abs(xDiff) > Math.abs(yDiff)) {
            xIter = KillBillGame.ITERATOR_SIZE;
            iterCount = xDiff / xIter;
            yIter = Math.abs(yDiff / iterCount);
        }
        else {
            yIter = KillBillGame.ITERATOR_SIZE;
            iterCount = yDiff / yIter;
            xIter = Math.abs(xDiff / iterCount);
        }

        if (xDiff > 0) xIter *= -1;
        if (yDiff > 0) yIter *= -1;

        float currX = getXCenter();
        float currY = getYCenter();
        //System.out.printf("--- ITER: %.1f %.1f\n", xIter, yIter);
        //System.out.printf("--- START: %.1f %.1f\n", currX, currY);
        //System.out.printf("--- TARGET: %.1f %.1f\n", target.getXCenter(), target.getYCenter());

        int i = 0;
        int max = (int) Math.ceil(Math.abs(iterCount));
        //System.out.printf("--- ITERS: %d\n", max);
        while (i <= max) {
            //System.out.printf("--- XY,%d: %.1f %.1f\n", i, currX, currY);

            ArrayList<MapObject> objects = game.getScreen().map.objectsAt(currX, currY);
            for (MapObject object : objects) {
                if (object != null && object.isSolid() && object != this && object != target) {
                    //game.debug.setValue("collid", String.format("COLLIDING AT %.1f, %.1f", currX, currY));
                    //System.out.println("---------------------------- COLLISION! ----------------------------------");
                    //System.exit(0);
                    return false;
                }
            }
            i++;
            currX += xIter;
            currY += yIter;
        }

        return true;
    }

    public void translate(float xDelta, float yDelta) {
        float currentX = getX();
        float currentY = getY();

        if (game.getMapScreen().map.getAnyCollision(this) != null) {
            // Already colliding. Just move them so they can get out.
            setLocation(currentX + xDelta, currentY + yDelta);
            return;
        }

        setLocation(currentX + xDelta, currentY);
        MapObject collidedWith = game.getMapScreen().map.getAnyCollision(this);
        if (collidedWith != null) {
            // Call collision checks
            collidedWith.onCollision(this);
            
            setLocation(currentX, currentY);
            translateUntilCollision(collidedWith, xDelta, 0);
        }

        currentX = getX();

        setLocation(currentX, currentY + yDelta);
        collidedWith = game.getMapScreen().map.getAnyCollision(this);
        if (collidedWith != null) {
            // Call collision checks
            collidedWith.onCollision(this);
            
            setLocation(currentX, currentY);
            translateUntilCollision(collidedWith, 0, yDelta);
        }
    }

    public void translateAndRedirect(float xDelta, float yDelta) {
        float currentX = getX();
        float currentY = getY();

        float targetX = currentX + xDelta;
        float targetY = currentY + yDelta;

        translate(xDelta, yDelta);
        // Find difference from target and apply to the opposite coordinate.
        float dx = 0, dy = 0;
        boolean move = false;
        
        float xAdjustment = Math.abs(targetX - getX());
        if (xAdjustment > 0.05f) {
            dy = xAdjustment;

            if (yDelta < -0.05f) {
                dy += -1 * yDelta;
            } 

            move = true;
        }

        float yAdjustment = Math.abs(targetY - getY());
        if (yAdjustment > 0.05f) {
            dx = yAdjustment;

            if (xDelta < -0.05f) {
                dx += -1 * xDelta;
            } 
            move = true;
        }

        if (move) {
            translate(dx, dy);
        }
    }
}
