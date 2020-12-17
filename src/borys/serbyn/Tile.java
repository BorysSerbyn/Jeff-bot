package borys.serbyn;

import java.awt.*;

public class Tile implements Cloneable{
    private int x;
    private int y;
    private Color color;

    public Tile(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public Object clone() {
        try {
            return (Tile) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Tile(this.x, this.y, this.getColor());
        }
    }

    @Override
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }
}
