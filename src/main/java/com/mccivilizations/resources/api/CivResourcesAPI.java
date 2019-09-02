package com.mccivilizations.resources.api;

import com.google.common.collect.Maps;
import com.mccivilizations.resources.api.resource.Resource;
import com.mccivilizations.resources.api.storage.IResourceStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Map;

public class CivResourcesAPI {
    public static final Map<ResourceLocation, Resource> resources = Maps.newHashMap();

    @CapabilityInject(IResourceStorage.class)
    public static Capability<IResourceStorage> RESOURCE_STORAGE_CAPABILITY;
}
