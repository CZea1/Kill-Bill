package brigade.killbill.items.weapons;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Weapon;
import brigade.killbill.items.WeaponType;
import brigade.killbill.map.MapObject;

public class GreatSword extends Weapon {
    public static final int DAMAGE = 7;
    public static final int REACH = 2 * KillBillGame.GRID_SIZE;
    public static final int COOLDOWN = 4000;

    public GreatSword(KillBillGame game, MapObject user, int xSize, int ySize) {
        super(game, WeaponType.AXE, user, xSize, ySize, game.textureStore.getTexture("weapons_greatsword_held"), game.textureStore.getTexture("weapons_greatsword_held"), COOLDOWN, REACH);
        setName("Great Sword");
    }

    @Override
    public void use() {
        // Melee weapon
        doMeleeAttack(DAMAGE);
    }
    
}
