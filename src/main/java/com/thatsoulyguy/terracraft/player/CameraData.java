package com.thatsoulyguy.terracraft.player;

import com.thatsoulyguy.terracraft.math.Transform;
import org.joml.*;

public class CameraData
{
    public float fov;
    public float nearPlane, farPlane;

    public Transform transform = Transform.Register(new Vector3f(0.0f, 0.0f, 0.0f));
    public float pitch = 0, yaw = 0;
}