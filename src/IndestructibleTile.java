/**
 * The IndestructibleTile class represents a permanent wall or barrier
 * that cannot be destroyed by bombs and never becomes walkable.
 *
 * <p>These tiles are typically the fixed internal pillars similar to
 * what we see in the Bomberman games. They serve as
 * immovable obstacles that permanently block movement.</p>
 */
public class IndestructibleTile extends Tile {
    /** The character used to represent an indestructible tile on the board. */
    private final char type = 'I';

    /**
     * Indicates whether this tile is walkable. For indestructible tiles,
     * this should always remain {@code false}.
     */
    private boolean walkability = false;

    /**
     * Constructs an indestructible tile at the specified board position.
     *
     * @param row the row index of the tile
     * @param col the column index of the tile
     */
    public IndestructibleTile(int row, int col) {
        super(row, col);
    }

    /**
     * Returns whether this tile can currently be walked on.
     *
     * @return {@code true} if this tile can be walked on,
     *         {@code false} if it is still intact and blocking movement
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
     * Returns the display character representing this tile on the board.
     *
     * @return the character 'I'
     */
    public char getType() {
        return type;
    }

}
