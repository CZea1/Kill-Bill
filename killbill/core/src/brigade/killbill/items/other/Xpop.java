package brigade.killbill.items.other;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;
import brigade.killbill.map.MapObject;
import brigade.killbill.player.EffectType;

/**
 * Potion which makes the player fast for a bit.
 * @author csenneff
 */
public class Xpop extends Item {
    public static final int SPEED_TIME = 30000;
    
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Constructs a new Xpop.
     * @param game          Parent Game object
     * @param user          User holding the item
     */
    public Xpop(KillBillGame game, MapObject user) {
        super(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE, game.textureStore.getTexture("items_xpop"), game.textureStore.getTexture("items_xpop"));
        this.game = game;
        setName("XPop");
    }

    @Override
    public void use() {
        // FAST
        game.getPlayer().setEffect(EffectType.SPEED, SPEED_TIME);

        // Remove the object
        game.getPlayer().inventory.removeItem(this);
        game.getPlayer().updateItem();
    }
}
