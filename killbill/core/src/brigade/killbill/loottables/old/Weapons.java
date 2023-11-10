package brigade.killbill.loottables.old;

import static brigade.killbill.loottables.old.Weapon.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Main class for obtaining a random weapon from an interactive object
 * e.g a loot chest
 * 
 * @author Brayden
 */
public class Weapons {

    private Weapon weapon = null;
	private String name = "";
    private String texture = "";
	private int damage = 0;
    
    public Weapons() {

    }

    /**
     * Uses a list with all of the different weapons and the Random function
     * to randomly pick an element from the list of weapons.
     */
    public Weapon chooseRandomWeapon() {
		List<Weapon> givenList = Arrays.asList(AXE, SPEAR, HAMMER, PLUSHIE, GREATSWORD);
		Random rand = new Random();
		weapon = givenList.get(rand.nextInt(givenList.size()));
        return weapon;
	}

    /**
     * This is the method that will be called in other classes when interacting with
     * an object that a player should receive a weapon item from. Calls chooseRandomWeapon
     * method to get a random weapon, and then calls initializeAttributes method to assign the
     * name and damage values related to the chosen weapon.
     * @return  The weapon that is chosen at random
     */
    public Weapon getRandomWeapon() {
		weapon = chooseRandomWeapon();
		initializeAttributes();
		return weapon;
	}

    /**
     * Assigns the name and damage attributes associated with whatever weapon was chosen
     * from the chooseRandomWeapon method.
     */
    public void initializeAttributes() {
		if (weapon == AXE) {
			name = "Great Axe";
			damage = 5;
            texture = "weapons_axe";
		} else if (weapon == SPEAR) {
			name = "Sharpened Spear";
			damage = 10;
            texture = "weapons_spear1";
		} else if (weapon == HAMMER) {
			name = "Iron Hammer";
			damage = 7;
            texture = "weapons_hammer";
		} else if (weapon == PLUSHIE) {
            name = "Plushie";
            damage = 1;
            texture = "weapons_plushy";
        } else if (weapon == GREATSWORD) {
            name = "Excalibur";
            damage = 12;
            texture = "weapons_greatsword";
        } else { // If this happens, something is very wrong.
            name = "null";
            damage = 0;
            texture = "default";
        }
	}

    /**
     * Accessor method used to return the amount of damage the chosen weapon does.
     * @return  amount of damage the chosen weapon does
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Accessor method used to return the name of the chosen weapon as a string
     * @return  the name of the chosen weapon
     */
    public String getName() {
        return name;
    }

    /**
     * Accessor method used to return the location of the texture for each weapon,
     * called in other methods to set the character texture to holding the specific weapon.
     * @return  the location of each weapon graphic as a string
     */
    public String getTexture() {
        return texture;
    }
}