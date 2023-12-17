package com.thatsoulyguy.terracraft.math;

import com.thatsoulyguy.terracraft.world.Chunk;
import com.thatsoulyguy.terracraft.world.World;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Raycast
{
    public static Vector3i Shoot(Vector3f origin, Vector3f direction, float maxDistance)
    {
        Vector3f ray = new Vector3f(origin);
        Vector3f step = new Vector3f(direction).normalize();

        for (float t = 0; t < maxDistance; t += 1.0f)
        {
            ray.add(step);

            Chunk chunk = World.GetChunk(new Vector3f(ray));

            if (chunk != null)
            {
                Vector3i blockPos = Chunk.WorldToBlockCoordinates(new Vector3f(ray));

                if (chunk.HasBlock(blockPos))
                    return new Vector3i((int)Math.floor(ray.x), (int)Math.floor(ray.y), (int)Math.floor(ray.z));
            }
        }

        return null;
    }
}