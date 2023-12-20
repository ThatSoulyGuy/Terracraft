package com.thatsoulyguy.terracraft.model;

import com.thatsoulyguy.terracraft.math.Transform;
import com.thatsoulyguy.terracraft.player.Player;
import com.thatsoulyguy.terracraft.records.NameIDTag;
import com.thatsoulyguy.terracraft.render.RenderableObject;
import com.thatsoulyguy.terracraft.render.Renderer;
import com.thatsoulyguy.terracraft.render.Vertex;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelPart
{
    public String name;
    public String texture;
    public Transform transform;
    private int indicesIndex = 0;

    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Integer> indices = new ArrayList<>();

    private RenderableObject object;

    public void Update()
    {
        object.data.transform.position = transform.position;
        object.data.transform.rotation = transform.rotation;
    }

    public ModelPart SetPivot(Vector3f pivot)
    {
        transform.pivot = pivot;

        return this;
    }

    public ModelPart AddCube(Vector3f position, Vector3f size)
    {
        return AddCube(position, size, new Vector2f[]
        {
            new Vector2f(0.0f, 0.0f),
            new Vector2f(0.0f, 1.0f),
            new Vector2f(1.0f, 1.0f),
            new Vector2f(1.0f, 0.0f)
        });
    }

    public ModelPart AddCube(Vector3f position, Vector3f size, Vector2f[] textureCoordinates)
    {
        Vector3f scaledSize = new Vector3f(size).mul(1.0f);
        Vector3f halfSize = new Vector3f(scaledSize).mul(0.5f);

        Vector3f[] positions = new Vector3f[]
        {
            new Vector3f(-halfSize.x,  halfSize.y,  halfSize.z),
            new Vector3f( halfSize.x,  halfSize.y,  halfSize.z),
            new Vector3f( halfSize.x, -halfSize.y,  halfSize.z),
            new Vector3f(-halfSize.x, -halfSize.y,  halfSize.z),
            new Vector3f(-halfSize.x,  halfSize.y, -halfSize.z),
            new Vector3f( halfSize.x,  halfSize.y, -halfSize.z),
            new Vector3f( halfSize.x, -halfSize.y, -halfSize.z),
            new Vector3f(-halfSize.x, -halfSize.y, -halfSize.z)
        };

        for (int i = 0; i < positions.length; i++)
            positions[i].add(position);

        vertices.add(Vertex.Register(positions[4], textureCoordinates[0]));
        vertices.add(Vertex.Register(positions[5], textureCoordinates[1]));
        vertices.add(Vertex.Register(positions[1], textureCoordinates[2]));
        vertices.add(Vertex.Register(positions[0], textureCoordinates[3]));

        vertices.add(Vertex.Register(positions[3], textureCoordinates[0]));
        vertices.add(Vertex.Register(positions[2], textureCoordinates[1]));
        vertices.add(Vertex.Register(positions[6], textureCoordinates[2]));
        vertices.add(Vertex.Register(positions[7], textureCoordinates[3]));

        vertices.add(Vertex.Register(positions[0], textureCoordinates[0]));
        vertices.add(Vertex.Register(positions[1], textureCoordinates[1]));
        vertices.add(Vertex.Register(positions[2], textureCoordinates[2]));
        vertices.add(Vertex.Register(positions[3], textureCoordinates[3]));

        vertices.add(Vertex.Register(positions[4], textureCoordinates[0]));
        vertices.add(Vertex.Register(positions[5], textureCoordinates[1]));
        vertices.add(Vertex.Register(positions[6], textureCoordinates[2]));
        vertices.add(Vertex.Register(positions[7], textureCoordinates[3]));

        vertices.add(Vertex.Register(positions[4], textureCoordinates[0]));
        vertices.add(Vertex.Register(positions[0], textureCoordinates[1]));
        vertices.add(Vertex.Register(positions[3], textureCoordinates[2]));
        vertices.add(Vertex.Register(positions[7], textureCoordinates[3]));

        vertices.add(Vertex.Register(positions[1], textureCoordinates[0]));
        vertices.add(Vertex.Register(positions[5], textureCoordinates[1]));
        vertices.add(Vertex.Register(positions[6], textureCoordinates[2]));
        vertices.add(Vertex.Register(positions[2], textureCoordinates[3]));

        for (int i = 0; i < 6; i++)
        {
            int startIndex = indicesIndex + i * 4;

            indices.add(startIndex);
            indices.add(startIndex + 1);
            indices.add(startIndex + 2);
            indices.add(startIndex);
            indices.add(startIndex + 2);
            indices.add(startIndex + 3);
        }

        indicesIndex += 24;

        return this;
    }

    public void Done()
    {
        object.RegisterTexture(texture);
        object.RegisterData(vertices, indices);
        object.Generate();
        object.data.cullBackfaces = false;
        object.data.transform = transform.Copy();
        object.data.transform.scale = new Vector3f(0.07f, 0.07f, 0.07f );
    }

    public void RenderToBuffer()
    {
        Renderer.RegisterRenderableObject(object);
    }

    public static ModelPart Register(String name, String texture, Transform transform)
    {
        ModelPart out = new ModelPart();

        out.name = name;
        out.texture = texture;
        out.transform = transform;
        out.object = RenderableObject.Register(NameIDTag.Register(name, out.object), null, null);

        return out;
    }
}