package com.mccivilizations.resources.capability;

import com.mccivilizations.resources.api.CivResourcesAPI;
import com.mccivilizations.resources.api.resource.IResourceStorage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ResourceStorageProvider implements ICapabilitySerializable<CompoundNBT> {
    private final IResourceStorage resourceStorage;
    private final LazyOptional<IResourceStorage> resourceStorageOptional;

    public ResourceStorageProvider() {
        this(new ResourceStorage());
    }

    public ResourceStorageProvider(IResourceStorage resourceStorage) {
        this.resourceStorage = resourceStorage;
        this.resourceStorageOptional = LazyOptional.of(() -> resourceStorage);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CivResourcesAPI.RESOURCE_STORAGE_CAPABILITY ? resourceStorageOptional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return resourceStorage.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        resourceStorage.deserializeNBT(nbt);
    }
}
