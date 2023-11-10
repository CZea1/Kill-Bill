package brigade.killbill.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.entities.EntityAttributes;
import brigade.killbill.map.Map;
import brigade.killbill.objects.Projectile;
import brigade.killbill.ui.elements.DialogPopup;

public class Bill extends Entity {
    /*
    shoots RAM sticks as projectiles, can make it more of a bullet hell than just shooting at the player
    
    if the player dies to the RAM sticks, you get the game over screen, and then when you click any form of continue 
    it give you a fake bluescreen with the QR code being whatever we want it to be.
    We can also adjust the RAM information in the debug screen to be mildly concerning

    Does something extra when gets to half health
    */

    public static final int HEALTH = 100;

    public static final int RAM_DAMAGE = 10;
    public static final int RAM_SPEED = 5 * KillBillGame.GRID_SIZE;
    public static final int RAM_SPLASH = 3;

    public static final int DELAY = 2000;
    public static final int SMALL_DELAY = 500;

    public static final int BIG_WIDTH = KillBillGame.GRID_SIZE * 3;

    public static final int SUMMON_TIME = 3000;
    public static final int SHOOT_TIME = 20000;
    public static final int SUMMON_DELAY = 1000;

    public static final int SHOCKWAVE_DURATION = 250;
    public static final int SHOCKWAVE_SPEED = 20 * KillBillGame.GRID_SIZE;

    private Texture bigBill;

    private Texture ramStickTexture;

    private int timer;

    private boolean isAngry;

    private enum BillStates {
        LOOK_AND_SHOOT,
        SUMMON,
        SHOCKWAVE
    }

    private BillStates state;

    private int stateTimer;

    private int startingRotation;

    private boolean doBsod;

    public Bill(KillBillGame game, int x, int y, int xSize, int ySize) {
        super(game, x, y, xSize, ySize, game.textureStore.getTexture("billy_small"), true, 1, HEALTH);

        //regularBill = game.textureStore.getTexture("bill_small");
        bigBill = game.textureStore.getTexture("billy_big");
        ramStickTexture = game.textureStore.getTexture("projectiles_ramstick");

        //readVoiceLines("lines/bill.txt");
        timer = 0;
        isAngry = false;
        state = BillStates.LOOK_AND_SHOOT;
        stateTimer = -1;
        doBsod = false;
    }

    @Override
    public void removeIfDead(Map map) {
        if (getHealth() <= 0) {
            doBsod = true;
        }
    }

    @Override
    public void afterRender(float delta) {
        if (doBsod) {
            long end = TimeUtils.millis() + 2000;
            while (TimeUtils.millis() < end) {
                
            }
            game.bsodScreen.bsod();
        }
    }

    @Override
    public void preRender(float delta) {
        if (hasAttr(EntityAttributes.INVINCIBLE)) {
            super.preRender(delta);
            return;
        }

        if (getHealth() > HEALTH / 2) {
            lookAtPlayer();
            timer -= (int) (1000 * delta);
    
            if (timer < 0) {
                Rectangle origin = getOrigin();
                Projectile proj = new Projectile(game, origin.getX(), origin.getY(), KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE, ramStickTexture, RAM_SPEED, RAM_DAMAGE, RAM_SPLASH, getRotation());
                proj.addImmune(this);
                game.getScreen().map.addObject(proj);
    
                timer = DELAY;
            }
        }
        else {
            if (!isAngry) {
                isAngry = true;
                resize(BIG_WIDTH, BIG_WIDTH);
                setLocation(getX() + KillBillGame.GRID_SIZE / 2 - BIG_WIDTH / 2, getY() + KillBillGame.GRID_SIZE / 2 - BIG_WIDTH / 2);
                setTexture(bigBill);
                stateTimer = SHOCKWAVE_DURATION;
                state = BillStates.SHOCKWAVE;
                setCollidable(false);

                lookAtPlayer();
                game.getPlayer().shockwave(SHOCKWAVE_DURATION, SHOCKWAVE_SPEED, (getRotation() + 180) % 360);
                
                DialogPopup dp = new DialogPopup(game, true, -1, Gdx.graphics.getHeight() / 4, 48, Gdx.graphics.getWidth() / 2, false, true, "YOU HAVE NO IDEA HOW POWERFUL I AM, MELINDA.");
                dp.setAnim(1000);
                dp.setSelfDestruct(5000);
                game.getScreen().elementRenderer.addElement(dp);
            }

            stateTimer -= (int) (1000 * delta);

            if (stateTimer < 0) {
                switch (state) {
                    case LOOK_AND_SHOOT:
                        game.soundStore.getSound("asjkhdvsauyv87").play(game.getVolume());
                        state = BillStates.SUMMON;
                        stateTimer = SUMMON_TIME;
                        startingRotation = getRotation();
                        break;

                    case SUMMON:
                        state = BillStates.LOOK_AND_SHOOT;
                        stateTimer = SHOOT_TIME;
                        break;

                    case SHOCKWAVE:
                        state = BillStates.SUMMON;
                        stateTimer = SUMMON_TIME;
                        startingRotation = getRotation();
                        setCollidable(true);
                        break;
                }
            } 

            switch (state) {
                case LOOK_AND_SHOOT:
                    lookAtPlayer();
                    timer -= (int) (1000 * delta);
        
                    if (timer < 0) {
                        Projectile proj = new Projectile(game, getXCenter(), getYCenter(), KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE, ramStickTexture, RAM_SPEED * 2, RAM_DAMAGE * 2, (int) (RAM_SPLASH * 1.5), getRotation() + (game.rand.nextInt(5) - 2));
                        proj.setDeceleration(1);
                        proj.addImmune(this);
                        game.getScreen().map.addObject(proj);
            
                        timer = SMALL_DELAY;
                    }
                    break;

                case SUMMON:
                    timer -= (int) (1000 * delta);

                    setRotation(startingRotation + (int) (360 * (1 - (float) stateTimer / SUMMON_TIME)));

                    if (timer < 0) {
                        Rectangle origin = getOrigin();
                        if (game.rand.nextInt(2) == 0) {
                            MicrosoftEmployee cronie = new MicrosoftEmployee(game, (int) origin.getX(), (int) origin.getY(), KillBillGame.GRID_SIZE, KillBillGame.GRID_SIZE);
                            cronie.setAttr(EntityAttributes.MOVE_TOWARDS_PLAYER);
                            game.getScreen().map.addObject(cronie);
                        } else {
                            SecurityRobot cronie = new SecurityRobot(game, (int) origin.getX(), (int) origin.getY(), KillBillGame.GRID_SIZE, KillBillGame.GRID_SIZE);
                            cronie.setAttr(EntityAttributes.MOVE_TOWARDS_PLAYER);
                            game.getScreen().map.addObject(cronie);
                        }
                        timer = SUMMON_DELAY;
                    }
                    break;

                case SHOCKWAVE:
                    break;
            }

            
        }


        super.preRender(delta);
    }

}
