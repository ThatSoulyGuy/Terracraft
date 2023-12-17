package com.thatsoulyguy.terracraft.core;

import com.thatsoulyguy.terracraft.util.TypeHelper;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Input
{
    private static final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static final boolean[] keysLast = new boolean[GLFW.GLFW_KEY_LAST];
    private static final boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

    public static Vector2f mousePosition;

    public static GLFWKeyCallback keyboard;
    public static GLFWCursorPosCallback mouseMovement;
    public static GLFWMouseButtonCallback mouseButtons;

    public static void Initialize()
    {
        mousePosition = new Vector2f(0.0f, 0.0f);


        keyboard = new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods)
            {
                boolean isPressed = TypeHelper.Int2Bool(action);
                keys[key] = isPressed;
                keysLast[key] = !isPressed;
            }
        };

        mouseMovement = new GLFWCursorPosCallback()
        {
            public void invoke(long window, double xpos, double ypos)
            {
                mousePosition.x = (float)xpos;
                mousePosition.y = (float)ypos;
            }
        };

        mouseButtons = new GLFWMouseButtonCallback()
        {
            public void invoke(long window, int button, int action, int mods)
            {
                buttons[button] = TypeHelper.Int2Bool(action);
            }
        };
    }
    public static void SetCursorMode(boolean value)
    {
        if (!value)
            GLFW.glfwSetInputMode(Window.window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        else
            GLFW.glfwSetInputMode(Window.window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

    public static boolean GetKey(int key, PressType type)
    {
        if(type == PressType.PRESSED)
            return keys[key];
        else
            return !keys[key];
    }

    public static boolean GetKeyOnce(int key)
    {
        boolean pressedNow = GetKey(key, PressType.PRESSED);

        boolean wasPressedLast = keysLast[key];
        keysLast[key] = pressedNow;

        return pressedNow && !wasPressedLast;
    }

    public static boolean GetMouseButton(int button, PressType type)
    {
        if(type == PressType.PRESSED)
            return buttons[button];
        else
            return !buttons[button];
    }

    public static void Update()
    {
        System.arraycopy(keys, 0, keysLast, 0, GLFW.GLFW_KEY_LAST);
    }

    public static void CleanUp()
    {
        keyboard.free();
        mouseMovement.free();
        mouseButtons.free();
    }
}