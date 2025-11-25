package model;

public class Bomba {
    private int x;
    private int y;
    private double timeTilExplosion;
    public boolean exploded = false;


    public Bomba(int x, int y, double timeTilExplosion) {
        this.x = x;
        this.y = y;
        this.timeTilExplosion = timeTilExplosion;
    }

    public void explode() {
        exploded = true;
        System.out.println("Exploded");
    }

    public void decreaseTime (double delta) {
        if (exploded == true) return;
        timeTilExplosion -= delta;
        if (timeTilExplosion <= 0) {
            explode();
        }
    }

    public int getX() {return x;}

    public int getY() {return y;}
}
