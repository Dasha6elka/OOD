package main.java.canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static java.awt.Cursor.HAND_CURSOR;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.event.InputEvent.BUTTON3_DOWN_MASK;
import static java.awt.event.MouseEvent.BUTTON1;

public class J2DCanvas extends JComponent implements Canvas {
    private transient List<Item> items;
    private transient Item selection;

    public J2DCanvas() {
        items = new ArrayList<>();
        DragShape dragShape = new DragShape();
        addMouseListener(dragShape);
        addMouseMotionListener(dragShape);
    }

    @Override
    public void drawPolygon(List<Point> points, Color fillColor) {
        Path2D.Double path = new Path2D.Double();
        ListIterator<Point> it = points.listIterator();
        Point first = it.next();
        path.moveTo(first.x, first.y);
        while (it.hasNext()) {
            Point current = it.next();
            path.lineTo(current.x, current.y);
        }
        path.closePath();
        AffineTransform transform = AffineTransform.getTranslateInstance(0, 0);
        items.add(new Item(path, fillColor, transform));
    }

    @Override
    public void drawCircle(Point center, double radius, Color fillColor) {
        Ellipse2D.Double ellipse = new Ellipse2D.Double(center.x, center.y, radius, radius);
        AffineTransform transform = AffineTransform.getTranslateInstance(0, 0);
        items.add(new Item(ellipse, fillColor, transform));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        for (Item item : items) {
            AffineTransform saved = g2d.getTransform();
            g2d.transform(item.transform);
            g2d.setColor(item.color);
            g2d.fill(item.shape);
            g2d.setTransform(saved);
        }
        if (selection != null) {
            AffineTransform saved = g2d.getTransform();
            g2d.transform(selection.transform);
            var bounds = selection.shape.getBounds();
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(bounds.x - 2, bounds.y - 2, bounds.width + 2, bounds.height + 2);
            g2d.setTransform(saved);
        }
    }

    private static class Item {
        Shape shape;
        Color color;
        AffineTransform transform;

        Item(Shape shape, Color color, AffineTransform transform) {
            this.shape = shape;
            this.color = color;
            this.transform = transform;
        }
    }

    private class DragShape extends MouseAdapter {
        private Item hoverItem;
        private boolean isDragging;
        private Point previous;
        private Cursor savedCursor;

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!isDragging)
                return;
            // don't move item if both mouse buttons are pressed
            if ((e.getModifiersEx() & BUTTON3_DOWN_MASK) == 0) {
                Point2D point = e.getPoint();
                Point2D prevPoint = previous;
                double tx = point.getX() - prevPoint.getX();
                double ty = point.getY() - prevPoint.getY();
                AffineTransform transform = AffineTransform.getTranslateInstance(tx, ty);
                transform.concatenate(hoverItem.transform);
                hoverItem.transform = transform;
                if (selection != null) {
                    selection.transform = transform;
                }
                repaint();
            }
            previous = new Point(e.getPoint());
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Point2D point = e.getPoint();
            Item item = findItem(point);
            if (hoverItem == null && item != null) {
                savedCursor = getCursor();
                setCursor(Cursor.getPredefinedCursor(HAND_CURSOR));
            } else if (hoverItem != null && item == null) {
                setCursor(savedCursor);
            }
            hoverItem = item;
        }

        @SuppressWarnings("ConstantConditions")
        private Item findItem(Point2D point) {
            // process items in the order reversed to drawing order
            ListIterator<Item> iter = items.listIterator(items.size());
            while (iter.hasPrevious()) {
                Item item = iter.previous();
                AffineTransform transform = item.transform;
                Shape shape = item.shape;
                Point2D transformed = null;
                try {
                    if (shape.contains(transform.inverseTransform(point, transformed)))
                        return item;
                } catch (NoninvertibleTransformException ignored) {
                    // noop
                }
            }
            return null;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == BUTTON1 && !isDragging && hoverItem != null) {
                isDragging = true;
                previous = new Point(e.getPoint());
                var item = findItem(e.getPoint());
                if (item != null) {
                    Rectangle itemBounds = item.shape.getBounds();
                    Point2D itemBottomRight = item.transform.transform(new Point(itemBounds.x + itemBounds.width, itemBounds.y + itemBounds.height), null);
                    Point2D itemTopLeft = item.transform.transform(new Point(itemBounds.x, itemBounds.y), null);
                    double bottomRightX = itemBottomRight.getX();
                    double bottomRightY = itemBottomRight.getY();
                    if (selection != null) {
                        Rectangle selectionBounds = selection.shape.getBounds();
                        Point2D selectionBottomRight = selection.transform.transform(new Point(selectionBounds.x + selectionBounds.width, selectionBounds.y + selectionBounds.height), null);
                        Point2D selectionTopLeft = selection.transform.transform(new Point(selectionBounds.x, selectionBounds.y), null);
                        double selectionTopLeftX = Math.min(selectionTopLeft.getX(), itemTopLeft.getX());
                        double selectionTopLeftY = Math.min(selectionTopLeft.getY(), itemTopLeft.getY());
                        itemTopLeft.setLocation(selectionTopLeftX, selectionTopLeftY);
                        bottomRightX = Math.max(itemBottomRight.getX(), selectionBottomRight.getX());
                        bottomRightY = Math.max(itemBottomRight.getY(), selectionBottomRight.getY());
                    }
                    Rectangle2D.Double rec = new Rectangle2D.Double(
                            itemTopLeft.getX(),
                            itemTopLeft.getY(),
                            bottomRightX - itemTopLeft.getX(),
                            bottomRightY - itemTopLeft.getY()
                    );
                    selection = new Item(rec, Color.BLUE, AffineTransform.getTranslateInstance(0, 0));
                }
            } else {
                selection = null;
            }
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == BUTTON1 && isDragging) {
                isDragging = false;
            }
        }
    }
}
