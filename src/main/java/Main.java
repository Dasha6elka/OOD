package main.java;

import main.java.canvas.Canvas;
import main.java.canvas.J2DCanvas;
import main.java.decorators.math.MathDecorator;
import main.java.decorators.math.MathDecoratorCircle;
import main.java.decorators.math.MathDecoratorRectangle;
import main.java.decorators.math.MathDecoratorTriangle;
import main.java.decorators.print.PrintDecorator;
import main.java.decorators.print.PrintDecoratorCircle;
import main.java.decorators.print.PrintDecoratorRectangle;
import main.java.decorators.print.PrintDecoratorTriangle;
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

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Main {
    private static final int FRAME_WIDTH = 1280;
    private static final int FRAME_HEIGHT = 720;
    private static final String FRAME_TITLE = "OOD";

    public static void main(String[] args) throws IOException {
        ArrayList<PrintDecorator> shapes = new ArrayList<>();
        final FileReader in = new FileReader(args[0]);
        final FileWriter out = new FileWriter(args[1]);
        try (Scanner scanner = new Scanner(in)) {
            while (scanner.hasNext()) {
                ArrayList<Point> points = new ArrayList<>();
                int circleRadius = 0;
                String shapeType = getShapeType(scanner);

                scanner.skip(":");

                String restOfLine = scanner.useDelimiter("\n").next();
                try (Scanner restOfLineScanner = new Scanner(restOfLine)) {
                    while (restOfLineScanner.hasNext()) {
                        String pointToken = restOfLineScanner.useDelimiter("=").next();
                        if (pointToken.contains("P") || pointToken.contains("C")) {
                            getPoint(points, restOfLineScanner);
                        } else if (pointToken.contains("R")) {
                            circleRadius = getCircleRadius(restOfLineScanner);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                addingNewShape(shapes, shapeType, points, circleRadius);
                in.close();
            }
            startUI(shapes, out);
            printResult(shapes, out);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getShapeType(Scanner scanner) {
        String shapeType = scanner.useDelimiter(":").next();
        shapeType = shapeType.replace("\n", "");
        return shapeType;
    }

    private static void getPoint(ArrayList<Point> points, Scanner restOfLineScanner) {
        restOfLineScanner.skip("=");

        String stringCoords = restOfLineScanner.useDelimiter(";").next();
        restOfLineScanner.skip(";");

        String[] splitterCoords = stringCoords.split(",");

        int x = Integer.parseInt(splitterCoords[0]);
        int y = Integer.parseInt(splitterCoords[1]);
        Point point = new Point(x, y);
        points.add(point);
    }

    private static int getCircleRadius(Scanner restOfLineScanner) {
        restOfLineScanner.skip("=");

        String stringRadius = restOfLineScanner.useDelimiter(";").next();
        restOfLineScanner.skip(";");

        return Integer.parseInt(stringRadius);
    }

    private static void addingNewShape(ArrayList<PrintDecorator> shapes, String shapeType, ArrayList<Point> points, int circleRadius) {
        switch (shapeType) {
            case "TRIANGLE":
                TriangleShape triangleShape = new TriangleShape(points.get(0), points.get(1), points.get(2));
                MathDecorator triangle = new MathDecoratorTriangle(
                        triangleShape,
                        points.get(0), points.get(1), points.get(2));
                PrintDecorator triangleForResult = new PrintDecoratorTriangle(
                        triangleShape,
                        points.get(0), points.get(1), points.get(2),
                        triangle.getPerimeter(),
                        triangle.getArea());
                shapes.add(triangleForResult);
                break;
            case "RECTANGLE":
                RectangleShape rectangleShape = new RectangleShape(points.get(0), points.get(1));
                MathDecorator rectangle = new MathDecoratorRectangle(
                        rectangleShape,
                        points.get(0), points.get(1));
                PrintDecorator rectangleForResult = new PrintDecoratorRectangle(
                        rectangleShape,
                        points.get(0), points.get(1),
                        rectangle.getPerimeter(),
                        rectangle.getArea());
                shapes.add(rectangleForResult);
                break;
            case "CIRCLE":
                CircleShape circleShape = new CircleShape(points.get(0), circleRadius);
                MathDecorator circle = new MathDecoratorCircle(
                        circleShape,
                        circleRadius);
                PrintDecorator circleForResult = new PrintDecoratorCircle(
                        circleShape,
                        points.get(0), circleRadius,
                        circle.getPerimeter(),
                        circle.getArea());
                shapes.add(circleForResult);
                break;
            default:
                break;
        }
    }

    private static void startUI(ArrayList<PrintDecorator> shapes, FileWriter out) {
        SwingUtilities.invokeLater(() -> {
            try {
                J2DCanvas canvas = new J2DCanvas();
                initUI(canvas);
                draw(shapes, canvas, out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void initUI(J2DCanvas panel) {
        JFrame frame = new JFrame();
        frame.setTitle(FRAME_TITLE);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setVisible(true);
    }

    private static void draw(List<PrintDecorator> shapes, Canvas canvas, FileWriter out) {
        for (PrintDecorator shape : shapes) {
            try {
                shape.draw(canvas, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printResult(ArrayList<PrintDecorator> shapes, FileWriter out) {
        for (PrintDecorator shape : shapes) {
            try {
                shape.print(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
