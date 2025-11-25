package main;

/**
 * The main.DestructibleTile class represents a tile that initially blocks movement
 * but can be destroyed by a bomb explosion. Once destroyed, the tile becomes
 * walkable, allowing the main.Hero to pass through its position.
 *
 */
public class DestructibleTile extends Tile {

    /** The character used to represent a destructible tile on the board. */
    private final char type = 'D';

    /** Indicates whether this tile can currently be walked on. */
    private boolean walkability = false;

    /**
     * Constructs a destructible tile at the specified board position.
     *
     * @param row the row index of the tile
     * @param col the column index of the tile
     */
    public DestructibleTile(int row, int col) {
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
     * Updates the walkability of this destructible tile.
     *
     * @param walkability whether the tile should now be walkable
     */
    public void setWalkable(boolean walkability) {
        this.walkability = walkability;
    }

    /**
     * Returns the display character used to represent this tile on the board.
     *
     * @return a space character ('D') indicating a normal walkable tile
     */
    public char getType() {
        return this.type;
    }
}
