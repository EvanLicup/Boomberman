package main;

/**
 * The main.Tile class serves as the base class for all terrain types on the game board.
 * Each tile tracks its position on the board and defines how it behaves in terms
 * of walkability and destructibility.
 *
 * <p>This class is abstract because specific tile types (such as grass, wall,
 * destructible brick, or bomb overlay) each handle walkability and visual
 * representation differently. Subclasses must define whether the tile can be
 * walked on and which character is used to display it on the board.</p>
 */
public abstract class Tile {

    /** The row index of the tile on the game board. */
    protected int row;

    /** The column index of the tile on the game board. */
    protected int col;

    /**
     * Constructs a tile at the specified board position.
     *
     * @param row the row index of the tile
     * @param col the column index of the tile
     */
    public Tile(int row, int col) {
        this.row = row;
        this.col = col;


    }

    /**
     * Indicates whether this tile can be walked on by the main.Hero.
     *
     * @return true if the tile allows movement, false otherwise
     */
    public abstract boolean isWalkable();

    /**
     * Updates the walkability of this tile.
     *
     * <p>This is commonly used when a bomb is placed on the tile. The tile becomes
     * temporarily non-walkable so that the main.Hero cannot move back onto it, mimicking
     * traditional Bomberman movement rules. After the bomb explodes, the tile may
     * be restored to walkable status by using this method and setting back walkability to true.</p>
     * @param walkability true if the tile should become walkable again, false otherwise
     */
    public abstract void setWalkable(boolean walkability);

    /**
     * Returns the display character representing this tile on the board.
     *
     * @return the tile type character
     */
    public abstract char getType();

    /**
     * Returns the row index of this tile on the board.
     *
     * @return the row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column index of this tile on the board.
     *
     * @return the column index
     */
    public int getCol() {
        return col;
    }

}
