package brigade.killbill.entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.enemies.Bill;
import brigade.killbill.entities.enemies.MicrosoftEmployee;
import brigade.killbill.entities.enemies.Shareholder;
import brigade.killbill.map.Map;
import brigade.killbill.map.MapObject;
import brigade.killbill.misc.MiscUtils;
import brigade.killbill.ui.elements.DialogPopup;

/**
 * Base class representing an entity (enemy or other).
 * @author csenneff
 */
public class Entity extends MapObject {
    public static final float MIN_SOUND_DELAY = 5.0f;
    public static final float SOUND_PROB = 0.01f;

    public class VoiceLine {
        public boolean isAction;
        public String action;
        public String soundName;
        public Sound sound;
        public float duration;
        public String text;
    }

    /**
     * Speed to move at
     */
    private int speed;

    /**
     * Parent Game object
     */
    public KillBillGame game;

    /**
     * Current health of the entity
     */
    private int health;

    /**
     * Max health (determined by the initial health amount)
     */
    private int maxHealth;

    /**
     * Invincibility/flash countdown since last hit
     */
    private int hitTimer;

    /**
     * Stores whether or not the object was hit recently
     */
    private boolean isHit;

    protected boolean walking;

    /**
     * All EntityAttributes applied to this entity
     */
    private ArrayList<EntityAttributes> attributes;

    /**
     * Textures and sprites holding images which are used to generate the health bar
     */
    private Texture colorBlock;
    private Sprite healthBarBg;
    private Sprite healthBar;

    private ArrayList<VoiceLine> lines; 
    private boolean linesSpoken;
    private boolean hasLines;
    private int lineIndex;
    private float timer;
    private VoiceLine currentLine;
    private DialogPopup dp;
    private long currentSound;

    private String soundPrefix;
    private float lastSound;

    /**
     * Constructs a new Entity.
     * @param game          Parent Game object
     * @param x             Starting X location (pixels, scaled)
     * @param y             Starting Y location (pixels, scaled)
     * @param xSize         Width
     * @param ySize         Height
     * @param texture       Texture to render with
     * @param collidable    Whether the entity can be collided with
     * @param speed         Speed to move at
     */
    public Entity(KillBillGame game, int x, int y, int xSize, int ySize, Texture texture, Boolean collidable, int speed, int health) {
        super(game, x, y, xSize, ySize, texture, collidable);
        setSolid(false);
        this.game = game;
        this.health = health;
        this.maxHealth = health;

        this.hitTimer = 0;
        this.isHit = false;
        this.speed = speed;
        this.attributes = new ArrayList<EntityAttributes>();

        this.colorBlock = game.textureStore.getTexture("ui_color_block");

        this.healthBarBg = new Sprite(colorBlock);
        healthBarBg.setColor(0, 0, 0, 1);
        this.healthBar = new Sprite(colorBlock);
        this.walking = false;

        lines = new ArrayList<VoiceLine>();
        hasLines = false;
        linesSpoken = true;
        lineIndex = -1;
        timer = 0;
        soundPrefix = null;
        lastSound = -1;
    }

    public void setSound(String prefix) {
        soundPrefix = prefix;
    }

    public void readVoiceLines(String filename) {
        File file = new File(filename);
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new GdxRuntimeException("[entity] Failed to read voice lines from file " + filename + ".");
        }
        Scanner lineScanner;

