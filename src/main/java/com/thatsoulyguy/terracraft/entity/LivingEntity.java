package com.thatsoulyguy.terracraft.entity;

import com.thatsoulyguy.terracraft.core.Input;
import com.thatsoulyguy.terracraft.core.LogLevel;
import com.thatsoulyguy.terracraft.core.Logger;
import com.thatsoulyguy.terracraft.core.PressType;
import com.thatsoulyguy.terracraft.math.AABB;
import com.thatsoulyguy.terracraft.math.Transform;
import org.joml.Math;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public abstract class LivingEntity extends Entity
{
    private static final float JUMP_FORCE = 5.2f;

    public float health;
    public float maxHealth;

    public float movementSpeed;

    public void LEBase_Initialize()
    {
        EBase_Initialize(null, null, true);

        LivingEntityRegistration registration = Register();

        health = registration.health;
        maxHealth = registration.maxHealth;
        movementSpeed = registration.movementSpeed;

        boundingBox = AABB.Register(new Vector3f(transform.position.x + 0.5f, transform.position.y, transform.position.z + 0.5f), registration.aabbDimensions);
        boundingBox.Update(transform.position);
    }

    public void LEBase_Update()
    {
        EBase_Update();
    }

    public void AddMovement(MovementImpulse direction)
    {
        float moveStep = movementSpeed;
        Vector3f potentialPosition = new Vector3f(transform.position);

        float yawRadians = (float) Math.toRadians(transform.rotation.y);

        Vector3f forward = new Vector3f(
                (float) -Math.sin(yawRadians),
                0,
                (float) Math.cos(yawRadians)
        ).normalize();

        Vector3f right = new Vector3f(
                forward.z,
                0,
                -forward.x
        ).normalize();

        if (direction == MovementImpulse.FORWARD || direction == MovementImpulse.BACKWARD)
        {
            float directionMultiplier = (direction == MovementImpulse.FORWARD) ? 1 : -1;
            Vector3f movement = new Vector3f(right).mul(moveStep * directionMultiplier);
            potentialPosition.add(movement);
        }

        if (direction == MovementImpulse.RIGHT || direction == MovementImpulse.LEFT)
        {
            float directionMultiplier = (direction == MovementImpulse.RIGHT) ? 1 : -1;
            Vector3f movement = new Vector3f(forward).mul(moveStep * directionMultiplier);
            potentialPosition.add(movement);
        }

        ResolveCollisionsUnified(potentialPosition);
        transform.position.set(potentialPosition);
    }

    public void Jump()
    {
        boolean onGround = IsOnGround();

        if (onGround)
            verticalVelocity = JUMP_FORCE;

        Logger.WriteConsole(onGround ? "on ground " + transform.position.y : "OFF GROUND " + transform.position.y, LogLevel.DEBUG);
    }

    public abstract LivingEntityRegistration Register();
}