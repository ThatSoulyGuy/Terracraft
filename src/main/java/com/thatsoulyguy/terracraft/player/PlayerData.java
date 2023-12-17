package com.thatsoulyguy.terracraft.player;

import com.thatsoulyguy.terracraft.math.AABB;
import com.thatsoulyguy.terracraft.math.Transform;
import com.thatsoulyguy.terracraft.render.RenderableObject;
import org.joml.*;

public class PlayerData
{
    public Camera camera = new Camera();

    public float mouseSensitivity = 0.08f;
    public float moveSpeed = 0.1f;
    public RenderableObject wireframeBox;
    public Vector2f oldMouse = new Vector2f(0, 0), newMouse = new Vector2f(0, 0);
}