package brigade.killbill.screens;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;
import brigade.killbill.ui.elements.Button;
import brigade.killbill.ui.elements.DialogPopup;

/**
 * Renders the intro to the screen.
 * @author csenneff
 */
public class IntroScreen extends GameScreen {
    /**
     * Various states the intro can be in.
     */
    private static enum IntroState {
        /**
         * Displaying the group logo
         */
        LOGO,

        /**
         * Telling the story
         */
        STORY,

        /**
         * Fading out
         */
        FADEOUT,

        /**
         * Self explanatory
         */
        DONE
    }

    /**
     * Represents a story line/sound.
     */
    private class StorySound {
        /**
         * Name of the sound
         */
        public String   soundName;

        /**
         * Sound object to play
         */
        public Sound    sound;

        /**
         * Text to render
         */
        public String   text;

        /**
         * Duration of the sound
         */
        public float    duration;
    }

    /**
     * Time to pause for in between lines
     */
    private final float LINE_PAUSE = 0.3f;

    /**
     * Parent Game object.
     */
    public KillBillGame game;

    /**
     * Current intro state
     */
    private IntroState state;

    /**
     * Current opacity
     */
    private int opacity;

    /**
     * Logo texture
     */
    private Texture logoTexture;

    /**
     * Sprite for the logo texture (allows opacity changes)
     */
    private Sprite logoSprite;

    /**
     * Timer used to calculate opacity
     */
    private int opacityTimer;
    
    /**
     * Current fade state (in, done, out)
     */
    private int fadeState;

    /**
     * Story lines
     */
    private ArrayList<StorySound> storySounds;

    /**
     * Current story line
     */
    private int soundIndex;

    /**
     * Time since storyline/fade started
     */
    private float currentTime;

    /**
     * Dialog box rendered with this story line
     */
    private DialogPopup dp;

    /**
     * Current sound playing
     */
    private StorySound currentSound;

    /**
     * Total duration of the intro for fading purposes
     */
    private float totalDuration;
    
    /**
     * Background texture during story
     */
    private Texture bgTexture;

    /**
     * Sprite representing background texture (for opacity)
     */
    private Sprite bgSprite;

    /**
     * Current sound ID, in case we need to cancel
     */
    private long currentId;

    /**
     * Skip button
     */
    private Button skipButton;

    /**
     * Constructs a new GameScreen.
     * @param game          Parent Game object.
     * @param name          A short string (use_snake_case) naming the screen.
     */
    public IntroScreen(KillBillGame game, String name) {
        super(game, name);
        this.game = game;
        
        state = IntroState.LOGO;
        opacity = 0;

        logoTexture = game.textureStore.getTexture("ui_intro_logo");
        int gridSize = Gdx.graphics.getWidth() / 25;
        int width = gridSize * 20;
        int height = gridSize * 5;
        logoSprite = new Sprite(logoTexture);
        logoSprite.setPosition(Gdx.graphics.getWidth() / 2 - width / 2, Gdx.graphics.getHeight() / 2 - height / 2);
        logoSprite.setSize(width, height);
        opacityTimer = 0;
        fadeState = 0;

        bgTexture = game.textureStore.getTexture("ui_story_bg");
        bgSprite = new Sprite(bgTexture);
        bgSprite.setPosition(0, 0);
        bgSprite.setSize(Gdx.graphics.getHeight(), Gdx.graphics.getHeight());
        width = gridSize * 2;
        height = gridSize / 2;
        skipButton = new Button(
            game, Gdx.graphics.getWidth() - width - gridSize, gridSize, width, height, 
            game.textureStore.getTexture("ui_skip_button"), game.textureStore.getTexture("ui_skip_button_pressed"),
            () -> {
                cancelAll();
            }
        );

        soundIndex = 0;
        currentId = -1;
        totalDuration = 0f;
        storySounds = new ArrayList<StorySound>();
        registerStory("data/story.txt");
    }

    /**
     * Skips the intro.
     */
    public void cancelAll() {
        state = IntroState.DONE;
        if (currentId != -1) {
            currentSound.sound.stop(currentId);
        }
        game.debug.setRender(true);
    }

    /**
     * Loads the story from the disk.
     * @param filename  Path to story file
     */
    private void registerStory(String filename) {
        File file = new File(filename);
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new GdxRuntimeException("[introScreen] Failed to read story from file " + filename + ".");
        }
        Scanner lineScanner;

