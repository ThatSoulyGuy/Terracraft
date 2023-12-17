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
            float directionMultiplier = (direction == MovementImpulse.FORWARD) ? movementSpeed : -movementSpeed;
            Vector3f movement = new Vector3f(right).mul(moveStep * directionMultiplier);
            potentialPosition.add(movement);
        }

        if (direction == MovementImpulse.RIGHT || direction == MovementImpulse.LEFT)
        {
            float directionMultiplier = (direction == MovementImpulse.RIGHT) ? movementSpeed : -movementSpeed;
            Vector3f movement = new Vector3f(forward).mul(moveStep * directionMultiplier);
            potentialPosition.add(movement);
        }

        if (!ProcessRegularBlockCollisions(potentialPosition, boundingBox))
            transform.position.set(potentialPosition);
    }

    public void Jump()
    {
        boolean onGround = IsOnGround();

        if (onGround)
            verticalVelocity = Settings.JUMP_FORCE;
    }
}