public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Hero hero) {
        int heroLeftX = hero.getX() + hero.hitBox.x;
        int heroRightX = hero.getX() + hero.hitBox.x + hero.hitBox.width;
        int heroBottomY = hero.getY() + hero.hitBox.y;
        int heroTopY = hero.getY() + hero.hitBox.y + hero.hitBox.height;

        int heroLeftCol = heroLeftX/gp.tileSize;
        int heroRightCol = heroRightX/gp.tileSize;
        int heroTopRow = heroTopY/gp.tileSize;
        int heroBottomRow = heroBottomY/gp.tileSize;

        Tile tileNum1, tileNum2;




        switch (hero.direction) {
            case "up":
                heroTopRow = (heroTopY - hero.getHeroSpeed()) / gp.tileSize;
                tileNum1 = gp.tiles[heroTopRow][heroLeftCol];
                tileNum2 = gp.tiles[heroTopRow][heroRightCol];

                if (tileNum1.isWalkable() == false || tileNum2.isWalkable() == false) {
                    hero.collision = true;
                }

                System.out.println("tileNum1: " + tileNum1.isWalkable() + " " + tileNum1.row + " " + tileNum1.col);
                System.out.print("tileNum2 " + tileNum2.isWalkable() + " " + tileNum2.row + " " + tileNum2.col);
                System.out.println();
                break;
            case "down":
                heroBottomRow = (heroBottomY + hero.getHeroSpeed()) / gp.tileSize;
                tileNum1 = gp.tiles[heroBottomRow][heroLeftCol];
                tileNum2 = gp.tiles[heroBottomRow][heroRightCol];
                if (tileNum1.isWalkable() == false || tileNum2.isWalkable() == false) {
                    hero.collision = true;
                }
                System.out.println("tileNum1: " + tileNum1.isWalkable() + " " + tileNum1.row + " " + tileNum1.col);
                System.out.print("tileNum2 " + tileNum2.isWalkable() + " " + tileNum2.row + " " + tileNum2.col);
                System.out.println();
                break;
            case "left":
                heroLeftCol = (heroLeftX - hero.getHeroSpeed()) / gp.tileSize;
                tileNum1 = gp.tiles[heroTopRow][heroLeftCol];
                tileNum2 = gp.tiles[heroBottomRow][heroLeftCol];
                if (tileNum1.isWalkable() == false || tileNum2.isWalkable() == false) {
                    hero.collision = true;
                }
                System.out.println("tileNum1: " + tileNum1.isWalkable() + " " + tileNum1.row + " " + tileNum1.col);
                System.out.print("tileNum2 " + tileNum2.isWalkable() + " " + tileNum2.row + " " + tileNum2.col);
                System.out.println();
                break;
            case "right":
                heroRightCol = (heroRightX + hero.getHeroSpeed()) / gp.tileSize;
                tileNum1 = gp.tiles[heroTopRow][heroRightCol];
                tileNum2 = gp.tiles[heroBottomRow][heroRightCol];
                if (tileNum1.isWalkable() == false || tileNum2.isWalkable() == false) {
                    hero.collision = true;
                }
                System.out.println("tileNum1: " + tileNum1.isWalkable() + " " + tileNum1.row + " " + tileNum1.col);
                System.out.print("tileNum2 " + tileNum2.isWalkable() + " " + tileNum2.row + " " + tileNum2.col);
                System.out.println();
                break;


        }



    }
}
