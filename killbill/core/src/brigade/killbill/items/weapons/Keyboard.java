package brigade.killbill.items.weapons;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Weapon;
import brigade.killbill.items.WeaponType;
import brigade.killbill.map.MapObject;

public class Keyboard extends Weapon {
    public static final int DAMAGE = 1;
    public static final int REACH = 2 * KillBillGame.GRID_SIZE;
    public static final int COOLDOWN = 1000;

    public Keyboard(KillBillGame game, MapObject user, int xSize, int ySize) {
        super(game, WeaponType.KEYBOARD, user, xSize, ySize, game.textureStore.getTexture("weapons_keyboard"), game.textureStore.getTexture("weapons_keyboard"), COOLDOWN, REACH);
        setName("Keyboard");
    }

    @Override
    public void use() {
        // Melee weapon
        doMeleeAttack(DAMAGE);
    }
}
