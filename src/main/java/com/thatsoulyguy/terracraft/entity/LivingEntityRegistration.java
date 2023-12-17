package com.thatsoulyguy.terracraft.entity;

import org.joml.Vector3f;

public class LivingEntityRegistration
{
    public float health;
    public float maxHealth;
    public float movementSpeed;

    public static LivingEntityRegistration Register(float maxHealth, float movementSpeed)
    {
        LivingEntityRegistration out = new LivingEntityRegistration();

        out.health = maxHealth;
        out.maxHealth = maxHealth;
        out.movementSpeed = movementSpeed;

        return out;
    }
}