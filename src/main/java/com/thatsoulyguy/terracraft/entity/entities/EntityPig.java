package com.thatsoulyguy.terracraft.entity.entities;

import com.thatsoulyguy.terracraft.entity.*;
import com.thatsoulyguy.terracraft.math.AABB;
import com.thatsoulyguy.terracraft.model.models.ModelPig;
import org.joml.Vector3f;

public class EntityPig extends LivingEntity
{
    public ModelPig model = new ModelPig();

    public void Initialize(Vector3f position)
    {
        LEBase_Initialize(new Vector3f(position));
        transform.position = new Vector3f(position);

        model.Initialize(new Vector3f(position));
    }

    @Override
    public void Update()
    {
        transform.Rotate(new Vector3f(0,  0.002f, 0));
        AddMovement(MovementImpulse.FORWARD);

        LEBase_Update();

        model.transform.position = transform.position;
        model.transform.rotation = transform.rotation;
        model.Update();
    }

    @Override
    public LivingEntityRegistration LE_Register()
    {
        return LivingEntityRegistration.Register(5.0f, 2f);
    }

    @Override
    public EntityRegistration E_Register()
    {
        return EntityRegistration.Register("", EntityType.ENTITY_PIG, AABB.Register(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)));
    }
}