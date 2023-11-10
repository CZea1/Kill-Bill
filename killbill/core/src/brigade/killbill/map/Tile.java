package brigade.killbill.map;

import brigade.killbill.KillBillGame;

import com.badlogic.gdx.graphics.Texture;

/**
 * Generic class representing a tiled object on the map.
 * @author csenneff
 */ 
public class Tile extends MapObject {

    /**
     * Tile location on the X axis (tiles, not pixels).
     */
    private int gridX;

    /**
     * Tile location on the Y axis (tiles, not pixels).
     */
    private int gridY;

    /**
     * Grid size (pixels per tile).
     */
    private int gridSize;

    /**
     * Constructs a new Tile.
     * @param game          Parent Game object
     * @param gridX         Location on the X axis (in tiles, not pixels)
     * @param gridY         Location on the Y axis (in tiles, not pixels)
     * @param gridSize      Grid size (pixels per tile)
     * @param texture       Texture object to attach to the object. Can be null for no rendering.
     */
    public Tile(KillBillGame game, int gridX, int gridY, int gridSize, Texture texture) {
        super(game, 0, 0, gridSize, gridSize, texture, true);
        this.gridSize = gridSize;
        this.gridX = gridX;
        this.gridY = gridY;

        this.setLocation(getXDisplayCoord(), getYDisplayCoord());
    }

    /**
     * Changes the object's location on the grid.
     * @param gridX     X location (in tiles)
     * @param gridY     Y location (in tiles)
     */
    public void setGridLocation(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.setLocation(getXDisplayCoord(), getYDisplayCoord());
    }

    /**
     * Changes the object's X location on the grid.
     * @param gridX     X location (in tiles)
     */
    public void setGridX(int gridX) {
        this.gridX = gridX;
        this.setLocation(getXDisplayCoord(), getYDisplayCoord());
    }

    /**
     * Changes the object's Y location on the grid.
     * @param gridY     Y location (in tiles)
     */
    public void setGridY(int gridY) {
        this.gridY = gridY;
        this.setLocation(getXDisplayCoord(), getYDisplayCoord());
    }

    /**
     * Gets the object's X location on the grid.
     * @return      X location (in tiles)
     */
    public int getGridX() {
        return gridX;
    }

    /**
     * Gets the object's Y location on the grid.
     * @return      Y location (in tiles)
     */
    public int getGridY() {
        return gridY;
    }

    /**
     * Gets the actual (in pixels) X coordinate of the tile.
     * @return  X coordinate (in pixels)
     */
    public int getXDisplayCoord() {
        return gridX * gridSize;
    }

    /**
     * Gets the actual (in pixels) Y coordinate of the tile.
     * @return  Y coordinate (in pixels)
     */
    public int getYDisplayCoord() {
        return gridY * gridSize;
    }
}