        String line;
        StorySound s;
        StringBuilder builder = new StringBuilder("");
        while (scanner.hasNextLine()) {
            line = scanner.nextLine().strip();
            if (line.charAt(0) == '#') continue;
            lineScanner = new Scanner(line);

            // Format is:
            //      filename duration text
            s = new StorySound();

            s.soundName = lineScanner.next();
            s.sound = game.soundStore.getSound(s.soundName);
            s.duration = lineScanner.nextFloat();
            totalDuration += s.duration + LINE_PAUSE;
            while (lineScanner.hasNext()) {
                builder.append(" " + lineScanner.next());
            }

            s.text = builder.toString().strip();
            lineScanner.close();
            builder.setLength(0);
            storySounds.add(s);
        }
        scanner.close();
    }

    /**
     * Runs before anything else in the render cycle.
     * @param delta     Delta time
     */
    @Override
    public void beforeRender(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Runs while the secondary (fixed) batch is active.
     * @param delta     Delta time
     */
    @Override
    public void renderFixed(float delta) {
        switch (state) {
            case LOGO:
                // Draw the logo centered on the display
                // Size is 8x2 (128x32)
                // Adjust opacity depending on delta
                // We should fade in for 1s, show for 3s, fade out for 1s
                if (opacityTimer == 0 && fadeState == 0) {
                    // Just started -- pause the debug display
                    game.debug.setRender(false);

                    //Sound sound = game.soundStore.getSound("h");
                    //currentId = sound.play(0.8f);
                }

                opacityTimer += (int) (delta * 1000);

                switch (fadeState) {
                    case 0:
                        // Cap is 1000
                        if (opacityTimer >= 1000) {
                            // Set fadeState to 1
                            fadeState = 1;
                            opacity = 100;
                            opacityTimer = 0;
                        } else {
                            opacity = opacityTimer / 10;
                        }
                        break;

                    case 1:
                        // Cap is 3000
                        if (opacityTimer >= 2000) {
                            fadeState = 2;
                            opacityTimer = 0;
                        }
                        break;

                    case 2:
                        if (opacityTimer >= 1000) {
                            fadeState = 3;
                            opacityTimer = 0;
                        } else {
                            opacity = 100 - (opacityTimer / 10);
                        }
                        break;

                    case 3:
                        if (opacityTimer >= 1000) {
                            state = IntroState.STORY;
                            opacity = 0;
                            opacityTimer = 0;
                            fadeState = 0;

                            currentSound = storySounds.get(0);
                            dp = new DialogPopup(game, true, -1, Gdx.graphics.getHeight() / 4, 48, Gdx.graphics.getWidth() / 2, false, true, currentSound.text);
                            dp.setAnim((int) (1000 * (currentSound.duration - 0.5f)));
                            elementRenderer.addElement(dp);
                            currentId = storySounds.get(0).sound.play(0.8f);

                            // Add skip button
                            elementRenderer.addElement(skipButton);
                        }
                        break;
                }

                if (opacity > 100) opacity = 100;
                else if (opacity < 0) opacity = 0;

                logoSprite.setAlpha(opacity / 100f);
                logoSprite.draw(fixedBatch);
                break;

            case STORY:
                currentTime += delta;
                opacityTimer += (int) (delta * 1000);
                opacity = opacityTimer / ((int) (totalDuration * 10));
                if (opacity > 100) opacity = 100;

                bgSprite.setAlpha(opacity / 100f);
                bgSprite.draw(fixedBatch);

                if (currentTime >= currentSound.duration + LINE_PAUSE) {
                    // Move on to next sound
                    soundIndex++;
                    elementRenderer.removeElement(dp);

                    if (soundIndex >= storySounds.size()) {
                        elementRenderer.removeElement(skipButton);
                        try {
                            elementRenderer.removeElement(dp);
                        } catch (Exception e) {}
                        state = IntroState.FADEOUT;
                        opacityTimer = 2500;

                        return;
                    }

                    currentTime = 0;

                    // Play it
                    currentSound = storySounds.get(soundIndex);

                    dp = new DialogPopup(game, true, -1, Gdx.graphics.getHeight() / 4, 48, Gdx.graphics.getWidth() / 2, false, true, currentSound.text);
                    dp.setAnim((int) (1000 * (currentSound.duration - 0.5f)));
                    elementRenderer.addElement(dp);

                    currentId = currentSound.sound.play(0.8f);
                }
                break;

            case FADEOUT:
                opacityTimer -= (int) (delta * 1000);
                opacity = opacityTimer / 25;

                if (opacityTimer <= 0) {
                    state = IntroState.DONE;
                    return;
                }

                bgSprite.setAlpha(opacity / 100f);
                bgSprite.draw(fixedBatch);
                break;
                

            case DONE:
                game.setScreen(game.menuScreen);
                game.debug.setRender(true);
                break;
        }
    }

    /**
     * Runs after everything else in the render cycle.
     * @param delta     Delta time
     */
    @Override
    public void afterRender(float delta) {
    }
}