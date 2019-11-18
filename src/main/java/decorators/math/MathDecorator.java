package main.java.decorators.math;

import main.java.canvas.Canvas;
import main.java.shapes.Shape;

import java.awt.*;
import java.io.IOException;

public abstract class MathDecorator implements Shape {
    protected Shape shape;

    MathDecorator(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void draw(Canvas canvas) throws IOException {
        shape.draw(canvas);
    }

    @Override
    public void setColor(Color color) {
        shape.setColor(color);
    }

    @Override
    public Color getColor() {
        return shape.getColor();
    }

    @Override
    public Rectangle getBounds() {
        return shape.getBounds();
    }

    public abstract int getPerimeter();

    public abstract int getArea();
}
