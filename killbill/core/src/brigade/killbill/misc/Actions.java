package brigade.killbill.misc;

import java.util.ArrayList;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;
import brigade.killbill.entities.Entity;
import brigade.killbill.entities.EntityAttributes;
import brigade.killbill.misc.ActionSet.Action;
import brigade.killbill.objects.Door;

public class Actions {
    public enum ActionType {
        LOCK_DOORS,
        UNLOCK_DOORS,
        PLAY,
        STOP,
        DELAY,
        TEXTURE,
        ATTRALL,
        UNATTRALL
    }

    public static void runAction(KillBillGame game, ActionSet set, Action action) {
        switch (action.type) {
            case LOCK_DOORS:
                setDoorState(game, set, action.args, true);
                break;
                
            case UNLOCK_DOORS:
                setDoorState(game, set, action.args, false);
                break;

            case PLAY:
                play(game, set, action.args);
                break;

            case STOP:
                stop(game, set, action.args);
                break;

            case DELAY:
                delay(game, set, action.args);
                break;

            case TEXTURE:
                texture(game, set, action.args);
                break;

            case ATTRALL:
                attrAll(game, set, action.args);
                break;

            case UNATTRALL:
                unattrAll(game, set, action.args);
                break;
        }
    }

    public static void setDoorState(KillBillGame game, ActionSet set, ArrayList<String> args, boolean state) {
        game.getScreen().map.forEach(obj -> {
            if (obj instanceof Door) {
                if (args.contains(obj.getName())) {
                    Door door = (Door) obj;
                    door.setLocked(state);
                }
            }
        });
    }

    public static void play(KillBillGame game, ActionSet set, ArrayList<String> args) {
        // Unpack into sound ID and file
        if (args.size() != 2) {
            throw new GdxRuntimeException("'play' action directives need two args: sound ID (for cancellations) and file");
        }
        String id = args.get(0);
        Sound sound = game.soundStore.getSound(args.get(1));

        if (sound == null) {
            throw new GdxRuntimeException("Sound not found: " + args.get(1));
        }

        long soundId = sound.play(game.getVolume());
        set.sounds.put(id, soundId);
    }

    public static void stop(KillBillGame game, ActionSet set, ArrayList<String> args) {// Unpack into sound ID and file
        if (args.size() != 2) {
            throw new GdxRuntimeException("'stop' action directives need two args: sound ID and file");
        }
        String id = args.get(0);
        Sound sound = game.soundStore.getSound(args.get(1));

        if (sound == null) {
            throw new GdxRuntimeException("Sound not found: " + args.get(1));
        }

        Long soundId = set.sounds.get(id);
        if (soundId == null) throw new GdxRuntimeException("Sound by ID " + id + " not playing.");
        sound.stop(soundId);
    }

    public static void delay(KillBillGame game, ActionSet set, ArrayList<String> args) {
        if (args.size() != 1) {
            throw new GdxRuntimeException("'delay' action directives need one arg: duration");
        }

        float duration;
        try {
            duration = Float.parseFloat(args.get(0));
        } catch (Exception e) {
            throw new GdxRuntimeException("Could not parse duration as a float.");
        }
        
        set.setDelay(duration);
    }

    public static void texture(KillBillGame game, ActionSet set, ArrayList<String> args) {
        if (args.size() != 2) {
            throw new GdxRuntimeException("'texture' action directives need two args: target object name and new texture");
        }
        String objName = args.get(0);
        Texture texture = game.textureStore.getTexture(args.get(1));

        game.getScreen().map.forEach(obj -> {
            if (obj.getName().equals(objName)) {
                obj.setTexture(texture);
            }
        });
    }

    public static void attrAll(KillBillGame game, ActionSet set, ArrayList<String> args) {
        if (args.size() != 1) {
            throw new GdxRuntimeException("'attrall' action directives need one arg: attr");
        }

        EntityAttributes attr = Parsers.toEntityAttributes(args.get(0));

        game.getScreen().map.forEach(obj -> {
            if (obj instanceof Entity) {
                Entity ent = (Entity) obj;
                if (!ent.hasAttr(attr)) ent.setAttr(attr);
            }
        });
    }

    public static void unattrAll(KillBillGame game, ActionSet set, ArrayList<String> args) {
        if (args.size() != 1) {
            throw new GdxRuntimeException("'unattrall' action directives need one arg: attr");
        }

        EntityAttributes attr = Parsers.toEntityAttributes(args.get(0));

        game.getScreen().map.forEach(obj -> {
            if (obj instanceof Entity) {
                Entity ent = (Entity) obj;
                if (ent.hasAttr(attr)) ent.unsetAttr(attr);
            }
        });
    }
}
