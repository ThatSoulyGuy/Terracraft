package com.thatsoulyguy.terracraft.render;

import com.thatsoulyguy.terracraft.core.LogLevel;
import com.thatsoulyguy.terracraft.core.Logger;
import com.thatsoulyguy.terracraft.records.NameIDTag;
import com.thatsoulyguy.terracraft.thread.TaskExecutor;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class RenderableObject implements Cloneable
{
    public RenderableData data = new RenderableData();

    private int StoreDataAsFloat(FloatBuffer buffer, int index, int size)
    {
        int out = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, out);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        data.queuedData++;

        int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR)
            Logger.WriteConsole("OpenGL error detected: " + error, LogLevel.ERROR);

        return out;
    }
    
    public void GenerateCube()
    {
        ArrayList<Vertex> vertices = new ArrayList<>();

        vertices.add(Vertex.Register(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 0.0f)));
        vertices.add(Vertex.Register(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector2f(0.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector2f(1.0f, 0.0f)));

        vertices.add(Vertex.Register(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 0.0f)));
        vertices.add(Vertex.Register(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector2f(0.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 0.0f)));

        vertices.add(Vertex.Register(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 0.0f)));
        vertices.add(Vertex.Register(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector2f(1.0f, 0.0f)));

        vertices.add(Vertex.Register(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector2f(0.0f, 0.0f)));
        vertices.add(Vertex.Register(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector2f(0.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f( 0.5f, -0.5f, -0.5f), new Vector2f(1.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f( 0.5f,  0.5f, -0.5f), new Vector2f(1.0f, 0.0f)));


        vertices.add(Vertex.Register(new Vector3f(0.5f,  0.5f, -0.5f), new Vector2f(0.0f, 0.0f)));
        vertices.add(Vertex.Register(new Vector3f(0.5f, -0.5f, -0.5f), new Vector2f(0.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f(0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f(0.5f,  0.5f,  0.5f), new Vector2f(1.0f, 0.0f)));

        vertices.add(Vertex.Register(new Vector3f(-0.5f,  0.5f, -0.5f), new Vector2f(0.0f, 0.0f)));
        vertices.add(Vertex.Register(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector2f(0.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(1.0f, 0.0f)));

        ArrayList<Integer> indices = new ArrayList<>();

        indices.add(0);
        indices.add(2);
        indices.add(1);
        indices.add(0);
        indices.add(3);
        indices.add(2);

        indices.add(4);
        indices.add(5);
        indices.add(6);
        indices.add(4);
        indices.add(6);
        indices.add(7);

        indices.add(8);
        indices.add(9);
        indices.add(10);
        indices.add(8);
        indices.add(10);
        indices.add(11);

        indices.add(12);
        indices.add(14);
        indices.add(13);
        indices.add(12);
        indices.add(15);
        indices.add(14);

        indices.add(16);
        indices.add(18);
        indices.add(17);
        indices.add(16);
        indices.add(19);
        indices.add(18);

        indices.add(20);
        indices.add(21);
        indices.add(22);
        indices.add(20);
        indices.add(22);
        indices.add(23);

        RegisterTexture("wireframe");
        RegisterData(vertices, indices);
        Generate();
    }

    public void GenerateSquare()
    {
        ArrayList<Vertex> vertices = new ArrayList<>();

        vertices.add(Vertex.Register(new Vector3f(-0.5f,  0.5f,  0.5f), new Vector2f(0.0f, 0.0f)));
        vertices.add(Vertex.Register(new Vector3f(-0.5f, -0.5f,  0.5f), new Vector2f(0.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f( 0.5f, -0.5f,  0.5f), new Vector2f(1.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f( 0.5f,  0.5f,  0.5f), new Vector2f(1.0f, 0.0f)));

        ArrayList<Integer> indices = new ArrayList<>();

        indices.add(0);
        indices.add(1);
        indices.add(3);

        indices.add(3);
        indices.add(1);
        indices.add(2);

        RegisterTexture("block");
        RegisterData(vertices, indices);
        Generate();
    }

    public void GenerateTriangle()
    {
        ArrayList<Vertex> vertices = new ArrayList<>();

        vertices.add(Vertex.Register(new Vector3f( 0.0f,  0.5f,  0.0f), new Vector2f(0.0f, 0.0f)));
        vertices.add(Vertex.Register(new Vector3f(-0.5f, -0.5f,  0.0f), new Vector2f(0.0f, 1.0f)));
        vertices.add(Vertex.Register(new Vector3f( 0.5f, -0.5f,  0.0f), new Vector2f(1.0f, 0.0f)));

        ArrayList<Integer> indices = new ArrayList<>();

        indices.add(0);
        indices.add(1);
        indices.add(2);

        RegisterTexture("block");
        RegisterData(vertices, indices);
        Generate();
    }

    public void RegisterTexture(String texture)
    {
        Texture textureReference = TextureManager.GetTexture(texture);

        assert textureReference != null;
        data.textures.put(textureReference.name, textureReference);

        data.textures.get(texture).Generate(TextureProperties.Register(TextureWrapping.REPEAT, TextureFiltering.NEAREST));
    }

    public void ReGenerate()
    {
        data.queuedData = -1;

        TaskExecutor.QueueTask(() ->
        {
            data.VAO = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(data.VAO);
        });

        FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(data.vertices.size() * 3);
        float[] positionData = new float[data.vertices.size() * 3];

        for (int v = 0; v < data.vertices.size(); v++)
        {
            positionData[v * 3] = data.vertices.get(v).position.x;
            positionData[v * 3 + 1] = data.vertices.get(v).position.y;
            positionData[v * 3 + 2] = data.vertices.get(v).position.z;
        }

        positionBuffer.put(positionData).flip();
        TaskExecutor.QueueTask(() ->
            data.VBO = StoreDataAsFloat(positionBuffer, 0, 3));

        FloatBuffer colorBuffer = MemoryUtil.memAllocFloat(data.vertices.size() * 3);
        float[] colorData = new float[data.vertices.size() * 3];

        for (int c = 0; c < data.vertices.size(); c++)
        {
            colorData[c * 3] = data.vertices.get(c).color.x;
            colorData[c * 3 + 1] = data.vertices.get(c).color.y;
            colorData[c * 3 + 2] = data.vertices.get(c).color.z;
        }

        colorBuffer.put(colorData).flip();
        TaskExecutor.QueueTask(() ->
            data.CBO = StoreDataAsFloat(colorBuffer, 1, 3));

        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(data.vertices.size() * 2);
        float[] textureData = new float[data.vertices.size() * 2];

        for (int t = 0; t < data.vertices.size(); t++)
        {
            textureData[t * 2] = data.vertices.get(t).textureCoordinates.x;
            textureData[t * 2 + 1] = data.vertices.get(t).textureCoordinates.y;
        }

        textureBuffer.put(textureData).flip();
        TaskExecutor.QueueTask(() ->
            data.TBO = StoreDataAsFloat(textureBuffer, 2, 2));

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(data.indices.size());
        indicesBuffer.put(data.indices.stream().mapToInt(i -> i).toArray()).flip();

        TaskExecutor.QueueTask(() ->
        {
            data.EBO = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, data.EBO);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_DYNAMIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        });


        data.queuedData++;
    }

    public void Generate()
    {
        TaskExecutor.QueueTask(() ->
        {
            data.shader.Generate();
            data.VAO = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(data.VAO);
        });

        FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(data.vertices.size() * 3);
        float[] positionData = new float[data.vertices.size() * 3];

        for (int v = 0; v < data.vertices.size(); v++)
        {
            positionData[v * 3] = data.vertices.get(v).position.x;
            positionData[v * 3 + 1] = data.vertices.get(v).position.y;
            positionData[v * 3 + 2] = data.vertices.get(v).position.z;
        }

        positionBuffer.put(positionData).flip();
        TaskExecutor.QueueTask(() ->
            data.VBO = StoreDataAsFloat(positionBuffer, 0, 3));

        FloatBuffer colorBuffer = MemoryUtil.memAllocFloat(data.vertices.size() * 3);
        float[] colorData = new float[data.vertices.size() * 3];

        for (int c = 0; c < data.vertices.size(); c++)
        {
            colorData[c * 3] = data.vertices.get(c).color.x;
            colorData[c * 3 + 1] = data.vertices.get(c).color.y;
            colorData[c * 3 + 2] = data.vertices.get(c).color.z;
        }

        colorBuffer.put(colorData).flip();
        TaskExecutor.QueueTask(() ->
            data.CBO = StoreDataAsFloat(colorBuffer, 1, 3));

        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(data.vertices.size() * 2);
        float[] textureData = new float[data.vertices.size() * 2];

        for (int t = 0; t < data.vertices.size(); t++)
        {
            textureData[t * 2] = data.vertices.get(t).textureCoordinates.x;
            textureData[t * 2 + 1] = data.vertices.get(t).textureCoordinates.y;
        }

        textureBuffer.put(textureData).flip();
        TaskExecutor.QueueTask(() ->
        data.TBO = StoreDataAsFloat(textureBuffer, 2, 2));

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(data.indices.size());
        indicesBuffer.put(data.indices.stream().mapToInt(i -> i).toArray()).flip();

        TaskExecutor.QueueTask(() ->
        {
            data.EBO = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, data.EBO);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_DYNAMIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        });

        data.queuedData++;

        TaskExecutor.QueueTask(() ->
        {
            data.shader.Use();
            data.shader.SetUniform("diffuse", 0);
        });
    }

    public void CleanUp()
    {
        GL30.glDeleteVertexArrays(data.VAO);
        GL30.glDeleteBuffers(data.VBO);
        GL30.glDeleteBuffers(data.CBO);
        GL30.glDeleteBuffers(data.TBO);
        GL30.glDeleteBuffers(data.EBO);

        for (Texture texture : data.textures.values())
            texture.CleanUp();

        data.shader.CleanUp();
    }

    @Override
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void RegisterData(List<Vertex> vertices, List<Integer> indices)
    {
        data.vertices = vertices;
        data.indices = indices;
    }

    public static RenderableObject Register(NameIDTag name, List<Vertex> vertices, List<Integer> indices)
    {
        return RenderableObject.Register(name, vertices, indices, false, "default");
    }

    public static RenderableObject Register(NameIDTag name, List<Vertex> vertices, List<Integer> indices, boolean wireFrame, String shader)
    {
        RenderableObject out = new RenderableObject();

        out.data.name = name;
        out.data.vertices = vertices;
        out.data.indices = indices;
        out.data.wireFrame = wireFrame;

        out.data.shader = ShaderManager.GetShader(shader);

        return out;
    }
}