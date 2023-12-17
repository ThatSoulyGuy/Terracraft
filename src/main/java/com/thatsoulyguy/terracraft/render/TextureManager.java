package com.thatsoulyguy.terracraft.render;

import java.util.HashMap;

public class TextureManager
{
    public static HashMap<String, Texture> registeredTextures = new HashMap<>();

    public static void RegisterTexture(Texture texture)
    {
        registeredTextures.put(texture.name, texture);
    }

    public static Texture GetTexture(String name)
    {
        return registeredTextures.get(name).Copy();
    }

    public static void CleanUp()
    {
        for(Texture texture : registeredTextures.values())
            texture.CleanUp();
    }
}