package org.jblib;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class JoystickReader {

    public static void main(String[] args) {
        // Find the joystick
        Controller joystick = null;
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
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

        System.out.println("Joystick found: " + joystick.getName());

        while (true) {
            // Poll the joystick
            joystick.poll();

            // Get the joystick components (axes and buttons)
            Component[] components = joystick.getComponents();
            for (Component component : components) {
                String name = component.getName();
                float value = component.getPollData();

                // Check if the component is an axis or a button
                if (component.isAnalog()) {
                    System.out.printf("Axis %s: %f%n", name, value);
                } else {
                    System.out.printf("Button %s: %b%n", name, value != 0.0f);
                }
            }

            // Sleep for a bit to avoid spamming the output
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