        String line;
        VoiceLine vl;
        StringBuilder builder = new StringBuilder("");
        while (scanner.hasNextLine()) {
            line = scanner.nextLine().strip();
            if (line.charAt(0) == '#') continue;
            if (line.charAt(0) == '!') {
                vl = new VoiceLine();
                vl.isAction = true;
                vl.action = line.substring(1, line.length());
                lines.add(vl);
                continue;
            }
            lineScanner = new Scanner(line);

            // Format is:
            //      filename duration text
            vl = new VoiceLine();
            vl.isAction = false;

            vl.soundName = lineScanner.next();
            if (vl.soundName.equals("none")) {
                vl.soundName = null;
                vl.sound = null;
            } else {
                vl.sound = game.soundStore.getSound(vl.soundName);
            }

            float duration;
            String durationStr = lineScanner.next();
            try {
                duration = Float.parseFloat(durationStr);
            } catch (Exception e) {
                if (durationStr.equals("auto")) {
                    duration = -1;
                } else {
                    scanner.close();
                    lineScanner.close();
                    throw new GdxRuntimeException("[entity] Failed to read voice lines from file " + filename + ": Duration must be a time (float, seconds) or 'auto'");
                }
            }

            while (lineScanner.hasNext()) {
                builder.append(" " + lineScanner.next());
            }

            vl.text = builder.toString().strip();
            lineScanner.close();
            builder.setLength(0);

            // Check for auto
            if (MiscUtils.areFloatsEqual(duration, -1f)) {
                vl.duration = 0.025f * vl.text.length();
            } else {
                vl.duration = duration;
            }

            lines.add(vl);
        }
        scanner.close();
        hasLines = true;
        linesSpoken = false;
    }

    /**
     * Sets the entity's health. Don't use this to deal damage to the entity, since it
     * bypasses invincibility and doesn't animate.
     * @param health        Health to change to
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Adds to the entity's health.
     * @param health        Health to add
     */
    public void addHealth(int health) {
        this.health += health;
    }

    /**
     * Deals damage to the entity. Also gives it invincibility for 500ms, and flashes it red.
     * @param damage        Amount of hearts to remove (positive)
     */
    public void doDamage(int damage) {
        if (isHit) return; // Do nothing if hit recently

        if (soundPrefix != null) {
            game.soundStore.pickOne("main_damage").play(game.getVolume());
        }

        this.health -= damage;

        // Make enemy turn red
        this.hitTimer = 500;
        this.isHit = true;
        setColor(0.7f, 0.2f, 0.2f, 1f);
        removeIfDead(game.getScreen().map);
    }

    /**
     * Removes this object from a map if it's dead.
     * @param map   Map to remove from
     */
    public void removeIfDead(Map map) {
        if (health <= 0) {
            map.removeObject(this);
            onDeath();
        }
    }

    /**
     * Drops all items if everything is dead and plays death sound.
     * CALL THIS IF YOU'RE KILLING THIS ENEMY MANUALLY.
     * If you're using removeIfDead(), no need to do so.
     */
    public void onDeath() {
        if (this instanceof MicrosoftEmployee || this instanceof Bill || this instanceof Shareholder) {
            game.soundStore.getSound("main_death").play(game.getVolume());
        }

        // Check if all enemies are dead
        for (MapObject object: game.getScreen().map.getObjects()) if (object instanceof Entity) return;

        // All dead
        game.getScreen().map.dropAll((int) getX(), (int) getY());
    }

    /**
     * Gets the current health of the entity.
     * @return      Health
     */
    public int getHealth() {
        return health;
    }

    /**
     * Gets this entity's movement speed.
     * @return  Movement speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Whether or not this entity moved in the previous frame.
     * @return  Walking state
     */
    public boolean isWalking() {
        return walking;
    }

    /**
     * Makes this enemy look at the player.
     */
    public void lookAtPlayer() {
        // Get the player's location.
        float xDifference = game.getPlayer().getXCenter() - getXCenter();
        float yDifference = game.getPlayer().getYCenter() - getYCenter();

        // Account for 1st and 4th quadrants
        int factor = 1;
        if (game.getPlayer().getXCenter() > getXCenter()) {
            factor = -1;
        }

        // Use arctan() to find the angle.
        double angle = Math.atan(yDifference / xDifference);

        setRotation(((int) (angle * 180 / Math.PI) + 90) + (factor < 0 ? 180 : 0));
    }

    // My failed attempt at enemy AI
    /*public void moveTowardsPlayer(float delta) {
        if (game.paused || isHit || !isOnScreen() || !canSee(game.player)) {
            walking = false;
            return;
        }

        // Are we already moving towards a point? If so, go there
        if (currentPoint != null) {
            moveTowardsPoint(delta);
            return;
        }

        // Find best path
        List<GridCell> path = game.getMapScreen().map.getPath(getXTiled(), getYTiled(), game.player.getXTiled(), game.player.getYTiled());
        if (path.size() == 0) return;

        currentPoint = path.get(0);
        moveTowardsPoint(delta);
    }

    private void moveTowardsPoint(float delta) {
        float targetX = currentPoint.x;
        float targetY = currentPoint.y;

        // Get direction between here and point
        float xDifference = getXCenter() - targetX * KillBillGame.GRID_SIZE + KillBillGame.GRID_SIZE / 2;
        float yDifference = getYCenter() - targetY * KillBillGame.GRID_SIZE + KillBillGame.GRID_SIZE / 2;

        // Use arctan() to find the angle.
        double angle = Math.atan2(yDifference, xDifference);
        
        // Find the base speed. Shoot for 1:1 movement at 60fps -- if lower, account for that.
        float baseSpeed = delta / (1f / 60f) * speed;

        // Use that angle to calculate scaled versions of X and Y.
        // We know that the hypotenuse will be speed -- and we need X and Y.
        float yDelta = (speed * (float) Math.sin(angle)) * baseSpeed * -1;
        float xDelta = (speed * (float) Math.cos(angle)) * baseSpeed * -1;

        game.debug.setValue("enmAI", String.format("At: %d %d, to: %d %d (dx: %.1f, dy: %.1f)", getXTiled(), getYTiled(), currentPoint.x, currentPoint.y, xDifference, yDifference));
        System.out.println(String.format("At: %d %d, to: %d %d (dx: %.1f, dy: %.1f)", getXTiled(), getYTiled(), currentPoint.x, currentPoint.y, xDifference, yDifference));

        float oldX = getX();
        float oldY = getY();

        translate(xDelta, yDelta);
        
        setRotation(((int) (angle * 180 / Math.PI) + 90));
        if (Math.abs(oldX - getX()) > 0.1f || Math.abs(oldY - getY()) > 0.1f) walking = true;

        if (Math.abs(getXTiled() - currentPoint.x) <= 1 && Math.abs(getYTiled() - currentPoint.y) <= 1) currentPoint = null;
    }*/

    /**
     * Override this class with whatever actions you want the entity to take.
     */
    public void moveTowardsPlayer(float delta) {
        if (game.paused || isHit || !isOnScreen() || !canSee(game.player)) {
            walking = false;
            return;
        }

        float currentX = getX();
        float currentY = getY();

        float oldX = getX();
        float oldY = getY();

        // Find the base speed. Shoot for 1:1 movement at 60fps -- if lower, account for that.
        float baseSpeed = delta / (1f / 60f) * speed;

        // Get the player's location.
        float xDifference = game.getPlayer().getX() - currentX;
        float yDifference = game.getPlayer().getY() - currentY;

        // Account for 1st and 4th quadrants
        int factor = 1;
        if (game.getPlayer().getX() > currentX) {
            factor = -1;
        }

        // Use arctan() to find the angle.
        double angle = Math.atan(yDifference / xDifference);

        // Use that angle to calculate scaled versions of X and Y.
        // We know that the hypotenuse will be speed -- and we need X and Y.
        float yDelta = (speed * (float) Math.sin(angle)) * factor * -1 * baseSpeed;
        float xDelta = (speed * (float) Math.cos(angle)) * factor * -1 * baseSpeed;

        // Move the entity.
        translate(xDelta, yDelta);

        //Spinny go BURRRR
        //setRotation((int)(angle*2*360/Math.PI));
        setRotation(((int) (angle * 180 / Math.PI) + 90) + (factor < 0 ? 180 : 0));
        //game.debug.setValue(getName(), String.format("%s: rot=%d, loc=%.1f, %.1f", getName(), ((int) (angle * 180/Math.PI) + 90) + (factor < 0 ? 180 : 0), getX(), getY()));

        // Set movement status
        if (Math.abs(oldX - getX()) > 0.1f || Math.abs(oldY - getY()) > 0.1f) walking = true;
    }

    /**
     * Manages pre-render tasks. DO NOT OVERRIDE. Use preRenderExt() instead.
     */
    @Override
    public void preRender(float delta) {
        if (hasAttr(EntityAttributes.MOVE_TOWARDS_PLAYER)) {
            moveTowardsPlayer(delta);
        }
        preRenderExt(delta);

        // Change texture back if done
        if (isHit) {
            hitTimer -= (int) (delta * 1000);

            if (hitTimer <= 0) {
                setColor(1, 1, 1, 1);
                isHit = false;
            }
        }

        // Check for voice lines
        if (hasLines && !linesSpoken) {
            // Get distance from player
            double distance = Math.sqrt(Math.pow(getX() - game.player.getX(), 2) + Math.pow(getY() - game.player.getY(), 2));

            if (distance < 5 * KillBillGame.GRID_SIZE) {
                linesSpoken = true;

                // Start speaking
                lineIndex = 0;
                timer = 0;
                currentLine = lines.get(lineIndex);
                doAction();
                dp = new DialogPopup(game, true, -1, Gdx.graphics.getHeight() / 4, 48, Gdx.graphics.getWidth() / 2, false, true, currentLine.text);
                dp.setAnim((int) (1000 * currentLine.duration));
                dp.setSelfDestruct((int) (currentLine.duration * 1000) + 1001);
                game.getScreen().elementRenderer.addElement(dp);

                if (currentLine.sound != null) {
                    currentSound = currentLine.sound.play();
                }
            }
        }

        if (linesSpoken && lineIndex != -1) {
            timer += delta;

            if (timer > currentLine.duration + 1f) {
                skipVoiceLine();
            }   
        }

        // Play sounds
        if (soundPrefix != null) {
            lastSound -= delta;

            if (lastSound < 0) {
                int randChoice = game.rand.nextInt(100) + 1;
                int targetChoice = (int) (SOUND_PROB * 100);

                if (randChoice == targetChoice) {
                    game.soundStore.pickOne(soundPrefix).play(game.getVolume());
                    lastSound = 5f;
                }
            }
        }
    }

    public void skipVoiceLine() {
        if (linesSpoken && lineIndex != -1) {
            timer = 0;
            // Change popup
            if (dp != null)
                game.getScreen().elementRenderer.removeElement(dp);

            lineIndex++;
            if (lineIndex >= lines.size()) {
                lineIndex = -1;
            } else {
                currentLine = lines.get(lineIndex);
                doAction();
                if (currentLine == null) return;
                dp = new DialogPopup(game, true, -1, Gdx.graphics.getHeight() / 4, 48, Gdx.graphics.getWidth() / 2, false, true, currentLine.text);
                dp.setAnim((int) (1000 * currentLine.duration));
                dp.setSelfDestruct((int) (currentLine.duration * 1000) + 1001);
                game.getScreen().elementRenderer.addElement(dp);

                if (currentLine.sound != null) {
                    currentSound = currentLine.sound.play();
                }
            }
        }
    }

    private void doAction() {
        if (currentLine == null) return;
        if (currentLine.isAction) {
            ActionRunner.runAction(game, this, currentLine.action);
            lineIndex++;
            if (lineIndex >= lines.size()) lineIndex = -1;
            if (lineIndex == -1) currentLine = null;
            else currentLine = lines.get(lineIndex);

            doAction();
        }
    }

    /**
     * Sets an EntityAttribute on this entity.
     * @param attr  Attribute to enable
     */
    public void setAttr(EntityAttributes attr) {
        if (!hasAttr(attr))
            this.attributes.add(attr);
    }

    /**
     * Removes a previously set EntityAttribute.
     * @param attr  Attribute to disable
     */
    public void unsetAttr(EntityAttributes attr) {
        this.attributes.remove(attr);
    }

    /**
     * Checks if an attribute is set.
     * @param attr  Attribute to check for
     * @return      Whether or not the entity has this attribute
     */
    public boolean hasAttr(EntityAttributes attr) {
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i) == attr) return true;
        }
        return false;
    }

    /**
     * Override with any tasks that need to be called before render.
     * @param delta
     */
    public void preRenderExt(float delta) {
        
    }

    /**
     * Handles rendering, after everything else -- makes it render on top.
     * @param delta     Delta time
     */
    @Override
    public void onRenderLast(float delta) {
        // Draw health bar
        // First, only draw if this entity is missing some health
        if (health < maxHealth) {
            // Ready to go
            // Width is grid size
            // Height is 1/8 of grid size

            int width = getXSize();
            int height = width / 8;

            // Y origin is just Y + height (some scale of grid size for spacing)
            float yOrigin = getY() + getYSize() + KillBillGame.GRID_SIZE / 8f;

            // Stretch images to the correct size
            healthBarBg.setPosition(getX(), yOrigin);
            healthBarBg.setSize(width, height);

            float proportion = (float) health / maxHealth;
            int scale = (int) (proportion * width);

            // Now, scale the other one
            healthBar.setPosition(getX(), yOrigin);
            healthBar.setSize(scale, height);
            healthBar.setColor(1f * (1 - proportion), 1f * proportion, 0, 1);

            // Draw to batch
            healthBarBg.draw(game.getScreen().batch);
            healthBar.draw(game.getScreen().batch);
        }
    }

    public Rectangle getOrigin() {
        // Location of this object is at 45 degrees behind the user's angle.
        // Using some math to figure out where that is.
        float x0 = getXCenter(), y0 = getYCenter();

        int angle = getRotation() + 65;
        if (angle >= 360) angle -= 360;
        double angleRad = Math.toRadians((double) angle);

        float cosAngle = (float) Math.cos(angleRad);
        float sinAngle = (float) Math.sin(angleRad);
        // Equations:
        // x1 = x0 + (i * cosAngle)
        // y1 = y0 + (i * sinAngle)
        
        // Find the distance it needs to traverse using pythag.
        float distance = (float) Math.sqrt(Math.pow(getXSize() / 2, 2) + Math.pow(getYSize() / 2, 2));

        float x1 = x0 + (distance * cosAngle);
        float y1 = y0 + (distance * sinAngle);

        // This should be the center of the object.
        Rectangle rectangle = new Rectangle();
        rectangle.setSize(KillBillGame.ITEM_SIZE, KillBillGame.ITEM_SIZE);
        rectangle.setX(x1 - rectangle.getWidth() / 2);
        rectangle.setY(y1 - rectangle.getHeight() / 2);

        return rectangle;
    }
}
