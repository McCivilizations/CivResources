package com.mccivilizations.resources.command;

import com.google.common.collect.Lists;
import com.mccivilizations.resources.ThrowingFunction;
import com.mccivilizations.resources.api.CivResourcesAPI;
import com.mccivilizations.resources.api.resource.Resource;
import com.mccivilizations.resources.api.resource.IResourceStorage;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.TeamArgument;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CivResourcesCommand {
    public static LiteralArgumentBuilder<CommandSource> create() {
        return LiteralArgumentBuilder.<CommandSource>literal("mccivilizations")
                .then(Commands.literal("resources")
                        .then(team())
                        .then(entity())
                        .executes(CivResourcesCommand::listAllResources)
                );
    }

    private static LiteralArgumentBuilder<CommandSource> entity() {
        return Commands.literal("entity")
                .then(Commands.argument("entity", EntityArgument.entities())
                        .then(Commands.argument("resource", new MapRegistryArgument<>(CivResourcesAPI.resources))
                                .then(Commands.literal("add")
                                        .then(Commands.argument("amount", LongArgumentType.longArg(1))
                                                .executes(CivResourcesCommand.handleAlter(context -> EntityArgument.getEntities(context, "entity"),
                                                        (context, entities, resource, amount) -> {
                                                            IResourceStorage storage = get(context);
                                                            return entities.stream()
                                                                    .map(entity -> storage.add(entity, resource, amount, true))
                                                                    .reduce(0L, Long::sum);
                                                        }, "added")
                                                )
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("amount", LongArgumentType.longArg(1))
                                                .executes(CivResourcesCommand.handleAlter(context -> EntityArgument.getEntities(context, "entity"),
                                                        (context, entities, resource, amount) -> {
                                                            IResourceStorage storage = get(context);
                                                            return entities.stream()
                                                                    .map(entity -> storage.remove(entity, resource, amount, true))
                                                                    .reduce(0L, Long::sum);
                                                        }, "removed")
                                                )
                                        )
                                )
                        )
                        .executes(CivResourcesCommand.listResourcesFor(context -> EntityArgument.getEntities(context, "entity"),
                                target -> target.getUniqueID().toString()))

                );
    }

    private static LiteralArgumentBuilder<CommandSource> team() {
        return Commands.literal("team")
                .then(Commands.argument("team", TeamArgument.team())
                        .then(Commands.argument("resource", new MapRegistryArgument<>(CivResourcesAPI.resources))
                                .then(Commands.literal("add")
                                        .then(Commands.argument("amount", LongArgumentType.longArg(1))
                                                .executes(CivResourcesCommand.handleAlter(context -> TeamArgument.getTeam(context, "team"),
                                                        (context, team, resource, amount) -> get(context).add(team, resource, amount, true), "added"))
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("amount", LongArgumentType.longArg(1))
                                                .executes(CivResourcesCommand.handleAlter(context -> TeamArgument.getTeam(context, "team"),
                                                        (context, team, resource, amount) -> get(context).remove(team, resource, amount, true), "removed"))
                                        )
                                )
                        ).executes(CivResourcesCommand.listResourcesFor(context -> Lists.newArrayList(TeamArgument.getTeam(context, "team")),
                                Team::getName))

                );
    }

    private static <T> Command<CommandSource> handleAlter(ThrowingFunction<CommandContext<CommandSource>, T, CommandSyntaxException> grabTarget,
                                                          ResourceAlterer<T> handleChange, String text) {
        return context -> {
            Resource resource = context.getArgument("resource", Resource.class);
            T target = grabTarget.apply(context);
            long amount = LongArgumentType.getLong(context, "amount");
            long amountAltered = handleChange.alter(context, target, resource, amount);
            message(context).accept(createTranslation(text, amountAltered));
            return 1;
        };
    }

    private static ITextComponent createTranslation(String additionalText, Object... inputs) {
        return new TranslationTextComponent("mccivilizations.resource.command." + additionalText, inputs);
    }

    private static <T> Command<CommandSource> listResourcesFor(ThrowingFunction<CommandContext<CommandSource>, Collection<T>, CommandSyntaxException> grabTarget,
                                                               Function<T, String> name) {
        return context -> {
            IResourceStorage storage = get(context);
            Collection<T> targets = grabTarget.apply(context);
            for (T target : targets) {
                String targetName = name.apply(target);
                context.getSource().sendFeedback(createTranslation("resources_for", targetName), false);
                storage.getResourcesFor(targetName).entrySet()
                        .stream()
                        .map(entry -> entry.getKey().getName().appendSibling(
                                new StringTextComponent(" - " + entry.getValue())))
                        .forEach(message(context));
            }
            return 1;
        };
    }

    private static int listAllResources(CommandContext<CommandSource> context) {
        CivResourcesAPI.resources.values()
                .stream()
                .map(Resource::getName)
                .forEach(message(context));
        return 1;
    }

    private static Consumer<ITextComponent> message(CommandContext<CommandSource> context) {
        return textComponent -> context.getSource().sendFeedback(textComponent, true);
    }

    private static IResourceStorage get(CommandContext<CommandSource> context) {
        return context.getSource()
                .getServer()
                .getWorld(DimensionType.OVERWORLD)
                .getCapability(CivResourcesAPI.RESOURCE_STORAGE_CAPABILITY)
                .orElseThrow(() -> new CommandException(createTranslation("missing_resource_storage")));
    }

    private interface ResourceAlterer<T> {
        long alter(CommandContext<CommandSource> context, T target, Resource resource, Long amount);
    }
}
