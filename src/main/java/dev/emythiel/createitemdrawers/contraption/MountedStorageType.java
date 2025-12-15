package dev.emythiel.createitemdrawers.contraption;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MountedStorageType extends MountedItemStorageType<MountedStorage> {
    public MountedStorageType() {
        super(MountedStorage.CODEC);
    }

    @Override
    public MountedStorage mount(Level level, BlockState state, BlockPos pos, BlockEntity be) {
        return be instanceof DrawerBlockEntity drawerBlock ? MountedStorage.fromStorage(drawerBlock) : null;
    }
}
