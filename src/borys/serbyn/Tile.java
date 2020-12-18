package borys.serbyn;

import java.awt.*;

public record Tile(int x, int y, Color color) implements Cloneable {

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
}
