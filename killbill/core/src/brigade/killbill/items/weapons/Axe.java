package brigade.killbill.items.weapons;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Weapon;
import brigade.killbill.items.WeaponType;
import brigade.killbill.map.MapObject;

public class Axe extends Weapon {
    public static final int DAMAGE = 3;
    public static final int REACH = 2 * KillBillGame.GRID_SIZE;
    public static final int COOLDOWN = 1500;

    public Axe(KillBillGame game, MapObject user, int xSize, int ySize) {
        super(game, WeaponType.AXE, user, xSize, ySize, game.textureStore.getTexture("weapons_axe_held"), game.textureStore.getTexture("weapons_axe_held"), COOLDOWN, REACH);
        setName("Axe");
    }

    @Override
    public void use() {
        // Melee weapon
        doMeleeAttack(DAMAGE);
    }
}
