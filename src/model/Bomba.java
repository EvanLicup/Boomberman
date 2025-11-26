package model;

public class Bomba {
    private int row;
    private int col;
    private double timeTilExplosion;
    public boolean exploded = false;
    private GameModel gm;

    public Bomba(int row, int col, double timeTilExplosion, GameModel gm) {
        this.row = row;
        this.col = col;
        this.gm = gm;
        this.timeTilExplosion = timeTilExplosion;
    }

    public void explode() {
        exploded = true;
        System.out.println("Exploded");
        int heroRow = (int) Math.round((double) gm.hero.getY() / gm.tileSize);
        int heroCol = (int) Math.round((double) gm.hero.getX() / gm.tileSize);
        System.out.println("Bomb: row=" + row + ", col=" + col);
        System.out.println("Hero: heroRow=" + heroRow + ", heroCol=" + heroCol);
        System.out.println("Checking: " + (heroRow >= row - 1 && heroRow <= row + 1) + " " +
                (heroCol >= col - 1 && heroCol <= col + 1));

        {
            // row = 9, col = 5, hero at 9,6:
             // true
             // true
             // true
             // true
            // what is wrong then ????
            if (heroRow >= row - 1 && heroRow <= row + 1 && heroCol >= col - 1 && heroCol <= col + 1)  {

                    gm.hero.loseHeart();
                    System.out.println("heart lost");

                }
                System.out.println("Trying to destroy tiles around: " + col + "," + row);

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
