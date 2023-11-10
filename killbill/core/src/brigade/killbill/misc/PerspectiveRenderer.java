package brigade.killbill.misc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import brigade.killbill.KillBillGame;
import brigade.killbill.map.Map;
import brigade.killbill.map.MapObject;

public class PerspectiveRenderer {
    private Texture stand;
    private Texture standHold;
    private Texture walk1;
    private Texture walk2;
    private Texture walk1Hold;
    private Texture walk2Hold;

    private KillBillGame game;

    private int timer;
    private int speed;
    private int msPerAnim;

    private boolean walkState;

    private Texture lastTexture;

    private MapObject target;

    public PerspectiveRenderer(
            KillBillGame game,
            Texture stand,
            Texture standHold,
            Texture walk1,
            Texture walk2,
            Texture walk1Hold,
            Texture walk2Hold,
            int speed,
            MapObject target
        ) {
        // this sucks
        this.stand = stand;
        this.standHold = standHold;
        this.walk1 = walk1;
        this.walk2 = walk2;
        this.walk1Hold = walk1Hold;
        this.walk2Hold = walk2Hold;

        this.game = game;

        this.speed = speed;
        this.timer = 0;
        this.msPerAnim = 250 / this.speed;
        this.walkState = false;

        this.target = target;
    }

    public void setSpeed(int newSpeed) {
        this.speed = newSpeed;
        this.msPerAnim = 1000 / this.speed;
    }

    public void changeTexture(float delta, boolean walking, boolean holding) {
        if (walking) {
            timer += (int) (1000 * delta);
    
            if (timer >= msPerAnim) {
                walkState = !walkState;
                timer = 0;
            }
        }

        Texture toUse;

        if (walking) {
            // Can be walk1, walk2, walk1hold, or walk2hold
            if (walkState) {
                toUse = holding ? walk1Hold : walk1;
            } else {
                toUse = holding ? walk2Hold : walk2;
            }
        } else {
            toUse = holding ? standHold : stand;
        }

        if (toUse != lastTexture) {
            lastTexture = toUse;
            target.setTexture(toUse);
        }
    }
}
