package brigade.killbill.items.other;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;
import brigade.killbill.map.MapObject;
import brigade.killbill.player.EffectType;

/**
 * Potion which makes the player invincible for a bit.
 * @author csenneff
 */
public class Billyjuice extends Item {
    public static final int INVINCIBLE_TIME = 20000;
    
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Constructs a new Billyjuice.
     * @param game          Parent Game object
     * @param user          User holding the item
     * @param uiTexture     Texture rendered into UI
     */
    public Billyjuice(KillBillGame game, MapObject user) {
        super(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE, game.textureStore.getTexture("items_billyjuice"), game.textureStore.getTexture("items_billyjuice"));
        this.game = game;
        setName("Billyjuice");
    }

    @Override
    public void use() {
        // Add health to the user
        game.getPlayer().setEffect(EffectType.INVINCIBLE, INVINCIBLE_TIME);

        // Remove the object
        game.getPlayer().inventory.removeItem(this);
        game.getPlayer().updateItem();
    }
}
