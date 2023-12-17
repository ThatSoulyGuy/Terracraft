package com.thatsoulyguy.terracraft.render;

import com.thatsoulyguy.terracraft.Terracraft;
import com.thatsoulyguy.terracraft.core.LogLevel;
import com.thatsoulyguy.terracraft.core.Logger;
import com.thatsoulyguy.terracraft.core.Settings;
import com.thatsoulyguy.terracraft.util.FileHelper;
import com.thatsoulyguy.terracraft.util.TypeHelper;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class ShaderObject
{
    public String name;
    public String vertexPath, fragmentPath;
    public String vertexData, fragmentData;
    public int program;

    private int GenerateShader(ShaderType type, String data)
    {
        int id = GL20.glCreateShader(type.GetRaw());

        GL20.glShaderSource(id, data);
        GL20.glCompileShader(id);

        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
        {
            System.err.println(type.name() + ": " + GL20.glGetShaderInfoLog(id));
            return -1;
        }

        return id;
    }

    private int LinkShaders(int vertex, int fragment)
    {
        GL20.glAttachShader(program, vertex);
        GL20.glAttachShader(program, fragment);

        GL20.glLinkProgram(program);

        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
        {
            System.err.println("Program Linking: " + GL20.glGetProgramInfoLog(program));
            return -1;
        }

        GL20.glValidateProgram(program);

        if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE)
        {
            System.err.println("Program Validation: " + GL20.glGetProgramInfoLog(program));
            return -1;
        }

        return 0;
    }

    public void Generate()
    {
        program = GL20.glCreateProgram();

        int vertex = GenerateShader(ShaderType.VERTEX_SHADER, vertexData);

        if(vertex == -1)
            throw new IllegalStateException("Failed to generate vertex shader!");

        int fragment = GenerateShader(ShaderType.FRAGMENT_SHADER, fragmentData);

        if(fragment == -1)
            throw new IllegalStateException("Failed to generate fragment shader!");

        if(LinkShaders(vertex, fragment) == -1)
            throw new IllegalStateException("Failed to link shaders!");

        GL20.glDeleteShader(vertex);
        GL20.glDeleteShader(fragment);
    }

    public void Use()
    {
        GL20.glUseProgram(program);

        int error = GL20.glGetError();

        if(error != GL20.GL_NO_ERROR)
            Logger.WriteConsole("Shr OpenGL error detected: " + error, LogLevel.ERROR);
    }

    public void UnUse()
    {
        GL20.glUseProgram(0);
    }

    public int GetUniformLocation(String name)
    {
        return GL20.glGetUniformLocation(program, name);
    }

    public void SetUniform(String name, float value)
    {
        GL20.glUniform1f(GetUniformLocation(name), value);
    }

    public void SetUniform(String name, int value)
    {
        GL20.glUniform1i(GetUniformLocation(name), value);
    }

    public void SetUniform(String name, boolean value)
    {
        GL20.glUniform1i(GetUniformLocation(name), TypeHelper.Bool2Int(value));
    }

    public void SetUniform(String name, Vector2f value)
    {
        GL20.glUniform2f(GetUniformLocation(name), value.x, value.y);
    }

    public void SetUniform(String name, Vector3f value)
    {
        GL20.glUniform3f(GetUniformLocation(name), value.x, value.y, value.z);
    }

    public void SetUniform(String name, Matrix4f value)
    {
        FloatBuffer matrix = MemoryUtil.memAllocFloat(16);
        value.get(matrix);

        GL20.glUniformMatrix4fv(GetUniformLocation(name), false, matrix);
    }

    public ShaderObject Copy()
    {
        ShaderObject out = new ShaderObject();

        out.name = this.name;
        out.vertexPath = this.vertexPath;
        out.fragmentPath = this.fragmentPath;
        out.vertexData = this.vertexData;
        out.fragmentData = this.fragmentData;

        return out;
    }

    public void CleanUp()
    {
        GL20.glDeleteProgram(program);
    }

    @Override
    protected Object clone()
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

    public static ShaderObject Register(String localPath, String name)
    {
        return ShaderObject.Register(localPath, name, Settings.defaultDomain);
    }

    public static ShaderObject Register(String localPath, String name, String domain)
    {
        ShaderObject object = new ShaderObject();

        object.name = name;

        object.vertexPath = "/assets/" + domain + "/" + localPath + "Vertex.glsl";
        object.fragmentPath = "/assets/" + domain + "/" + localPath + "Fragment.glsl";

        object.vertexData = FileHelper.LoadFile(object.vertexPath);
        object.fragmentData = FileHelper.LoadFile(object.fragmentPath);

        return object;
    }
}