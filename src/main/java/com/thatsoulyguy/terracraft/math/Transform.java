package com.thatsoulyguy.terracraft.math;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform
{
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;
    public Vector3f pivot;
    public Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

    public void Translate(Vector3f translation)
    {
        position.add(translation);
    }

    public void Rotate(Vector3f rotation)
    {
        this.rotation.add(rotation);
    }

    public Transform Copy()
    {
        return Transform.Register(new Vector3f(this.position), new Vector3f(this.rotation), new Vector3f(this.scale));
    }


    public static Transform Register(Vector3f position)
    {
        return Transform.Register(position, new Vector3f(0, 0, 0), new Vector3f(1.0f, 1.0f, 1.0f));
    }
    public static Transform Register(Vector3f position, Vector3f rotation)
    {
        return Transform.Register(position, rotation, new Vector3f(1.0f, 1.0f, 1.0f));
    }

    public static Transform Register(Vector3f position, Vector3f rotation, Vector3f scale)
    {
        Transform out = new Transform();

        out.position = position;
        out.rotation = rotation;
        out.pivot = new Vector3f(position);
        out.scale = scale;

        return out;
    }
}