package com.thatsoulyguy.terracraft.math;

import org.joml.Vector3f;

public class Transform
{
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

    public void Translate(Vector3f translation)
    {
        position.add(translation);
    }

    public void Rotate(Vector3f rotation)
    {
        this.rotation.add(rotation);
    }

    public static Transform Register(Vector3f position)
    {
        return Transform.Register(position, new Vector3f(0, 0, 0));
    }

    public static Transform Register(Vector3f position, Vector3f rotation)
    {
        Transform out = new Transform();

        out.position = position;
        out.rotation = rotation;

        return out;
    }
}