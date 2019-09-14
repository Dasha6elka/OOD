package main.java.shapes;

import main.java.canvas.Canvas;
import main.java.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class RectangleShape extends Shape {
    private Point mTopLeftVertex;
    private Point mBottomRightVertex;
    private int mFillColor = 0;
    private int mOutlineColor = 0;


    public RectangleShape(Point topLeftVertex, Point bottomRightVertex) {
        mTopLeftVertex = topLeftVertex;
        mBottomRightVertex = bottomRightVertex;
    }

    @Override
    public int getPerimeter() {
        return (int) (2 * (mBottomRightVertex.x - mTopLeftVertex.x + mBottomRightVertex.y - mTopLeftVertex.y));
    }

    @Override
    public int getArea() {
        return (int) ((mBottomRightVertex.x - mTopLeftVertex.x) * (mBottomRightVertex.y - mTopLeftVertex.y));
    }

    @Override
    public void draw(Canvas canvas, FileWriter out) {
        Point rightTop = new Point(mBottomRightVertex.x, mTopLeftVertex.y);
        Point leftBottom = new Point(mTopLeftVertex.x, mBottomRightVertex.y);
        canvas.drawLine(mTopLeftVertex, rightTop, mOutlineColor);
        canvas.drawLine(rightTop, mBottomRightVertex, mOutlineColor);
        canvas.drawLine(mBottomRightVertex, leftBottom, mOutlineColor);
        canvas.drawLine(leftBottom, mTopLeftVertex, mOutlineColor);
        canvas.fillPolygon(Arrays.asList(mTopLeftVertex, rightTop, mBottomRightVertex, leftBottom), mFillColor);
    }

    @Override
    public void print(FileWriter out) throws IOException {

    }

    @Override
    public String getName() {
        return "Rectangle";
    }
}
