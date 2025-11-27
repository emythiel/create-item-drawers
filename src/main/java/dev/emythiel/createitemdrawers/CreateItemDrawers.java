package dev.emythiel.createitemdrawers;

import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.config.ClientConfig;
import dev.emythiel.createitemdrawers.config.ServerConfig;
import dev.emythiel.createitemdrawers.registry.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateItemDrawers.MODID)
public class CreateItemDrawers {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_item_drawers";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "create_item_drawers" namespace

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CreateItemDrawers(IEventBus modEventBus, ModContainer modContainer) {
        // Register mod configuration files
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        // Register mod configuration screen
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (CreateItemDrawers) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register Create Registrate
        CreateItemDrawersRegistrate.REGISTRATE.registerEventListeners(modEventBus);

        // Register blocks and items
        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register(modEventBus);

        // Register creative tab
        ModCreativeModeTab.register(modEventBus);

        // Register GUI screens
        ModMenus.register();
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.DRAWER_BLOCK_ENTITY.get(),
            DrawerBlockEntity::getItemHandler
        );
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
