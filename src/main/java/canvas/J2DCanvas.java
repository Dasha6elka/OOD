package main.java.canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import static java.awt.Cursor.HAND_CURSOR;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.event.InputEvent.BUTTON3_DOWN_MASK;
import static java.awt.event.MouseEvent.BUTTON1;

public class J2DCanvas extends JComponent implements Canvas {
    private transient List<Item> items = new ArrayList<>();
    private boolean selecting = false;

    public J2DCanvas() {
        MouseAdapter mouseAdapter = new DragShapeAdapter();
        KeyAdapter keyAdapter = new KeyShapeAdapter();
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addKeyListener(keyAdapter);
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
        items.add(new SingleItem(path, fillColor, transform));
    }

    @Override
    public void drawCircle(Point center, double radius, Color fillColor) {
        Ellipse2D.Double ellipse = new Ellipse2D.Double(center.x, center.y, radius, radius);
        AffineTransform transform = AffineTransform.getTranslateInstance(0, 0);
        items.add(new SingleItem(ellipse, fillColor, transform));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        int selectionTopLeftX = Integer.MAX_VALUE;
        int selectionTopLeftY = Integer.MAX_VALUE;
        int selectionBottomRightX = Integer.MIN_VALUE;
        int selectionBottomRightY = Integer.MIN_VALUE;
        for (Item item : items) {
            AffineTransform saved = g2d.getTransform();
            item.paint(g2d);
            g2d.setTransform(saved);
            if (item.isSelected()) {
                Rectangle bounds = item.getBounds();
                Point2D itemTopLeft = item.getTransform().transform(new Point(bounds.x, bounds.y), null);
                Point2D itemBottomRight = item.getTransform().transform(new Point(bounds.x + bounds.width, bounds.y + bounds.height), null);
                selectionTopLeftX = (int) Math.min(selectionTopLeftX, itemTopLeft.getX());
                selectionTopLeftY = (int) Math.min(selectionTopLeftY, itemTopLeft.getY());
                selectionBottomRightX = (int) Math.max(selectionBottomRightX, itemBottomRight.getX());
                selectionBottomRightY = (int) Math.max(selectionBottomRightY, itemBottomRight.getY());
            }
        }
        if (selectionTopLeftX != 0 &&
                selectionTopLeftY != 0 &&
                selectionBottomRightX != 0 &&
                selectionBottomRightY != 0) {
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(
                    selectionTopLeftX - 2,
                    selectionTopLeftY - 2,
                    selectionBottomRightX - selectionTopLeftX + 2,
                    selectionBottomRightY - selectionTopLeftY + 2
            );
        }
    }

    private class KeyShapeAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                selecting = true;
            }
            if (selecting && e.getKeyCode() == KeyEvent.VK_G) {
                List<Item> group = items.stream().filter(Item::isSelected).collect(Collectors.toList());
                items = items.stream().filter(item -> !item.isSelected()).collect(Collectors.toList());
                CompositeItem composite = new CompositeItem(group);
                items.add(composite);
                repaint();
            }
            if (selecting && e.getKeyCode() == KeyEvent.VK_U) {
                var selected = items.stream().filter(Item::isSelected).findFirst();
                selected.ifPresent(item -> item.ungroup(items));
                repaint();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                selecting = false;
            }
        }
    }

    private class DragShapeAdapter extends MouseAdapter {
        private Item hoverItem;
        private boolean dragging;
        private Point prevPoint;
        private Cursor savedCursor;

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!dragging)
                return;
            // don't move item if both mouse buttons are pressed
            if ((e.getModifiersEx() & BUTTON3_DOWN_MASK) == 0) {
                Point2D point = e.getPoint();
                double tx = point.getX() - prevPoint.getX();
                double ty = point.getY() - prevPoint.getY();
                AffineTransform transform = AffineTransform.getTranslateInstance(tx, ty);
                transform.concatenate(hoverItem.getTransform());
                hoverItem.setTransform(transform);
                repaint();
            }
            prevPoint = new Point(e.getPoint());
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

        private Item findItem(Point2D point) {
            ListIterator<Item> iter = items.listIterator(items.size());
            while (iter.hasPrevious()) {
                Item item = iter.previous();
                if (item.containsPoint(point)) {
                    return item;
                }
            }
            return null;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == BUTTON1 && !dragging && hoverItem != null) {
                dragging = true;
                prevPoint = new Point(e.getPoint());
                var found = findItem(e.getPoint());
                if (found != null && selecting) {
                    if (found.isSelected()) {
                        found.unselect();
                    } else {
                        found.select();
                    }
                } else {
                    selecting = false;
                    for (Item item : items) {
                        item.unselect();
                    }
                }
            } else {
                selecting = false;
                for (Item item : items) {
                    item.unselect();
                }
            }
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == BUTTON1 && dragging) {
                dragging = false;
            }
        }
    }
}
