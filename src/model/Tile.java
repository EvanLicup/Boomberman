package model;

/**
 * The {@code Tile} class is the abstract base class for all terrain types on the
 * Bomberman-style game board.
 * <p>
 * Each tile tracks its board position (row, column) and defines how it behaves
 * regarding:
 * <ul>
 *     <li>Walkability — whether the hero and drones may step on this tile.</li>
 *     <li>Destructibility — whether bombs can destroy or modify this tile.</li>
 *     <li>Tile type — represented as a character used for rendering logic.</li>
 * </ul>
 * <p>
 * Subclasses such as {@link WalkableTile}, {@link DestructibleTile},
 * {@link IndestructibleTile}, and {@link BarrierTile} provide specific
 * implementations for these behaviors.
 * </p>
 */
public abstract class Tile {

    /** The row index of the tile on the game board grid. */
    protected int row;

    /** The column index of the tile on the game board grid. */
    protected int col;

    /**
     * Constructs a tile located at the specified grid position.
     *
     * @param row the row index where this tile exists
     * @param col the column index where this tile exists
     */
    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns whether entities (hero, drones, bombs, etc.) can walk onto this tile.
     *
     * @return {@code true} if movement is allowed, {@code false} if blocked
     */
    public abstract boolean isWalkable();

    /**
     * Updates the walkability status of this tile.
     * <p>
     * This is commonly used when a bomb occupies a tile. During bomb placement,
     * many Bomberman-style mechanics temporarily make the tile non-walkable so
     * the hero cannot step back onto it until the explosion resolves.
     *
     * @param walkability {@code true} to allow movement; {@code false} to block movement
     */
    public abstract void setWalkable(boolean walkability);

    /**
     * Returns the character identifying this tile type (e.g., ' ', 'D', 'I', 'B', etc.).
     * This character is used by the rendering system to determine which sprite to draw.
     *
     * @return a character representing the tile type
     */
    public abstract char getType();

    /**
     * Returns the row index where this tile is located.
     *
     * @return the row index of the tile
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column index where this tile is located.
     *
     * @return the column index of the tile
     */
    public int getCol() {
        return col;
    }

    /**
     * Optional hook used by {@link BarrierTile} subclasses to assign specific barrier types
     * such as corners, edges, or special shapes.
     * <p>
     * Default implementation does nothing; subclasses override as needed.
     *
     * @param type the barrier type character to assign
     */
    public void setBarrierType(char type) {
        // Overridden in BarrierTile
    }

    /**
     * Optional hook used mainly by destructible tiles when being destroyed by an explosion.
     * <p>
     * Default implementation does nothing and is overridden by classes like
     * {@link DestructibleTile}.
     */
    public void setDestroyed() {
        // Overridden by DestructibleTile
    }

    /**
     * Returns whether this tile has been destroyed.
     * <p>
     * Only destructible tiles override this behavior. All other tiles return {@code true}
     * by default as a neutral/no-op behavior.
     *
     * @return {@code true} if destroyed, otherwise {@code false}
     */
    public boolean getDestroyedStatus() {
        return true;
    }
}
