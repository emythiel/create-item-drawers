package dev.emythiel.createitemdrawers.contraption;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.client.renderer.DrawerRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.minecraft.client.renderer.MultiBufferSource;

import javax.annotation.Nullable;

public class MountedMovementBehaviour implements MovementBehaviour {

    /*@Override
    public void tick(MovementContext context) {
        if (context.world.isClientSide()) return;
        if (!context.stall) return;

        CreateItemDrawers.LOGGER.debug("[BEHAVIOUR] tick called at {}", context.localPos);
        @Nullable MountedStorage storage = getMountedStorage(context);
        if (storage != null) {
            CreateItemDrawers.LOGGER.debug("[BEHAVIOUR] Found Storage, initalized: {}, dirty: {}", storage.initialized, storage.isDirty());
            if (!storage.initialized) {
                storage.initialized = true;
                storage.updateClientStorageData(context);
            }
            if (storage.isDirty()) {
                storage.updateClientStorageData(context);
            }
        } else {
            CreateItemDrawers.LOGGER.debug("[BEHAVIOUR] No storage found at {}", context.localPos);
        }
    }*/

    /*@Nullable
    private MountedStorage getMountedStorage(MovementContext context) {
        if (context.world.isClientSide()) return null;

        AbstractContraptionEntity entity = context.contraption.entity;
        if (entity == null) {
            CreateItemDrawers.LOGGER.debug("[BEHAVIOUR] No contraption entity");
            return null;
        }
        MountedItemStorage storage = entity.getContraption().getStorage().getAllItemStorages().get(context.localPos);
        CreateItemDrawers.LOGGER.debug("[BEHAVIOUR] Raw storage at {}: {}", context.localPos, storage);

        if (storage instanceof MountedStorage drawer) {
            return drawer;
        }

        return null;
    }*/

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                    ContraptionMatrices matrices, MultiBufferSource buffer) {
        //if (!VisualizationManager.supportsVisualization(context.world))
        DrawerRenderer.renderFromContraptionContext(context, renderWorld, matrices, buffer);
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }
}
