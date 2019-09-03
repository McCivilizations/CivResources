package com.mccivilizations.resources;

import com.mccivilizations.resources.api.CivResourcesAPI;
import com.mccivilizations.resources.api.resource.IResourceStorage;
import com.mccivilizations.resources.capability.ResourceStorage;
import com.mccivilizations.resources.capability.ResourceStorageProvider;
import com.mccivilizations.resources.command.CivResourcesCommand;
import com.mccivilizations.resources.json.JsonLoader;
import com.mccivilizations.resources.json.MapJsonDirector;
import com.mccivilizations.resources.json.ResourceJsonProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static com.mccivilizations.resources.CivResources.ID;

@Mod(ID)
public class CivResources {
    public static final String ID = "resources";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public CivResources() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CivResources::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(CivResources::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(CivResources::serverAboutToStart);
        MinecraftForge.EVENT_BUS.addGenericListener(World.class, CivResources::attachCapability);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(IResourceStorage.class, new Capability.IStorage<IResourceStorage>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IResourceStorage> capability, IResourceStorage instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IResourceStorage> capability, IResourceStorage instance, Direction side, INBT nbt) {
                instance.deserializeNBT((CompoundNBT) nbt);
            }
        }, ResourceStorage::new);
    }

    private static void serverStarting(FMLServerStartingEvent event) {
        event.getCommandDispatcher().register(CivResourcesCommand.create());
    }

    private static void serverAboutToStart(FMLServerAboutToStartEvent event) {
        event.getServer().getResourceManager().addReloadListener(new JsonLoader<>("McCivilizations Resources", "mccivilizations/resources",
                LOGGER, new MapJsonDirector<>(CivResourcesAPI.resources), new ResourceJsonProvider()));
    }

    private static void attachCapability(AttachCapabilitiesEvent<World> event) {
        World world = event.getObject();
        if (world.getDimension().getType() == DimensionType.OVERWORLD) {
            event.addCapability(new ResourceLocation(ID, "resource_storage"), new ResourceStorageProvider());
        }
    }
}
