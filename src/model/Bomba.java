package model;

public class Bomba {
    private int row;
    private int col;
    private double timeTilExplosion;
    public boolean exploded = false;
    private GameModel gm;

    public Bomba(int x, int y, double timeTilExplosion, GameModel gm) {
        row = x;
        col = y;
        this.gm = gm;
        this.timeTilExplosion = timeTilExplosion;
    }

    public void explode() {
        exploded = true;
        System.out.println("Exploded");
        {
                if (gm.hero.getX() >= row - 1 && gm.hero.getX() <= row + 1 &&
                        gm.hero.getY() >= col - 1 && gm.hero.getY() <= row + 1) {

                    gm.hero.loseHeart();

                }
                System.out.println("Trying to destroy tiles around: " + row + "," + col);

            /* Destroy surrounding tiles (cross pattern) */
                gm.destroyTile(col, row + 1);
                gm.destroyTile(col, row - 1);
                gm.destroyTile(col + 1, row);
                gm.destroyTile(col - 1, row);


        }

    }

    public void decreaseTime (double delta) {
        if (exploded == true) return;
        timeTilExplosion -= delta;
        if (timeTilExplosion <= 0) {
            explode();
        }
    }

    public int getRow() {return row;}

    public int getCol() {return col;}
}
