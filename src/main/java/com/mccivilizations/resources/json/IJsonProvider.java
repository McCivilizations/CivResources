package com.mccivilizations.resources.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.ResourceLocation;

@FunctionalInterface
public interface IJsonProvider<T> {
    T provide(ResourceLocation resourceLocation, JsonObject jsonObject) throws JsonParseException;
}