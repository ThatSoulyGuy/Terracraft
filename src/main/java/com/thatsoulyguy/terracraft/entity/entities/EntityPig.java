package com.thatsoulyguy.terracraft.entity.entities;

import com.thatsoulyguy.terracraft.entity.LivingEntity;
import com.thatsoulyguy.terracraft.entity.LivingEntityRegistration;
import com.thatsoulyguy.terracraft.entity.MovementImpulse;
import com.thatsoulyguy.terracraft.model.models.ModelPig;
import org.joml.Vector3f;

public class EntityPig extends LivingEntity
{
    public ModelPig model = new ModelPig();

    public void Initialize(Vector3f position)
    {
        transform.position = position;

        model.Initialize(position);
        LEBase_Initialize();
    }

    @Override
    public void Update()
    {
        transform.Rotate(new Vector3f(0,  0.002f, 0));
        AddMovement(MovementImpulse.FORWARD);

        LEBase_Update();

        model.transform.position = new Vector3f(transform.position.x, transform.position.y + 0.2f, transform.position.z);
        model.transform.rotation = transform.rotation;
        model.Update();
    }

    @Override
    public LivingEntityRegistration Register()
    {
        return LivingEntityRegistration.Register(5.0f, 0.008f, new Vector3f(1.0f, 0.98f, 1.0f));
    }
}