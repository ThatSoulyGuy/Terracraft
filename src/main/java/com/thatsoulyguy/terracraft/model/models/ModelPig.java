package com.thatsoulyguy.terracraft.model.models;

import com.thatsoulyguy.terracraft.model.EntityModel;
import com.thatsoulyguy.terracraft.model.ModelPart;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ModelPig extends EntityModel
{
    public void Initialize(Vector3f position)
    {
        EMBase_Initialize(position);
    }

    @Override
    public List<ModelPart> RegisterEntityParts()
    {
        List<ModelPart> parts = new ArrayList<>();

        ModelPart head = ModelPart.Register("head", "entity_pig", transform);
        head.AddCube(new Vector3f(-4, 8, -14), new Vector3f(8, 8, 8));
        head.AddCube(new Vector3f(-4, 6, -18.5f), new Vector3f(4, 3, 1));
        head.Done();
        parts.add(head);

        ModelPart body = ModelPart.Register("body", "entity_pig", transform);
        body.AddCube(new Vector3f(-4, 7, -5), new Vector3f(10, 8, 16));
        body.Done();
        parts.add(body);


        ModelPart topRightLeg = ModelPart.Register("topRightLeg", "entity_pig", transform);
        topRightLeg.AddCube(new Vector3f(-1, 0, -10), new Vector3f(4, 6, 4));
        topRightLeg.Done();
        parts.add(topRightLeg);

        ModelPart topLeftLeg = ModelPart.Register("topLeftLeg", "entity_pig", transform);
        topLeftLeg.AddCube(new Vector3f(-7, 0, -10), new Vector3f(4, 6, 4));
        topLeftLeg.Done();
        parts.add(topLeftLeg);


        ModelPart bottomRightLeg = ModelPart.Register("bottomRightLeg", "entity_pig", transform);
        bottomRightLeg.AddCube(new Vector3f(-1, 0, 2), new Vector3f(4, 6, 4));
        bottomRightLeg.Done();
        parts.add(bottomRightLeg);

        ModelPart bottomLeftLeg = ModelPart.Register("bottomLeftLeg", "entity_pig", transform);
        bottomLeftLeg.AddCube(new Vector3f(-7, 0, 2), new Vector3f(4, 6, 4));
        bottomLeftLeg.Done();
        parts.add(bottomLeftLeg);

        return parts;
    }

    @Override
    public void RegisterRenderableParts()
    {
        parts.get("head").RenderToBuffer();
        parts.get("body").RenderToBuffer();

        parts.get("topRightLeg").RenderToBuffer();
        parts.get("topLeftLeg").RenderToBuffer();

        parts.get("bottomRightLeg").RenderToBuffer();
        parts.get("bottomLeftLeg").RenderToBuffer();
    }
}