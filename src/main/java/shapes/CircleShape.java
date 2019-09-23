package main.java.shapes;

import main.java.Point;

public class CircleShape implements Shape {
    private int mRadius;
    private Point mCenter;

    public CircleShape(Point center, int radius) {
        mRadius = radius;
        mCenter = center;
    }

    public int getRadius() {
        return mRadius;
    }

    public Point getCenter() {
        return mCenter;
    }
}
