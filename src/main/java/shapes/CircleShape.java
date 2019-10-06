package main.java.shapes;

import java.awt.*;

public class CircleShape implements Shape {
    private int radius;
    private Point center;

    public CircleShape(Point center, int radius) {
        this.radius = radius;
        this.center = center;
    }

    public int getRadius() {
        return radius;
    }

    public Point getCenter() {
        return center;
    }
}
