/**
 * The Bomb class represents an explosive object that can be placed on the game board.
 * A bomb has a countdown timer that decrements each turn, after which it will explode.
 *
 * <p>This class is abstract because different bomb types (such as a normal bomb
 * or a walking bomb that will soon be implemented for MCO2) may behave differently before detonation.
 * For example, a normal bomb remains stationary, whereas a walking bomb may move across
 * the board while its timer is decreasing and can also be detonated prematurely.</p>
 */
public abstract class Bomb {

    /** The row index of the bomb on the board. */
    protected int rowIndex;

    /** The column index of the bomb on the board. */
    protected int colIndex;

    /** Number of turns remaining before the bomb explodes. */
    protected int turnsTilExplosion;

    /** Indicates whether the bomb is currently active. */
    private boolean isActive = false;

    /** The initial countdown value restored when the bomb is activated. */
    protected final int initialTurnsTilExplosion;

    /** The bomb type that will also indicate which symbol is to be printed in the board*/
    private char type;


    /**
     * Constructs a new Bomb with a given board position and countdown time.
     *
     * @param rowIndex the row where the bomb is placed
     * @param colIndex the column where the bomb is placed
     * @param turnsTilExplosion the number of turns before detonation
     */
    public Bomb(final int rowIndex, final int colIndex, int turnsTilExplosion) {

        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.turnsTilExplosion = turnsTilExplosion;
        this.initialTurnsTilExplosion = turnsTilExplosion;
    }

    /**
     * Decreases the bomb's countdown timer by 1 turn if it is still above zero.
     * This should typically be called once per game turn/tick.
     */
    public void decrementCountdown() {
        if (turnsTilExplosion > 0) {
            turnsTilExplosion--;
        }
    }

    /**
     * Returns the row index where this bomb is placed.
     *
     * @return the row index
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Returns the column index where this bomb is placed.
     *
     * @return the column index
     */
    public int getColIndex() {
        return colIndex;
    }

    /**
     * Updates the row index of this bomb (e.g., if it is moved).
     *
     * @param rowIndex the new row position
     */
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    /**
     * Updates the column index of this bomb (e.g., if it is moved).
     *
     * @param colIndex the new column position
     */
    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    /**
     * Returns the number of turns remaining before the bomb explodes.
     *
     * @return the turns left until explosion
     */
    public int getTurnsTillExplosion() {
        return turnsTilExplosion;
    }

    /**
     * Returns the character representing this bomb on the board.
     * This is abstract because different bomb types may have different visual symbols.
     *
     * @return the character type of the bomb
     */
    public abstract char getType();

    /**
     * Returns the state of the bomb.
     *
     * @return true if the bomb is active, false otherwise
     */
    public boolean getIsActive() {
        return isActive;
    }

    /**
     * Activates the bomb and resets its countdown timer to its original value.
     *
     */
    public void activate() {
        isActive = true;
        this.turnsTilExplosion = this.initialTurnsTilExplosion;
    }

    /**
     * Deactivates the bomb
     *
     */
    public void deactivate() {
        isActive = false;
    }


}