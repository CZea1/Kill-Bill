package brigade.killbill.map.maploader;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.entities.EntityAttributes;
import brigade.killbill.entities.enemies.Bill;
import brigade.killbill.entities.enemies.MicrosoftEmployee;
import brigade.killbill.entities.enemies.SecurityRobot;
import brigade.killbill.entities.enemies.Shareholder;
import brigade.killbill.items.Item;
import brigade.killbill.map.MapObject;
import brigade.killbill.map.Tile;
import brigade.killbill.misc.Parsers;
import brigade.killbill.objects.Chest;
import brigade.killbill.objects.Door;
import brigade.killbill.objects.DroppedItem;

/**
 * Helper class which creates Objects from a .map file.
 * @author csenneff
 */
public class ObjectFactory {
    /**
     * Parent Game object.
     */
    private KillBillGame game;    

    /**
     * Constructs a new ObjectFactory.
     * @param game  Parent Game object
     */
    public ObjectFactory(KillBillGame game) {
        this.game = game;
    }

    /**
     * Creates an Entity.
     * @param name  Entity name
     * @param args  Arguments provided to the map loader
     * @return      Created Entity
     */
    public Entity createEntity(String name, HashMap<String, String> args) {
        int xSize = KillBillGame.GRID_SIZE;
        int ySize = KillBillGame.GRID_SIZE;
        int health = 5;
        int reach = 2 * KillBillGame.GRID_SIZE;
        Texture texture = game.textureStore.getDefaultTexture();
        boolean collidable = true;
        int speed = 1;
        ArrayList<EntityAttributes> attrs = new ArrayList<EntityAttributes>();
        String currentAttr;

        for (HashMap.Entry<String, String> arg : args.entrySet()) {
            String value = arg.getValue();

            switch (arg.getKey().toLowerCase()) {
                case "texture":
                    texture = game.textureStore.getTexture(value);
                    break;
                
                case "width":
                case "xsize":
                    xSize = Parsers.toPixels(value);
                    break;
                
                case "height":
                case "ysize":
                    ySize = Parsers.toPixels(value);
                    break;
                
                case "collidable":
                    collidable = Parsers.toBool(value);
                    break;
                
                case "speed":
                    speed = Parsers.toInt(value);
                    break;

                case "health":
                    health = Parsers.toInt(value);
                    break;

                case "reach":
                    reach = Parsers.toInt(value);
                    break;

                case "attrs":
                case "attributes":
                    String[] attrsStr = value.split("\\&");

                    for (int i = 0; i < attrsStr.length; i++) {
                        currentAttr = attrsStr[i].strip();

                        if (currentAttr.length() == 0) continue;

                        attrs.add(Parsers.toEntityAttributes(currentAttr));
                    }
                    break;

                default:
                    throw new GdxRuntimeException(String.format("Error parsing object args: Undefined value %s", arg.getKey()));
            }
        }

        Entity res = new Entity(game, 0, 0, xSize, ySize, texture, collidable, speed, health);
        for (int i = 0; i < attrs.size(); i++) res.setAttr(attrs.get(i));
        res.setReach(reach);
        res.setName(name);
        return res;

    }

    /**
     * Creates a Tile.
     * @param name  Tile name
     * @param args  Arguments provided to the map loader
     * @return      Created Tile
     */
    public Tile createTile(String name, HashMap<String, String> args) {
        int gridSize = KillBillGame.GRID_SIZE;
        boolean collidable = true;
        boolean visible = true;
        Texture texture = game.textureStore.getDefaultTexture();
        Boolean solid = true;
        int rotation = 0;

        for (HashMap.Entry<String, String> arg : args.entrySet()) {
            String value = arg.getValue();

            switch (arg.getKey()) {
                case "texture":
                    texture = game.textureStore.getTexture(value);
                    break;
                
                case "gridSize":
                    gridSize = Parsers.toInt(value);
                    break;
                
                case "collidable":
                    collidable = Parsers.toBool(value);
                    break;
                
                case "visible":
                    visible = Parsers.toBool(value);
                    break;
                
                case "solid":
                    solid = Parsers.toBool(value);
                    break;
                
                case "rotation":
                    rotation = Parsers.toInt(value);
                    break;

                default:
                    throw new GdxRuntimeException(String.format("Error parsing object args: Undefined value %s", arg.getKey()));
            }
        }

        Tile res = new Tile(game, 0, 0, gridSize, texture);
        res.setSolid(solid);
        res.setVisible(visible);
        res.setCollidable(collidable);
        res.setVisible(visible);
        res.setName(name);
        return res;
    }

