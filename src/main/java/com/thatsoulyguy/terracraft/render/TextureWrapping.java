package com.thatsoulyguy.terracraft.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

public enum TextureWrapping
{
    REPEAT(GL_REPEAT),
    MIRRORED_REPEAT(GL_MIRRORED_REPEAT),
    CLAMP_TO_EDGE(GL_CLAMP_TO_EDGE),
    CLAMP_TO_BORDER(GL_CLAMP_TO_BORDER);

    private final int type;

    TextureWrapping(int type)
    {
        this.type = type;
    }

    public int GetType()
    {
        return type;
    }
}