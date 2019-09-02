package com.mccivilizations.resources.api.resource;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class Resource {
    private final ResourceLocation registryName;
    private final long maxAmount;
    private final long minAmount;
    private final ITextComponent name;

    public Resource(ResourceLocation registryName, long maxAmount, long minAmount, ITextComponent name) {
        this.registryName = registryName;
        this.maxAmount = maxAmount;
        this.minAmount = minAmount;
        this.name = name;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public long getMaxAmount() {
        return maxAmount;
    }

    public long getMinAmount() {
        return minAmount;
    }

    public ITextComponent getName() {
        return name;
    }
}
