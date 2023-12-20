package com.thatsoulyguy.terracraft.model;

import com.thatsoulyguy.terracraft.math.Transform;
import com.thatsoulyguy.terracraft.render.RenderableObject;
import com.thatsoulyguy.terracraft.render.Renderer;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EntityModel {
    public Map<String, ModelPart> parts = new HashMap<>();
    public Transform transform = Transform.Register(new Vector3f(0, 0, 0));

    public void EMBase_Initialize(Vector3f position)
    {
        transform.position = position;

        var modelPartsRaw = RegisterEntityParts();

        for (ModelPart part : modelPartsRaw)
            parts.put(part.name, part);

        RegisterRenderableParts();
    }

    public void Update()
    {
        for (ModelPart part : parts.values())
        {
            part.transform.position = transform.position;
            part.transform.rotation = transform.rotation;
            part.Update();
        }
    }

    public ModelPart GetPart(String name)
    {
        return parts.get(name);
    }

    public abstract List<ModelPart> RegisterEntityParts();

    public abstract void RegisterRenderableParts();
}