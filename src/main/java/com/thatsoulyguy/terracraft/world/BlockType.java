package com.thatsoulyguy.terracraft.world;

import org.joml.Vector2i;

public enum BlockType
{
    BLOCK_AIR(0),
    BLOCK_BLOCK(1),
    BLOCK_GRASS(2),
    BLOCK_DIRT(3),
    BLOCK_STONE(4);

    private final int type;

    BlockType(int type)
    {
        this.type = type;
    }

    public int GetType()
    {
        return type;
    }

    public static BlockType GetFromRaw(int type)
    {
        for (BlockType blockType : BlockType.values())
        {
            if (blockType.GetType() == type)
                return blockType;
        }

        throw new IllegalArgumentException("No block type with the specified type: " + type);
    }

    public static Vector2i[] GetBlockTexture(BlockType type)
    {
        Vector2i[] out = new Vector2i[6];

        switch (type)
        {
            case BLOCK_AIR:
            {
                out[0] = new Vector2i(9, 2);
                out[1] = new Vector2i(9, 2);
                out[2] = new Vector2i(9, 2);
                out[3] = new Vector2i(9, 2);
                out[4] = new Vector2i(9, 2);
                out[5] = new Vector2i(9, 2);
                break;
            }

            case BLOCK_BLOCK:
            {
                out[0] = new Vector2i(0, 0);
                out[1] = new Vector2i(0, 0);
                out[2] = new Vector2i(0, 0);
                out[3] = new Vector2i(0, 0);
                out[4] = new Vector2i(0, 0);
                out[5] = new Vector2i(0, 0);
                break;
            }

            case BLOCK_GRASS:
            {
                out[0] = new Vector2i(0, 0);
                out[1] = new Vector2i(2, 0);
                out[2] = new Vector2i(3, 0);
                out[3] = new Vector2i(3, 0);
                out[4] = new Vector2i(3, 0);
                out[5] = new Vector2i(3, 0);
                break;
            }

            case BLOCK_DIRT:
            {
                out[0] = new Vector2i(2, 0);
                out[1] = new Vector2i(2, 0);
                out[2] = new Vector2i(2, 0);
                out[3] = new Vector2i(2, 0);
                out[4] = new Vector2i(2, 0);
                out[5] = new Vector2i(2, 0);
                break;
            }

            case BLOCK_STONE:
            {
                out[0] = new Vector2i(1, 0);
                out[1] = new Vector2i(1, 0);
                out[2] = new Vector2i(1, 0);
                out[3] = new Vector2i(1, 0);
                out[4] = new Vector2i(1, 0);
                out[5] = new Vector2i(1, 0);
                break;
            }
        }

        return out;
    }
}