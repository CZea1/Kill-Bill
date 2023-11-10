package brigade.killbill.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.entities.EntityAttributes;
import brigade.killbill.map.MapObject;
import brigade.killbill.player.Player;

public class Projectile extends MapObject {
    public float decel;
    public float speed;
    public boolean stopped;
    public int rotation;
    public boolean hasHit;

    public ArrayList<MapObject> immune;

    public Texture texture;

    public int splash;
    public int damage;

    public Projectile(KillBillGame game, float x, float y, int xSize, int ySize, Texture texture, float speed, int damage, int splash, int rotation) {
        super(game, x, y, xSize, ySize, texture, false);
        setSolid(false);

        stopped = false;
        decel = 0;
        hasHit = false;
        this.speed = speed;
        this.rotation = rotation;
        this.immune = new ArrayList<MapObject>();
        this.splash = splash;
        this.damage = damage;
        this.texture = texture;
    }

    /**
     * Makes an object immune to impact with this projectile.
     * @param obj
     */
    public void addImmune(MapObject obj) {
        immune.add(obj);
    }

    public void setDeceleration(float decel) {
        this.decel = decel;
    }

    public void setRange(int range) {
        // Calculate deceleration
        // v^2 = v0^2 + 2a(x - x0)
        // a = (v^2 - v0^2)/(2(x - x0))
        decel = (float) (-Math.pow(speed, 2) / (2 * range));
    }

    @Override
    public void preRender(float delta) {
        if (stopped) return;

        // Decelerate first
        if (speed > 0) speed += (decel * delta);
        
        if (speed < 0) {
            speed = 0;
            stopped = true;
            doHit();
            return;
        }

        // Move the object
        float currentX = getX();
        float currentY = getY();

        float yDelta = (speed * (float) Math.sin(Math.toRadians(rotation - 90))) * -1 * delta;
        float xDelta = (speed * (float) Math.cos(Math.toRadians(rotation - 90))) * -1 * delta;

        setLocation(currentX + xDelta, currentY + yDelta);
        boolean oldState = isCollidable();
        setCollidable(true);
        MapObject collidedWith = game.getMapScreen().map.getAnyCollision(this);
        setCollidable(oldState);
        if (collidedWith != null) {
            if (!immune.contains(collidedWith)) {
                // Call collision checks
                if (collidedWith instanceof Entity) {
                    Entity ent = (Entity) collidedWith;
                    if (ent.hasAttr(EntityAttributes.INVINCIBLE)) return;
                    ent.doDamage(damage);
                } else if (collidedWith instanceof Player) {
                    Player player = (Player) collidedWith;
                    player.doDamage(1);
                }

                doHit();
                collidedWith.onCollision(this);
            }
        }
        setRotation((int) (rotation + (rotation < 0 ? 180 : 0)));
    }

    public void doHit() {
        if (hasHit) return;
        hasHit = true;

        // Turn into a bomb
        Bomb bomb = new Bomb(game, getX(), getY(), getXSize(), getYSize(), texture, texture, 1, splash, damage);
        bomb.start();
        for (MapObject obj: immune)
            bomb.addImmune(obj);
        game.getScreen().map.addObject(bomb);
        game.getScreen().map.removeObject(this);
    }
}
