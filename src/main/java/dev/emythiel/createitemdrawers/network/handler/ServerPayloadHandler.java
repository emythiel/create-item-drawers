package dev.emythiel.createitemdrawers.network.handler;

import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.network.RenderPacket;
import dev.emythiel.createitemdrawers.network.SlotTogglePacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleDrawerConfig(final RenderPacket packet, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            var level = player.level();
            var be = level.getBlockEntity(packet.pos());

            if (be instanceof DrawerBlockEntity drawer) {
                drawer.applyRenderMode(packet.renderMode());
            }
        });
    }

    public void handleSlotToggle(final SlotTogglePacket packet, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();

            var level = player.level();
            var be = level.getBlockEntity(packet.pos());
            if (be instanceof DrawerBlockEntity drawer) {
                var slot = drawer.getStorage().getSlot(packet.slot());

                if (packet.lock()) {
                    slot.setLockMode(packet.value());
                } else {
                    slot.setVoidMode(packet.value());
                }

                drawer.setChangedAndSync();
            }
        });
    }
}
