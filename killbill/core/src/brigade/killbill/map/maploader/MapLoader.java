package brigade.killbill.map.maploader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.entities.enemies.Bill;
import brigade.killbill.entities.enemies.MicrosoftEmployee;
import brigade.killbill.entities.enemies.SecurityRobot;
import brigade.killbill.entities.enemies.Shareholder;
import brigade.killbill.items.Item;
import brigade.killbill.map.Map;
import brigade.killbill.map.MapObject;
import brigade.killbill.map.Tile;
import brigade.killbill.misc.ActionSet;
import brigade.killbill.misc.Parsers;
import brigade.killbill.objects.Chest;
import brigade.killbill.objects.Door;
import brigade.killbill.objects.DroppedItem;

/**
 * Helper class which loads a map from a file.
 * @author csenneff
 */
public class MapLoader {
    /**
     * Parent Game object.
     */
    private KillBillGame game;

    /**
     * Object factory. (great comments today)
     */
    private ObjectFactory objectFactory;

    /**
     * Constructs a new MapLoader. Can be reused.
     * @param game  Parent game object
     */
    public MapLoader(KillBillGame game) {
        this.game = game;
        this.objectFactory = new ObjectFactory(game);
    }

    /**
     * Loads a file from a .map file into a Map object.
     * @param fileName      Path to .map file
     * @param map           Map to store loaded data into
     */
    public void loadTo(String fileName, Map map) {
        // Read the file, line by line
        String line;
        MapHeaders currentHeader = MapHeaders.NONE;
        int lineNum = 0;
        boolean sizeDefined = false;
        ArrayList<int[]> wallExcludes = new ArrayList<int[]>();

        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) {
                line = scanner.nextLine().strip();
                lineNum++;

                if (line.length() == 0) {
                    continue;
                } else if (line.startsWith("!")) {
                    // New header -- store it
                    String headerName = line.substring(1).strip().toLowerCase();

                    switch (headerName) {
                        case "name":
                            currentHeader = MapHeaders.NAME;
                            break;

                        case "level":
                            currentHeader = MapHeaders.LEVEL;
                            break;

                        case "size":
                            currentHeader = MapHeaders.SIZE;
                            break;

                        case "objects":
                            currentHeader = MapHeaders.OBJECTS;
                            break;

                        case "player_spawn":
                            currentHeader = MapHeaders.PLAYER_SPAWN;
                            break;

                        case "walls":
                            currentHeader = MapHeaders.WALLS;
                            if (!sizeDefined) {
                                throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Size must be defined before walls", fileName, lineNum));
                            }
                            break;

                        case "background":
                            currentHeader = MapHeaders.BACKGROUND;
                            break;

                        case "drops":
                            currentHeader = MapHeaders.DROPS;
                            break;

                        case "actions":
                            currentHeader = MapHeaders.ACTIONS;
                            break;

                        default:
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Header %s is not valid", fileName, lineNum, headerName));
                    }
                    continue;
                } else if (line.startsWith("#")) {
                    continue;
                } else if (line.contains("#")) {
                    line = line.substring(0, line.indexOf("#")).strip();
                }

                // This is data otherwise
                String[] args;
                switch (currentHeader) {
                    case NAME:
                        map.setName(line.strip());
                        currentHeader = MapHeaders.DONE;
                        break;

                    case LEVEL:
                        // Try to read this string into an int
                        int level;
                        try {
                            level = Integer.parseInt(line.strip());
                        } catch (NumberFormatException e) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Level ID is not an integer", fileName, lineNum));
                        }

                        map.setLevel(level);
                        currentHeader = MapHeaders.DONE;
                        break;

                    case SIZE:
                        // Try to read this string into two ints
                        int xSize, ySize;
                        if (!line.contains("x")) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Size must be of format __x__ (ie: 5x5), width x height", fileName, lineNum));
                        }

                        // Split the string at that delimeter
                        String[] sizes = line.split("x", 2);

