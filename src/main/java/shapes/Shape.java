package main.java.shapes;

import main.java.canvas.Canvas;

import java.awt.*;
import java.io.IOException;

public interface Shape {
    void draw(Canvas canvas) throws IOException;

    Color getColor();

    void setColor(Color color);

    Rectangle getBounds();
}
