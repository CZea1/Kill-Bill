package brigade.killbill.loottables;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;
import brigade.killbill.map.maploader.ItemFactory;
import brigade.killbill.misc.Parsers;

public class LootTable {
    public static LootTable fromFile(KillBillGame game, String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner lineScanner = new Scanner(file);

        LootTable lt = new LootTable(game);

        String line;
        while (lineScanner.hasNextLine()) {
            line = lineScanner.nextLine().strip();
            
            if (line.contains("#")) {
                line = line.substring(0, line.indexOf("#")).strip();
            }

            if (line.length() == 0) continue;

            String[] args = line.split(" ", 2);
            if (args.length != 2) {
                lineScanner.close();
                throw new GdxRuntimeException("Failed to register loot table: Format is [probability/auto] [item]");
            }

            float probability;
            if (args[0].strip().toLowerCase().equals("auto")) {
                probability = -1;
            } else {
                probability = Float.parseFloat(args[0]);
            }

            lt.addItem(args[1], probability);
        }
        lineScanner.close();

        lt.assignAutoProbability();
        return lt;
    }

    public class LootTableItem {
        public String item;
        public float probability;
    }

    private ArrayList<LootTableItem> items;

    private KillBillGame game;

    private Random rand;

    private LootTable(KillBillGame game) {
        this.game = game;
        items = new ArrayList<LootTableItem>();
        rand = new Random();
    }

    public void addItem(String item, float probability) {
        LootTableItem ltItem = new LootTableItem();
        ltItem.item = item;
        ltItem.probability = probability;
        items.add(ltItem);
    }

    private void assignAutoProbability() {
        // Find sum of all items' probabilities
        float sum = 0;
        ArrayList<LootTableItem> autoItems = new ArrayList<LootTableItem>();

        for (LootTableItem item: items) {
            if (item.probability >= 0) sum += item.probability;
            else autoItems.add(item);
        }

        if (sum > 1.001f) throw new GdxRuntimeException("Failed to register loot table: Sum of all probabilities is greater than 1.0");

        if (autoItems.size() != 0) {
            float autoProb = (1f - sum) / autoItems.size();

            for (LootTableItem item: autoItems) {
                item.probability = autoProb;
            }
        }
    }

    public Item getItem() {
        // 0 - 99
        int choice = rand.nextInt(100);

        float prob = choice / 100f;

        float totalProb = 0f;
        String itemString = null;
        for (LootTableItem item: items) {
            totalProb += item.probability;
            if (prob <= totalProb) {
                itemString = item.item;
                break;
            } 
        }
        // Fallback: if it isn't exactly 1
        if (itemString == null) itemString = items.get(items.size() - 1).item;

        return ItemFactory.itemFromString(game, game.getPlayer(), itemString);
    }
}
