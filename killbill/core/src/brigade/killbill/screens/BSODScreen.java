package brigade.killbill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting;

import brigade.killbill.KillBillGame;

public class BSODScreen extends GameScreen {
    public final static int DURATION_MS = 20000;

    /**
     * Parent Game object.
     */
    public KillBillGame game;

    private BitmapFont titleFont, infoFont, stopCodeFont;

    private Texture qr;
    private Texture sad;

    private int frownSize, titleSize, infoSize, stopCodeSize, lineSpacing;

    private int percentageTimer;
    private int percentage;

    /**
     * Constructs a new GameScreen.
     * @param game          Parent Game object.
     * @param name          A short string (use_snake_case) naming the screen.
     */
    public BSODScreen(KillBillGame game, String name) {
        super(game, name);
        this.game = game;

        qr = game.textureStore.getTexture("ui_qr");
        sad = game.textureStore.getTexture("ui_sad");
    }

    public void getFonts() {
        frownSize = Gdx.graphics.getHeight() / 6;
        titleSize = Gdx.graphics.getHeight() / 30;
        infoSize = Gdx.graphics.getHeight() / 55;
        stopCodeSize = Gdx.graphics.getHeight() / 70;

        lineSpacing = infoSize;

        FreeTypeFontParameter parameters;
        
        parameters = new FreeTypeFontParameter();
        parameters.color = Color.WHITE;
        parameters.size = titleSize;
        parameters.mono = false;
        parameters.hinting = Hinting.Full;
		titleFont = game.fontManager.getFont("segoe", parameters);
        
        parameters = new FreeTypeFontParameter();
        parameters.color = Color.WHITE;
        parameters.size = infoSize;
        parameters.mono = false;
        parameters.hinting = Hinting.Full;
		infoFont = game.fontManager.getFont("segoe", parameters);

        parameters = new FreeTypeFontParameter();
        parameters.color = Color.WHITE;
        parameters.size = stopCodeSize;
        parameters.mono = false;
        parameters.hinting = Hinting.Full;
		stopCodeFont = game.fontManager.getFont("segoe", parameters);
    }

    public void bsod() {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        getFonts();
        game.setScreen(this);
        percentageTimer = 1250;
        percentage = 0;
    }

    private void end() {
        Gdx.app.exit();
        System.exit(0);
    }

    /**
     * Runs before anything else in the render cycle.
     * @param delta     Delta time
     */
    @Override
    public void beforeRender(float delta) {
    }

    /**
     * Runs while the secondary (fixed) batch is active.
     * @param delta     Delta time
     */
    @Override
    public void renderFixed(float delta) {
    }

    /**
     * Runs after everything else in the render cycle.
     * @param delta     Delta time
     */
    @Override
    public void afterRender(float delta) {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.471f, 0.843f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Overriding the base render function to ensure the user can't quit.
        try {
            game.presence.core.runCallbacks();
        } catch (Exception e) {}
        updateCameraLocations();
        updateCameras();

        // FIXED BATCH
        fixedViewport.apply();
        fixedBatch.enableBlending();
        fixedBatch.begin();

        // Find our overall size.
        // Should be 5 lines of title, 1 frown, one info, 4 stopcode.
        int qrHeight = infoSize + 2 * stopCodeSize + 3 * lineSpacing;
        int overallHeight = frownSize + 5 * titleSize + 4 * lineSpacing + qrHeight;

        // Center that
        int y = Gdx.graphics.getHeight() / 2 + overallHeight / 2;
        int x = Gdx.graphics.getHeight() - y;

        // Start drawing
        int deltaMs = (int) (1000 * delta);

        // Check for percentage addition
        percentageTimer -= deltaMs;
        if (percentageTimer < 0) {
            if (percentage >= 100) {
                if (percentageTimer < -5000) {
                    fixedBatch.end();
                    end();
                    return;
                }
            }
            else {
                percentage += game.rand.nextInt(10) + 2;
                percentageTimer = game.rand.nextInt(2000);
    
                if (percentage > 100) percentage = 100;

            }
        }

        // Frown
        fixedBatch.draw(sad, x, y - frownSize, frownSize, frownSize);
        //frownFont.draw(fixedBatch, ":(", x, y);
        y -= frownSize + lineSpacing * 2;

        // Title
        titleFont.draw(fixedBatch, "Your PC ran into a problem and needs to restart. We're", x, y);
        y -= titleSize + lineSpacing;

        titleFont.draw(fixedBatch, "just collecting some error info, and then we'll restart for", x, y);
        y -= titleSize + lineSpacing;
        
        titleFont.draw(fixedBatch, "you.", x, y);
        y -= titleSize + lineSpacing * 3;

        titleFont.draw(fixedBatch, percentage + "% complete", x, y);
        y -= titleSize + lineSpacing * 3;

        // QR code
        fixedBatch.draw(qr, x, y - qrHeight, qrHeight, qrHeight);
        x += qrHeight + infoSize;

        infoFont.draw(fixedBatch, "For more information about this issue and possible fixes, visit https://tecktip.today", x, y);
        y -= infoSize * 2 + lineSpacing;

        stopCodeFont.draw(fixedBatch, "If you call a support person, give them this info:", x, y);
        y -= lineSpacing + lineSpacing / 2;
        stopCodeFont.draw(fixedBatch, "Stop code: BILL_DIED", x, y);

        fixedBatch.end();
    }
}
