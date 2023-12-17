package com.thatsoulyguy.terracraft.render;

import org.w3c.dom.Text;

public class TextureProperties
{
    public TextureWrapping wrapping;
    public TextureFiltering filtering;

    public static TextureProperties Register(TextureWrapping wrapping, TextureFiltering filtering)
    {
        TextureProperties out = new TextureProperties();

        out.wrapping = wrapping;
        out.filtering = filtering;

        return out;
    }
}