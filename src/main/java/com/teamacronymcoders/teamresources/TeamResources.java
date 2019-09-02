package com.teamacronymcoders.teamresources;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod(TeamResources.ID)
public class TeamResources {
    public static final String ID = "teamresources";

    public TeamResources() {
        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
    }

    private void onServerStart(FMLServerStartingEvent event) {
        event.getCommandDispatcher().register(TeamResourcesCommand.create());
    }
}