                        if (sizes.length != 2) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Size must be of format __x__ (ie: 5x5), width x height", fileName, lineNum));
                        }

                        try {
                            xSize = Integer.parseInt(sizes[0].strip());
                            ySize = Integer.parseInt(sizes[1].strip());
                        } catch (NumberFormatException e) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Size must be of format __x__ (ie: 5x5), width x height", fileName, lineNum));
                        }
                        map.setXSize(xSize);
                        map.setYSize(ySize);
                        currentHeader = MapHeaders.DONE;
                        sizeDefined = true;
                        break;

                    case OBJECTS:
                        // Find location. Will be x,y
                        args = line.split(" ", 3);
                        if (args.length < 3) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Malformed object detected. Must be formatted 'x,y name Object[args]' (missing args)", fileName, lineNum));
                        }
                        String location = args[0];
                        String objectName = args[1];
                        String objectStr = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                        // Parse location
                        if (!location.contains(",")) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Malformed object detected. Must be formatted 'x,y Object[args]' (missing , in location)", fileName, lineNum));
                        }

                        // Get X and Y
                        String[] locationArgs = location.split("\\,", 2);

                        if (locationArgs.length != 2) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Invalid location. Must be formatted as 'x,y'.", fileName, lineNum));
                        }

                        int xLoc = Parsers.toPixels(locationArgs[0]);
                        int yLoc = Parsers.toPixels(locationArgs[1]);

                        if (!objectStr.contains("[") || !objectStr.contains("]")) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Malformed object detected. Object format is Type[args].", fileName, lineNum));
                        }

                        String[] objSplit = objectStr.split("\\[", 2);
                        String objType = objSplit[0];
                        String[] objArgs = objSplit[1].substring(0, objSplit[1].indexOf("]")).split(",");

                        // Turn these args into a HashMap
                        HashMap<String, String> argsMap = new HashMap<String, String>();
                        String arg;
                        for (int i = 0; i < objArgs.length; i++) {
                            arg = objArgs[i].strip();
                            if (arg.length() == 0) continue;
                            // Find equals, split there
                            if (!arg.contains("=")) {
                                throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Malformed object detected. Args must be key=value.", fileName, lineNum));
                            }
                            String[] argSplit = arg.split("\\=", 2);

                            argsMap.put(argSplit[0].strip(), argSplit[1].strip());
                        }

                        switch (objType) {
                            case "MapObject":
                                MapObject createdObject = objectFactory.createObject(objectName, argsMap);
                                createdObject.setLocation(xLoc, yLoc);
                                map.addObject(createdObject);
                                break;

                            case "Tile":
                                Tile createdTile = objectFactory.createTile(objectName, argsMap);
                                createdTile.setGridLocation(xLoc / KillBillGame.GRID_SIZE, yLoc / KillBillGame.GRID_SIZE);
                                map.addObject(createdTile);
                                break;

                            case "Entity":
                                Entity createdEntity = objectFactory.createEntity(objectName, argsMap);
                                createdEntity.setLocation(xLoc, yLoc);
                                map.addObject(createdEntity);
                                break;

                            case "MicrosoftEmployee":
                                MicrosoftEmployee createdEmployee = objectFactory.createMicrosoftEmployee(objectName, argsMap);
                                createdEmployee.setLocation(xLoc, yLoc);
                                map.addObject(createdEmployee);
                                break;

                            case "SecurityRobot":
                                SecurityRobot createdRobot = objectFactory.createSecurityRobot(objectName, argsMap);
                                createdRobot.setLocation(xLoc, yLoc);
                                map.addObject(createdRobot);
                                break;

                            case "Shareholder":
                                Shareholder createdShareholder = objectFactory.createShareholder(objectName, argsMap);
                                createdShareholder.setLocation(xLoc, yLoc);
                                map.addObject(createdShareholder);
                                break;

                            case "Door":
                                Door createdDoor = objectFactory.createDoor(objectName, argsMap);
                                createdDoor.setGridLocation(xLoc / KillBillGame.GRID_SIZE, yLoc / KillBillGame.GRID_SIZE);
                                map.addObject(createdDoor);
                                break;

                            case "Chest":
                                Chest createdChest = objectFactory.createChest(objectName, argsMap);
                                createdChest.setLocation(xLoc, yLoc);
                                map.addObject(createdChest);
                                break;

                            case "DroppedItem":
                                DroppedItem createdDroppedItem = objectFactory.createDroppedItem(objectName, argsMap);
                                createdDroppedItem.setLocation(xLoc, yLoc);
                                map.addObject(createdDroppedItem);
                                break;

                            case "Bill":
                                Bill createdBill = objectFactory.createBill(objectName, argsMap);
                                createdBill.setLocation(xLoc, yLoc);
                                map.addObject(createdBill);
                                break;

                            default:
                                throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Malformed object detected. Object type %s is not valid.", fileName, lineNum, objType));
                        }
                        break;

                    case PLAYER_SPAWN:
                        // Try to read this string into two ints
                        int xPlayerLoc, yPlayerLoc;
                        if (!line.contains(",")) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Player spawnpoint must be of format x,y (ie: 5,5)", fileName, lineNum));
                        }

                        // Split the string at that delimeter
                        String[] locs = line.split(",", 2);

                        if (locs.length != 2) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Player spawnpoint must be of format x,y (ie: 5,5)", fileName, lineNum));
                        }

                        try {
                            xPlayerLoc = Parsers.toPixels(locs[0].strip());
                            yPlayerLoc = Parsers.toPixels(locs[1].strip());
                        } catch (Exception e) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Player spawnpoint must be of format x,y (ie: 5,5)", fileName, lineNum));
                        }
                        map.setPlayerSpawnpoint(xPlayerLoc, yPlayerLoc);
                        currentHeader = MapHeaders.DONE;
                        break;

                    case WALLS:
                        if (!line.contains("=")) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Walls must be of format area=texture, each on separate lines (ie: tl=walls_tl, b=walls_b, etc.)", fileName, lineNum));
                        }

                        if (line.startsWith("exclude=")) {
                            String[] excludes = line.substring(line.indexOf("=") + 1, line.length()).split(" ");
                            String currentExclude;

                            for (int i = 0; i < excludes.length; i++) {
                                currentExclude = excludes[i];

                                if (!currentExclude.contains(",")) {
                                    throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Wall exclusions must be of format x,y (ie: 5,5), separated by spaces for multiple.", fileName, lineNum));
                                }

                                String[] excludeCoords = currentExclude.split(",", 2);

                                // Turn into ints
                                wallExcludes.add(new int[]{Parsers.toInt(excludeCoords[0]), Parsers.toInt(excludeCoords[1])});
                                continue;
                            }
                        }

                        String[] wallSplit = line.split("\\=", 2);
                        String wallDir = wallSplit[0].strip().toLowerCase();
                        String wallTexture = wallSplit[1].strip();
                        
                        int maxX = map.getXSize() - 1;
                        int maxY = map.getYSize() - 1;
                        switch (wallDir) {
                            case "tl":
                                addWallAt(map, 0, maxY, wallTexture, wallExcludes);
                                break;
                            case "bl":
                                addWallAt(map, 0, 0, wallTexture, wallExcludes);
                                break;
                            case "tr":
                                addWallAt(map, maxX, maxY, wallTexture, wallExcludes);
                                break;
                            case "br":
                                addWallAt(map, maxX, 0, wallTexture, wallExcludes);
                                break;
                            case "t":
                                for (int wallX = 1; wallX < maxX; wallX++) {
                                    addWallAt(map, wallX, maxY, wallTexture, wallExcludes);
                                }
                                break;
                            case "b":
                                for (int wallX = 1; wallX < maxX; wallX++) {
                                    addWallAt(map, wallX, 0, wallTexture, wallExcludes);
                                }
                                break;
                            case "l":
                                for (int wallY = 1; wallY < maxY; wallY++) {
                                    addWallAt(map, 0, wallY, wallTexture, wallExcludes);
                                }
                                break;
                            case "r":
                                for (int wallY = 1; wallY < maxY; wallY++) {
                                    addWallAt(map, maxX, wallY, wallTexture, wallExcludes);
                                }
                                break;
                        }
                        break;

                    case BACKGROUND:
                        // First arg is tile/not tile, second is image
                        String[] bgArgs = line.split(" ", 2);
                        if (bgArgs.length != 2) throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Background must be formatted '[tile/fit] [image]'", fileName, lineNum));

                        boolean isTiled = false;
                        switch (bgArgs[0]) {
                            case "tile":
                                isTiled = true;
                                break;
                            case "fit":
                                isTiled = false;
                                break;
                            default:
                                throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Background format must be 'tile' or 'fit'", fileName, lineNum));
                        }

                        map.setBackground(game.textureStore.getTexture(bgArgs[1]), isTiled);
                        currentHeader = MapHeaders.DONE;
                        break;

                    case DROPS:
                        if (!line.contains("[") || !line.contains("]")) {
                            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Malformed drop detected. Item format is Type[args].", fileName, lineNum));
                        }

                        String[] itemSplit = line.split("\\[", 2);
                        String itemType = itemSplit[0];
                        String[] itemArgs = itemSplit[1].substring(0, itemSplit[1].indexOf("]")).split(",");

                        // Turn these args into a HashMap
                        HashMap<String, String> itemArgsMap = new HashMap<String, String>();
                        String currentArg;
                        for (int i = 0; i < itemArgs.length; i++) {
                            currentArg = itemArgs[i].strip();
                            if (currentArg.length() == 0) continue;
                            // Find equals, split there
                            if (!currentArg.contains("=")) {
                                throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Malformed drop detected. Args must be key=value.", fileName, lineNum));
                            }
                            String[] argSplit = currentArg.split("\\=", 2);

                            itemArgsMap.put(argSplit[0].strip(), argSplit[1].strip());
                        }

                        Item resItem = ItemFactory.createItem(game, null, itemType, itemArgsMap);
                        map.addDrop(resItem);
                        break;

                    case ACTIONS:
                        if (map.getActionSet() == null) {
                            map.setActionSet(new ActionSet(game));
                        }
                        map.getActionSet().addAction(line);
                        break;

                    case NONE:
                        throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: Data was provided before a header", fileName, lineNum));

                    default:
                        break;
                }

                if (currentHeader == MapHeaders.DONE) {
                    currentHeader = MapHeaders.NONE;
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new GdxRuntimeException(String.format("Failed to read map file %s at line %d: File not found", fileName, lineNum));
        }
    }

    /**
     * Internal method to add a wall at a specific location, if it's not excluded.
     * @param map               Map to add to
     * @param x                 X tile
     * @param y                 Y tile
     * @param textureName       Wall texture name
     * @param wallExcludes      ArrayList of int[]{x, y} representing tiles to not place walls on
     */
    private void addWallAt(Map map, int x, int y, String textureName, ArrayList<int[]> wallExcludes) {
        // Ensure coords aren't excluded
        for (int i = 0; i < wallExcludes.size(); i++) {
            int[] coords = wallExcludes.get(i);

            if (x == coords[0] && y == coords[1]) return;
        }

        map.addObject(new Tile(this.game, x, y, KillBillGame.GRID_SIZE, game.textureStore.getTexture(textureName)));
    }

}
