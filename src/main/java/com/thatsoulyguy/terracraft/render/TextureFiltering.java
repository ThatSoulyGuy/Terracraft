package com.thatsoulyguy.terracraft.render;

import static org.lwjgl.opengl.GL11.*;

public enum TextureFiltering
{
    NEAREST(GL_NEAREST),
    LINEAR(GL_LINEAR);

    private final int type;

    TextureFiltering(int type)
    {
        this.type = type;
    }

    public int GetType()
    {
        return type;
    }
}
