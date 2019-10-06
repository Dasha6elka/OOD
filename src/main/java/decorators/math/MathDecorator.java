package main.java.decorators.math;

import main.java.shapes.Shape;

public abstract class MathDecorator implements Shape {
    protected Shape shape;

    MathDecorator(Shape shape) {
        this.shape = shape;
    }

    public abstract int getPerimeter();

    public abstract int getArea();
}
