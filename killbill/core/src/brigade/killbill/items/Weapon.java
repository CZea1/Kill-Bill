package brigade.killbill.items;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.entities.EntityAttributes;
import brigade.killbill.entities.enemies.Bill;
import brigade.killbill.map.MapObject;
import brigade.killbill.objects.Projectile;
import brigade.killbill.player.EffectType;
import brigade.killbill.items.weapons.Axe;
import brigade.killbill.items.weapons.Spear;
import brigade.killbill.items.weapons.Hammer;
import brigade.killbill.items.weapons.Plushie;
import brigade.killbill.items.weapons.GreatSword;

public class Weapon extends Item {
    public static final WeaponType[] WEAPONS = {WeaponType.AXE, WeaponType.SPEAR, WeaponType.HAMMER, WeaponType.PLUSHIE, WeaponType.GREATSWORD};
    public static Random randomizer = new Random();

    /**
     * Picks a random weapon type.
     * @return  Type of weapon chosen
     */
    public static WeaponType chooseRandomWeapon() {
		return WEAPONS[randomizer.nextInt(WEAPONS.length)];
	}

    /**
     * Generates and returns a random weapon.
     * @param game      Parent Game object
     * @param user      User to attach weapon to
     * @param xSize     Width
     * @param ySize     Height
     * @return          Generated Weapon object
     */
    public static Weapon randomWeapon(KillBillGame game, MapObject user, int xSize, int ySize) {
        WeaponType type = chooseRandomWeapon();
        switch (type) {
            case AXE:
                return new Axe(game, user, xSize, ySize);
            case SPEAR:
                return new Spear(game, user, xSize, ySize);
            case HAMMER:
                return new Hammer(game, user, xSize, ySize);
            case PLUSHIE:
                return new Plushie(game, user, xSize, ySize);
            case GREATSWORD:
                return new GreatSword(game, user, xSize, ySize);
            default:
                throw new GdxRuntimeException(String.format("Weapon type " + type + " not found."));
        }
    }
    
    /**
     * Parent Game object
     */
    private KillBillGame game;

    /**
     * Type of this weapon
     */
    private WeaponType type;

    /**
     * Cooldown between hits (ms)
     */
    private int cooldown;

    /**
     * Current cooldown timer
     */
    private long cooldownTime;

    /**
     * Reach of this weapon (in tiles)
     */
    private int reach;

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
    public Weapon(KillBillGame game, WeaponType type, MapObject user, int xSize, int ySize, Texture uiTexture, Texture heldTexture, int cooldown, int reach) {
        super(game, user, xSize, ySize, uiTexture, heldTexture);
        this.reach = reach;
        this.game = game;
        this.cooldown = cooldown;
        this.type = type;
        this.cooldownTime = -1;
    }

    /**
     * Gets the type of this weapon.
     * @return  Weapon type
     */
    public WeaponType getType() {
        return type;
    }

    /**
     * Performs a melee attack with the specified damage.
     * @param damage        Damage to apply
     */
    public void doMeleeAttack(int damage) {
        // Check cooldown
        if (cooldownTime != -1) {
            if (cooldownTime < TimeUtils.millis()) cooldownTime = -1;
            else return;
        }

        boolean doInstakill = game.getPlayer().hasEffect(EffectType.INSTA_KILL);

        // Check if player is in range of anyone
        game.getMapScreen().map.forEach( (object) -> {
            if (!(object instanceof Entity)) return;

            Entity currentEntity = (Entity) object;
            if (getUser().canInteract(object, KillBillGame.ATTACK_ANGLES, (int) reach)) {
                if (currentEntity.hasAttr(EntityAttributes.INVINCIBLE)) return;
                if (!currentEntity.canSee(game.player)) return;
                // Do damage to that entity
                if (doInstakill && !(currentEntity instanceof Bill)) currentEntity.doDamage(currentEntity.getHealth() + 1);
                else currentEntity.doDamage(damage);

                currentEntity.removeIfDead(game.getMapScreen().map);
            }
        });

        // Cooldown
        cooldownTime = TimeUtils.millis() + cooldown;
    }

    public Rectangle getOrigin() {
        // Location of this object is at 45 degrees behind the user's angle.
        // Using some math to figure out where that is.
        float x0 = getUser().getXCenter(), y0 = getUser().getYCenter();

        int angle = getRotation() + 65;
        if (angle >= 360) angle -= 360;
        double angleRad = Math.toRadians((double) angle);

        float cosAngle = (float) Math.cos(angleRad);
        float sinAngle = (float) Math.sin(angleRad);
        // Equations:
        // x1 = x0 + (i * cosAngle)
        // y1 = y0 + (i * sinAngle)
        
        // Find the distance it needs to traverse using pythag.
        float distance = (float) Math.sqrt(Math.pow(getUser().getXSize() / 2, 2) + Math.pow(getUser().getYSize() / 2, 2));

        float x1 = x0 + (distance * cosAngle);
        float y1 = y0 + (distance * sinAngle);

        // This should be the center of the object.
        Rectangle rectangle = new Rectangle();
        rectangle.setSize(KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE);
        rectangle.setX(x1 - rectangle.getWidth() / 2);
        rectangle.setY(y1 - rectangle.getHeight() / 2);

        return rectangle;
    }

    public void launchProjectile(Texture texture, float speed, int damage, int splash, int rotation) {
        // Check cooldown
        if (cooldownTime != -1) {
            if (cooldownTime < TimeUtils.millis()) cooldownTime = -1;
            else return;
        }

        game.soundStore.pickOne("main_launcher").play(game.getVolume());

        Rectangle origin = getOrigin();

        Projectile projectile = new Projectile(game, origin.getX(), origin.getY(), KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE, texture, speed, damage, splash, rotation);
        projectile.addImmune(getUser());
        game.getScreen().map.addObject(projectile);
        cooldownTime = TimeUtils.millis() + cooldown;
    }

    /**
     * Gets time (in ms) at which cooldown ends
     * @return     Cooldown end time
     */
    public long getCooldownTime() {
        return cooldownTime;
    }

    /**
     * Gets the cooldown duration.
     * @return      Cooldown duration (in ms)
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * Begins the cooldown.
     */
    public void startCooldown() {
        cooldownTime = TimeUtils.millis() + cooldown;
    }

    public void stopCooldown() {
        cooldownTime = -1;
    }
}
