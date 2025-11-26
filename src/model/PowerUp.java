package model;

/**
 * Represents an in-game power-up that the hero can pick up.
 * <p>
 * Power-ups appear when destructible tiles are broken. Each one grants
 * a specific ability or reward such as:
 * <ul>
 *     <li>Walking Bomb ability</li>
 *     <li>Increased bomb radius</li>
 *     <li>Extra life (or bonus points)</li>
 * </ul>
 */
public class PowerUp {

    /**
     * Types of power-ups available in the game.
     */
    public enum Type {
        /** Grants the ability to spawn and control a walking bomb. */
        WALKING,

        /** Increases the radius of the hero's normal bombs. */
        RADIUS,

        /** Grants +1 life (or +100 points if already at max). */
        EXTRA_LIFE
    }

    /** The tile row where this power-up is located. */
    public final int row;

    /** The tile column where this power-up is located. */
    public final int col;

    /** The type of power-up (walking bomb, radius up, extra life). */
    public final Type type;

    /** True if the hero has already collected this power-up. */
    private boolean picked = false;

    /** True once the instruction text has been shown to the player. */
    private boolean instructionShown = false;

    /**
     * Creates a new PowerUp at a specific tile with a given type.
     *
     * @param row the tile row where the power-up is spawned
     * @param col the tile column where the power-up is spawned
     * @param t the type of power-up
     */
    public PowerUp(int row, int col, Type t) {
        this.row = row;
        this.col = col;
        this.type = t;
    }

    /**
     * Returns whether this power-up has already been picked up by the hero.
     *
     * @return {@code true} if picked up, otherwise {@code false}
     */
    public boolean isPicked() { return picked; }

    /**
     * Marks this power-up as collected.
     */
    public void pick() { picked = true; }

    /**
     * Checks whether the instructional message for this power-up
     * has already been displayed to the player.
     *
     * @return {@code true} if already shown, otherwise {@code false}
     */
    public boolean isInstructionShown() { return instructionShown; }

    /**
     * Marks the instruction text as shown so it is not shown again.
     *
     * @param v value to set
     */
    public void setInstructionShown(boolean v) { instructionShown = v; }

    /**
     * Returns the instructional message explaining what this power-up does.
     *
     * @return the instruction text associated with this power-up type
     */
    public String getInstructionText() {
        switch (type) {
            case WALKING:
                return "Walking Bomb: Use arrow keys to move it. Press J to detonate.";
            case RADIUS:
                return "Power+1: Bomb radius increased to 2 tiles!";
            case EXTRA_LIFE:
                return "Extra Life: If lives < 3, gain +1. Otherwise, +100 bonus points!";
        }
        return "";
    }
}
