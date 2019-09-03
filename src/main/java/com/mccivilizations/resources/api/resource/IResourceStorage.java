package com.mccivilizations.resources.api.resource;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;

public interface IResourceStorage extends INBTSerializable<CompoundNBT> {
    /**
     * @param name     holder name
     * @param resource name of the resource
     * @param amount   number of resource to add
     * @param commit   true should commit the change
     * @return amount actually added
     */
    long add(String name, Resource resource, long amount, boolean commit);

    /**
     * @param name     holder name
     * @param resource name of the resource
     * @param amount   number of resource to remove
     * @param commit   true should commit the change
     * @return amount actually removed
     */
    long remove(String name, Resource resource, long amount, boolean commit);

    void empty(String name, Resource resource);

    long getAmount(String name, Resource resource);

    Map<Resource, Long> getResourcesFor(String name);

    default long add(Team team, Resource resource, long amount, boolean commit) {
        return add(team.getName(), resource, amount, commit);
    }

    default long add(Entity entity, Resource resource, long amount, boolean commit) {
        return add(entity.getUniqueID().toString(), resource, amount, commit);
    }

    default long add(Entity entity, Resource resource, long amount, boolean tryTeamFirst, boolean commit) {
        if (tryTeamFirst) {
            Team team = entity.getTeam();
            if (team != null) {
                return add(team, resource, amount, commit);
            } else {
                return add(entity, resource, amount, commit);
            }
        } else {
            return add(entity, resource, amount, commit);
        }
    }

    default long remove(Team team, Resource resource, long amount, boolean commit) {
        return remove(team.getName(), resource, amount, commit);
    }

    default long remove(Entity entity, Resource resource, long amount, boolean commit) {
        return remove(entity.getUniqueID().toString(), resource, amount, commit);
    }

    default long remove(Entity entity, Resource resource, long amount, boolean tryTeamFirst, boolean commit) {
        if (tryTeamFirst) {
            return 0;
        } else {
            return remove(entity, resource, amount, commit);
        }
    }

    default long getAmount(Team team, Resource resource) {
        return getAmount(team.getName(), resource);
    }

    default long getAmount(Entity entity, Resource resource) {
        return getAmount(entity.getUniqueID().toString(), resource);
    }

    default long getAmount(Entity entity, Resource resource, boolean includeTeam) {
        if (includeTeam) {
            Team team = entity.getTeam();
            long amount = getAmount(entity, resource);
            if (team != null) {
                amount += getAmount(team, resource);
            }
            return amount;
        } else {
            return getAmount(entity, resource);
        }
    }

    default void empty(Team team, Resource resource) {
        empty(team.getName(), resource);
    }

    default void empty(Entity entity, Resource resource) {
        empty(entity.getUniqueID().toString(), resource);
    }

    default void empty(Entity entity, Resource resource, boolean includeTeam) {
        empty(entity, resource);
        if (includeTeam) {
            Team team = entity.getTeam();
            if (team != null) {
                empty(team, resource);
            }
        }
    }
}
