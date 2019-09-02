package com.mccivilizations.resources;

import com.mccivilizations.resources.api.CivResourcesAPI;
import com.mccivilizations.resources.api.resource.Resource;
import com.mccivilizations.resources.api.storage.IResourceStorage;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.TeamArgument;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CivResourcesCommand {
    public static LiteralArgumentBuilder<CommandSource> create() {
        return LiteralArgumentBuilder.<CommandSource>literal("civilizations")
                .then(Commands.literal("resources")
                        .then(team())
                        .then(entity())
                        .executes(CivResourcesCommand::listAllResources)
                );
    }

    private static LiteralArgumentBuilder<CommandSource> entity() {
        return Commands.literal("entity");
    }

    private static LiteralArgumentBuilder<CommandSource> team() {
        return Commands.literal("team")
                .then(Commands.argument("team", TeamArgument.team())
                        .then(Commands.argument("resource",  ))
                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", LongArgumentType.longArg())

                                )
                        ).executes(CivResourcesCommand::listResourcesForTeam)
                );
    }

    private static int listResourcesForTeam(CommandContext<CommandSource> context) {

        return 0;
    }

    private static int listAllResources(CommandContext<CommandSource> context) {
        CivResourcesAPI.resources.values()
                .stream()
                .map(Resource::getName)
                .forEach(message(context));
        return 0;
    }

    private static Consumer<ITextComponent> message(CommandContext<CommandSource> context) {
        return textComponent -> context.getSource().sendFeedback(textComponent, true);
    }

    private static IResourceStorage get(CommandContext<CommandSource> context) {
        return context.getSource()
                .getServer()
                .getWorld(DimensionType.OVERWORLD)
                .getCapability(CivResourcesAPI.RESOURCE_STORAGE_CAPABILITY)
                .orElseThrow(exceptionSupplier("civs.resources.command.missing_resource_storage")));
    }

    private static NonNullSupplier<CommandException> exceptionSupplier(String translationKey) {
        return () -> new CommandException(new TranslationTextComponent(translationKey);
    }
}
