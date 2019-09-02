package com.teamacronymcoders.teamresources;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.TeamArgument;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;

import java.util.Collection;
import java.util.Objects;

public class TeamResourcesCommand {
    public static LiteralArgumentBuilder<CommandSource> create() {
        return LiteralArgumentBuilder.<CommandSource>literal("teamresources")
                .requires(source -> source.hasPermissionLevel(2))
                .then(resources());
    }

    public static LiteralArgumentBuilder<CommandSource> resources() {
        return LiteralArgumentBuilder.<CommandSource>literal("resources")
                .then(Commands.literal("team")
                        .then(Commands.argument("team", TeamArgument.team())
                                .then(Commands.argument("resource", StringArgumentType.string())
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                                        .executes(context -> {
                                                            ScorePlayerTeam team = TeamArgument.getTeam(context, "team");
                                                            String resource = StringArgumentType.getString(context, "resource");
                                                            long amount = LongArgumentType.getLong(context, "amount");
                                                            getSavedData(context).add(team, resource, amount);
                                                            return 0;
                                                        })
                                                )
                                        ).then(Commands.literal("empty")
                                                .executes(context -> {
                                                    ScorePlayerTeam team = TeamArgument.getTeam(context, "team");
                                                    String resource = StringArgumentType.getString(context, "resource");
                                                    getSavedData(context).empty(team, resource);
                                                    return 0;
                                                })
                                        ).executes(context -> {
                                            ScorePlayerTeam team = TeamArgument.getTeam(context, "team");
                                            String resource = StringArgumentType.getString(context, "resource");
                                            Long amount = getSavedData(context).getAmount(team, resource);
                                            if (amount != null) {
                                                context.getSource().sendFeedback(new StringTextComponent("Amount is " + amount), true);
                                            } else {
                                                context.getSource().sendFeedback(new StringTextComponent("Amount is 0"), true);
                                            }
                                            return 0;
                                        })
                                ).executes(context -> {
                                    ScorePlayerTeam team = TeamArgument.getTeam(context, "team");
                                    getSavedData(context).getTeamResources(team)
                                            .entrySet()
                                            .stream()
                                            .map(entry -> new StringTextComponent(entry.getKey() + " - " + entry.getValue()))
                                            .forEach(stringTextComponent ->
                                                    context.getSource().sendFeedback(stringTextComponent, true));
                                    return 0;
                                })
                        )
                ).then(Commands.literal("target")
                        .then(Commands.argument("target", EntityArgument.entities())
                                .then(Commands.argument("resource", StringArgumentType.string())
                                        .suggests(suggestedResources())
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                                        .executes(context -> {
                                                            Collection<? extends Entity> entities = EntityArgument.getEntities(context, "target");
                                                            String resource = StringArgumentType.getString(context, "resource");
                                                            long amount = LongArgumentType.getLong(context, "amount");
                                                            TeamResourcesWSD teamResourcesWSD = getSavedData(context);
                                                            entities.stream()
                                                                    .map(Entity::getTeam)
                                                                    .filter(Objects::nonNull)
                                                                    .forEach(team -> teamResourcesWSD.add(team, resource, amount));
                                                            return 0;
                                                        })
                                                )
                                        ).then(Commands.literal("empty")
                                                .executes(context -> {
                                                    Collection<? extends Entity> entities = EntityArgument.getEntities(context, "target");
                                                    String resource = StringArgumentType.getString(context, "resource");
                                                    TeamResourcesWSD teamResourcesWSD = getSavedData(context);
                                                    entities.stream()
                                                            .map(Entity::getTeam)
                                                            .filter(Objects::nonNull)
                                                            .forEach(team -> teamResourcesWSD.empty(team, resource));
                                                    return 0;
                                                })
                                        )
                                )
                        )
                ).executes(context -> {
                    getResources(context)
                            .stream()
                            .map(StringTextComponent::new)
                            .forEach(iTextComponents -> context.getSource().sendFeedback(iTextComponents, true));
                    return 0;
                });
    }

    private static Collection<String> getResources(CommandContext<CommandSource> context) {
        return getSavedData(context)
                .getAllResources();

    }

    private static TeamResourcesWSD getSavedData(CommandContext<CommandSource> context) {
        return context.getSource()
                .getServer()
                .getWorld(DimensionType.OVERWORLD)
                .getSavedData()
                .getOrCreate(TeamResourcesWSD::new, "teamresources");
    }

    private static SuggestionProvider<CommandSource> suggestedResources() {
        return (context, builder) -> ISuggestionProvider.suggest(getResources(context).stream(), builder);
    }
}
