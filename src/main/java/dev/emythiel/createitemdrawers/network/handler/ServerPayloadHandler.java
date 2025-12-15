package dev.emythiel.createitemdrawers.network.handler;

import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.network.RenderPacket;
import dev.emythiel.createitemdrawers.network.SlotTogglePacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

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
                drawer.setRenderItems(packet.renderMode());
                drawer.setRenderCounts(packet.renderMode());
                drawer.setRenderSettings(packet.renderMode());
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

                switch (packet.mode()) {
                    case "lock" -> slot.setLockMode(packet.value());
                    case "void" -> slot.setVoidMode(packet.value());
                    case "items" -> drawer.setRenderItems(packet.value());
                    case "counts" -> drawer.setRenderCounts(packet.value());
                    case "settings" -> drawer.setRenderSettings(packet.value());
                }

                drawer.setChangedAndSync();
            }
        });
    }
}
