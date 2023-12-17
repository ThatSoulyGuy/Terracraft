package com.thatsoulyguy.terracraft.entity;

import org.joml.Vector3f;

public class LivingEntityRegistration
{
    public float health;
    public float maxHealth;
    public float movementSpeed;
    public Vector3f aabbDimensions;

    public static LivingEntityRegistration Register(float maxHealth, float movementSpeed, Vector3f aabbDimensions)
    {
        LivingEntityRegistration out = new LivingEntityRegistration();

        out.health = maxHealth;
        out.maxHealth = maxHealth;
        out.movementSpeed = movementSpeed;
        out.aabbDimensions = aabbDimensions;

        return out;
    }
}