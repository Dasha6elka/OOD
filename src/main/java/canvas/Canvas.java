package main.java.canvas;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public interface Canvas {
    CanvasShape findCanvasShape(Point2D point);

    List<CanvasShape> getCanvasShapes();

    void drawPolygon(List<Point> points, Color fillColor);

    void drawCircle(Point center, double radius, Color fillColor);

    void repaint();
}


