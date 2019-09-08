package com.mccivilizations.resources.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mccivilizations.resources.api.resource.Resource;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;
import java.util.function.Supplier;

public class ResourceJsonProvider implements IJsonProvider<Resource> {
    @Override
    public Resource provide(ResourceLocation resourceLocation, JsonObject jsonObject) throws JsonParseException {
        return new Resource(
                resourceLocation,
                JSONUtils.func_219796_a(jsonObject, "min", 0),
                JSONUtils.func_219796_a(jsonObject, "max", Long.MAX_VALUE),
                Optional.ofNullable(jsonObject.get("name"))
                    .map(ITextComponent.Serializer::fromJson)
                    .orElseGet(provideTranslation(resourceLocation))
        );
    }

    private Supplier<? extends ITextComponent> provideTranslation(ResourceLocation resourceLocation) {
        return () -> {
            String rlToString = resourceLocation.getNamespace() + "." +
                    resourceLocation.getPath().replace("/", ".");
            return new TranslationTextComponent("mccivilizations.resources." + rlToString);
        };
    }
}