    /**
     * Creates a MapObject.
     * @param name  MapObject name
     * @param args  Arguments provided to the map loader
     * @return      Created MapObject
     */
    public MapObject createObject(String name, HashMap<String, String> args) {
        // Iterate over the HashMap
        Texture texture = game.textureStore.getDefaultTexture();
        int xSize = 32;
        int ySize = 32;
        Boolean collidable = true;
        Boolean visible = true;
        Boolean solid = true;
        int rotation = 0;
        
        for (HashMap.Entry<String, String> arg : args.entrySet()) {
            String value = arg.getValue();

            switch (arg.getKey()) {
                case "texture":
                    texture = game.textureStore.getTexture(value);
                    break;

                case "xSize":
                    xSize = Parsers.toPixels(value);
                    break;

                case "ySize":
                    ySize = Parsers.toPixels(value);
                    break;
                
                case "collidable":
                    collidable = Parsers.toBool(value);
                    break;
                
                case "visible":
                    visible = Parsers.toBool(value);
                    break;
                
                case "solid":
                    solid = Parsers.toBool(value);
                    break;
                
                case "rotation":
                    rotation = Parsers.toInt(value);
                    break;

                default:
                    throw new GdxRuntimeException(String.format("Error parsing object args: Undefined value %s", arg.getKey()));
            }
        }

        MapObject res = new MapObject(game, 0, 0, xSize, ySize, texture, collidable);
        res.setSolid(solid);
        res.setVisible(visible);
        res.setName(name);
        res.setRotation(rotation);
        return res;
    }

    /**
     * Creates a Door.
     * @param name  Door name
     * @param args  Arguments provided to the map loader
     * @return      Created Tile
     */
    public Door createDoor(String name, HashMap<String, String> args) {
        int gridSize = KillBillGame.GRID_SIZE;
        boolean collidable = true;
        boolean visible = true;
        Texture texture = game.textureStore.getDefaultTexture();
        Texture openedTexture = game.textureStore.getDefaultTexture();
        String destination = null;
        boolean requiresKey = false;

        int xSpawn = -1;
        int ySpawn = -1;

        for (HashMap.Entry<String, String> arg : args.entrySet()) {
            String value = arg.getValue();

            switch (arg.getKey()) {
                case "texture":
                    texture = game.textureStore.getTexture(value);
                    break;

                case "openedTexture":
                    openedTexture = game.textureStore.getTexture(value);
                    break;
                
                case "gridSize":
                    gridSize = Parsers.toInt(value);
                    break;
                
                case "collidable":
                    collidable = Parsers.toBool(value);
                    break;
                
                case "visible":
                    visible = Parsers.toBool(value);
                    break;
                
                case "destination":
                    destination = value;
                    break;

                case "key":
                    requiresKey = Parsers.toBool(value);
                    break;

                case "spawn":
                    String[] coordArgs = value.split("x", 2);

                    if (coordArgs.length != 2) throw new GdxRuntimeException(String.format("Error parsing object args: Expected format [x]x[y] for spawnpoint"));

                    xSpawn = Parsers.toPixels(coordArgs[0]);
                    ySpawn = Parsers.toPixels(coordArgs[1]);
                    break;

                default:
                    throw new GdxRuntimeException(String.format("Error parsing object args: Undefined value %s", arg.getKey()));
            }
        }

        if (destination == null) throw new GdxRuntimeException("Door is missing a destination and cannot be added.");

        Door res = new Door(game, 0, 0, gridSize, texture, openedTexture, destination, requiresKey);
        if (xSpawn != -1) res.setPlayerSpawn(xSpawn, ySpawn);
        res.setCollidable(collidable);
        res.setVisible(visible);
        res.setName(name);
        return res;
    }

