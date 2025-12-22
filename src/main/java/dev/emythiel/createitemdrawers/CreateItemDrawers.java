package dev.emythiel.createitemdrawers;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.config.ClientConfig;
import dev.emythiel.createitemdrawers.config.ServerConfig;
import dev.emythiel.createitemdrawers.ponder.CreateItemDrawersPonderPlugin;
import dev.emythiel.createitemdrawers.registry.*;
import dev.emythiel.createitemdrawers.util.connection.DrawerSpriteShifts;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
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
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(CreateItemDrawers.MODID)
public class CreateItemDrawers {
    public static final String MODID = "create_item_drawers";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
        .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
        .setTooltipModifierFactory(item ->
            new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
        );

    public CreateItemDrawers(IEventBus modEventBus, ModContainer modContainer) {
        // Register Create Registrate
        REGISTRATE.registerEventListeners(modEventBus);

        // Client setup
        modEventBus.addListener(this::onClientSetup);

        // Register mod configuration files
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        // Register mod configuration screen
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modEventBus.register(ModPackets.class);
        modEventBus.addListener(this::registerCapabilities);


        ModBlocks.register();
        ModBlockEntities.register();
        ModMountedStorageTypes.register();
        ModItems.register();
        ModMenus.register();
        ModTabs.register(modEventBus);
    }

    static {
        DrawerSpriteShifts.init();
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // Otherwise textures don't load properly on start grr
        event.enqueueWork(DrawerSpriteShifts::init);
        PonderIndex.addPlugin(new CreateItemDrawersPonderPlugin());
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        DrawerStorageBlockEntity.registerCapabilities(event, ModBlockEntities.DRAWER_BLOCK_ENTITY.get());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }
}
