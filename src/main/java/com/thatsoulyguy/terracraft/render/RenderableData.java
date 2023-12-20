package com.thatsoulyguy.terracraft.render;

import com.thatsoulyguy.terracraft.math.Transform;
import com.thatsoulyguy.terracraft.records.NameIDTag;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RenderableData
{
    public NameIDTag name;
    public List<Vertex> vertices = new ArrayList<>();
    public List<Integer> indices = new ArrayList<>();
    public ShaderObject shader;

    public HashMap<String, Texture> textures = new HashMap<>();
    public Transform transform = Transform.Register(new Vector3f(0.0f, 0.0f, 0.0f));
    public boolean wireFrame = false;
    public boolean active = true;
    public boolean cullBackfaces = true;

    public int VBO, VAO, CBO, TBO, EBO;
    public int queuedData = -1;
}