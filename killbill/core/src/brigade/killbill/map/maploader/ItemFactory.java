package brigade.killbill.map.maploader;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;
import brigade.killbill.items.other.Billyjuice;
import brigade.killbill.items.other.Bsoda;
import brigade.killbill.items.other.Key;
import brigade.killbill.items.other.Microsauce;
import brigade.killbill.items.other.Xpop;
import brigade.killbill.items.weapons.Axe;
import brigade.killbill.items.weapons.Computer;
import brigade.killbill.items.weapons.GreatSword;
import brigade.killbill.items.weapons.Hammer;
import brigade.killbill.items.weapons.Keyboard;
import brigade.killbill.items.weapons.PenguinLauncher;
import brigade.killbill.items.weapons.Plushie;
import brigade.killbill.items.weapons.Spear;
import brigade.killbill.map.MapObject;
import brigade.killbill.misc.Parsers;

public class ItemFactory {
    public static Item itemFromString(KillBillGame game, MapObject user, String input) {
        // Find item type
        HashMap<String, String> itemArgs;
        String itemType;
        if (!input.contains("[") || !input.contains("]"))  {
            itemArgs = new HashMap<String, String>();
            itemType = input;
        } else {
            String[] iArgs = input.split("\\[", 2);
            itemType = iArgs[0];
            String itemArgStr = iArgs[1].strip();
            itemArgs = Parsers.argsToHashMap(itemArgStr.substring(0, itemArgStr.length() - 1));
        }

        return createItem(game, user, itemType, itemArgs);
    } 

    public static Item createItem(KillBillGame game, MapObject user, String itemType, HashMap<String, String> args) {
        Item res;

        switch (itemType) {
            case "Item":
                res = generateItem(game, user, args);
                break;

            case "Key":
                res = generateKey(game, user, args);
                break;

            case "Axe":
                res = generateAxe(game, user, args);
                break;

            case "GreatSword":
                res = generateGreatSword(game, user, args);
                break;

            case "Hammer":
                res = generateHammer(game, user, args);
                break;

            case "Keyboard":
                res = generateKeyboard(game, user, args);
                break;

            case "PenguinLauncher":
                res = generatePenuginLauncher(game, user, args);
                break;

            case "Plushie":
                res = generatePlushie(game, user, args);
                break;

            case "Computer":
                res = generateComputer(game, user, args);
                break;

            case "Spear":
                res = generateSpear(game, user, args);
                break;

            case "Microsauce":
                res = generateMicrosauce(game, user, args);
                break;

            case "Billyjuice":
                res = generateBillyjuice(game, user, args);
                break;

            case "Xpop":
                res = generateXpop(game, user, args);
                break;

            case "Bsoda":
                res = generateBsoda(game, user, args);
                break;

            default:
                throw new GdxRuntimeException("Invalid item type " + itemType + ".");
        }

        return res;
    }

    private static Item generateItem(KillBillGame game, MapObject user, HashMap<String, String> args) {
        String texture = "default";

        for (HashMap.Entry<String, String> arg : args.entrySet()) {
            String value = arg.getValue();

            switch (arg.getKey().toLowerCase()) {
                case "texture":
                    texture = value;
                    break;

                default:
                    throw new GdxRuntimeException(String.format("Error parsing item args: Undefined value %s", arg.getKey()));
            }
        }
        Texture actualTexture = game.textureStore.getTexture(texture);

        Item item = new Item(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE, actualTexture, actualTexture);
        return item;
    }

    private static Item generateKey(KillBillGame game, MapObject user, HashMap<String, String> args) {
        String to = null;
        String texture = "items_key";

        for (HashMap.Entry<String, String> arg : args.entrySet()) {
            String value = arg.getValue();

            switch (arg.getKey().toLowerCase()) {
                case "to":
                    to = value;
                    break;

                case "texture":
                    texture = value;
                    break;

                default:
                    throw new GdxRuntimeException(String.format("Error parsing item args: Undefined value %s", arg.getKey()));
            }
        }

        if (to == null) {
            throw new GdxRuntimeException("Error parsing item args: 'to' must be defined for items of type Key");
        }

        Key key = new Key(game, game.getPlayer(), game.textureStore.getTexture(texture), to);
        return key;
    }

    private static Item generateAxe(KillBillGame game, MapObject user, HashMap<String, String> args) {
        return new Axe(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE);
    }

    private static Item generateGreatSword(KillBillGame game, MapObject user, HashMap<String, String> args) {
        return new GreatSword(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE);
    }

    private static Item generateHammer(KillBillGame game, MapObject user, HashMap<String, String> args) {
        return new Hammer(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE);
    }

    private static Item generateKeyboard(KillBillGame game, MapObject user, HashMap<String, String> args) {
        return new Keyboard(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE);
    }

    private static Item generatePenuginLauncher(KillBillGame game, MapObject user, HashMap<String, String> args) {
        return new PenguinLauncher(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE);
    }

    private static Item generatePlushie(KillBillGame game, MapObject user, HashMap<String, String> args) {
        return new Plushie(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE);
    }

    private static Item generateComputer(KillBillGame game, MapObject user, HashMap<String, String> args) {
        return new Computer(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE);
    }

    private static Item generateSpear(KillBillGame game, MapObject user, HashMap<String, String> args) {
        return new Spear(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE);
    }

    private static Item generateMicrosauce(KillBillGame game, MapObject user, HashMap<String, String> args) {
        return new Microsauce(game, user);
    }

    private static Item generateBillyjuice(KillBillGame game, MapObject user, HashMap<String, String> args) {
        return new Billyjuice(game, user);
    }

    private static Item generateXpop(KillBillGame game, MapObject user, HashMap<String, String> args) {
        return new Xpop(game, user);
    }

    private static Item generateBsoda(KillBillGame game, MapObject user, HashMap<String, String> args) {
        return new Bsoda(game, user);
    }
}
