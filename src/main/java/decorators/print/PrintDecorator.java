package main.java.decorators.print;

import main.java.canvas.Canvas;
import main.java.shapes.Shape;

import java.io.FileWriter;
import java.io.IOException;

public abstract class PrintDecorator implements Shape {
    private Shape shape;

    PrintDecorator(Shape shape) {
        this.shape = shape;
    }

    public abstract void draw(Canvas canvas, FileWriter out) throws IOException;

    public abstract void print(FileWriter out) throws IOException;
}
