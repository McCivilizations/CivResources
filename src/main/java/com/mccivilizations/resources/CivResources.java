package com.mccivilizations.resources;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import static com.mccivilizations.resources.CivResources.ID;

@Mod(ID)
public class CivResources {
    public static final String ID = "civ_resources";

    public CivResources() {
        MinecraftForge.EVENT_BUS.addListener(CivResources::serverStarting);
    }

    private static void serverStarting(FMLServerStartingEvent event) {
        event.getCommandDispatcher().register(CivResourcesCommand.create());
    }
}
