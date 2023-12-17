package com.thatsoulyguy.terracraft.world;

import com.thatsoulyguy.terracraft.math.AABB;
import com.thatsoulyguy.terracraft.math.TransformI;
import com.thatsoulyguy.terracraft.render.RenderableObject;
import com.thatsoulyguy.terracraft.render.Vertex;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChunkData
{
    public final List<Vertex> vertices = Collections.synchronizedList(new ArrayList<>());
    public final List<Integer> indices = Collections.synchronizedList(new ArrayList<>());
    public ConcurrentHashMap<Vector3i, AABB> blockAABBs = new ConcurrentHashMap<>();
    public int indicesIndex;

    public TransformI transform;
    public RenderableObject mesh;

    public ChunkRenderSides sides = new ChunkRenderSides();

    public boolean firstRebuild = true;
    public int[][][] blocks = new int[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];
}