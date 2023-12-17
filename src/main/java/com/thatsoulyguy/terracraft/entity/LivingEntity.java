package com.thatsoulyguy.terracraft.entity;

import com.thatsoulyguy.terracraft.core.LogLevel;
import com.thatsoulyguy.terracraft.core.Logger;
import com.thatsoulyguy.terracraft.core.Settings;
import org.joml.Math;
import org.joml.Vector3f;

public abstract class LivingEntity extends Entity
{
    public float health;
    public float maxHealth;

    public float movementSpeed;

    public void LEBase_Initialize(Vector3f position)
    {
        EBase_Initialize(position);

        LivingEntityRegistration registration = LE_Register();

        health = registration.health;
        maxHealth = registration.maxHealth;
        movementSpeed = registration.movementSpeed;
    }

    public void LEBase_Update()
    {
        EBase_Update();
    }

    public abstract LivingEntityRegistration LE_Register();

    public void AddMovement(MovementImpulse direction)
    {
        float moveStep = movementSpeed * deltaTime;
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
            potentialPosition.add(right.mul(moveStep * directionMultiplier));
        }

        if (direction == MovementImpulse.RIGHT || direction == MovementImpulse.LEFT)
        {
            float directionMultiplier = (direction == MovementImpulse.RIGHT) ? 1 : -1;
            potentialPosition.add(forward.mul(moveStep * directionMultiplier));
        }

        Vector3f collisionResponse = ProcessCollisions(potentialPosition);
        potentialPosition.add(collisionResponse);

        transform.position.set(potentialPosition);
        boundingBox.Update(transform.position);
    }

    public void Jump()
    {
        boolean onGround = IsOnGround();

        if (onGround)
            verticalVelocity = Settings.JUMP_FORCE;
    }
}