    /**
     * Creates a Chest.
     * @param name  Chest name
     * @param args  Arguments provided to the map loader
     * @return      Created Chest
     */
    public Chest createChest(String name, HashMap<String, String> args) {
        int gridSize = KillBillGame.GRID_SIZE;
        boolean collidable = true;
        boolean visible = true;
        Texture texture = game.textureStore.getTexture("items_chest");
        Texture openedTexture = game.textureStore.getTexture("items_open_chest");
        String lootTable = null;
        int rotation = 0;

        for (HashMap.Entry<String, String> arg : args.entrySet()) {
            String value = arg.getValue();

            switch (arg.getKey()) {
                case "texture":
                    texture = game.textureStore.getTexture(value);
                    break;

                case "openedTexture":
                    openedTexture = game.textureStore.getTexture(value);
                    break;
                
                case "gridSize":
                    gridSize = Parsers.toInt(value);
                    break;
                
                case "collidable":
                    collidable = Parsers.toBool(value);
                    break;
                
                case "visible":
                    visible = Parsers.toBool(value);
                    break;

                case "lootTable":
                    lootTable = value;
                    break;
                
                case "rotation":
                    rotation = Parsers.toInt(value);
                    break;

                default:
                    throw new GdxRuntimeException(String.format("Error parsing object args: Undefined value %s", arg.getKey()));
            }
        }
        Chest res = new Chest(game, 0, 0, gridSize, texture, openedTexture);
        if (lootTable != null) {
            res.setLootTable(game.lootTables.getTable(lootTable));
        }
        res.setRotation(rotation);
        res.setCollidable(collidable);
        res.setVisible(visible);
        res.setName(name);
        return res;
    }

    /**
     * Creates a MicrosoftEmployee.
     * @param name  MicrosoftEmployee name
     * @param args  Arguments provided to the map loader
     * @return      Created MicrosoftEmployee
     */
    public MicrosoftEmployee createMicrosoftEmployee(String name, HashMap<String, String> args) {
        int xSize = KillBillGame.GRID_SIZE;
        int ySize = KillBillGame.GRID_SIZE;
        ArrayList<EntityAttributes> attrs = new ArrayList<EntityAttributes>();
        String currentAttr;
        String lines = null;
        int rotation = 0;
        int textureSet = -1;

        for (HashMap.Entry<String, String> arg : args.entrySet()) {
            String value = arg.getValue();

            switch (arg.getKey().toLowerCase()) {
                case "width":
                case "xsize":
                    xSize = Parsers.toPixels(value);
                    break;
                
                case "height":
                case "ysize":
                    ySize = Parsers.toPixels(value);
                    break;

                case "attrs":
                case "attributes":
                    String[] attrsStr = value.split("\\&");

                    for (int i = 0; i < attrsStr.length; i++) {
                        currentAttr = attrsStr[i].strip();

                        if (currentAttr.length() == 0) continue;

                        attrs.add(Parsers.toEntityAttributes(currentAttr));
                    }
                    break;

                case "lines":
                    lines = "lines/" + value + ".txt";
                    break;

                case "rotation":
                    rotation = Parsers.toInt(value);
                    break;

                case "textureSet":
                    textureSet = Parsers.toInt(value);
                    break;

                default:
                    throw new GdxRuntimeException(String.format("Error parsing object args: Undefined value %s", arg.getKey()));
            }
        }

        MicrosoftEmployee res = new MicrosoftEmployee(game, 0, 0, xSize, ySize);
        if (textureSet >= 0) {
            res.setTextureSet(textureSet);
        }
        res.setRotation(rotation);
        if (lines != null) {
            res.readVoiceLines(lines);
        }
        for (int i = 0; i < attrs.size(); i++) if (!res.hasAttr(attrs.get(i))) res.setAttr(attrs.get(i));
        res.setName(name);
        return res;
    }

