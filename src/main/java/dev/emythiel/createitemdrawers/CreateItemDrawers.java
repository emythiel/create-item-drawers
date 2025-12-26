package dev.emythiel.createitemdrawers;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.ponder.CreateItemDrawersPonderPlugin;
import dev.emythiel.createitemdrawers.registry.*;
import dev.emythiel.createitemdrawers.renderer.DrawerSlotHighlighter;
import dev.emythiel.createitemdrawers.renderer.DrawerTooltip;
import dev.emythiel.createitemdrawers.util.connection.DrawerSpriteShifts;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
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
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        // Register Create Registrate
        REGISTRATE.registerEventListeners(modEventBus);

        modEventBus.register(ModPackets.class);

        ModTabs.register(modEventBus);
        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        ModMenuTypes.register();
        ModMountedStorageTypes.register();

        ModConfigs.register(modLoadingContext, modContainer);
    }

    @EventBusSubscriber(modid = MODID)
    public static class CommonModEvents {

        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            DrawerStorageBlockEntity.registerCapabilities(event, ModBlockEntities.DRAWER_BLOCK_ENTITY.get());
        }
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
            event.enqueueWork(DrawerSpriteShifts::init);
            PonderIndex.addPlugin(new CreateItemDrawersPonderPlugin());
        }

        @SubscribeEvent
        public static void onRenderHighlight(RenderHighlightEvent.Block event) {
            DrawerSlotHighlighter.onRenderHighlight(event);
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            DrawerTooltip.tick();
        }
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
