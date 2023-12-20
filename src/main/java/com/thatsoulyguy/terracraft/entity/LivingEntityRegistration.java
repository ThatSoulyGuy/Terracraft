package com.thatsoulyguy.terracraft.entity;

import org.joml.Vector3f;

public class LivingEntityRegistration
{
    public float health;
    public float maxHealth;
    public float movementSpeed;

    public boolean invertMovement;

    public static LivingEntityRegistration Register(float maxHealth, float movementSpeed)
    {
        return LivingEntityRegistration.Register(maxHealth, movementSpeed, false);
    }

    public static LivingEntityRegistration Register(float maxHealth, float movementSpeed, boolean invertMovement)
    {
        LivingEntityRegistration out = new LivingEntityRegistration();

        out.health = maxHealth;
        out.maxHealth = maxHealth;
        out.movementSpeed = movementSpeed;
        out.invertMovement = invertMovement;

        return out;
    }
}