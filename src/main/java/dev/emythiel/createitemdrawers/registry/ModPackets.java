package dev.emythiel.createitemdrawers.registry;

import dev.emythiel.createitemdrawers.network.RenderPacket;
import dev.emythiel.createitemdrawers.network.SlotTogglePacket;
import dev.emythiel.createitemdrawers.network.SyncMountedStoragePacket;
import dev.emythiel.createitemdrawers.network.handler.ClientPayloadHandler;
import dev.emythiel.createitemdrawers.network.handler.ServerPayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPackets {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {

        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
            SyncMountedStoragePacket.TYPE,
            SyncMountedStoragePacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleSyncMountedStorage
        );

        registrar.playToServer(
            RenderPacket.TYPE,
            RenderPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleDrawerConfig
        );

        registrar.playToServer(
            SlotTogglePacket.TYPE,
            SlotTogglePacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleSlotToggle
        );
    }
}
