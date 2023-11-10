package brigade.killbill.ui;

import java.util.ArrayList;

import brigade.killbill.KillBillGame;

/**
 * Draws elements to the screen.
 * @author csenneff
 */
public class ElementRenderer {
    /**
     * Parent Game object
     */
    public KillBillGame game;
    
    /**
     * All elements to be rendered
     */
    public ArrayList<Element> elements;

    private ArrayList<Element> remove;

    /**
     * Constructs a new ElementRenderer.
     * @param game
     */
    public ElementRenderer(KillBillGame game) {
        this.game = game;
        this.elements = new ArrayList<Element>();
        this.remove = new ArrayList<Element>();
    }

    /**
     * Renders elements to the fitted batch.
     * @param delta Delta time
     */
    public void render(float delta) {
        elements.forEach(element -> {
            if (game.getScreen().elementRenderer != this) return;
            if (!element.rendersToFixed()) element.renderTo(game.getScreen().batch, delta);
        });
    }

    /**
     * Renders elements to the fixed batch.
     * @param delta Delta time
     */
    public void renderFixed(float delta) {
        if (!game.getExternRender()) return;
        elements.forEach(element -> {
            if (game.getScreen().elementRenderer != this) return;
            if (element.rendersToFixed()) element.renderTo(game.getScreen().fixedBatch, delta);
        });

    }

    /**
     * Finishes rendering.
     * @param delta Delta time
     */
    public void finish(float delta) {
        for (Element elem: remove) {
            elements.remove(elem);
        }
        remove.clear();
    }

    /**
     * Adds an element to the renderer.
     * @param element       Element to add
     */
    public void addElement(Element element) {
        elements.add(element);
    }

    /**
     * Removes an element from the renderer.
     * @param element   Element to remove
     */
    public void removeElement(Element element) {
        elements.remove(element);
    }

    /**
     * Schedules an element for destruction after the current render cycle.
     * Allows elements to destroy themselves without an array modification error.
     * @param element   Element to destroy
     */
    public void removeAfterRender(Element element) {
        remove.add(element);
    }

    /**
     * Clears all elements from the renderer.
     */
    public void clearAll() {
        elements.clear();
    }

    /**
     * Handles mouse clicks on all elements.
     */
    public void onMouseClick() {
        elements.forEach(element -> { element.onMouseClick(); });
    }
}
