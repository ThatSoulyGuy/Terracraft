package com.thatsoulyguy.terracraft.player;

import com.thatsoulyguy.terracraft.core.Window;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera
{
    public CameraData data = new CameraData();

    public Matrix4f projection = new Matrix4f().identity();
    public Matrix4f view = new Matrix4f().identity();

    public void Initialize(Vector3f position)
    {
        Initialize(position, 45.0f, 0.004f, 500.0f);
    }

    public void Initialize(Vector3f position, float fov, float nearPlane, float farPlane)
    {
        data.transform.position = position;
        data.fov = fov;
        data.nearPlane = nearPlane;
        data.farPlane = farPlane;

        projection.identity().perspective(Math.toRadians(data.fov), ((float)Window.size.x / (float)Window.size.y), data.nearPlane, data.farPlane);
    }

    public void Update()
    {
        projection.identity().perspective(Math.toRadians(data.fov), ((float)Window.size.x / (float)Window.size.y), data.nearPlane, data.farPlane);

        Vector3f direction = new Vector3f(
                Math.cos(Math.toRadians(data.yaw)) * (float) Math.cos(Math.toRadians(data.pitch)),
                Math.sin(Math.toRadians(data.pitch)),
                Math.sin(Math.toRadians(data.yaw)) * (float) Math.cos(Math.toRadians(data.pitch))
        );

        data.transform.rotation = direction.normalize();

        view.identity().lookAt(data.transform.position, new Vector3f(data.transform.position).add(direction), data.transform.up);
    }
}