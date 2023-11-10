package brigade.killbill.items.other;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;
import brigade.killbill.map.MapObject;
import brigade.killbill.player.Player;

/**
 * Potion which gives the player health.
 * @author csenneff
 */
public class Microsauce extends Item {
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Constructs a new Microsauce.
     * @param game          Parent Game object
     * @param user          User holding the item
     */
    public Microsauce(KillBillGame game, MapObject user) {
        super(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE, game.textureStore.getTexture("items_microsauce"), game.textureStore.getTexture("items_microsauce"));
        this.game = game;
        setName("Microsauce");
    }

    @Override
    public void use() {
        // Add health to the user
        game.getPlayer().setHealth(game.getPlayer().getHealth() + 1);
        //game.getPlayer().setInvincible(Player.HIT_TIME);

        // Remove the object
        game.getPlayer().inventory.removeItem(this);
        game.getPlayer().updateItem();
    }
}
