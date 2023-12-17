package com.thatsoulyguy.terracraft.world;

import com.thatsoulyguy.terracraft.math.AABB;
import com.thatsoulyguy.terracraft.math.TransformI;
import com.thatsoulyguy.terracraft.records.NameIDTag;
import com.thatsoulyguy.terracraft.render.RenderableObject;
import com.thatsoulyguy.terracraft.render.Renderer;
import com.thatsoulyguy.terracraft.render.Vertex;
import com.thatsoulyguy.terracraft.thread.TaskExecutor;
import org.joml.*;
import org.joml.Math;

public class Chunk
{
    public static final byte CHUNK_SIZE = 16;

    public ChunkData data = new ChunkData();

    public void Initialize(Vector3i position)
    {
        Initialize(position, false);
    }

    private int GetBlockTypeForPosition(int x, int y, int z)
    {
        if (y == 15)
            return BlockType.BLOCK_GRASS.GetType();
        else if (y > 12)
            return BlockType.BLOCK_DIRT.GetType();
        else
            return BlockType.BLOCK_STONE.GetType();
    }

    public void Initialize(Vector3i position, boolean generateNothing)
    {
        data.transform = TransformI.Register(position);

        if(generateNothing)
        {
            for (int x = 0; x < CHUNK_SIZE; x++)
            {
                for (int y = 0; y < CHUNK_SIZE; y++)
                {
                    for (int z = 0; z < CHUNK_SIZE; z++)
                        data.blocks[x][y][z] = BlockType.BLOCK_AIR.GetType();
                }
            }
        }
        else
        {
            for (int x = 0; x < CHUNK_SIZE; x++)
            {
                for (int y = 0; y < CHUNK_SIZE; y++)
                {
                    for (int z = 0; z < CHUNK_SIZE; z++)
                        data.blocks[x][y][z] = GetBlockTypeForPosition(x, y, z);
                }
            }
        }

        TaskExecutor.QueueTask(() ->
        {
            data.mesh = RenderableObject.Register(NameIDTag.Register(String.format("Chunk_%d_%d_%d", position.x, position.y, position.z), data.mesh), null, null);
            data.mesh.data.transform = data.transform.ToTransform();
            data.mesh.RegisterTexture("atlas");
        });

        Rebuild();
    }

    public void SetBlock(Vector3i position, BlockType type)
    {
        if (position.x < 0 || position.x >= Chunk.CHUNK_SIZE)
            return;

        if (position.y < 0 || position.y >= Chunk.CHUNK_SIZE)
            return;

        if (position.z < 0 || position.z >= Chunk.CHUNK_SIZE)
            return;

        data.blocks[position.x][position.y][position.z] = type.GetType();

        if (type == BlockType.BLOCK_AIR)
            data.blockAABBs.remove(position);
        else
        {
            if (BlockExposed(position.x, position.y, position.z))
            {
                AABB blockAABB = GenerateBlockAABB(position);
                data.blockAABBs.put(position, blockAABB);
            }
        }

        UpdateNeighboringBlocksAABBs(position);

        Rebuild();
    }

