package com.thatsoulyguy.terracraft.entity;

import com.thatsoulyguy.terracraft.core.Settings;
import com.thatsoulyguy.terracraft.math.AABB;
import com.thatsoulyguy.terracraft.math.Transform;
import com.thatsoulyguy.terracraft.world.Chunk;
import com.thatsoulyguy.terracraft.world.World;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class Entity
{
    public String name;
    public EntityType type;
    public String uuid;

    public AABB boundingBox;

    public Transform transform;

    public float lastFrameTime;
    public float deltaTime;

    public float verticalVelocity = 0.0f;

    public void EBase_Initialize(Vector3f position)
    {
        transform = Transform.Register(new Vector3f(position));

        EntityRegistration registration = E_Register();

        name = registration.name;
        type = registration.type;
        uuid = registration.uuid;
        boundingBox = registration.boundingBox;
    }

    public void EBase_Update()
    {
        float currentTime = (float) GLFW.glfwGetTime();
        deltaTime = currentTime - lastFrameTime;
        lastFrameTime = currentTime;

        UpdatePhysics();

        Vector3f originalPosition = new Vector3f(transform.position);
        Vector3f newPosition = new Vector3f(transform.position);
        Vector3f collisionResponse = ProcessCollisions(newPosition);

        newPosition.add(collisionResponse);

        if (collisionResponse.x != 0) transform.position.x = newPosition.x;
        if (collisionResponse.y != 0) transform.position.y = newPosition.y;
        if (collisionResponse.z != 0) transform.position.z = newPosition.z;

        if (collisionResponse.y == 0 && verticalVelocity < 0)
            transform.position.y = originalPosition.y + (verticalVelocity * deltaTime);

        boundingBox.Update(transform.position);
    }

    public abstract EntityRegistration E_Register();

    public boolean IsOnGround()
    {
        Vector3f checkPosition = new Vector3f(transform.position.x, transform.position.y - boundingBox.GetHalfSize().y - 0.01f, transform.position.z);

        AABB tempAABB = AABB.Register(boundingBox.position, boundingBox.dimensions);
        tempAABB.Update(checkPosition);

        return ProcessRegularBlockCollisions(checkPosition, tempAABB);
    }

    protected Vector3f ProcessCollisions(Vector3f newPosition)
    {
        Vector3i chunkCoords = World.WorldToChunkCoordinates(newPosition);
        List<Vector3i> chunksToCheck = GetChunksAround(chunkCoords);
        Vector3f collisionResponse = new Vector3f(0, 0, 0);

        for (Vector3i chunkCoord : chunksToCheck)
        {
            Chunk chunk = World.chunks.get(chunkCoord);
            if (chunk == null) continue;

            for (AABB blockAABB : chunk.data.blockAABBs.values())
            {
                if (boundingBox.IsColliding(blockAABB))
                {
                    Vector3f response = AvoidCollision(boundingBox, blockAABB, newPosition);
                    collisionResponse.add(response);
                }
            }
        }

        return collisionResponse;
    }

    protected Vector3f AvoidCollision(AABB entityAABB, AABB blockAABB, Vector3f newPosition)
    {
        float dx1 = blockAABB.max.x - entityAABB.min.x;
        float dx2 = blockAABB.min.x - entityAABB.max.x;
        float dy1 = blockAABB.max.y - entityAABB.min.y;
        float dy2 = blockAABB.min.y - entityAABB.max.y;
        float dz1 = blockAABB.max.z - entityAABB.min.z;
        float dz2 = blockAABB.min.z - entityAABB.max.z;

        float dx = (Math.abs(dx1) < Math.abs(dx2)) ? dx1 : dx2;
        float dy = (Math.abs(dy1) < Math.abs(dy2)) ? dy1 : dy2;
        float dz = (Math.abs(dz1) < Math.abs(dz2)) ? dz1 : dz2;

        if (Math.abs(dx) < Math.abs(dy) && Math.abs(dx) < Math.abs(dz))
            return new Vector3f(dx, 0.0f, 0.0f);
         else if (Math.abs(dy) < Math.abs(dz))
            return new Vector3f(0.0f, dy, 0.0f);
        else
            return new Vector3f(0.0f, 0.0f, dz);
    }

    private List<Vector3i> GetChunksAround(Vector3i chunkCoords)
    {
        List<Vector3i> chunks = new ArrayList<>();
        for (int x = -1; x <= 1; x++)
        {
            for (int y = -1; y <= 1; y++)
            {
                for (int z = -1; z <= 1; z++)
                    chunks.add(new Vector3i(chunkCoords.x + x, chunkCoords.y + y, chunkCoords.z + z));
            }
        }

        return chunks;
    }

    public abstract void Update();

    public void UpdatePhysics()
    {
        verticalVelocity += Settings.GRAVITY * deltaTime;
        float verticalMovement = verticalVelocity * deltaTime;
        int steps = (int) Math.ceil(Math.abs(verticalMovement / 0.1f));
        float stepMovement = verticalMovement / steps;

        for (int i = 0; i < steps; i++)
        {
            Vector3f potentialPosition = new Vector3f(transform.position.x, transform.position.y + stepMovement, transform.position.z);
            AABB tempAABB = AABB.Register(boundingBox.position, boundingBox.dimensions);
            tempAABB.Update(potentialPosition);

            if (CheckVerticalCollision(potentialPosition, tempAABB))
            {
                verticalVelocity = 0.0f;
                break;
            }

            transform.position.y = potentialPosition.y;
        }
    }

    protected boolean ProcessRegularBlockCollisions(Vector3f newPosition, AABB entityAABB)
    {
        Vector3i chunkCoords = World.WorldToChunkCoordinates(newPosition);
        List<Vector3i> chunksToCheck = GetChunksAround(chunkCoords);

        for (Vector3i chunkCoord : chunksToCheck)
        {
            Chunk chunk = World.chunks.get(chunkCoord);
            if (chunk == null) continue;

            for (AABB blockAABB : chunk.data.blockAABBs.values())
            {
                if (entityAABB.IsColliding(blockAABB))
                    return true;
            }
        }

        return false;
    }

    private boolean CheckVerticalCollision(Vector3f position, AABB tempAABB)
    {
        return ProcessRegularBlockCollisions(position, tempAABB);
    }
}