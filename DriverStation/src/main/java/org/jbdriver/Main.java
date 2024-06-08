package org.jbdriver;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Main {

    public static void main(String[] args) {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        Controller joystick = null;

        // Find a joystick
        for (Controller controller : controllers) {
            if (controller.getType() == Controller.Type.STICK) {
                joystick = controller;
                break;
            }
        }

        if (joystick == null) {
            System.out.println("No joystick found.");
            System.exit(0);
        }

        while (true) {
            joystick.poll();
            Component[] components = joystick.getComponents();

            for (Component component : components) {
                Component.Identifier identifier = component.getIdentifier();

                // Axes
                if (identifier instanceof Component.Identifier.Axis) {
                    System.out.printf("%s: %.2f%n", identifier.getName(), component.getPollData());
                }

                // Buttons
                if (identifier instanceof Component.Identifier.Button) {
                    System.out.printf("%s: %b%n", identifier.getName(), component.getPollData() == 1.0f);
                }

                // POV Hat Switch
                if (identifier == Component.Identifier.Axis.POV) {
                    float povValue = component.getPollData();
                    String direction = "CENTER";

                    if (povValue == Component.POV.LEFT) {
                        direction = "LEFT";
                    } else if (povValue == Component.POV.RIGHT) {
                        direction = "RIGHT";
                    } else if (povValue == Component.POV.UP) {
                        direction = "UP";
                    } else if (povValue == Component.POV.DOWN) {
                        direction = "DOWN";
                    }
                    System.out.printf("POV: %s%n", direction);
                }
            }

            // Sleep to prevent flooding the output
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
