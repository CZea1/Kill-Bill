package brigade.killbill.misc;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;
import brigade.killbill.misc.Actions.ActionType;

public class ActionSet {
    public HashMap<String, Long> sounds;

    private float currentDelay;

    private ArrayList<Action> actions;

    private KillBillGame game;
    
    private int currentIndex;

    public class Action {
        public ActionType type;
        public ArrayList<String> args;
    }

    public ActionSet(KillBillGame game) {
        this.game = game;
        actions = new ArrayList<Action>();
        currentDelay = -1;
        sounds = new HashMap<String, Long>();
        currentIndex = 0;
    }

    public void addAction(String action) {
        // Get action name
        String[] strArgs = action.split(" ", 2);

        if (strArgs.length < 1) {
            throw new GdxRuntimeException("Invalid action: Empty string not allowed");
        }

        String actionName = strArgs[0];
        ArrayList<String> actionArgs = new ArrayList<String>();
        if (strArgs.length > 1) {
            String[] args = strArgs[1].split(" ");
            for (String arg: args) {
                actionArgs.add(arg);
            }
        }

        Action ac = new Action();
        switch (actionName) {
            case "lock_doors":
                ac.type = ActionType.LOCK_DOORS;
                break;
            case "unlock_doors":
                ac.type = ActionType.UNLOCK_DOORS;
                break;
            case "play":
                ac.type = ActionType.PLAY;
                break;
            case "stop":
                ac.type = ActionType.STOP;
                break;
            case "delay":
                ac.type = ActionType.DELAY;
                break;
            case "texture":
                ac.type = ActionType.TEXTURE;
                break;
            case "attrall":
                ac.type = ActionType.ATTRALL;
                break;
            case "unattrall":
                ac.type = ActionType.UNATTRALL;
                break;
            default:
                throw new GdxRuntimeException("Invalid action type: " + actionName);
        }
        ac.args = actionArgs;

        actions.add(ac);
    }

    public void setDelay(float delay) {
        currentDelay = delay;
    }

    public void start() {
        currentIndex = 0;
        runNext();
    }

    public void tick(float delta) {
        if (currentIndex == -1) return;

        if (currentDelay >= 0) {
            currentDelay -= delta;
            return;
        }

        // Go until we hit a delay or the end of the action set
        while (currentDelay < 0 && currentIndex != -1) {
            runNext();
        }
    }

    private void runNext() {
        if (currentIndex >= actions.size()) {
            currentIndex = -1;
            return;
        }
        Actions.runAction(game, this, actions.get(currentIndex));
        currentIndex++;
    }

}
