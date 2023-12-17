package com.thatsoulyguy.terracraft.core;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window
{
    public static String title;
    public static Vector2i size;
    public static Vector3f color;
    public static Vector2i position;
    public static long window;

    private static GLFWFramebufferSizeCallback resizeWindow = new GLFWFramebufferSizeCallback()
    {
        @Override
        public void invoke(long window, int width, int height)
        {
            GL11.glViewport(0, 0, width, height);
        }
    };

    public static void Initialize()
    {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_SAMPLES, 16);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    }

    public static void Generate(String title, Vector2i size, Vector3f color)
    {
        Window.title = title;
        Window.size = size;
        Window.color = color;
        Window.position = new Vector2i(0, 0);

        window = glfwCreateWindow(size.x, size.y, title, NULL, NULL);

        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        Center();

        RegisterCallbacks();

        glfwMakeContextCurrent(window);

        GL.createCapabilities();

        glfwSwapInterval(1);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_MULTISAMPLE);
        glCullFace(GL_BACK);

        glfwShowWindow(window);
    }

    public static void RegisterCallbacks()
    {
        glfwSetFramebufferSizeCallback(window, resizeWindow);
        glfwSetKeyCallback(window, Input.keyboard);
        glfwSetCursorPosCallback(window, Input.mouseMovement);
        glfwSetMouseButtonCallback(window, Input.mouseButtons);
    }

    public static void Center()
    {
        try (MemoryStack stack = stackPush())
        {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode video = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert video != null;
            glfwSetWindowPos(window, (video.width() - pWidth.get(0)) / 2, (video.height() - pHeight.get(0)) / 2);
        }
    }

    public static void UpdateColors()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(color.x, color.y, color.z, 1.0f);

        try (MemoryStack stack = stackPush())
        {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            size.x = pWidth.get();
            size.y = pHeight.get();

            pWidth = stack.mallocInt(1);
            pHeight = stack.mallocInt(1);

            glfwGetWindowPos(window, pWidth, pHeight);

            position.x = pWidth.get();
            position.y = pHeight.get();
        }
    }

    public static void UpdateBuffers()
    {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public static boolean ShouldClose()
    {
        return glfwWindowShouldClose(window);
    }

    public static void CleanUp()
    {
        glfwDestroyWindow(window);

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}