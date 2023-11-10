package brigade.killbill.misc;

import brigade.killbill.KillBillGame;

/**
 * Class which keeps track of time to ensure crucial functions run exactly as much as they're supposed to.
 * @author csenneff
 */
public class TimeKeeper {
    /**
     * Parent Game object.
     */
    private KillBillGame game;

    /**
     * Cycles to run per second.
     */
    private int cyclesPerSecond;
    
    /**
     * Last cycle index that was called.
     */
    private int lastCycle;

    /**
     * Calculated miliseconds in between cycles.
     */
    private int msPerCycle;

    /**
     * Cycles run, represented as a float, to improve accuracy.
     */
    private float floatCycles;
    
    /**
     * Constructs a new TimeKeeper.
     * @param game              Parent Game object
     * @param cyclesPerSecond   Number of cycles to run per second
     */
    public TimeKeeper(KillBillGame game, int cyclesPerSecond) {
        this.game = game;
        setCyclesPerSecond(cyclesPerSecond);
    }

    /**
     * Changes this TimeKeeper's cycles per second.
     * @param cyclesPerSecond       New cycles per second value
     */
    public void setCyclesPerSecond(int cyclesPerSecond) {
        this.cyclesPerSecond = cyclesPerSecond;
        this.msPerCycle = 1000 / cyclesPerSecond;
        this.lastCycle = -1;
        this.floatCycles = 0;
    }

    /**
     * Converts game time to a number of cycles to run.
     * @param time      Time since the game started (in ms)
     * @return          Number of cycles to run
     */
    public int timeToRunCount(int time) {
        time %= 1000;
        int cycle = (int) time / msPerCycle;

        int cycleCount = 0;
        if (lastCycle != cycle) {
            if (cycle < lastCycle) {
                // lastCycle -> cyclesPerSecond + cycles
                cycleCount = cyclesPerSecond - lastCycle + cycle;
            } else {
                cycleCount = cycle - lastCycle;
            }
            lastCycle = cycle;
        }

        return cycleCount;
    }

    /**
     * Converts the delta time into a number of cycles to run.
     * @param delta     Delta time (in sec)
     * @return          Number of cycles to run
     */
    public int deltaToRunCount(float delta) {
        delta *= 1000; // to MS
        float cyclesApprox = delta / msPerCycle;
        this.floatCycles += cyclesApprox;

        // Remove all whole number cycles from floatCycles, and run them
        int cyclesToRun = 0;
        while (floatCycles >= 1) {
            floatCycles--;
            cyclesToRun++;
        }

        return cyclesToRun;
    }
}
