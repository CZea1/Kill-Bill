package brigade.killbill.items.other;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;
import brigade.killbill.map.MapObject;
import brigade.killbill.player.EffectType;

/**
 * Potion which makes the player do insta-kills for a bit.
 * @author csenneff
 */
public class Bsoda extends Item {
    public static final int INSTA_KILL_TIME = 15000;
    
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Constructs a new Bsoda.
     * @param game          Parent Game object
     * @param user          User holding the item
     * @param uiTexture     Texture rendered into UI
     */
    public Bsoda(KillBillGame game, MapObject user) {
        super(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE, game.textureStore.getTexture("items_bsoda"), game.textureStore.getTexture("items_bsoda"));
        this.game = game;
        setName("BSODA");
    }

    @Override
    public void use() {
        // MURGER
        game.getPlayer().setEffect(EffectType.INSTA_KILL, INSTA_KILL_TIME);

        // Remove the object
        game.getPlayer().inventory.removeItem(this);
        game.getPlayer().updateItem();
    }
}
