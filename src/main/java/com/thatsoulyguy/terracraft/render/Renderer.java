package com.thatsoulyguy.terracraft.render;

import com.thatsoulyguy.terracraft.core.LogLevel;
import com.thatsoulyguy.terracraft.core.Logger;
import com.thatsoulyguy.terracraft.player.Camera;
import com.thatsoulyguy.terracraft.records.NameIDTag;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Renderer
{
    private static final ConcurrentHashMap<NameIDTag, RenderableObject> registeredObjects = new ConcurrentHashMap<>();
    private static final Matrix4f translation = new Matrix4f().identity();
    public static boolean drawLines = false;

    public static void RegisterRenderableObject(RenderableObject object)
    {
        synchronized (registeredObjects)
        {
            registeredObjects.put(object.data.name, object);
        }
    }

    public static void RemoveRenderableObject(NameIDTag tag)
    {
        synchronized (registeredObjects)
        {
            registeredObjects.remove(tag);
        }
    }

    public static void RenderObjects(Camera camera)
    {
        for(RenderableObject object : registeredObjects.values())
        {
            if(!object.data.active)
                continue;

            GL30.glBindVertexArray(object.data.VAO);

            for (int q = 0; q < object.data.queuedData; q++)
                GL30.glEnableVertexAttribArray(q);

            int count = 0;
            for (String key : object.data.textures.keySet())
            {
                GL13.glActiveTexture(GL13.GL_TEXTURE0 + count);
                GL13.glBindTexture(GL11.GL_TEXTURE_2D, object.data.textures.get(key).id);

                count++;
            }

            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, object.data.EBO);

            Vector3f translatedPosition = new Vector3f(object.data.transform.position).sub(new Vector3f(object.data.transform.pivot));

            Matrix4f rotationMatrix = new Matrix4f()
                    .rotateXYZ(
                            object.data.transform.rotation.x,
                            object.data.transform.rotation.y,
                            object.data.transform.rotation.z
                    );

            Vector3f rotatedPosition = rotationMatrix.transformPosition(translatedPosition);
            Vector3f finalPosition = rotatedPosition.add(new Vector3f(object.data.transform.pivot));

            translation.setTranslation(finalPosition);
            translation.setRotationXYZ(
                    object.data.transform.rotation.x,
                    object.data.transform.rotation.y,
                    object.data.transform.rotation.z
            );
            translation.scale(object.data.transform.scale);

            object.data.shader.Use();
            object.data.shader.SetUniform("projection", camera.projection);
            object.data.shader.SetUniform("view", camera.view);

            object.data.shader.SetUniform("model", translation);

            if(!object.data.cullBackfaces)
                GL11.glDisable(GL11.GL_CULL_FACE);

            GL11.glLineWidth(5.0f);
            if(drawLines || object.data.wireFrame)
                GL11.glDrawElements(GL11.GL_LINE_STRIP, object.data.indices.size(), GL11.GL_UNSIGNED_INT, 0);
            else
                GL11.glDrawElements(GL11.GL_TRIANGLES, object.data.indices.size(), GL11.GL_UNSIGNED_INT, 0);

            if(!object.data.cullBackfaces)
                GL11.glEnable(GL11.GL_CULL_FACE);

            object.data.shader.UnUse();

            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

            for (int q = 0; q < object.data.queuedData; q++)
                GL30.glDisableVertexAttribArray(q);

            GL30.glBindVertexArray(0);

            int error = GL11.glGetError();
            if (error != GL11.GL_NO_ERROR)
                Logger.WriteConsole("RenOpenGL error detected: " + error, LogLevel.ERROR);
        }
    }

    public static RenderableObject GetObject(NameIDTag tag)
    {
        return registeredObjects.get(tag);
    }

    public static void CleanUp()
    {
        for(RenderableObject object : registeredObjects.values())
            object.CleanUp();

        registeredObjects.clear();
    }
}