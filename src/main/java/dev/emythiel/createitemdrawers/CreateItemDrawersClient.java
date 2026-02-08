package dev.emythiel.createitemdrawers;

import dev.emythiel.createitemdrawers.ponder.CreateItemDrawersPonderPlugin;
import dev.emythiel.createitemdrawers.registry.ModConfigs;
import dev.emythiel.createitemdrawers.util.connection.DrawerSpriteShifts;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = CreateItemDrawers.MODID, dist = Dist.CLIENT)
public class CreateItemDrawersClient {

    public CreateItemDrawersClient(IEventBus modEventBus) {
        onClientSetup(modEventBus);
    }

    private void onClientSetup(IEventBus modEventBus) {
        modEventBus.addListener(CreateItemDrawersClient::clientInit);
    }

    private static void clientInit(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new CreateItemDrawersPonderPlugin());

        DrawerSpriteShifts.init();

        BaseConfigScreen.setDefaultActionFor(CreateItemDrawers.MODID, base -> base
            .withButtonLabels("Client Settings", null, "Balancing Settings")
            .withSpecs(ModConfigs.client().specification, null, ModConfigs.server().specification)
        );
    }
}
