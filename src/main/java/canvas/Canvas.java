package main.java.canvas;

import java.awt.*;
import java.util.List;

public interface Canvas {
    void drawPolygon(List<Point> points, Color fillColor);

    void drawCircle(Point center, double radius, Color fillColor);
}


