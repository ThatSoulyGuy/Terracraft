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
        model.Initialize(new Vector3f(position));
    }

    @Override
    public void Update()
    {
        LEBase_Update();

        transform.Rotate(new Vector3f(0, 0.006f, 0));
        AddMovement(MovementImpulse.FORWARD);

        model.transform.position = new Vector3f(transform.position.x, transform.position.y - 0.3f, transform.position.z);
        model.transform.rotation = transform.rotation;

        //model.GetPart("head").transform.Rotate(new Vector3f(0.05f, 0.002f, 0.0042f));

        model.Update();
    }

    @Override
    public LivingEntityRegistration LE_Register()
    {
        return LivingEntityRegistration.Register(5.0f, 0.8f);
    }

    @Override
    public EntityRegistration E_Register()
    {
        return EntityRegistration.Register("", EntityType.ENTITY_PIG, AABB.Register(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)));
    }
}