package main.java.decorators.math;

import main.java.shapes.Shape;

public class MathDecoratorCircle extends MathDecorator {
    private int radius;

    public MathDecoratorCircle(Shape shape, int radius) {
        super(shape);
        this.radius = radius;
    }

    @Override
    public int getPerimeter() {
        return (int) (2 * Math.PI * radius);
    }

    @Override
    public int getArea() {
        return (int) (Math.PI * Math.pow(radius, 2));
    }
}
