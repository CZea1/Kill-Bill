package brigade.killbill.map;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

// import org.xguzm.pathfinding.grid.GridCell;
// import org.xguzm.pathfinding.grid.NavigationGrid;
// import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
// import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;
import brigade.killbill.misc.ActionSet;
import brigade.killbill.misc.lambda.MapFunction;
import brigade.killbill.misc.lambda.SearchFunction;

/**
 * Class which contains and manages all items on the map.
 * @author csenneff
 */
public class Map {
    /**
     * Parent Game object.
     */
    private KillBillGame game;

    /**
     * ArrayList containing all objects on the map.
     */
    private ArrayList<MapObject> objects;

    /**
     * ArrayList containing all items dropped when the last enemy dies.
     */
    private ArrayList<Item> drops;

    /**
     * Map's name
     */
    private String name;

    /**
     * Level number
     */
    private int level;

    /**
     * Width (in tiles)
     */
    private int xSize;

    /**
     * Height (in tiles)
     */
    private int ySize;

    /**
     * Player's spawnpoint coordinate (x)
     */
    private int xPlayerSpawnpoint;

    /**
     * Player's spawnpoint coordinate (y)
     */
    private int yPlayerSpawnpoint;

    /**
     * Map background texture.
     */
    private Texture backgroundTexture;

    /**
     * Whether or not background is tiled
     */
    private boolean isBackgroundTiled;

    //private NavigationGrid<GridCell> navigationGrid;
    //private AStarGridFinder<GridCell> finder;
    
    private ActionSet actions;

    /**
     * Constructs a new Map.
     * @param game  Parent Game object
     */
    public Map(KillBillGame game) {
        this.game = game;
        this.objects = new ArrayList<MapObject>();
        this.drops = new ArrayList<Item>();
        xPlayerSpawnpoint = 5 * KillBillGame.GRID_SIZE;
        yPlayerSpawnpoint = 5 * KillBillGame.GRID_SIZE;
        isBackgroundTiled = false;
    }

    /**
     * Adds a drop to this map.
     * @param drop      Drop to add
     */
    public void addDrop(Item drop) {
        drops.add(drop);
    }

    /**
     * Drops all drops at the specified coordinates.
     * @param x     X location
     * @param y     Y location
     */
    public void dropAll(int x, int y) {
        drops.forEach(item -> {
            item.drop(x, y);
        });
        drops.clear();
    }

    public void setActionSet(ActionSet set) {
        actions = set;
    }

    public ActionSet getActionSet() {
        return actions;
    }

    /**
     * Changes the background texture.
     * @param newTexture    Texture to set to
     * @param tiled 
     */
    public void setBackground(Texture newTexture, boolean tiled) {
        this.backgroundTexture = newTexture;
        this.isBackgroundTiled = tiled;
    }

    /**
     * Changes the spawnpoint of the player.
     * @param xLoc  X coordinate (in tiles)
     * @param yLoc  Y coordinate (in tiles)
     */
    public void setPlayerSpawnpoint(int xLoc, int yLoc) {
        xPlayerSpawnpoint = xLoc;
        yPlayerSpawnpoint = yLoc;
    }

    /**
     * Moves the player to the spawnpoint.
     */
    public void movePlayerSpawn() {
        game.getPlayer().setLocation(xPlayerSpawnpoint, yPlayerSpawnpoint);
    }

    /**
     * Changes the map's name.
     * @param name  New name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the map.
     * @return  Map name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Changes the map's level ID.
     * @param level     Map's new level ID
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Returns the map's level ID.
     * @return      Map's current level ID
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Changes the width of the map. Up to the screen to implement.
     * @param xSize     Width of the map (in tiles)
     */
    public void setXSize(int xSize) {
        this.xSize = xSize;
    }

    /**
     * Gets the width of the map.
     * @return      Width of the map (in tiles)
     */
    public int getXSize() {
        return this.xSize;
    }

    /**
     * Changes the height of the map. Up to the screen to implement.
     * @param ySize     Height of the map (in tiles)
     */
    public void setYSize(int ySize) {
        this.ySize = ySize;
    }

    /**
     * Gets the height of the map.
     * @return      Height of the map (in tiles)
     */
    public int getYSize() {
        return this.ySize;
    }

    /**
     * Adds a MapObject to the map.
     * @param object    Object to add
     */
    public void addObject(MapObject object) {
        objects.add(object);
    }

    /**
     * Removes a MapObject from the map.
     * @param object    Object to remove
     */
    public void removeObject(MapObject object) {
        objects.remove(object);
    }

