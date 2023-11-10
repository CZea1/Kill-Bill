package brigade.killbill.items.weapons;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Weapon;
import brigade.killbill.items.WeaponType;
import brigade.killbill.map.MapObject;

public class Hammer extends Weapon {
    public static final int DAMAGE = 5;
    public static final int REACH = 2 * KillBillGame.GRID_SIZE;
    public static final int COOLDOWN = 3000;


    public Hammer(KillBillGame game, MapObject user, int xSize, int ySize) {
        super(game, WeaponType.AXE, user, xSize, ySize, game.textureStore.getTexture("weapons_hammer_held"), game.textureStore.getTexture("weapons_hammer_held"), COOLDOWN, REACH);
        setName("Hammer");
    }

    @Override
    public void use() {
        // Melee weapon
        doMeleeAttack(DAMAGE);
    }
    
}
