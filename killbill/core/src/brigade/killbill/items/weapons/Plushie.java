package brigade.killbill.items.weapons;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Weapon;
import brigade.killbill.items.WeaponType;
import brigade.killbill.map.MapObject;
import brigade.killbill.objects.Bomb;
import brigade.killbill.player.Player;

public class Plushie extends Weapon {
    public static final int DAMAGE = 5;
    public static final int REACH = 2 * KillBillGame.GRID_SIZE;
    public static final int COOLDOWN = 2000;

    //THIS WILL BE A BOMB
    private int xSize;
    private int ySize;
    private KillBillGame game;
    private MapObject user;

    public Plushie(KillBillGame game, MapObject user, int xSize, int ySize) {
        super(game, WeaponType.PLUSHIE, user, xSize, ySize, game.textureStore.getTexture("weapons_plushie_held"), game.textureStore.getTexture("weapons_plushie_held"), COOLDOWN, REACH);
        this.game = game;
        this.user = user;
        this.xSize = xSize;
        this.ySize = ySize;
        setName("Plushie");
    }

    @Override
    public void use() {
        Bomb bomb = new Bomb(game, user.getX() + xSize / 2, user.getY() + ySize / 2, xSize, ySize, game.textureStore.getTexture("weapons_plushie_held"), game.textureStore.getTexture("weapons_plushie_bomb"), 2000, 5, 500);
        game.getMapScreen().map.addObject(bomb);
        bomb.start();

        // Remove from inventory
        if (user instanceof Player) {
            Player player = (Player) user;
            player.inventory.removeItem(this);
            player.setItem(player.inventory.getCurrentItem());
        }
    }
    
}
