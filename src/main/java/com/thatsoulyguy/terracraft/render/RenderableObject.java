package com.thatsoulyguy.terracraft.render;

import com.thatsoulyguy.terracraft.core.LogLevel;
import com.thatsoulyguy.terracraft.core.Logger;
import com.thatsoulyguy.terracraft.records.NameIDTag;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class RenerableObject implements Cloneable
{
    RenderableData data = new RenderableData();

    private int StoreDataAsFloat(FloatBuffer buffer, int index, int size)
    {
        int out = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, out);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        data.queuedData++;

        int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR)
            Logger.WriteConsole("OpenGL error detected: " + error, LogLevel.ERROR);

        return out;
    }

    public void Generate()
    {
        data.VAO = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(data.VAO);

        FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(data.vertices.size() * 3);
        float[] positionData = new float[data.vertices.size() * 3];

        for (int v = 0; v < data.vertices.size(); v++)
        {
            positionData[v * 3] = data.vertices.get(v).position.x;
            positionData[v * 3 + 1] = data.vertices.get(v).position.y;
            positionData[v * 3 + 2] = data.vertices.get(v).position.z;
        }

        positionBuffer.put(positionData).flip();
        data.VBO = StoreDataAsFloat(positionBuffer, 0, 3);

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(data.indices.size());
        int[] indicesData = new int[data.indices.size()];

        for (int i = 0; i < data.indices.size(); i++)
            indicesData[i] = data.indices.get(i);

        indicesBuffer.put(indicesData).flip();

        data.EBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, data.EBO);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        data.queuedData++;

        int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR)
            Logger.WriteConsole("OpenGL error detected: " + error, LogLevel.ERROR);
    }

    public void CleanUp()
    {
        GL30.glDeleteVertexArrays(data.VAO);
        GL30.glDeleteBuffers(data.VBO);
        GL30.glDeleteBuffers(data.EBO);

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

    public static RenerableObject Register(NameIDTag name, List<Vertex> vertices, List<Integer> indices)
    {
        RenerableObject out = new RenerableObject();

        out.data.name = name;
        out.data.vertices = vertices;
        out.data.indices = indices;

        out.data.shader = ShaderManager.GetShader("default");
        out.data.shader.Generate();

        return out;
    }

    public static RenerableObject Register(NameIDTag name, List<Vertex> vertices, List<Integer> indices, String shader)
    {
        RenerableObject out = new RenerableObject();

        out.data.name = name;
        out.data.vertices = vertices;
        out.data.indices = indices;

        out.data.shader = ShaderManager.GetShader(shader);
        out.data.shader.Generate();

        return out;
    }
}