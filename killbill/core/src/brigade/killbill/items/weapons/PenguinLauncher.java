package brigade.killbill.items.weapons;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Weapon;
import brigade.killbill.items.WeaponType;
import brigade.killbill.map.MapObject;

public class PenguinLauncher extends Weapon {
    public static final int DAMAGE = 10;
    public static final int COOLDOWN = 5000;
    public static final int SPEED = 7 * KillBillGame.GRID_SIZE;
    public static final int SPLASH = 2;

    public PenguinLauncher(KillBillGame game, MapObject user, int xSize, int ySize) {
        super(game, WeaponType.AXE, user, xSize, ySize, game.textureStore.getTexture("weapons_penguin"), game.textureStore.getTexture("weapons_penguin"), COOLDOWN, 1);
        setName("Penguin Launcher");
    }

    @Override
    public void use() {
        launchProjectile(getHeldTexture(), SPEED, DAMAGE, SPLASH, getUser().getRotation());
    }
    
}
