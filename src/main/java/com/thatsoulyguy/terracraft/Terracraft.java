package com.thatsoulyguy.terracraft;

import com.thatsoulyguy.terracraft.core.*;
import com.thatsoulyguy.terracraft.entity.entities.EntityPig;
import com.thatsoulyguy.terracraft.player.Player;
import com.thatsoulyguy.terracraft.records.NameIDTag;
import com.thatsoulyguy.terracraft.render.*;
import com.thatsoulyguy.terracraft.thread.TaskExecutor;
import com.thatsoulyguy.terracraft.world.World;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Terracraft implements Runnable
{
    public Thread mainThread;

    public Player player = new Player();
    public EntityPig pig = new EntityPig();

    public static Terracraft Instance = null;

    public void PreInitialize()
    {
        mainThread = new Thread(this, "main");
        mainThread.start();

        Instance = this;
    }

    public void Initialize()
    {
        Logger.WriteConsole("Hello, Terracraft!", LogLevel.INFO);

        Window.Initialize();

        ShaderManager.RegisterShader(ShaderObject.Register("shaders/default", "default"));
        TextureManager.RegisterTexture(Texture.Register("textures/block.png", "block"));
        TextureManager.RegisterTexture(Texture.Register("textures/terrain.png", "atlas"));
        TextureManager.RegisterTexture(Texture.Register("textures/wireframe.png", "wireframe"));
        TextureManager.RegisterTexture(Texture.Register("textures/entity/pig.png", "entity_pig"));

        Input.Initialize();
        Window.Generate("Terracraft* 0.3.6", new Vector2i(750, 450), new Vector3f(0.0f, 0.45f, 0.75f));

        player.Initialize(new Vector3f(0, 20, 0));

        pig.Initialize(new Vector3f(0, 40, 0));

        World.Initialize();
        World.StartUpdating(player.transform.position);
    }

    public void Update()
    {
        Window.UpdateColors();

        Input.Update();

        player.Update();
        pig.Update();

        TaskExecutor.UpdateTasks();

        Renderer.RenderObjects(player.data.camera);

        Window.UpdateBuffers();
    }

    public void CleanUp()
    {
        World.StopUpdating();
        ShaderManager.CleanUp();
        Renderer.CleanUp();
        Input.CleanUp();
        Window.CleanUp();
    }

    @Override
    public void run()
    {
        Initialize();

        while (!Window.ShouldClose())
            Update();

        CleanUp();
    }

    public static void main(String[] arguments)
    {
        new Terracraft().PreInitialize();
    }
}