    private void UpdateNeighboringBlocksAABBs(Vector3i position)
    {
        for (int dx = -1; dx <= 1; dx++)
        {
            for (int dy = -1; dy <= 1; dy++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    Vector3i neighborPos = new Vector3i(position.x + dx, position.y + dy, position.z + dz);
                    if (BlockExposed(neighborPos.x, neighborPos.y, neighborPos.z))
                    {
                        AABB blockAABB = GenerateBlockAABB(neighborPos);
                        data.blockAABBs.put(neighborPos, blockAABB);
                    }
                    else
                        data.blockAABBs.remove(neighborPos);
                }
            }
        }
    }

    public boolean HasBlock(Vector3i position)
    {
        if (position.x < 0 || position.x >= Chunk.CHUNK_SIZE)
            return false;
        
        if (position.y < 0 || position.y >= Chunk.CHUNK_SIZE)
            return false;
        
        if (position.z < 0 || position.z >= Chunk.CHUNK_SIZE)
            return false;

        return data.blocks[position.x][position.y][position.z] != BlockType.BLOCK_AIR.GetType();
    }

    public static Vector3i WorldToBlockCoordinates(Vector3f worldPosition)
    {
        int blockX = (int)Math.floor(worldPosition.x) % Chunk.CHUNK_SIZE;
        int blockY = (int)Math.floor(worldPosition.y) % Chunk.CHUNK_SIZE;
        int blockZ = (int)Math.floor(worldPosition.z) % Chunk.CHUNK_SIZE;

        blockX = (blockX < 0) ? Chunk.CHUNK_SIZE + blockX : blockX;
        blockY = (blockY < 0) ? Chunk.CHUNK_SIZE + blockY : blockY;
        blockZ = (blockZ < 0) ? Chunk.CHUNK_SIZE + blockZ : blockZ;

        return new Vector3i(blockX, blockY, blockZ);
    }

    public static Vector3i WorldToBlockCoordinates(Vector3i worldPosition)
    {
        int blockX = (int)Math.floor(worldPosition.x) % Chunk.CHUNK_SIZE;
        int blockY = (int)Math.floor(worldPosition.y) % Chunk.CHUNK_SIZE;
        int blockZ = (int)Math.floor(worldPosition.z) % Chunk.CHUNK_SIZE;

        blockX = (blockX < 0) ? Chunk.CHUNK_SIZE + blockX : blockX;
        blockY = (blockY < 0) ? Chunk.CHUNK_SIZE + blockY : blockY;
        blockZ = (blockZ < 0) ? Chunk.CHUNK_SIZE + blockZ : blockZ;

        return new Vector3i(blockX, blockY, blockZ);
    }

    public void Rebuild()
    {
        data.vertices.clear();
        data.indices.clear();
        data.blockAABBs.clear();
        data.indicesIndex = 0;

        for(int x = 0; x < CHUNK_SIZE; x++)
        {
            for(int y = 0; y < CHUNK_SIZE; y++)
            {
                for(int z = 0; z < CHUNK_SIZE; z++)
                {
                    if(data.blocks[x][y][z] == BlockType.BLOCK_AIR.GetType())
                        continue;

                    Vector2i[] textureCoordinates = BlockType.GetBlockTexture(BlockType.GetFromRaw(data.blocks[x][y][z]));

                    if (ShouldRenderFace(x, y, z, "top"))
                        GenerateTopFace(new Vector3f(x, y, z), TextureAtlasManager.GetTextureCoordinates(textureCoordinates[0]));

                    if (ShouldRenderFace(x, y, z, "bottom"))
                        GenerateBottomFace(new Vector3f(x, y, z), TextureAtlasManager.GetTextureCoordinates(textureCoordinates[1]));

                    if (ShouldRenderFace(x, y, z, "front"))
                        GenerateFrontFace(new Vector3f(x, y, z), TextureAtlasManager.GetTextureCoordinatesRotated(textureCoordinates[2], 90));

                    if (ShouldRenderFace(x, y, z, "back"))
                        GenerateBackFace(new Vector3f(x, y, z), TextureAtlasManager.GetTextureCoordinatesRotated(textureCoordinates[3], 90));

                    if (ShouldRenderFace(x, y, z, "right"))
                        GenerateRightFace(new Vector3f(x, y, z), TextureAtlasManager.GetTextureCoordinatesRotated(textureCoordinates[4], 90));

                    if (ShouldRenderFace(x, y, z, "left"))
                        GenerateLeftFace(new Vector3f(x, y, z), TextureAtlasManager.GetTextureCoordinatesRotated(textureCoordinates[5], 90));

                    if (BlockExposed(x, y, z))
                    {
                        Vector3i blockPos = new Vector3i(x, y, z);
                        AABB blockAABB = GenerateBlockAABB(blockPos);
                        data.blockAABBs.put(blockPos, blockAABB);
                    }
                }
            }
        }

        TaskExecutor.QueueTask(() ->
                data.mesh.RegisterData(data.vertices, data.indices));

        if(data.firstRebuild)
        {
            TaskExecutor.QueueTask(() ->
                data.mesh.Generate());

            data.firstRebuild = false;
        }
        else
        {
            TaskExecutor.QueueTask(() ->
                data.mesh.ReGenerate());
        }

        TaskExecutor.QueueTask(() ->
            Renderer.RegisterRenderableObject(data.mesh));
    }

    private AABB GenerateBlockAABB(Vector3i blockPosition)
    {
        Vector3f worldPos = new Vector3f(blockPosition.x + data.transform.position.x + 0.5f, blockPosition.y + data.transform.position.y + 0.5f, blockPosition.z + data.transform.position.z + 0.5f);
        Vector3f blockDimensions = new Vector3f(1.0f, 1.0f, 1.0f);

        return AABB.Register(worldPos, blockDimensions);
    }

    private boolean BlockExposed(int x, int y, int z)
    {
        return IsAir(x - 1, y, z) || IsAir(x + 1, y, z) ||
                IsAir(x, y - 1, z) || IsAir(x, y + 1, z) ||
                IsAir(x, y, z - 1) || IsAir(x, y, z + 1);
    }

    public boolean IsAir(int x, int y, int z)
    {
        if (x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_SIZE || z < 0 || z >= CHUNK_SIZE)
            return true;

        return data.blocks[x][y][z] == BlockType.BLOCK_AIR.GetType();
    }

    private boolean ShouldRenderFace(int x, int y, int z, String face)
    {
        switch (face)
        {
            case "top":
                return y == CHUNK_SIZE - 1 || data.blocks[x][y + 1][z] == BlockType.BLOCK_AIR.GetType();
            case "bottom":
                return y == 0 || data.blocks[x][y - 1][z] == BlockType.BLOCK_AIR.GetType();
            case "front":
                return z == CHUNK_SIZE - 1 || data.blocks[x][y][z + 1] == BlockType.BLOCK_AIR.GetType();
            case "back":
                return z == 0 || data.blocks[x][y][z - 1] == BlockType.BLOCK_AIR.GetType();
            case "right":
                return x == CHUNK_SIZE - 1 || data.blocks[x + 1][y][z] == BlockType.BLOCK_AIR.GetType();
            case "left":
                return x == 0 || data.blocks[x - 1][y][z] == BlockType.BLOCK_AIR.GetType();
            default:
                return false;
        }
    }

    public void CleanUp()
    {
        Renderer.RemoveRenderableObject(data.mesh.data.name);
    }

    private void GenerateTopFace(Vector3f position, Vector2f[] uvs)
    {
        data.vertices.add(Vertex.Register(new Vector3f(0.0f + position.x, 1.0f + position.y, 1.0f + position.z), uvs[0]));
        data.vertices.add(Vertex.Register(new Vector3f(0.0f + position.x, 1.0f + position.y, 0.0f + position.z), uvs[1]));
        data.vertices.add(Vertex.Register(new Vector3f(1.0f + position.x, 1.0f + position.y, 0.0f + position.z), uvs[2]));
        data.vertices.add(Vertex.Register(new Vector3f(1.0f + position.x, 1.0f + position.y, 1.0f + position.z), uvs[3]));

        data.indices.add(data.indicesIndex);
        data.indices.add(data.indicesIndex + 2);
        data.indices.add(data.indicesIndex + 1);

        data.indices.add(data.indicesIndex);
        data.indices.add(data.indicesIndex + 3);
        data.indices.add(data.indicesIndex + 2);

        data.indicesIndex += 4;
    }

    private void GenerateBottomFace(Vector3f position, Vector2f[] uvs)
    {
        data.vertices.add(Vertex.Register(new Vector3f(0.0f + position.x, 0.0f + position.y, 0.0f + position.z), uvs[0]));
        data.vertices.add(Vertex.Register(new Vector3f(1.0f + position.x, 0.0f + position.y, 0.0f + position.z), uvs[1]));
        data.vertices.add(Vertex.Register(new Vector3f(1.0f + position.x, 0.0f + position.y, 1.0f + position.z), uvs[2]));
        data.vertices.add(Vertex.Register(new Vector3f(0.0f + position.x, 0.0f + position.y, 1.0f + position.z), uvs[3]));

        data.indices.add(data.indicesIndex);
        data.indices.add(data.indicesIndex + 1);
        data.indices.add(data.indicesIndex + 2);

        data.indices.add(data.indicesIndex);
        data.indices.add(data.indicesIndex + 2);
        data.indices.add(data.indicesIndex + 3);

        data.indicesIndex += 4;
    }

    private void GenerateFrontFace(Vector3f position, Vector2f[] uvs)
    {
        data.vertices.add(Vertex.Register(new Vector3f(0.0f + position.x, 0.0f + position.y, 1.0f + position.z), uvs[0]));
        data.vertices.add(Vertex.Register(new Vector3f(1.0f + position.x, 0.0f + position.y, 1.0f + position.z), uvs[1]));
        data.vertices.add(Vertex.Register(new Vector3f(1.0f + position.x, 1.0f + position.y, 1.0f + position.z), uvs[2]));
        data.vertices.add(Vertex.Register(new Vector3f(0.0f + position.x, 1.0f + position.y, 1.0f + position.z), uvs[3]));

        data.indices.add(data.indicesIndex);
        data.indices.add(data.indicesIndex + 1);
        data.indices.add(data.indicesIndex + 2);

        data.indices.add(data.indicesIndex);
        data.indices.add(data.indicesIndex + 2);
        data.indices.add(data.indicesIndex + 3);

        data.indicesIndex += 4;
    }

    private void GenerateBackFace(Vector3f position, Vector2f[] uvs)
    {
        data.vertices.add(Vertex.Register(new Vector3f(1.0f + position.x, 0.0f + position.y, 0.0f + position.z), uvs[0]));
        data.vertices.add(Vertex.Register(new Vector3f(0.0f + position.x, 0.0f + position.y, 0.0f + position.z), uvs[1]));
        data.vertices.add(Vertex.Register(new Vector3f(0.0f + position.x, 1.0f + position.y, 0.0f + position.z), uvs[2]));
        data.vertices.add(Vertex.Register(new Vector3f(1.0f + position.x, 1.0f + position.y, 0.0f + position.z), uvs[3]));

        data.indices.add(data.indicesIndex);
        data.indices.add(data.indicesIndex + 1);
        data.indices.add(data.indicesIndex + 2);

        data.indices.add(data.indicesIndex);
        data.indices.add(data.indicesIndex + 2);
        data.indices.add(data.indicesIndex + 3);

        data.indicesIndex += 4;
    }

    private void GenerateRightFace(Vector3f position, Vector2f[] uvs)
    {
        data.vertices.add(Vertex.Register(new Vector3f(1.0f + position.x, 0.0f + position.y, 1.0f + position.z), uvs[0]));
        data.vertices.add(Vertex.Register(new Vector3f(1.0f + position.x, 0.0f + position.y, 0.0f + position.z), uvs[1]));
        data.vertices.add(Vertex.Register(new Vector3f(1.0f + position.x, 1.0f + position.y, 0.0f + position.z), uvs[2]));
        data.vertices.add(Vertex.Register(new Vector3f(1.0f + position.x, 1.0f + position.y, 1.0f + position.z), uvs[3]));

        data.indices.add(data.indicesIndex);
        data.indices.add(data.indicesIndex + 1);
        data.indices.add(data.indicesIndex + 2);

        data.indices.add(data.indicesIndex);
        data.indices.add(data.indicesIndex + 2);
        data.indices.add(data.indicesIndex + 3);

        data.indicesIndex += 4;
    }

    private void GenerateLeftFace(Vector3f position, Vector2f[] uvs)
    {
        data.vertices.add(Vertex.Register(new Vector3f(0.0f + position.x, 0.0f + position.y, 0.0f + position.z), uvs[0]));
        data.vertices.add(Vertex.Register(new Vector3f(0.0f + position.x, 0.0f + position.y, 1.0f + position.z), uvs[1]));
        data.vertices.add(Vertex.Register(new Vector3f(0.0f + position.x, 1.0f + position.y, 1.0f + position.z), uvs[2]));
        data.vertices.add(Vertex.Register(new Vector3f(0.0f + position.x, 1.0f + position.y, 0.0f + position.z), uvs[3]));

        data.indices.add(data.indicesIndex);
        data.indices.add(data.indicesIndex + 1);
        data.indices.add(data.indicesIndex + 2);

        data.indices.add(data.indicesIndex);
        data.indices.add(data.indicesIndex + 2);
        data.indices.add(data.indicesIndex + 3);

        data.indicesIndex += 4;
    }
}