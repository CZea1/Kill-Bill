package brigade.killbill.items.weapons;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Weapon;
import brigade.killbill.items.WeaponType;
import brigade.killbill.map.MapObject;
import brigade.killbill.objects.Bomb;
import brigade.killbill.player.Player;

public class Computer extends Weapon {
    public static final int DAMAGE = 10;
    public static final int REACH = 6;
    public static final int COOLDOWN = 2000;

    //THIS WILL BE A BOMB
    private int xSize;
    private int ySize;
    private KillBillGame game;
    private MapObject user;

    public Computer(KillBillGame game, MapObject user, int xSize, int ySize) {
        super(game, WeaponType.PLUSHIE, user, xSize, ySize, game.textureStore.getTexture("weapons_computer_bomb"), game.textureStore.getTexture("weapons_computer_bomb"), COOLDOWN, REACH);
        this.game = game;
        this.user = user;
        this.xSize = xSize;
        this.ySize = ySize;
        setName("Computer");
    }

    @Override
    public void use() {
        Bomb bomb = new Bomb(game, game.player.getXCenter(), game.player.getYCenter(), xSize, ySize, game.textureStore.getTexture("weapons_computer_bomb"), game.textureStore.getTexture("weapons_computer_bomb_lit"), 2000, REACH, DAMAGE);
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
