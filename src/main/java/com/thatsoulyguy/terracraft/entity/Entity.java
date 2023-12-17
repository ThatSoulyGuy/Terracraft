package com.thatsoulyguy.terracraft.entity;

import com.thatsoulyguy.terracraft.core.LogLevel;
import com.thatsoulyguy.terracraft.core.Logger;
import com.thatsoulyguy.terracraft.math.AABB;
import com.thatsoulyguy.terracraft.math.Raycast;
import com.thatsoulyguy.terracraft.math.Transform;
import com.thatsoulyguy.terracraft.world.Chunk;
import com.thatsoulyguy.terracraft.world.World;
import org.joml.Math;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

public abstract class Entity
{
    public static final float GRAVITY = -9.81f;
    public float verticalVelocity = 0.0f;

    public Transform transform = Transform.Register(new Vector3f(0.0f, 0.0f, 0.0f));

    public float lastFrameTime;
    public float deltaTime;

    public AABB boundingBox;

    public void EBase_Initialize(Vector3f position, Vector3f aabbDimensions)
    {
        EBase_Initialize(position, aabbDimensions, false);
    }

    public void EBase_Initialize(Vector3f position, Vector3f aabbDimensions, boolean isNull)
    {
        if(isNull)
            return;

        transform.position = new Vector3f(position);

        boundingBox = AABB.Register(transform.position, aabbDimensions);
        boundingBox.Update(transform.position);
    }

    public void EBase_Update()
    {
        float currentTime = (float) GLFW.glfwGetTime();
        deltaTime = currentTime - lastFrameTime;
        lastFrameTime = currentTime;

        UpdatePhysics();
        boundingBox.Update(transform.position);
    }

    private void UpdatePhysics()
    {
        verticalVelocity += GRAVITY * deltaTime;

        float verticalMove = verticalVelocity * deltaTime;

        int steps = (int)Math.ceil(Math.abs(verticalMove) / 0.1f);
        float stepMove = verticalMove / steps;

        for (int i = 0; i < steps; i++)
        {
            Vector3f potentialPosition = new Vector3f(transform.position.x, transform.position.y + stepMove, transform.position.z);

            if (!ResolveCollisionsSingleAxis2(potentialPosition, 'y'))
            {
                verticalVelocity = 0.0f;
                break;
            }

            transform.position.y = potentialPosition.y;
        }
    }

    private boolean ResolveCollisionsSingleAxis2(Vector3f potentialPosition, char axis)
    {
        Vector3i playerChunkCoordinates = World.WorldToChunkCoordinates(potentialPosition);
        Chunk playerChunk = World.chunks.get(playerChunkCoordinates);

        if (playerChunk != null)
        {
            for (AABB blockAABB : playerChunk.data.blockAABBs.values())
            {
                if (boundingBox.IsColliding(blockAABB))
                {
                    if (axis == 'y' && boundingBox.IsColliding(blockAABB))
                        potentialPosition.y = blockAABB.max.y + boundingBox.GetHalfSize().y;

                    ResolveCollision(potentialPosition, boundingBox, blockAABB, axis);
                    return false;
                }
            }
        }

        return true;
    }

    public abstract void Update();

    public boolean IsOnGround()
    {
        Vector3f checkPosition = new Vector3f(transform.position.x, transform.position.y - boundingBox.GetHalfSize().y - 0.1f, transform.position.z);
        Vector3i chunkCoordinates = World.WorldToChunkCoordinates(checkPosition);

        Chunk chunk = World.chunks.get(chunkCoordinates);

        if (chunk != null)
        {
            Vector3i blockUnderPlayer = Chunk.WorldToBlockCoordinates(checkPosition);
            return chunk.HasBlock(blockUnderPlayer);
        }

        return false;
    }

    protected void ResolveCollisionsUnified(Vector3f potentialPosition)
    {
        Vector3i playerChunkCoordinates = World.WorldToChunkCoordinates(potentialPosition);
        Chunk playerChunk = World.chunks.get(playerChunkCoordinates);

        if (playerChunk != null)
        {
            for (AABB blockAABB : playerChunk.data.blockAABBs.values())
            {
                if (boundingBox.IsColliding(blockAABB))
                {
                    Vector3f collisionResponse = CalculateCollisionResponse(boundingBox, blockAABB);
                    potentialPosition.add(collisionResponse);
                }
            }
        }
    }

    protected void ResolveCollisionsSingleAxis(Vector3f potentialPosition, char axis)
    {
        Vector3i playerChunkCoordinates = World.WorldToChunkCoordinates(potentialPosition);
        Chunk playerChunk = World.chunks.get(playerChunkCoordinates);

        if (playerChunk != null)
        {
            for (AABB blockAABB : playerChunk.data.blockAABBs.values())
            {
                if (boundingBox.IsColliding(blockAABB))
                {
                    ResolveCollision(potentialPosition, boundingBox, blockAABB, axis);
                    break;
                }
            }
        }
    }

    protected void ResolveCollision(Vector3f playerPosition, AABB playerAABB, AABB blockAABB, char axis)
    {
        float dx1 = blockAABB.max.x - playerAABB.min.x;
        float dx2 = blockAABB.min.x - playerAABB.max.x;
        float dy1 = blockAABB.max.y - playerAABB.min.y;
        float dy2 = blockAABB.min.y - playerAABB.max.y;
        float dz1 = blockAABB.max.z - playerAABB.min.z;
        float dz2 = blockAABB.min.z - playerAABB.max.z;

        float dx = Math.abs(dx1) < Math.abs(dx2) ? dx1 : dx2;
        float dy = Math.abs(dy1) < Math.abs(dy2) ? dy1 : dy2;
        float dz = Math.abs(dz1) < Math.abs(dz2) ? dz1 : dz2;

        switch (axis)
        {
            case 'x':
                if (Math.abs(dx) < Math.abs(dy) && Math.abs(dx) < Math.abs(dz))
                    playerPosition.x += dx;
                break;

            case 'y':
                //if (Math.abs(dy) < Math.abs(dx) && Math.abs(dy) < Math.abs(dz))
                    playerPosition.y += dy;
                break;

            case 'z':
                if (Math.abs(dz) < Math.abs(dx) && Math.abs(dz) < Math.abs(dy))
                    playerPosition.z += dz;
                break;
        }

        Logger.WriteConsole(playerPosition.y + " compare " + transform.position.y, LogLevel.DEBUG);
    }

    protected Vector3f CalculateCollisionResponse(AABB playerAABB, AABB blockAABB)
    {
        Vector3f response = new Vector3f();

        float dx1 = blockAABB.max.x - playerAABB.min.x;
        float dx2 = blockAABB.min.x - playerAABB.max.x;
        float dy1 = blockAABB.max.y - playerAABB.min.y;
        float dy2 = blockAABB.min.y - playerAABB.max.y;
        float dz1 = blockAABB.max.z - playerAABB.min.z;
        float dz2 = blockAABB.min.z - playerAABB.max.z;

        float dx = Math.abs(dx1) < Math.abs(dx2) ? dx1 : dx2;
        float dy = Math.abs(dy1) < Math.abs(dy2) ? dy1 : dy2;
        float dz = Math.abs(dz1) < Math.abs(dz2) ? dz1 : dz2;

        if (Math.abs(dx) < Math.abs(dy) && Math.abs(dx) < Math.abs(dz))
            response.x = dx;
        else if (Math.abs(dy) < Math.abs(dz))
            response.y = dy;
        else
            response.z = dz;

        return response;
    }
}