    /**
     * Creates a SecurityRobot.
     * @param name  SecurityRobot name
     * @param args  Arguments provided to the map loader
     * @return      Created SecurityRobot
     */
    public SecurityRobot createSecurityRobot(String name, HashMap<String, String> args) {
        int xSize = KillBillGame.GRID_SIZE;
        int ySize = KillBillGame.GRID_SIZE;
        ArrayList<EntityAttributes> attrs = new ArrayList<EntityAttributes>();
        String currentAttr;
        String lines = null;
        int rotation = 0;

        for (HashMap.Entry<String, String> arg : args.entrySet()) {
            String value = arg.getValue();

            switch (arg.getKey().toLowerCase()) {
                case "width":
                case "xsize":
                    xSize = Parsers.toPixels(value);
                    break;
                
                case "height":
                case "ysize":
                    ySize = Parsers.toPixels(value);
                    break;

                case "attrs":
                case "attributes":
                    String[] attrsStr = value.split("\\&");

                    for (int i = 0; i < attrsStr.length; i++) {
                        currentAttr = attrsStr[i].strip();

                        if (currentAttr.length() == 0) continue;

                        attrs.add(Parsers.toEntityAttributes(currentAttr));
                    }
                    break;

                case "lines":
                    lines = "lines/" + value + ".txt";
                    break;

                case "rotation":
                    rotation = Parsers.toInt(value);
                    break;

                default:
                    throw new GdxRuntimeException(String.format("Error parsing object args: Undefined value %s", arg.getKey()));
            }
        }

        SecurityRobot res = new SecurityRobot(game, 0, 0, xSize, ySize);
        res.setRotation(rotation);
        if (lines != null) {
            res.readVoiceLines(lines);
        }
        for (int i = 0; i < attrs.size(); i++) if (!res.hasAttr(attrs.get(i))) res.setAttr(attrs.get(i));
        res.setName(name);
        return res;
    }

