package main.java.shapes;

import main.java.Point;

public class RectangleShape implements Shape {
    private Point mTopLeftVertex;
    private Point mBottomRightVertex;

    public RectangleShape(Point topLeftVertex, Point bottomRightVertex) {
        mTopLeftVertex = topLeftVertex;
        mBottomRightVertex = bottomRightVertex;
    }

    public Point getTopLeftVertex() {
        return mTopLeftVertex;
    }

    public Point getBottomRightVertex() {
        return mBottomRightVertex;
    }
}