    /**
     * Performs pre-render tasks on all objects being rendered.
     * @param delta     Delta time
     */
    public void doPreRender(float delta) {
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).preRender(delta);
        }
    }

    /**
     * Performs after-render (but before end of batch) tasks.
     * @param delta     Delta time
     */
    public void doLastRender(float delta) {
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).onRenderLast(delta);
        }
    }

    /**
     * Performs post-render tasks on all objects being rendered.
     * @param delta     Delta time
     */
    public void doPostRender(float delta) {
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).afterRender(delta);
        }
    }

    /**
     * Draws all objects to the fitted batch.
     * Make sure the fitted batch has begun first (in other terms: use this in renderMain()).
     * @param delta     Delta time since last render
     */
    public void drawAll(float delta) {
        // Draw background
        if (backgroundTexture != null) {
            if (isBackgroundTiled) {
                for (int x = 0; x < xSize; x++) {
                    for (int y = 0; y < ySize; y++) {
                        game.getScreen().batch.draw(backgroundTexture, x * KillBillGame.GRID_SIZE, y * KillBillGame.GRID_SIZE, KillBillGame.GRID_SIZE, KillBillGame.GRID_SIZE);
                    }
                }

            } else
                game.getScreen().batch.draw(backgroundTexture, 0, 0, xSize * KillBillGame.GRID_SIZE, ySize * KillBillGame.GRID_SIZE);
        }

        // And then objects
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).drawToBatch(delta);
        }
    }

    /**
     * Draws debugging boxes to the screen (+ looking lines).
     * @param delta     Delta time
     */
    public void drawDebug(float delta) {
        if (!game.debug.isEnabled()) return;
        if (!game.getExternRender()) return;

        // Draw boxes
        game.shapeRenderer.setColor(Color.RED);
        MapObject obj;
        for (int i = 0; i < objects.size(); i++) {
            obj = objects.get(i);
            game.shapeRenderer.rect(obj.getX(), obj.getY(), obj.getXSize(), obj.getYSize());
        }

        game.shapeRenderer.setColor(Color.BLUE);
        // Draw looking lines
        for (int i = 0; i < objects.size(); i++) {
            obj = objects.get(i);
            obj.drawLookingLine(game.shapeRenderer);
        }
    }

    /**
     * Gets any object colliding with another object.   
     * @param object        Object to check collision with
     * @return              Object colliding with it (if found, otherwise null)
     */
    public MapObject getAnyCollision(MapObject object) {
        if (!object.isCollidable()) return null;
        for (int i = 0; i < objects.size(); i++) {
            MapObject targetObject = objects.get(i);
            if (object == targetObject) continue;
            if (!targetObject.isCollidable()) continue;

            if (targetObject.isColliding(object)) return targetObject;
        }
        return null;
    }

    /**
     * Loads a map into this class from a source .map file.
     * @param path      Path to the file (with assets/ as root)
     */
    public void loadFromFile(String path) {
        game.mapLoader.loadTo(path, this);
    }

    /**
     * Returns an ArrayList containing all objects in this map.
     * @return  Map objects
     */
    public ArrayList<MapObject> getObjects() {
        return objects;
    }

    /**
     * Searches for an object using a Boolean lambda function.
     * Returns the object if found. Otherwise, returns null.
     * Example:
     * {@code
     * MapObject foundObject = map.searchFor( (object) -> object.isColliding(this) )
     * }
     * would return the first object found colliding with this object.
     * @param func      Lambda expression to evaluate against each object
     * @return          Object matched, if one was found
     */
    public MapObject searchFor(SearchFunction func) {
        MapObject currentObject;
        for (int i = 0; i < objects.size(); i++) {
            currentObject = objects.get(i);
            if (func.run(currentObject)) return currentObject;
        }
        return null;
    }

    /**
     * Runs a lambda expression on each object in htis map.
     * Example:
     * {@code
     * map.forEach( (object) -> object.setTexture(someTexture) )
     * }
     * would change every object to someTexture.
     * @param func      Lambda expression to run
     */
    public void forEach(MapFunction func) {
        for (int i = 0; i < objects.size(); i++) {
            func.run(objects.get(i));
        }
    }

    public ArrayList<MapObject> objectsAt(float x, float y) {
        ArrayList<MapObject> res = new ArrayList<MapObject>();
        for (MapObject object : objects) {
            if (object.getRectangle().contains(x, y)) {
                res.add(object);
            }
        }
        return res;
    }

    // More AI stuff
    // RIP :'(
    /*public void generatePaths() {
        GridCell[][] cells = new GridCell[xSize][ySize];

        ArrayList<MapObject> objs;
        boolean walkable;
        System.out.println("--- " + getName() + " ---");
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                objs = objectsAt(x * KillBillGame.GRID_SIZE + KillBillGame.GRID_SIZE / 2, y * KillBillGame.GRID_SIZE + KillBillGame.GRID_SIZE / 2);
                walkable = true;

                for (MapObject obj: objs) {
                    if (obj instanceof Player || obj instanceof Entity) continue;

                    if (obj.isCollidable()) walkable = false;
                }

                if (walkable) System.out.print("  ");
                else System.out.print("##");

                cells[x][y] = new GridCell(x, y, walkable);
            }
            System.out.println();
        }

        navigationGrid = new NavigationGrid<GridCell>(cells, false);
        GridFinderOptions opt = new GridFinderOptions();
        opt.allowDiagonal = true;
        opt.dontCrossCorners = true;
        finder = new AStarGridFinder<GridCell>(GridCell.class, opt);
    }

    public List<GridCell> getPath(int x0, int y0, int x1, int y1) {
        game.debug.setValue("pathInfo", String.format("%d,%d -> %d,%d", x0, y0, x1, y1));
        return finder.findPath(x0, y0, x1, y1, navigationGrid);
    }*/
}