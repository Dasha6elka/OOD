package main.java.decorators.math;

import main.java.Point;
import main.java.shapes.Shape;

public class MathDecoratorRectangle extends MathDecorator {
    private Point topLeftVertex;
    private Point bottomRightVertex;

    public MathDecoratorRectangle(Shape shape, Point topLeftVertex, Point bottomRightVertex) {
        super(shape);
        this.topLeftVertex = topLeftVertex;
        this.bottomRightVertex = bottomRightVertex;
    }

    @Override
    public int getPerimeter() {
        return (int) (2 * (bottomRightVertex.x - topLeftVertex.x + bottomRightVertex.y - topLeftVertex.y));
    }

    @Override
    public int getArea() {
        return (int) ((bottomRightVertex.x - topLeftVertex.x) * (bottomRightVertex.y - topLeftVertex.y));
    }
}
