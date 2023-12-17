package com.thatsoulyguy.terracraft.world;

import org.joml.Vector2f;
import org.joml.Vector2i;

public class TextureAtlasManager
{
    public static int atlasSize = 256;
    public static int tilePixelSize = 16;
    public static final float PADDING_RATIO = 1.0f/256.0f;

    public static Vector2f[] GetTextureCoordinates(Vector2i position)
    {
        float perTextureSize = (float)tilePixelSize / atlasSize;

        float u0 = position.x * perTextureSize + PADDING_RATIO;
        float v0 = position.y * perTextureSize + PADDING_RATIO;

        float u1 = u0 + perTextureSize - 2 * PADDING_RATIO;
        float v1 = v0 + perTextureSize - 2 * PADDING_RATIO;

        return new Vector2f[]
        {
                new Vector2f(u0, v0),
                new Vector2f(u1, v0),
                new Vector2f(u1, v1),
                new Vector2f(u0, v1)
        };
    }

    public static Vector2f[] GetTextureCoordinatesRotated(Vector2i position, float rotation)
    {
        float perTextureSize = (float) tilePixelSize / atlasSize;

        float u0 = position.x * perTextureSize + PADDING_RATIO;
        float v0 = position.y * perTextureSize + PADDING_RATIO;
        float u1 = u0 + perTextureSize - 2 * PADDING_RATIO;
        float v1 = v0 + perTextureSize - 2 * PADDING_RATIO;

        float centerX = (u0 + u1) / 2;
        float centerY = (v0 + v1) / 2;

        Vector2f[] coordinates = new Vector2f[]
        {
            new Vector2f(u1, v0),
            new Vector2f(u1, v1),
            new Vector2f(u0, v1),
            new Vector2f(u0, v0)
        };

        float rad = (float) Math.toRadians(rotation);

        for (int i = 0; i < coordinates.length; i++)
        {
            float translatedX = coordinates[i].x - centerX;
            float translatedY = coordinates[i].y - centerY;

            float rotatedX = translatedX * (float) Math.cos(rad) - translatedY * (float) Math.sin(rad);
            float rotatedY = translatedX * (float) Math.sin(rad) + translatedY * (float) Math.cos(rad);

            coordinates[i].x = rotatedX + centerX;
            coordinates[i].y = rotatedY + centerY;
        }

        return coordinates;
    }
}