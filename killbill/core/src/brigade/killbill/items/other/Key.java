package brigade.killbill.items.other;

import com.badlogic.gdx.graphics.Texture;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;
import brigade.killbill.map.MapObject;

public class Key extends Item {
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Door name which this key unlocks.
     */
    private String to;

    /**
     * Constructs a new Weapon.
     * @param game          Parent Game object
     * @param type          Type of this weapon
     * @param user          User holding the weapon
     * @param xSize         Width
     * @param ySize         Height
     * @param uiTexture     Texture rendered into UI
     * @param heldTexture   Texture rendered into hand
     * @param cooldown      Cooldown between hits (in ms)
     * @param reach         Reach (in tiles)
     */
    public Key(KillBillGame game, MapObject user, Texture texture, String to) {
        super(game, user, KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE, texture, texture);
        this.game = game;
        this.to = to;
        setName("Keycard");
    }

    public String getTo() {
        return to;
    }
}
