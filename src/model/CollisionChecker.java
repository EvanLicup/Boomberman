package model;

public class CollisionChecker {
    GameModel gm;

    public CollisionChecker(GameModel gm) {
        this.gm = gm;
    }

    public void checkTile(Hero hero) {
        int heroLeftX = hero.getX() + hero.hitBox.x;
        int heroRightX = hero.getX() + hero.hitBox.x + hero.hitBox.width;
        int heroTopY = hero.getY() + hero.hitBox.y;
        int heroBottomY = hero.getY() + hero.hitBox.y + hero.hitBox.height;

        int tileSize = gm.tileSize;

        // Hero edges in tile coordinates
        int heroLeftCol = heroLeftX / tileSize;
        int heroRightCol = heroRightX / tileSize;
        int heroTopRow = heroTopY / tileSize;
        int heroBottomRow = heroBottomY / tileSize;

        Tile tile1, tile2;

        switch (hero.direction) {
            case "up":
                int nextTopRow = (heroTopY - hero.getHeroSpeed()) / tileSize;
                tile1 = gm.tiles[nextTopRow][heroLeftCol];
                tile2 = gm.tiles[nextTopRow][heroRightCol];
                if (!tile1.isWalkable() || !tile2.isWalkable()) hero.collision = true;
                break;

            case "down":
                int nextBottomRow = (heroBottomY + hero.getHeroSpeed()) / tileSize;
                tile1 = gm.tiles[nextBottomRow][heroLeftCol];
                tile2 = gm.tiles[nextBottomRow][heroRightCol];
                if (!tile1.isWalkable() || !tile2.isWalkable()) hero.collision = true;
                break;

            case "left":
                int nextLeftCol = (heroLeftX - hero.getHeroSpeed()) / tileSize;
                tile1 = gm.tiles[heroTopRow][nextLeftCol];
                tile2 = gm.tiles[heroBottomRow][nextLeftCol];
                if (!tile1.isWalkable() || !tile2.isWalkable()) hero.collision = true;
                break;

            case "right":
                int nextRightCol = (heroRightX + hero.getHeroSpeed()) / tileSize;
                tile1 = gm.tiles[heroTopRow][nextRightCol];
                tile2 = gm.tiles[heroBottomRow][nextRightCol];
                if (!tile1.isWalkable() || !tile2.isWalkable()) hero.collision = true;
                break;
        }
    }
}