package com.thatsoulyguy.terracraft.render;

import com.thatsoulyguy.terracraft.core.LogLevel;
import com.thatsoulyguy.terracraft.core.Logger;

import java.util.HashMap;

public class ShaderManager
{
    public static HashMap<String, ShaderObject> registeredShaders = new HashMap<>();

    public static void RegisterShader(ShaderObject object)
    {
        Logger.WriteConsole("Registering ShaderObject: '" + object.name + "'.", LogLevel.INFO);

        registeredShaders.put(object.name, object);
    }

    public static ShaderObject GetShader(String name)
    {
        return registeredShaders.get(name).Copy();
    }

    public static void CleanUp()
    {
        for(ShaderObject object : registeredShaders.values())
            object.CleanUp();
    }
}