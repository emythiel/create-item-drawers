package dev.emythiel.createitemdrawers.network.handler;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.contraption.DrawerMountedStorage;
import dev.emythiel.createitemdrawers.network.ContraptionSlotTogglePacket;
import dev.emythiel.createitemdrawers.network.RenderPacket;
import dev.emythiel.createitemdrawers.network.SlotTogglePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
    @SuppressWarnings("resource")
    public void handleContraptionSlotToggle(final ContraptionSlotTogglePacket packet, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var level = ((ServerPlayer) ctx.player()).serverLevel();
            Entity entity = level.getEntity(packet.entityId());
            if (!(entity instanceof AbstractContraptionEntity contraptionEntity)) return;

            var storage = contraptionEntity.getContraption()
                .getStorage().getAllItemStorages().get(packet.localPos());
            if (!(storage instanceof DrawerMountedStorage drawer)) return;

            switch (packet.mode()) {
                case LOCK  -> { var s = drawer.getDrawerSlot(packet.slot()); if (s != null) s.setLockMode(packet.value()); }
                case VOID  -> { var s = drawer.getDrawerSlot(packet.slot()); if (s != null) s.setVoidMode(packet.value()); }
                case ITEMS  -> drawer.setRenderItems(packet.value());
                case COUNTS -> drawer.setRenderCounts(packet.value());
                case ICONS  -> drawer.setRenderIcons(packet.value());
            }

            drawer.markDirty();
        });
    }
}
