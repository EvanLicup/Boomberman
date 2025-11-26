package model;

/**
 * Represents an indestructible barrier tile on the game board.
 * <p>
 * A {@code BarrierTile} cannot normally be walked through and has a specific
 * barrier type used for rendering map shapes (B, T, L, R, 1–4).
 */
public class BarrierTile extends Tile {

    /** The character representing the specific barrier type. */
    private char type;

    /**
     * Sets the type/shape of this barrier tile.
     * <p>
     * Valid values include:
     * <ul>
     *   <li>'B' – bottom piece</li>
     *   <li>'T' – top piece</li>
     *   <li>'L' – left piece</li>
     *   <li>'R' – right piece</li>
     *   <li>'1'–'4' – corner/variant pieces</li>
     * </ul>
     *
     * @param inputChar the barrier type character to assign
     */
    @Override
    public void setBarrierType (char inputChar) {
        if (inputChar == 'B') {
            type = 'B';
        }
        else if (inputChar == 'T') {
            type = 'T';
        }
        else if (inputChar == 'L') {
            type = 'L';
        }
        else if (inputChar == 'R') {
            type = 'R';
        }
        else if (inputChar == '1') {
            type = '1';
        }
        else if (inputChar == '2') {
            type = '2';
        }
        else if (inputChar == '3') {
            type = '3';
        }
        else if (inputChar == '4') {
            type = '4';
        }
    }

    /** Whether the tile can be walked on (normally false). */
    private boolean walkability = false;

    /**
     * Creates a new {@code BarrierTile} at the specified board position.
     *
     * @param row the tile's row
     * @param col the tile's column
     */
    public BarrierTile(int row, int col) {
        super(row, col);
    }

    /**
     * Returns whether the tile is currently walkable.
     *
     * @return true if the tile can be walked on; false otherwise
     */
    public boolean isWalkable() {
        return walkability;
    }

    /**
     * Updates the walkability of this indestructible tile.
     *
     * @param walkability whether the tile should now be walkable
     */
    public void setWalkable(boolean walkability) {
        this.walkability = walkability;
    }

    /**
     * Returns the barrier type character assigned to this tile.
     *
     * @return the character representing the tile's type
     */
    public char getType() {
        return type;
    }
}
