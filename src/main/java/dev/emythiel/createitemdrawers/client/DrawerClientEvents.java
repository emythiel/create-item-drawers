package dev.emythiel.createitemdrawers.client;

import dev.emythiel.createitemdrawers.client.renderer.DrawerSlotHighlighter;
import dev.emythiel.createitemdrawers.client.renderer.DrawerTooltip;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;


@EventBusSubscriber(Dist.CLIENT)
public class DrawerClientEvents {

    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        DrawerSlotHighlighter.onRenderHighlight(event);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        DrawerTooltip.tick();
    }
}
