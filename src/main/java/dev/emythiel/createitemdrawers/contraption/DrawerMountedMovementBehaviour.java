package dev.emythiel.createitemdrawers.contraption;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.emythiel.createitemdrawers.client.renderer.DrawerRenderer;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler;
import net.minecraft.client.renderer.MultiBufferSource;

import javax.annotation.Nullable;

public class DrawerMountedMovementBehaviour implements MovementBehaviour {

    @Override
    public void tick(MovementContext context) {
        if (context.world.isClientSide()) return;

        @Nullable DrawerMountedStorage storage = getMountedStorage(context);
        if (storage != null) {
            if (!storage.initialized)
                storage.initBlockEntityData(context);
            if (storage.isDirty())
                storage.updateClientStorageData(context, context.contraption.entity.level().registryAccess());
        }
    }

    @Nullable
    private DrawerMountedStorage getMountedStorage(MovementContext context) {
        AbstractContraptionEntity entity = context.contraption.entity;
        MountedItemStorage storage = entity.getContraption().getStorage().getAllItemStorages().get(context.localPos);

        if (storage instanceof DrawerMountedStorage drawer) {
            return drawer;
        }

        return null;
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                    ContraptionMatrices matrices, MultiBufferSource buffer) {
        DrawerRenderer.renderFromContraptionContext(context, renderWorld, matrices, buffer);
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }
}
