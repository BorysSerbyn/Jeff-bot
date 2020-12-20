package ca.borysserbyn;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

public class Tile implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    private int x;
    private int y;
    private Color color;

    public Tile(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return x == tile.x &&
                y == tile.y &&
                Objects.equals(color, tile.color);
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
