package dev.emythiel.createitemdrawers.client;


import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.client.renderer.DrawerRenderer;
import dev.emythiel.createitemdrawers.client.renderer.DrawerSlotHighlighter;
import dev.emythiel.createitemdrawers.registry.ModBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;


@Mod(value = CreateItemDrawers.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CreateItemDrawers.MODID, value = Dist.CLIENT)
public class DrawerClientEvents {

    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        DrawerSlotHighlighter.onRenderHighlight(event);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
            ModBlockEntities.DRAWER_BLOCK_ENTITY.get(),
            DrawerRenderer::new
        );
    }

}
