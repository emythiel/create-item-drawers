package dev.emythiel.createitemdrawers.network.handler;

import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.network.RenderPacket;
import dev.emythiel.createitemdrawers.network.SlotTogglePacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("resource")
    public void handleDrawerConfig(final RenderPacket packet, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            var level = player.level();
            var be = level.getBlockEntity(packet.pos());

            if (be instanceof DrawerStorageBlockEntity drawer) {
                drawer.setRenderItems(packet.renderMode());
                drawer.setRenderCounts(packet.renderMode());
                drawer.setRenderIcons(packet.renderMode());
            }
        });
    }

    @SuppressWarnings("resource")
    public void handleSlotToggle(final SlotTogglePacket packet, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            var level = player.level();
            var be = level.getBlockEntity(packet.pos());
            if (be instanceof DrawerStorageBlockEntity drawer) {
                var slot = drawer.getStorage().getSlot(packet.slot());

                switch (packet.mode()) {
                    case LOCK -> slot.setLockMode(packet.value());
                    case VOID -> slot.setVoidMode(packet.value());
                    case ITEMS -> drawer.setRenderItems(packet.value());
                    case COUNTS -> drawer.setRenderCounts(packet.value());
                    case ICONS -> drawer.setRenderIcons(packet.value());

                    //default -> throw new IllegalArgumentException("Unexpected toggle mode: " + packet.mode());
                }

                drawer.setChangedAndSync();
            }
        });
    }
}
