package dev.emythiel.createitemdrawers;

import dev.emythiel.createitemdrawers.ponder.CreateItemDrawersPonderPlugin;
import dev.emythiel.createitemdrawers.registry.ModConfigs;
import dev.emythiel.createitemdrawers.renderer.DrawerSlotHighlighter;
import dev.emythiel.createitemdrawers.renderer.DrawerTooltip;
import dev.emythiel.createitemdrawers.util.connection.DrawerSpriteShifts;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Supplier;

@Mod(value = CreateItemDrawers.MODID, dist = Dist.CLIENT)
public class CreateItemDrawersClient {

    public CreateItemDrawersClient(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(CreateItemDrawersClient::clientInit);
    }

    private static void clientInit() {
        PonderIndex.addPlugin(new CreateItemDrawersPonderPlugin());

        DrawerSpriteShifts.init();

        BaseConfigScreen.setDefaultActionFor(CreateItemDrawers.MODID, base -> base
            .withButtonLabels("Client Settings", null, "Balancing Settings")
            .withSpecs(ModConfigs.client().specification, null, ModConfigs.server().specification)
        );
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onLoadComplete(FMLLoadCompleteEvent event) {
            ModContainer createContainer = ModList.get()
                .getModContainerById(CreateItemDrawers.MODID)
                .orElseThrow(() -> new IllegalStateException("Create mod container missing on LoadComplete"));
            Supplier<IConfigScreenFactory> configScreen = () -> (mc, previousScreen) ->
                new BaseConfigScreen(previousScreen, CreateItemDrawers.MODID);
            createContainer.registerExtensionPoint(IConfigScreenFactory.class, configScreen);
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
}
