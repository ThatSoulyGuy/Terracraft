package com.thatsoulyguy.terracraft.math;

import org.joml.Vector3f;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AABB
{
    public Vector3f min;
    public Vector3f max;
    public Vector3f dimensions;
    public Vector3f position;

    public boolean IsColliding(AABB other)
    {
        return (this.min.x <= other.max.x && this.max.x >= other.min.x) &&
                (this.min.y <= other.max.y && this.max.y >= other.min.y) &&
                (this.min.z <= other.max.z && this.max.z >= other.min.z);
    }

    public void Update(Vector3f position)
    {
        this.min = new Vector3f(
                position.x - dimensions.x / 2,
                position.y,
                position.z - dimensions.z / 2
        );

        this.max = new Vector3f(
                position.x + dimensions.x / 2,
                position.y + dimensions.y,
                position.z + dimensions.z / 2
        );

        this.position = position;
    }

    public Vector3f GetHalfSize()
    {
        return new Vector3f(dimensions.x / 2, dimensions.y / 2, dimensions.z / 2);
    }

    public static AABB Register(Vector3f position, Vector3f dimensions)
    {
        AABB out = new AABB();

        out.dimensions = dimensions;
        out.Update(position);

        return out;
    }
}