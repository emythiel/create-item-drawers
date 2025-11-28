package dev.emythiel.createitemdrawers.registry;

import dev.emythiel.createitemdrawers.network.DrawerConfigPacket;
import dev.emythiel.createitemdrawers.network.handler.ServerPayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPackets {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {

        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
            DrawerConfigPacket.TYPE,
            DrawerConfigPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleDrawerConfig
        );
    }
}
