package dev.emythiel.createitemdrawers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DrawerBlockEntity extends BaseBlockEntity {

    public DrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // TODO:
    // Inventories
    // Ticking
    // NBT storage
    // Render data (items, counts, flags)
}