    /**
     * Creates a Shareholder.
     * @param name  Shareholder name
     * @param args  Arguments provided to the map loader
     * @return      Created Shareholder
     */
    public Shareholder createShareholder(String name, HashMap<String, String> args) {
        int xSize = KillBillGame.GRID_SIZE;
        int ySize = KillBillGame.GRID_SIZE;
        ArrayList<EntityAttributes> attrs = new ArrayList<EntityAttributes>();
        String currentAttr;
        String lines = null;
        int rotation = 0;
        int textureSet = -1;

        for (HashMap.Entry<String, String> arg : args.entrySet()) {
            String value = arg.getValue();

            switch (arg.getKey().toLowerCase()) {
                case "width":
                case "xsize":
                    xSize = Parsers.toPixels(value);
                    break;
                
                case "height":
                case "ysize":
                    ySize = Parsers.toPixels(value);
                    break;

                case "attrs":
                case "attributes":
                    String[] attrsStr = value.split("\\&");

                    for (int i = 0; i < attrsStr.length; i++) {
                        currentAttr = attrsStr[i].strip();

                        if (currentAttr.length() == 0) continue;

                        attrs.add(Parsers.toEntityAttributes(currentAttr));
                    }
                    break;

                case "lines":
                    lines = "lines/" + value + ".txt";
                    break;

                case "rotation":
                    rotation = Parsers.toInt(value);
                    break;

                case "textureSet":
                    textureSet = Parsers.toInt(value);
                    break;

                default:
                    throw new GdxRuntimeException(String.format("Error parsing object args: Undefined value %s", arg.getKey()));
            }
        }

        Shareholder res = new Shareholder(game, 0, 0, xSize, ySize);
        if (textureSet >= 0) {
            res.setTextureSet(textureSet);
        }
        res.setRotation(rotation);
        if (lines != null) {
            res.readVoiceLines(lines);
        }
        for (int i = 0; i < attrs.size(); i++) if (!res.hasAttr(attrs.get(i))) res.setAttr(attrs.get(i));
        res.setName(name);
        return res;
    }

    
    /**
     * Creates a DroppedItem.
     * @param name  DroppedItem name
     * @param args  Arguments provided to the map loader
     * @return      Created DroppedItem
     */
    public DroppedItem createDroppedItem(String name, HashMap<String, String> args) {
        int xSize = KillBillGame.ITEM_SIZE;
        int ySize = KillBillGame.ITEM_SIZE;
        int rotation = 0;
        Item item = null;

        for (HashMap.Entry<String, String> arg : args.entrySet()) {
            String value = arg.getValue();

            switch (arg.getKey().toLowerCase()) {
                case "width":
                case "xsize":
                    xSize = Parsers.toPixels(value);
                    break;
                
                case "height":
                case "ysize":
                    ySize = Parsers.toPixels(value);
                    break;

                case "item":
                    item = ItemFactory.itemFromString(game, game.getPlayer(), value.replace('(', '[').replace(')', ']'));
                    break;

                case "rotation":
                    rotation = Parsers.toInt(value);
                    break;

                default:
                    throw new GdxRuntimeException(String.format("Error parsing object args: Undefined value %s", arg.getKey()));
            }
        }

        if (item == null) throw new GdxRuntimeException("An 'item' tag must be defined for objects of type DroppedItem.");
        DroppedItem res = new DroppedItem(game, 0, 0, xSize, ySize, item);
        res.setRotation(rotation);
        return res;
    }

    /**
     * Creates a Bill.
     * @param name  Bill name
     * @param args  Arguments provided to the map loader
     * @return      Created Bill
     */
    public Bill createBill(String name, HashMap<String, String> args) {
        int xSize = KillBillGame.GRID_SIZE;
        int ySize = KillBillGame.GRID_SIZE;
        ArrayList<EntityAttributes> attrs = new ArrayList<EntityAttributes>();
        String currentAttr;
        String lines = null;
        int rotation = 0;

        for (HashMap.Entry<String, String> arg : args.entrySet()) {
            String value = arg.getValue();

            switch (arg.getKey().toLowerCase()) {
                case "width":
                case "xsize":
                    xSize = Parsers.toPixels(value);
                    break;
                
                case "height":
                case "ysize":
                    ySize = Parsers.toPixels(value);
                    break;

                case "attrs":
                case "attributes":
                    String[] attrsStr = value.split("\\&");

                    for (int i = 0; i < attrsStr.length; i++) {
                        currentAttr = attrsStr[i].strip();

                        if (currentAttr.length() == 0) continue;

                        attrs.add(Parsers.toEntityAttributes(currentAttr));
                    }
                    break;

                case "lines":
                    lines = "lines/" + value + ".txt";
                    break;

                case "rotation":
                    rotation = Parsers.toInt(value);
                    break;

                default:
                    throw new GdxRuntimeException(String.format("Error parsing object args: Undefined value %s", arg.getKey()));
            }
        }

        Bill bill = new Bill(game, 0, 0, xSize, ySize);
        bill.setRotation(rotation);
        if (lines != null) {
            bill.readVoiceLines(lines);
        }
        for (int i = 0; i < attrs.size(); i++) if (!bill.hasAttr(attrs.get(i))) bill.setAttr(attrs.get(i));
        bill.setName(name);
        return bill;
    }
}
