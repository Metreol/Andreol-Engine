package Andreol;

import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/*
Most of this code is taken/adapted from the LWJGL Getting Started page: https://www.lwjgl.org/guide
 */
public class Window {
    private int width, height;
    private String title;
    // This long is the address in memory where the window is.
    private long glfwWindow;

    private static Window window = null;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "MyGame";
    }

    public static Window get() {
        if (window == null) {
            window = new Window();
        }
        return window;
    }

    public void run() {
        System.out.println("Hello LWJGL" + Version.getVersion() + "!");

        init();
        loop();

        cleanUp();
    }

    public void init() {
        // Set up an ERROR callback.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialise GLFW (Game Library Framework)
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to init GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Only make it visible once it's finished being created.
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the Window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW Window.");
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable V-Sync - Means no restriction, no wait time between frames, goes as fast as we can.
        glfwSwapInterval(1);

        // Make window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
    }

    public void loop() {
        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            glClearColor(1f, 1f, 1f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);

            glfwSwapBuffers(glfwWindow);
        }
   }

   // I believe the system SHOULD free up memory but the LWJGL docs include this
   private void cleanUp() {
       // Free memory
       Callbacks.glfwFreeCallbacks(glfwWindow);
       glfwDestroyWindow(glfwWindow);

       // Terminate GLFW and free the Error callback
       glfwTerminate();
       glfwSetErrorCallback(null).free();
   }
}
