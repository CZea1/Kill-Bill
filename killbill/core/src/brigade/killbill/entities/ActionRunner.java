package brigade.killbill.entities;

import com.badlogic.gdx.utils.GdxRuntimeException;

import brigade.killbill.KillBillGame;
import brigade.killbill.items.Item;
import brigade.killbill.map.maploader.ItemFactory;
import brigade.killbill.misc.Parsers;

public class ActionRunner {
    public static void runAction(KillBillGame game, Entity target, String command) {
        // Parse the action
        String[] args = command.split(" ", 2);
        if (args.length < 1) throw new GdxRuntimeException("Line actions must be formatted as: ![action] (args)");

        String action = args[0];
        String argStr = "";
        if (args.length == 2) argStr = args[1];

        final String lambdaArgStringWhichIHaveToUseBecauseJavaIsAStupidLanguageWhichShouldntExistAndIsDumbAndStupidAndBadAndAlsoDidIMentionThatIDontLikeItWellItsTrueIDontLikeItExceptMaybeThisIsAnExaggerationIDontKnowWhateverSoAnywaysTodayIAmInTheLabWorkingOnTheLabWhichIsAboutRecursionWhichIsReallyFunAndAwesomeAndIAmHavingAnIncredibleTimeSoBasicallyIFinishedFastBecauseCalebIsReallySlowAndNowImWorkingOnThisGameAndIAmVeryAngryAboutThisStupidJavaThingBecauseIJustWantToMakeTheEmployeesKillPeopleAsTheyShouldButTheyWontBecauseOfJavaAndNowAndMyDayIsRuined = argStr;

        switch (action) {
            case "hold":
                if (argStr.equals("none")) target.setItem(null);
                else holdItem(game, target, argStr);
                break;

            case "holdall":
                if (argStr.equals("none")) 
                    game.getMapScreen().map.forEach((object) -> {
                        if (object instanceof Entity) {
                            object.setItem(null);
                        }
                    });
                else
                    game.getMapScreen().map.forEach((object) -> {
                        if (object instanceof Entity) {
                            holdItem(game, (Entity) object, lambdaArgStringWhichIHaveToUseBecauseJavaIsAStupidLanguageWhichShouldntExistAndIsDumbAndStupidAndBadAndAlsoDidIMentionThatIDontLikeItWellItsTrueIDontLikeItExceptMaybeThisIsAnExaggerationIDontKnowWhateverSoAnywaysTodayIAmInTheLabWorkingOnTheLabWhichIsAboutRecursionWhichIsReallyFunAndAwesomeAndIAmHavingAnIncredibleTimeSoBasicallyIFinishedFastBecauseCalebIsReallySlowAndNowImWorkingOnThisGameAndIAmVeryAngryAboutThisStupidJavaThingBecauseIJustWantToMakeTheEmployeesKillPeopleAsTheyShouldButTheyWontBecauseOfJavaAndNowAndMyDayIsRuined);
                        }
                    });
                break;

            case "attr":
                setAttr(game, target, argStr);
                break;

            case "unattr":
                unsetAttr(game, target, argStr);
                break;

            case "attrall":
                game.getMapScreen().map.forEach((object) -> {
                    if (object instanceof Entity) {
                        setAttr(game, (Entity) object, lambdaArgStringWhichIHaveToUseBecauseJavaIsAStupidLanguageWhichShouldntExistAndIsDumbAndStupidAndBadAndAlsoDidIMentionThatIDontLikeItWellItsTrueIDontLikeItExceptMaybeThisIsAnExaggerationIDontKnowWhateverSoAnywaysTodayIAmInTheLabWorkingOnTheLabWhichIsAboutRecursionWhichIsReallyFunAndAwesomeAndIAmHavingAnIncredibleTimeSoBasicallyIFinishedFastBecauseCalebIsReallySlowAndNowImWorkingOnThisGameAndIAmVeryAngryAboutThisStupidJavaThingBecauseIJustWantToMakeTheEmployeesKillPeopleAsTheyShouldButTheyWontBecauseOfJavaAndNowAndMyDayIsRuined);
                    }
                });
                break;

            case "unattrall":
                game.getMapScreen().map.forEach((object) -> {
                    if (object instanceof Entity) {
                        unsetAttr(game, (Entity) object, lambdaArgStringWhichIHaveToUseBecauseJavaIsAStupidLanguageWhichShouldntExistAndIsDumbAndStupidAndBadAndAlsoDidIMentionThatIDontLikeItWellItsTrueIDontLikeItExceptMaybeThisIsAnExaggerationIDontKnowWhateverSoAnywaysTodayIAmInTheLabWorkingOnTheLabWhichIsAboutRecursionWhichIsReallyFunAndAwesomeAndIAmHavingAnIncredibleTimeSoBasicallyIFinishedFastBecauseCalebIsReallySlowAndNowImWorkingOnThisGameAndIAmVeryAngryAboutThisStupidJavaThingBecauseIJustWantToMakeTheEmployeesKillPeopleAsTheyShouldButTheyWontBecauseOfJavaAndNowAndMyDayIsRuined);
                    }
                });
                break;

            case "play":
                game.soundStore.getSound(argStr).play(game.getVolume());
                break;

            default:
                throw new GdxRuntimeException("Invalid line action " + action + ".");
        }
    }

    public static void holdItem(KillBillGame game, Entity target, String args) {
        Item resItem = ItemFactory.itemFromString(game, target, args);
        target.setItem(resItem);
    }

    public static void setAttr(KillBillGame game, Entity target, String args) {
        EntityAttributes attr = Parsers.toEntityAttributes(args);
        if (!target.hasAttr(attr)) target.setAttr(attr);
    }

    public static void unsetAttr(KillBillGame game, Entity target, String args) {
        EntityAttributes attr = Parsers.toEntityAttributes(args);
        if (target.hasAttr(attr)) target.unsetAttr(attr);
    }
}
