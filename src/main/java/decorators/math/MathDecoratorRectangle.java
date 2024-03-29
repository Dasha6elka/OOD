package main.java.decorators.math;

import main.java.shapes.Shape;

import java.awt.*;

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
        return 2 * (bottomRightVertex.x - topLeftVertex.x + bottomRightVertex.y - topLeftVertex.y);
    }

    @Override
    public int getArea() {
        return (bottomRightVertex.x - topLeftVertex.x) * (bottomRightVertex.y - topLeftVertex.y);
    }
}
