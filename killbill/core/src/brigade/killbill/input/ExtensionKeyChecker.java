package brigade.killbill.input;

import brigade.killbill.KillBillGame;
import brigade.killbill.misc.TimeKeeper;

/**
 * Extensible key checker for any actions we need to add later.
 * @author csenneff
 */
public class ExtensionKeyChecker {
    /**
     * Parent Game object
     */
    public KillBillGame game;

    /**
     * Timekeeper -- makes sure some events only run x times per second.
     */
    public TimeKeeper timer;

    /**
     * Enables/disables the timekeeper.
     */
    public Boolean timeKeeperEnabled;

    /**
     * Constructs a new KeyChecker.
     * Be sure to run super(game) in your constructor.
     * @param game      Parent Game object
     */
    public ExtensionKeyChecker(KillBillGame game) {
        this.game = game;
        this.timer = new TimeKeeper(game, 20);
        this.timeKeeperEnabled = false;
    }

    /**
     * Changes the timekeeper's cycles per second. Applies to checkKeys() function, if enabled.
     * @param cyclesPerSecond   New CPS to run at
     */
    public void setCyclesPerSecond(int cyclesPerSecond) {
        this.timer.setCyclesPerSecond(cyclesPerSecond);
    }

    /**
     * Runs all keychecks, passing it through the timekeeper first.
     * @param delta     Delta time
     */
    public void runKeyChecks(float delta) {
        if (!timeKeeperEnabled) {
            checkKeys(delta);
            return;
        }

        for (int i = 0; i < this.timer.deltaToRunCount(delta); i++) {
            checkKeys(delta);
        }
    }

    /**
     * Enables or disables the time keeper.
     * @param state     New state of the timekeeper -- true=enabled.
     */
    public void enableTimeKeeper(Boolean state) {
        this.timeKeeperEnabled = state;
    }

    /**
     * Override this function with your key checks.
     * Uses the timer system. By default, this will run 20 times per second.
     * @param delta     Time since last run
     */
    public void checkKeys(float delta) {}
}
