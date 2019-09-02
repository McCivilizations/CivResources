package com.mccivilizations.resources.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.logging.Logger;

@ParametersAreNonnullByDefault
public class JsonLoader<T> extends JsonReloadListener {
    private final IJsonDirector<T> director;
    private final String type;
    private final Logger logger;
    private final IJsonProvider<T> jsonProvider;

    public JsonLoader(String type, Logger logger, IJsonDirector<T> director, IJsonProvider<T> jsonProvider) {
        super(new Gson(), type);
        this.type = type;
        this.logger = logger;
        this.director = director;
        this.jsonProvider = jsonProvider;
    }

    private ResourceLocation transformRL(ResourceLocation resource) {
        return new ResourceLocation(resource.getNamespace(), resource.getPath().replace(type + "/", ""));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> ts, IResourceManager resourceManager, IProfiler iProfiler) {
        director.clear();
        ts.entrySet()
                .parallelStream()
                .map(entry -> new Tuple<>(entry.getKey(), jsonProvider.provide(entry.getKey(), entry.getValue())))
                .forEach(tuple -> director.put(tuple.getA(), tuple.getB()));
        logger.info("Loaded " + ts.size() + " " + type);
    }
}