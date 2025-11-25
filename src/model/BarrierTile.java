package model;

public class BarrierTile extends Tile{
    private final char type = 'B';

    private boolean walkability = false;

    public BarrierTile(int row, int col) {
        super(row, col);
    }

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
