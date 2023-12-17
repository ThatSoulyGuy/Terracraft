package com.thatsoulyguy.terracraft.player;

import com.thatsoulyguy.terracraft.core.Input;
import com.thatsoulyguy.terracraft.core.PressType;
import com.thatsoulyguy.terracraft.entity.LivingEntity;
import com.thatsoulyguy.terracraft.entity.LivingEntityRegistration;
import com.thatsoulyguy.terracraft.entity.MovementImpulse;
import com.thatsoulyguy.terracraft.math.Raycast;
import com.thatsoulyguy.terracraft.records.NameIDTag;
import com.thatsoulyguy.terracraft.render.RenderableObject;
import com.thatsoulyguy.terracraft.render.Renderer;
import com.thatsoulyguy.terracraft.world.BlockType;
import com.thatsoulyguy.terracraft.world.World;
import org.joml.Vector3f;
import org.joml.Math;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

public class Player extends LivingEntity
{
    public PlayerData data = new PlayerData();

    private float cameraOffset = 1.55f;

    public void Initialize(Vector3f position)
    {
        data.camera.Initialize(position);

        Input.SetCursorMode(false);
        transform.position = position;
        data.wireframeBox = RenderableObject.Register(NameIDTag.Register("wireframe", data.wireframeBox), null, null, true, "default");
        data.wireframeBox.GenerateCube();
        data.wireframeBox.data.active = false;
        Renderer.RegisterRenderableObject(data.wireframeBox);

        LEBase_Initialize();
    }

    public void Update()
    {
        data.camera.Update();

        LEBase_Update();

        UpdateControls();
        UpdateMouseLook();
        UpdateMovement();

        data.camera.data.transform.position = new Vector3f(transform.position.x, transform.position.y + cameraOffset, transform.position.z);
        transform.rotation.y = data.camera.data.yaw;
    }

    private void UpdateControls()
    {
        if(Input.GetKey(GLFW.GLFW_KEY_L, PressType.PRESSED))
            Renderer.drawLines = true;

        Vector3i wireframeBlockPosition = Raycast.Shoot(data.camera.data.transform.position, data.camera.data.transform.rotation, 5.0f);

        if(wireframeBlockPosition != null)
        {
            data.wireframeBox.data.active = true;
            data.wireframeBox.data.transform.position = new Vector3f((float)wireframeBlockPosition.x + 0.5f, (float)wireframeBlockPosition.y + 0.5f, (float)wireframeBlockPosition.z + 0.5f);
        }
        else
            data.wireframeBox.data.active = false;


        if(Input.GetMouseButton(0, PressType.PRESSED))
        {
            Vector3i blockPosition = Raycast.Shoot(data.camera.data.transform.position, data.camera.data.transform.rotation, 5.0f);

            if(blockPosition != null)
                World.SetBlock(blockPosition, BlockType.BLOCK_AIR);
        }

        if (Input.GetMouseButton(1, PressType.PRESSED))
        {
            Vector3i blockPosition = Raycast.Shoot(data.camera.data.transform.position, data.camera.data.transform.rotation, 5.0f);

            if (blockPosition != null)
            {
                Vector3f direction = new Vector3f();

                float pitch = data.camera.data.pitch;
                float yaw = data.camera.data.yaw;

                direction.x = (float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
                direction.y = (float) (Math.sin(Math.toRadians(pitch)));
                direction.z = (float) (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));

                Vector3i face = new Vector3i();

                if (Math.abs(direction.x) > Math.abs(direction.y) && Math.abs(direction.x) > Math.abs(direction.z))
                    face.x = (int) Math.signum(direction.x);
                else if (Math.abs(direction.y) > Math.abs(direction.z))
                    face.y = (int) Math.signum(direction.y);
                else
                    face.z = (int) Math.signum(direction.z);

                Vector3i newBlockPosition = blockPosition.sub(face);
                World.SetBlock(newBlockPosition, BlockType.BLOCK_GRASS);
            }
        }
    }

    private void UpdateMovement()
    {
        if (Input.GetKey(GLFW.GLFW_KEY_W, PressType.PRESSED))
            AddMovement(MovementImpulse.FORWARD);

        if (Input.GetKey(GLFW.GLFW_KEY_S, PressType.PRESSED))
            AddMovement(MovementImpulse.BACKWARD);

        if (Input.GetKey(GLFW.GLFW_KEY_A, PressType.PRESSED))
            AddMovement(MovementImpulse.LEFT);

        if (Input.GetKey(GLFW.GLFW_KEY_D, PressType.PRESSED))
            AddMovement(MovementImpulse.RIGHT);

        if (Input.GetKey(GLFW.GLFW_KEY_SPACE, PressType.PRESSED))
            Jump();

        if (Input.GetKey(GLFW.GLFW_KEY_LEFT_SHIFT, PressType.PRESSED))
            cameraOffset = 1.34f;
        else
            cameraOffset = 1.55f;
    }

    private void UpdateMouseLook()
    {
        data.newMouse.x = Input.mousePosition.x;
        data.newMouse.y = Input.mousePosition.y;

        float dx = (data.newMouse.x - data.oldMouse.x);
        float dy = (data.newMouse.y - data.oldMouse.y);

        data.camera.data.yaw += dx * data.mouseSensitivity;
        data.camera.data.pitch -= dy * data.mouseSensitivity;

        if(data.camera.data.pitch > 90)
            data.camera.data.pitch = 89.99f;

        if(data.camera.data.pitch < -90)
            data.camera.data.pitch = -89.99f;

        data.oldMouse.x = data.newMouse.x;
        data.oldMouse.y = data.newMouse.y;
    }

    @Override
    public LivingEntityRegistration Register()
    {
        return LivingEntityRegistration.Register(20, 0.1f, new Vector3f(0.4f, 1.98f, 0.4f));
    }
}