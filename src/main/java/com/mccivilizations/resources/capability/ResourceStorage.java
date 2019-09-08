package com.mccivilizations.resources.capability;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mccivilizations.resources.api.CivResourcesAPI;
import com.mccivilizations.resources.api.resource.IResourceStorage;
import com.mccivilizations.resources.api.resource.Resource;
import net.minecraft.nbt.CompoundNBT;

import java.util.Map;

public class ResourceStorage implements IResourceStorage {
    private final Table<String, Resource, Long> resources;

    public ResourceStorage() {
        this.resources = HashBasedTable.create();
    }

    @Override
    public long add(String name, Resource resource, long amount, boolean commit) {
        long amountAdded = 0;
        if (amount > 0) {
            long currentAmount = this.getAmount(name, resource);

            if (currentAmount < resource.getMaxAmount()) {
                amountAdded = Math.min(amount, resource.getMaxAmount() - currentAmount);
                if (commit) {
                    resources.put(name, resource, currentAmount + amountAdded);
                }
            }
        }

        return amountAdded;
    }

    @Override
    public long remove(String name, Resource resource, long amount, boolean commit) {
        long amountRemoved = 0;
        if (amount > 0) {
            long currentAmount = this.getAmount(name, resource);

            if (currentAmount > resource.getMinAmount()) {
                amountRemoved = Math.min(amount, currentAmount - resource.getMinAmount());
                if (commit) {
                    resources.put(name, resource, currentAmount - amountRemoved);
                }
            }
        }

        return amountRemoved;
    }

    @Override
    public void empty(String name, Resource resource) {
        resources.put(name, resource, 0L);
    }

    @Override
    public long getAmount(String name, Resource resource) {
        ensureExists(name, resource);
        return resources.get(name, resource);
    }

    @Override
    public Map<Resource, Long> getResourcesFor(String name) {
        return resources.row(name);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        for (String teamName : resources.rowKeySet()) {
            Map<Resource, Long> teamResources = resources.row(teamName);
            CompoundNBT teamCompound = new CompoundNBT();
            for (Map.Entry<Resource, Long> resource : teamResources.entrySet()) {
                teamCompound.putLong(resource.getKey().getRegistryName().toString(), resource.getValue());
            }
            compound.put(teamName, teamCompound);
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        for (String teamName : nbt.keySet()) {
            CompoundNBT teamInfo = nbt.getCompound(teamName);
            for (Resource resource: CivResourcesAPI.resources.values()) {
                resources.put(teamName, resource, teamInfo.getLong(resource.getRegistryName().toString()));
            }
        }
    }

    private void ensureExists(String name, Resource resource) {
        if (!resources.contains(name, resource)) {
            resources.put(name, resource, 0L);
        }
    }
}
