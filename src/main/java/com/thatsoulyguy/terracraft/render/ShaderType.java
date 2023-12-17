package com.thatsoulyguy.terracraft.render;

public enum ShaderType
{
    VERTEX_SHADER(35633),
    FRAGMENT_SHADER(35632);

    private int type;

    ShaderType(int type) {
        this.type = type;
    }

    int GetRaw() {
        return type;
    }
}