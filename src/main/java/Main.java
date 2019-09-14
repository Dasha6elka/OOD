package main.java;

import main.java.canvas.Canvas;
import main.java.canvas.CanvasPanel;
import main.java.canvas.J2DCanvas;
import main.java.shapes.CircleShape;
import main.java.shapes.RectangleShape;
import main.java.shapes.TriangleShape;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class Main {
    private static final int FRAME_WIDTH = 1280;
    private static final int FRAME_HEIGHT = 720;
    private static final String FRAME_TITLE = "OOD";

    public static void main(String[] args) throws IOException {
        ArrayList<main.java.shapes.Shape> shapes = new ArrayList<>();
        final FileReader in = new FileReader(args[0]);
        final FileWriter out = new FileWriter(args[1]);
        try (Scanner scanner = new Scanner(in)) {
            while (scanner.hasNext()) {
                String shapeType = scanner.useDelimiter(":").next();
                scanner.skip(":");

                ArrayList<Point> points = new ArrayList<>();
                int circleRadius = 0;

                String restOfLine = scanner.useDelimiter("\n").next();
                try (Scanner restOfLineScanner = new Scanner(restOfLine)) {
                    while (restOfLineScanner.hasNext()) {
                        String pointToken = restOfLineScanner.useDelimiter("=").next();
                        // TRIANGLE/RECTANGLE point
                        if (pointToken.contains("P") || pointToken.contains("C")) {
                            restOfLineScanner.skip("=");

                            String stringCoords = restOfLineScanner.useDelimiter(";").next();
                            restOfLineScanner.skip(";");

                            String[] splitterCoords = stringCoords.split(",");

                            int x = Integer.parseInt(splitterCoords[0]);
                            int y = Integer.parseInt(splitterCoords[1]);
                            Point point = new Point(x, y);
                            points.add(point);
                        }
                        // CIRCLE point
                        else if (pointToken.contains("R")) {
                            restOfLineScanner.skip("=");

                            String stringRadius = restOfLineScanner.useDelimiter(";").next();
                            restOfLineScanner.skip(";");

                            int radius = Integer.parseInt(stringRadius);
                            circleRadius = radius;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                shapeType = shapeType.replace("\n", "");
                switch (shapeType) {
                    case "TRIANGLE":
                        main.java.shapes.Shape triangle = new TriangleShape(points.get(0), points.get(1), points.get(2));
                        triangle = new PrintParameter(triangle);
                        shapes.add(triangle);
                        break;
                    case "RECTANGLE":
                        main.java.shapes.Shape rectangle = new RectangleShape(points.get(0), points.get(1));
                        rectangle = new PrintParameter(rectangle);
                        shapes.add(rectangle);
                        break;
                    case "CIRCLE":
                        main.java.shapes.Shape circle = new CircleShape(points.get(0), circleRadius);
                        circle = new PrintParameter(circle);
                        shapes.add(circle);
                        break;
                    default:
                        break;
                }
                in.close();
            }
            EventQueue.invokeLater(() -> {
                try {
                    J2DCanvas canvas = new J2DCanvas();
                    CanvasPanel panel = new CanvasPanel(canvas);
                    initUI(panel);
                    draw(shapes, canvas, out);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void initUI(CanvasPanel panel) {
        JFrame frame = new JFrame();
        frame.setTitle(FRAME_TITLE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        frame.add(panel);
    }

    private static void draw(List<main.java.shapes.Shape> shapes, Canvas canvas, FileWriter out) {
        shapes.forEach(shape -> {
            try {
                shape.draw(canvas, out);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
    }
}
