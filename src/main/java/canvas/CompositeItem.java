package main.java.canvas;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;

public class CompositeItem implements Item {
    private List<Item> items;

    CompositeItem(List<Item> items) {
        this.items = items;
    }

    @Override
    public void paint(Graphics2D g2d) {
        items.forEach(item -> item.paint(g2d));
    }

    @Override
    public boolean isSelected() {
        return items.get(0).isSelected();
    }

    @Override
    public boolean containsPoint(Point2D point) {
        for (Item item : items) {
            if (item.containsPoint(point)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AffineTransform getTransform() {
        return items.get(0).getTransform();
    }

    @Override
    public void setTransform(AffineTransform transform) {
        for (Item item : items) {
            item.setTransform(transform);
        }
    }

    @Override
    public Color getColor() {
        return items.get(0).getColor();
    }

    @Override
    public Rectangle getBounds() {
        Rectangle result = new Rectangle();
        int selectionTopLeftX = Integer.MAX_VALUE;
        int selectionTopLeftY = Integer.MAX_VALUE;
        int selectionBottomRightX = Integer.MIN_VALUE;
        int selectionBottomRightY = Integer.MIN_VALUE;
        for (Item item : items) {
            Rectangle bounds = item.getBounds();
            Point2D itemTopLeft = item.getTransform().transform(new Point(bounds.x, bounds.y), null);
            Point2D itemBottomRight = item.getTransform().transform(new Point(bounds.x + bounds.width, bounds.y + bounds.height), null);
            selectionTopLeftX = (int) Math.min(selectionTopLeftX, itemTopLeft.getX());
            selectionTopLeftY = (int) Math.min(selectionTopLeftY, itemTopLeft.getY());
            selectionBottomRightX = (int) Math.max(selectionBottomRightX, itemBottomRight.getX());
            selectionBottomRightY = (int) Math.max(selectionBottomRightY, itemBottomRight.getY());
        }
        result.x = selectionTopLeftX;
        result.y = selectionTopLeftY;
        result.height = selectionBottomRightY - selectionTopLeftY;
        result.width = selectionBottomRightX - selectionTopLeftX;
        return result;
    }

    @Override
    public void select() {
        items.forEach(Item::select);
    }

    @Override
    public void unselect() {
        items.forEach(Item::unselect);
    }

    @Override
    public void ungroup(List<Item> items) {
        items.removeIf(item -> item == this);
        items.addAll(this.items);
    }
}
