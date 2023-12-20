package com.thatsoulyguy.terracraft.world;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class World
{
    public static final int VIEW_DISTANCE = 1;
    public static ConcurrentHashMap<Vector3i, Chunk> chunks;
    private static ExecutorService worldExecutor;

    private static final Thread.UncaughtExceptionHandler handler = (thread, throwable) ->
    {
        System.err.println("Exception in thread " + thread.getName() + ": " + throwable.getMessage());
        throwable.printStackTrace();

        System.exit(1);
    };

    public static void Initialize()
    {
        chunks = new ConcurrentHashMap<>();
        worldExecutor = Executors.newSingleThreadExecutor();
    }

    public static void StartUpdating(Vector3f playerPosition)
    {
        Runnable updateTask = () ->
        {
            try
            {
                while (!Thread.currentThread().isInterrupted())
                {
                    Update(playerPosition);
                    Thread.sleep(100);
                }
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        };

        Thread updateThread = new Thread(updateTask);
        updateThread.setUncaughtExceptionHandler(handler);
        worldExecutor.submit(updateThread);
    }

    public static void StopUpdating()
    {
        worldExecutor.shutdownNow();
    }

    public static void Update(Vector3f playerPosition)
    {
        Vector3i playerChunkCoordinates = WorldToChunkCoordinates(playerPosition);

        for (int x = -VIEW_DISTANCE; x <= VIEW_DISTANCE; x++)
        {
            for (int z = -VIEW_DISTANCE; z <= VIEW_DISTANCE; z++)
            {
                Vector3i chunkCoordinate = new Vector3i(playerChunkCoordinates.x + x, 0, playerChunkCoordinates.z + z);

                if (!chunks.containsKey(chunkCoordinate))
                {
                    Chunk chunk = new Chunk();

                    Vector3i worldPosition = new Vector3i(chunkCoordinate.x * Chunk.CHUNK_SIZE, chunkCoordinate.y * Chunk.CHUNK_SIZE, chunkCoordinate.z * Chunk.CHUNK_SIZE);

                    chunk.Initialize(worldPosition);

                    /*
                    if(chunks.containsKey(new Vector3i(chunkCoordinate.x, chunkCoordinate.y + 1, chunkCoordinate.z)))
                        chunkCheckList.add(new Vector3i(chunkCoordinate.x, chunkCoordinate.y + 1, chunkCoordinate.z));
                    if(chunks.containsKey(new Vector3i(chunkCoordinate.x, chunkCoordinate.y - 1, chunkCoordinate.z)))
                        chunkCheckList.add(new Vector3i(chunkCoordinate.x, chunkCoordinate.y - 1, chunkCoordinate.z));

                    if(chunks.containsKey(new Vector3i(chunkCoordinate.x, chunkCoordinate.y, chunkCoordinate.z + 1)))
                        chunkCheckList.add(new Vector3i(chunkCoordinate.x, chunkCoordinate.y, chunkCoordinate.z + 1));
                    if(chunks.containsKey(new Vector3i(chunkCoordinate.x, chunkCoordinate.y, chunkCoordinate.z - 1)))
                        chunkCheckList.add(new Vector3i(chunkCoordinate.x, chunkCoordinate.y, chunkCoordinate.z - 1));

                    if(chunks.containsKey(new Vector3i(chunkCoordinate.x + 1, chunkCoordinate.y, chunkCoordinate.z)))
                        chunkCheckList.add(new Vector3i(chunkCoordinate.x + 1, chunkCoordinate.y, chunkCoordinate.z));
                    if(chunks.containsKey(new Vector3i(chunkCoordinate.x - 1, chunkCoordinate.y, chunkCoordinate.z)))
                        chunkCheckList.add(new Vector3i(chunkCoordinate.x - 1, chunkCoordinate.y, chunkCoordinate.z));

                    for(Vector3i chunkChecklistCoordinate : chunkCheckList)
                        UpdateChunkOcclusion(chunks.get(chunkChecklistCoordinate));
                    */

                    UpdateChunkOcclusion(chunk);

                    chunks.put(chunkCoordinate, chunk);
                }
            }
        }

        HashSet<Vector3i> chunkSet = new HashSet<>(chunks.keySet());
        for(Vector3i chunkCoordinate : chunkSet)
        {
            if((Math.abs(chunkCoordinate.x - playerChunkCoordinates.x) > VIEW_DISTANCE) && (Math.abs(chunkCoordinate.z - playerChunkCoordinates.z) > VIEW_DISTANCE))
            {
                chunks.get(chunkCoordinate).CleanUp();
                chunks.remove(chunkCoordinate);
            }
        }
    }

    private static boolean AllZeroes(int[][][] array)
    {
        if (array.length != 16 || array[0].length != 16 || array[0][0].length != 16)
            throw new IllegalArgumentException("Array dimensions must be 16x16x16.");

        for (int i = 0; i < 16; i++)
        {
            for (int j = 0; j < 16; j++)
            {
                for (int k = 0; k < 16; k++)
                {
                    if (array[i][j][k] != 0)
                        return false;
                }
            }
        }

        return true;
    }

    public static Chunk GetChunk(Vector3f worldPosition)
    {
        Vector3i position = WorldToChunkCoordinates(worldPosition);

        if(chunks.containsKey(position))
            return chunks.get(position);

        return null;
    }

    public static void SetBlock(Vector3f worldPosition, BlockType type)
    {
        Vector3i chunkCoordinates = WorldToChunkCoordinates(worldPosition);

        Chunk chunk = chunks.get(chunkCoordinates);

        if (chunk == null && type != BlockType.BLOCK_AIR)
        {
            chunk = new Chunk();
            chunk.Initialize(new Vector3i(chunkCoordinates).mul(Chunk.CHUNK_SIZE), true);

            chunks.put(chunkCoordinates, chunk);
        }

        if(chunk != null)
        {
            Vector3i blockPosition = Chunk.WorldToBlockCoordinates(worldPosition);

            chunk.SetBlock(blockPosition, type);

            UpdateAdjacentChunksIfNecessary(chunkCoordinates);
        }
    }

    public static void SetBlock(Vector3i worldPosition, BlockType type)
    {
        Vector3f worldPositionFloat = new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z);
        SetBlock(worldPositionFloat, type);
    }

    private static void UpdateAdjacentChunksIfNecessary(Vector3i chunkCoordinates)
    {
        int[][] adjacentCoords = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] coord : adjacentCoords)
        {
            Vector3i adjacentChunkCoord = new Vector3i(chunkCoordinates.x + coord[0], chunkCoordinates.y, chunkCoordinates.z + coord[1]);

            if (chunks.containsKey(adjacentChunkCoord))
                UpdateChunkOcclusion(chunks.get(adjacentChunkCoord));
        }
    }

    public static BlockType GetBlock(Vector3f worldPosition)
    {
        Vector3i chunkCoordinates = WorldToChunkCoordinates(worldPosition);
        Chunk chunk = chunks.get(chunkCoordinates);

        if (chunk != null)
        {
            Vector3i blockPosition = Chunk.WorldToBlockCoordinates(worldPosition);
            return BlockType.GetFromRaw(chunk.data.blocks[blockPosition.x][blockPosition.y][blockPosition.z]);
        }

        return null;
    }

    private static void UpdateChunkOcclusion(Chunk chunk)
    {
        Vector3i chunkCoordinate = new Vector3i(
                (int) Math.floor(chunk.data.transform.position.x / (float) Chunk.CHUNK_SIZE),
                (int) Math.floor(chunk.data.transform.position.y / (float) Chunk.CHUNK_SIZE),
                (int) Math.floor(chunk.data.transform.position.z / (float) Chunk.CHUNK_SIZE)
        );

        chunk.data.sides.renderTop = !IsChunkPresent(new Vector3i(chunkCoordinate.x, chunkCoordinate.y + 1, chunkCoordinate.z));
        chunk.data.sides.renderBottom = !IsChunkPresent(new Vector3i(chunkCoordinate.x, chunkCoordinate.y - 1, chunkCoordinate.z));
        chunk.data.sides.renderFront = !IsChunkPresent(new Vector3i(chunkCoordinate.x, chunkCoordinate.y, chunkCoordinate.z + 1));
        chunk.data.sides.renderBack = !IsChunkPresent(new Vector3i(chunkCoordinate.x, chunkCoordinate.y, chunkCoordinate.z - 1));
        chunk.data.sides.renderRight = !IsChunkPresent(new Vector3i(chunkCoordinate.x + 1, chunkCoordinate.y, chunkCoordinate.z));
        chunk.data.sides.renderLeft = !IsChunkPresent(new Vector3i(chunkCoordinate.x - 1, chunkCoordinate.y, chunkCoordinate.z));

        chunk.Rebuild();
    }

    private static boolean IsChunkFar(Vector3i chunkCoordinate, Vector3i playerChunkCoordinates)
    {
        return Math.abs(chunkCoordinate.x - playerChunkCoordinates.x) > VIEW_DISTANCE + 2 ||
                Math.abs(chunkCoordinate.z - playerChunkCoordinates.z) > VIEW_DISTANCE + 2;
    }

    public static boolean IsChunkPresent(Vector3i chunkCoordinate)
    {
        return chunks.containsKey(chunkCoordinate);
    }

    public static Vector3i WorldToChunkCoordinates(Vector3f worldPosition)
    {
        return new Vector3i(
                (int) Math.floor(worldPosition.x / Chunk.CHUNK_SIZE),
                (int) Math.floor(worldPosition.y / Chunk.CHUNK_SIZE),
                (int) Math.floor(worldPosition.z / Chunk.CHUNK_SIZE)
        );
    }

    public static Vector3i WorldToChunkCoordinates(Vector3i worldPosition)
    {
        return new Vector3i(
                (int) Math.floor((double) worldPosition.x / Chunk.CHUNK_SIZE),
                (int) Math.floor((double) worldPosition.y / Chunk.CHUNK_SIZE),
                (int) Math.floor((double) worldPosition.z / Chunk.CHUNK_SIZE)
        );
    }
}