/**
 * The NormalBomb class represents a standard stationary bomb that explodes
 * in place after its countdown reaches zero. When it explodes, it destroys
 * nearby tiles in a 3x3 radius.
 *
 * <p>This type of bomb does not move. Once placed by the Hero, it waits
 * until detonation and then affects its surrounding tiles. The bomb can
 * also damage the Hero if they are standing too close to the blast.</p>
 */

public class NormalBomb extends Bomb {

    /** Character used to display a NormalBomb on the game board. */
    private char type = 'B';

    /**
     * Constructs a NormalBomb at the specified board position.
     *
     * @param rowIndex the row where the bomb is placed
     * @param colIndex the column where the bomb is placed
     * @param stepsTilExplosion ignored parameter (countdown is fixed to 4)
     *
     * <p>The explosion timer is always set to 4 turns because the initial
     * placement by the Hero already consumes one turn.</p>
     */
    public NormalBomb(int rowIndex, int colIndex, int stepsTilExplosion) {
        super(rowIndex, colIndex, 4);
        // stepsTilExplosion is intentionally not used. The countdown is fixed. (Although it may change as we progress
        // on the development of the game.)
    }



    /**
     * Causes the bomb to explode, destroying nearby tiles and potentially
     * damaging the Hero if they are within the blast radius.
     *
     * <p>The blast radius extends one tile in every direction from the bomb
     * (both cross and diagonal). After exploding, the bomb deactivates and allows
     * the Hero to plant another bomb.</p>
     *
     * @param board the game board on which the explosion occurs
     * @param hero the hero who may be affected by the blast
     */
    public void explode(GameBoard board, Hero hero) {
            boolean heroHit = false;
        {
            if (this.rowIndex < board.getRows() && this.rowIndex >= 0 && 
                this.colIndex < board.getCols() && this.colIndex >= 0) {
                
                if (hero.getX() >= this.rowIndex - 1 && hero.getX() <= this.rowIndex + 1 && 
                    hero.getY() >= this.colIndex - 1 && hero.getY() <= this.colIndex + 1) {

                    hero.loseHeart();

                    }
                /* Destroy surrounding tiles (cross pattern) */
                board.destroyTile(this.rowIndex + 1, this.colIndex);
                board.destroyTile(this.rowIndex - 1, this.colIndex);
                board.destroyTile(this.rowIndex, this.colIndex + 1);
                board.destroyTile(this.rowIndex, this.colIndex - 1);

                /* Destroy surrounding tiles (diagonally) */
                board.destroyTile(this.rowIndex - 1, this.colIndex - 1); 
                board.destroyTile(this.rowIndex - 1, this.colIndex + 1); 
                board.destroyTile(this.rowIndex + 1, this.colIndex - 1); 
                board.destroyTile(this.rowIndex + 1, this.colIndex + 1);

                // cross pattern + diagonal = destroys tiles in a 3x3 radius

                // Make the current tile walkable after bomb disappears
                board.tileBoard[rowIndex][colIndex].setWalkable(true);

                // Update bomb and hero status
                this.deactivate();
                hero.setHasActiveBomb(false);
            }
        }

    }

    /**
     * Returns the display character for this bomb.
     *
     * @return 'B' representing a normal stationary bomb
     */
    public char getType() {
        return type;
    }




}