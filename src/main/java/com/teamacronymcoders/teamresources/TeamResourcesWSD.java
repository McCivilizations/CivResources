package com.teamacronymcoders.teamresources;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

public class TeamResourcesWSD extends WorldSavedData {
    private final Table<String, String, Long> resources;

    public TeamResourcesWSD() {
        super("teamresources");
        this.resources = HashBasedTable.create();
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        for (String teamName : nbt.keySet()) {
            CompoundNBT teamInfo = nbt.getCompound(teamName);
            for (String resourceName : teamInfo.keySet()) {
                Long resourceAmount = teamInfo.getLong(resourceName);
                resources.put(teamName, resourceName, resourceAmount);
            }
        }
    }

    @Override
    @Nonnull
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        for (String teamName : resources.columnKeySet()) {
            Map<String, Long> teamResources = resources.column(teamName);
            CompoundNBT teamCompound = new CompoundNBT();
            for (Map.Entry<String, Long> resource : teamResources.entrySet()) {
                teamCompound.putLong(resource.getKey(), resource.getValue());
            }
            compound.put(teamName, teamCompound);
        }
        return compound;
    }

    public Collection<String> getAllResources() {
        return resources.columnKeySet();
    }

    public void add(Team team, String resource, long amount) {
        if (resources.contains(team.getName(), resource)) {
            resources.put(team.getName(), resource, resources.get(team.getName(), resource) + amount);
        } else {
            resources.put(team.getName(), resource, amount);
        }
    }

    public Map<String, Long> getTeamResources(Team team) {
        return resources.column(team.getName());
    }

    public void empty(Team team, String resource) {
        resources.put(team.getName(), resource, 0L);
    }

    public Long getAmount(Team team, String resource) {
        return resources.get(team.getName(), resource);
    }
}
