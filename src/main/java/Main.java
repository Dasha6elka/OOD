package main.java;

import main.java.application.Application;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        var application = Application.getInstance();
        try {
            application.init(args[0], args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
