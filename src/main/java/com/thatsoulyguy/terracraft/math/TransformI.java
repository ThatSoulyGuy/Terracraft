package com.thatsoulyguy.terracraft.math;

import org.joml.Vector3f;
import org.joml.Vector3i;

public class TransformI
{
    public Vector3i position;
    public Vector3i rotation;
    public Vector3i up = new Vector3i(0, 1, 0);

    public void Translate(Vector3i translation)
    {
        position.add(translation);
    }

    public void Rotate(Vector3i rotation)
    {
        this.rotation.add(rotation);
    }

    public Transform ToTransform()
    {
        Vector3f positionF = new Vector3f((float)position.x, (float)position.y, (float)position.z);
        Vector3f rotationF = new Vector3f((float)rotation.x, (float)rotation.y, (float)rotation.z);

        return Transform.Register(positionF, rotationF);
    }

    public static TransformI Register(Vector3i position)
    {
        return TransformI.Register(position, new Vector3i(0, 0, 0));
    }

    public static TransformI Register(Vector3i position, Vector3i rotation)
    {
        TransformI out = new TransformI();

        out.position = position;
        out.rotation = rotation;

        return out;
    }
}