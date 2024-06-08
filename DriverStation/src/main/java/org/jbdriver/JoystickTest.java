package org.jbdriver;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class JoystickTest {

    private long window;

    public static void main(String[] args) {
        new JoystickTest().run();
    }

    public void run() {
        init();
        loop();

        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    private void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        window = GLFW.glfwCreateWindow(640, 480, "Joystick Test", MemoryUtil.NULL, MemoryUtil.NULL);

        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            GLFW.glfwSetWindowPos(
                    window,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2
            );
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwShowWindow(window);

        GLFW.glfwSetJoystickCallback(new GLFWJoystickCallback() {
            @Override
            public void invoke(int jid, int event) {
                if (event == GLFW.GLFW_CONNECTED) {
                    System.out.println("Joystick connected: " + jid);
                } else if (event == GLFW.GLFW_DISCONNECTED) {
                    System.out.println("Joystick disconnected: " + jid);
                }
            }
        });
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            GLFW.glfwPollEvents();

            for (int jid = GLFW.GLFW_JOYSTICK_1; jid <= GLFW.GLFW_JOYSTICK_LAST; jid++) {
                if (GLFW.glfwJoystickPresent(jid)) {
                    FloatBuffer axes = GLFW.glfwGetJoystickAxes(jid);
                    ByteBuffer buttons = GLFW.glfwGetJoystickButtons(jid);
                    ByteBuffer hats = GLFW.glfwGetJoystickHats(jid);

                    if (axes != null) {
                        for (int i = 0; i < axes.limit(); i++) {
                            System.out.printf("Joystick %d Axis %d: %.2f%n", jid, i, axes.get(i));
                        }
                    }

                    if (buttons != null) {
                        for (int i = 0; i < buttons.limit(); i++) {
                            System.out.printf("Joystick %d Button %d: %b%n", jid, i, buttons.get(i) == GLFW.GLFW_PRESS);
                        }
                    }

                    if (hats != null) {
                        for (int i = 0; i < hats.limit(); i++) {
                            System.out.printf("Joystick %d Hat %d: %d%n", jid, i, hats.get(i));
                        }
                    }
                }
            }

            GLFW.glfwSwapBuffers(window);
        }
    }
}
