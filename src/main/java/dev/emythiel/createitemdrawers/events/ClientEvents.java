package dev.emythiel.createitemdrawers.events;

import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.renderer.DrawerSlotHighlighter;
import dev.emythiel.createitemdrawers.renderer.DrawerTooltip;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Supplier;

@EventBusSubscriber(modid = CreateItemDrawers.MODID, value = Dist.CLIENT)
public class ClientEvents {

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
