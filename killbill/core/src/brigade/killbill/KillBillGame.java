package brigade.killbill;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;

import brigade.killbill.input.KeyChecker;
import brigade.killbill.input.PlayerKeyChecker;
import brigade.killbill.loottables.LootTables;
import brigade.killbill.map.MapStore;
import brigade.killbill.map.maploader.MapLoader;
import brigade.killbill.misc.Presence;
import brigade.killbill.player.Player;
import brigade.killbill.resources.FontManager;
import brigade.killbill.resources.SoundStore;
import brigade.killbill.resources.TextureStore;
import brigade.killbill.screens.BSODScreen;
import brigade.killbill.screens.DieScreen;
import brigade.killbill.screens.GameScreen;
import brigade.killbill.screens.IntroScreen;
import brigade.killbill.screens.MapScreen;
import brigade.killbill.screens.MenuScreen;
import brigade.killbill.screens.PauseScreen;
import brigade.killbill.ui.DebugDisplay;
import brigade.killbill.ui.EffectRenderer;
import brigade.killbill.ui.HealthRenderer;
import brigade.killbill.ui.InventoryRenderer;

/**
 * Main Game object. Contains references to all the necessary components of the game.
 * @author csenneff
 */
public class KillBillGame extends Game {
	/**
	 * Global game grid size.
	 */
	public static final int GRID_SIZE = 32;

	/**
	 * Global game item size (as rendered held in the hand).
	 */
	public static final int ITEM_SIZE = 16;

	/**
	 * Game version.
	 */
	public static final String VERSION = "0.0.1";

	/**
	 * Angles from which user can interact with something.
	 */
	public static final int[] INTERACTION_ANGLES = {25, 0, -25};

	/**
	 * Angles from which user can attack something.
	 */
	public static final int[] ATTACK_ANGLES = {60, 40, 20, 0, -20, -40, -60};

	/**
	 * Iterator size when checking for obstacles in the way of line of sight.
	 * Must be at most the size of your smallest blocking object.
	 */
    public static final int ITERATOR_SIZE = 16;

	/**
	 * Default map to spawn into.
	 */
	public static final String DEFAULT_MAP = "lobby_1";

	/**
	 * Key checker. Handles global keypresses (like Esc or F3).
	 */
	public KeyChecker keyChecker;

	/**
	 * Debug display. Renders debugging info after F3 is pressed.
	 */
	public DebugDisplay debug;

	/**
	 * Currently active screen.
	 */
	private GameScreen activeScreen;

	/**
	 * A testing screen.
	 */
	private MapScreen mapScreen;

	/**
	 * Menu screen.
	 */
	public MenuScreen menuScreen;

	/**
	 * Intro screen.
	 */
	public IntroScreen introScreen;

	/**
	 * Pause screen
	 */
	public PauseScreen pauseScreen;

	/**
	 * BSOD screen
	 */
	public BSODScreen bsodScreen;

	/**
	 * Death screen.
	 */
	public DieScreen death;

	/**
	 * The player object.
	 */
	public Player player;

	/**
	 * Texture loader.
	 */
	public TextureStore textureStore;

	/**
	 * Sound loader.
	 */
	public SoundStore soundStore;

	/**
	 * Font manager.
	 */
	public FontManager fontManager;

	/**
	 * Handles loading of maps from files.
	 */
	public MapLoader mapLoader;

	/**
	 * Registers and stores all map classes.
	 */
	public MapStore mapStore;

	/**
	 * Renders the inventory to the screen.
	 */
	public InventoryRenderer inventoryRenderer;
	
	/**
	 * Renders the player's health to the screen.
	 */
	public HealthRenderer healthRenderer;
	
	/**
	 * Renders the player's effects to the screen.
	 */
	public EffectRenderer effectRenderer;

	/**
	 * Renders any shapes to the screen.
	 */
	public ShapeRenderer shapeRenderer;

	/**
	 * Loot table loader
	 */
	public LootTables lootTables;

	/**
	 * Time at which the game was launched.
	 */
	public long startTime;

	/**
	 * Stores whether or not the game is paused.
	 */
	public boolean paused;

	private boolean externRender;

	private float volume;

	public Random rand;

	public Presence presence;

	/**
	 * Changes the active screen.
	 * @param screen	Screen to change to.
	 */
	public void setScreen(GameScreen screen) {
		super.setScreen(screen);
		this.activeScreen = screen;
	}

	/**
	 * Returns the active Player object.
	 * @return		Player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the current screen.
	 * @return 	Active screen.
	 */
	@Override
	public GameScreen getScreen() {
		return activeScreen;
	}

	/**
	 * Gets the map screen.
	 * @return		Map screen.
	 */
	public MapScreen getMapScreen() {
		return mapScreen;
	}
	
	@Override
	public void create() {
		// Info
		startTime = TimeUtils.millis();
		paused = false;

		externRender = true;
		volume = 0.5f;

		rand = new Random();

		// Texture store
		textureStore = new TextureStore();
		textureStore.registerFromDirectory("images/", "");

		// Sound store
		soundStore = new SoundStore();
		soundStore.registerFromDirectory("sounds/", "");

		// Font manager
		fontManager = new FontManager();
		fontManager.registerAllFonts("fonts/");

		// Player
		player = new Player(this, 0, 0, GRID_SIZE, GRID_SIZE, textureStore.getTexture("misc_testmonster"));

		// Key checker
		keyChecker = new KeyChecker(this);
		keyChecker.registerKeyChecker(new PlayerKeyChecker(this));

		// Loot tables
		lootTables = new LootTables(this);
		lootTables.registerAll("loottables/");

		// Maps
		mapLoader = new MapLoader(this);
		mapStore = new MapStore(this);
		mapStore.registerAllMaps("maps/");

		// Renderers
		debug = new DebugDisplay(this, false);
		inventoryRenderer = new InventoryRenderer(this);
		healthRenderer = new HealthRenderer(this);
		effectRenderer = new EffectRenderer(this);
		shapeRenderer = new ShapeRenderer();

		// Screens
		mapScreen = new MapScreen(this);
		menuScreen = new MenuScreen(this, "menu");
		introScreen = new IntroScreen(this, "intro");
		death = new DieScreen(this, "death");
		pauseScreen = new PauseScreen(this, "pause");
		bsodScreen = new BSODScreen(this, "bsod");
		setScreen(introScreen);

		try {
			presence = new Presence(this);
		} catch (Exception e) {
			System.err.println("Failed to register Discord rich presence. Ignoring.");
		}

		/////////// wh	
		//Pixmap pm = new Pixmap(Gdx.files.internal("images/ui/cursor.png"));
		//Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
		//bsodScreen.bsod();
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public void reset() {
		// Resets maps, player, etc...
		// Maps first
		mapStore.clearAllMaps();
		mapStore.registerAllMaps("maps/");

		// Player
		player = new Player(this, 0, 0, GRID_SIZE, GRID_SIZE, textureStore.getTexture("misc_testmonster"));

		// Set map
		mapScreen.setMap(DEFAULT_MAP);
	}

	public boolean getExternRender() {
		return externRender;
	}

	public void setExternRender(boolean state) {
		externRender = state;
	}

	@Override
	public void render() {
		super.render();
	}
	
	@Override
	public void dispose() { }
}
