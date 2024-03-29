package main.java.decorators.print;

import main.java.canvas.Canvas;
import main.java.shapes.Shape;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class PrintDecoratorCircle extends PrintDecorator {
    private int radius;
    private Point center;
    private int perimeter;
    private int area;

    public PrintDecoratorCircle(Shape shape, Point center, int radius, int perimeter, int area) {
        super(shape);
        this.radius = radius;
        this.center = center;
        this.perimeter = perimeter;
        this.area = area;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(center, radius, shape.getColor());
    }

    @Override
    public void print(FileWriter out) throws IOException {
        String output = "Circle: P=" + perimeter + "; S=" + area + "\n";
        out.write(output);
    }
}
