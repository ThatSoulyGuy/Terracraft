package com.thatsoulyguy.terracraft.entity;

import com.thatsoulyguy.terracraft.math.AABB;
import com.thatsoulyguy.terracraft.records.Hash;

import java.util.Objects;

public class EntityRegistration
{
    public String uuid;
    public EntityType type;
    public String name;
    public AABB boundingBox;

    public static EntityRegistration Register(String name, EntityType type, AABB boundingBox)
    {
        EntityRegistration out = new EntityRegistration();

        out.name = name;
        out.type = type;
        if(Objects.equals(name, ""))
            out.uuid = Hash.GenerateMD5Hash(String.valueOf(Math.random()));
        else
            out.uuid = Hash.GenerateMD5Hash(name);

        out.boundingBox = boundingBox;

        return out;
    }
}