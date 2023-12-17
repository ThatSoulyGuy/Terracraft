package com.thatsoulyguy.terracraft.render;

import org.joml.*;

public class Vertex
{
    public Vector3f position;
    public Vector3f color;
    public Vector2f textureCoordinates;

    public static Vertex Register(Vector3f position, Vector2f textureCoordinates)
    {
        return Vertex.Register(position, new Vector3f(1.0f, 1.0f, 1.0f), textureCoordinates);
    }

    public static Vertex Register(Vector3f position, Vector3f color, Vector2f textureCoordinates)
    {
        Vertex out = new Vertex();

        out.position = position;
        out.color = color;
        out.textureCoordinates = textureCoordinates;

        return out;
    }
}