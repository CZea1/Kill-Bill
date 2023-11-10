# to do list

This is a list of all the things we still have to finish before the game is done. Before working on one, please claim it (or talk to whoever you're collaborating with)!

__Basic key:__
* 🔴: Unclaimed task
* 🟡: Claimed, uncompleted task
* 🟢: Completed task
* ⭐: Priority rating from 1-3.
* ❓: Optional feature, if we have time.

## core mechanics
* **Enemy AI**  [🟢 Camden] [⭐⭐]
    * Enemies need to pathfind to the player without running into obstacles.
    * There is a libGDX library available for it -- not sure how it would integrate though.
* **Sound System** [🟢 Camden] [⭐]
    * Make a sound system which loads mp3s or another format from the assets folder.
    * This can follow (almost exactly) the textures.TextureStore format, just with .playSound() instead of .getTexture().
* **Map Loader** [🟢 Camden] [⭐⭐⭐]
    * Loads a map from a file into a screen.
    * We also need a reliable way to create multiple screens and switch between them based on this.
* **Loot Tables** [🟢 Brayden] [⭐⭐⭐]
    * How the player gets items -- chests, entity/boss kills, etc.
* **Health** [🟢 Caleb] [⭐⭐]
    * Health/HP system. Should be applicable to both the player and all enemies.
    * Make sure this scales well -- for example, if an enemy only has 3 hearts it'll be hard to make weapons not too OP as the game progresses.
    * Render player health and boss health to the screen in a cool way. Check with Camden for info on how to draw fixed stuff to the screen (outside of the main gameplay area).
* **Inventory** [🟢 Camden] [⭐⭐⭐]
    * Depending on how we want to do this, this could just be the various weapons the player has.
    * If we add potions or other things to the game, it should be able to handle that.
    * Register keypresses to switch weapons.
* **Weapons** [🟢 Camden] [⭐⭐⭐]
    * Base extensible Weapon object. 
    * Should have some kind of use() method, damage attributes, cooldowns, etc.
    * **Left to do:** Projectiles, ranged attacks, custom damage attributes, animations
* **Savegames** [❌] (not happening due to lack of time -- not in design doc so we're good)

## design
* **Sprites** [🟡 Aaron] [⭐⭐]
    * Tiles, entities, and weapons.
* **Maps** [🟡] [⭐⭐ Aaron, Brayden]
    * Create all the levels, once the map loader is complete.
* **Backgrounds** [🟢 Camden, Aaron] [⭐]
    * Make a background for both the in-game screen and title screen so it's not just white.
* **Sounds** [🟢 Camden] [⭐]
    * Curate some sound effects and add them to the game. Get James to voice act
* **Animations** [🟢 Aaron] [❓]
    * Animate player walking, sword swinging, weapon firing, chest opening, etc.

## enemies
* **Base Enemy** [🟢 Camden] [⭐⭐]
    * Base enemy object which is extended to create different types of enemies.
* **Basic Enemies** [🟢 Camden ] [⭐⭐⭐]
    * Simple enemies which are killed in the game progression up to the boss.
    * Various weapons, loottables, and etc. Should get harder as the game progresses (?)
* **Boss** [🟢 Caleb] [⭐⭐⭐]
    * Design Bill, his cronies, his weapon, and the basic layout of the boss room.
    * Actually implement that into the game as a new map.

## objects
* **Chest** [🟢 Brayden] [⭐⭐]
    * Create the chest object which, when the player interacts with it in some way, opens a loot table.
* **Door** [🟢 Camden] [⭐⭐]
    * Object which transports the user to another room. Add some kind of animation too, maybe?
* **Projectile** [🟢 Camden] [⭐⭐⭐]
    * Moves in a specified direction until it hits an object or entity. Remove health if latter.

## menus
* **Base Menu** [🟢 Camden] [⭐⭐⭐]
    * Base extensible class (screens.MenuScreen, probably) which represents *any* game menu. Only has a fixed renderer.
    * Should be able to handle mouse and keyboard presses, if feasible.
* **Main Menu** [🟢 Brayden] [⭐⭐]
    * Play, continue, how-to, about, settings, etc.
* **Death Menu** [🟢 Caleb] [⭐⭐]
    * Lets the player leave when they die.
* **Pause Menu** [🟢] [⭐⭐]
    * Continue, restart, return to main menu, exit, etc.

## small stuff
* **Movement Scaling** [🟢 Caleb] [⭐]
    * The player moves faster if going diagonal.
    * Make the player movement algorithm make the resultant movement vector the actual speed (and limit X/Y) if moving diagonally.
    * Feel free to commit theft from enemies.Enemy.moveTowardsPlayer()
* **Collision Debugging** [🟢 Camden] [⭐]
    * Fix the weird collision edge cases where enemies can trap you into corners, glitch out, etc.
* **Framerate Scaling** [🟢 Camden] [⭐⭐]
    * Make sure movement for *all* objects uses the deltaTime passed to their render() functions to determine how far to move.
    * Otherwise, the game will run in slow motion on low FPS devices.
    * Already implemented for the player -- same needs to be done for enemies, projectiles, etc.

Below are all our custom weapons and enemies. Put all tasks related to them under their name.
## custom enemies
* **Microsoft Employee** [🟢] [⭐⭐⭐]
* **Security Robot** [🟢] [⭐⭐⭐]
* **Shareholder** [🟢] [⭐⭐⭐]
* **Bill** [🟢 Caleb] [⭐⭐⭐]

## custom weapons
* **Spear** [🟢] [⭐⭐]
* **Hammer** [🟢] [⭐⭐]
* **Great Sword** [🟢] [⭐⭐]
* **Axe** [🟢] [⭐⭐]
* **Plushie** [🟢] [⭐⭐]
