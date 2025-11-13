package dev.emythiel.createitemdrawers.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;

public class DrawerBlock extends Block {
    // Number of slots
    private final int slotCount;

    public DrawerBlock(Properties properties, int slotCount) {
        super(properties);
        if (slotCount < 1) throw new IllegalArgumentException("slotCount must be >= 1");
        this.slotCount = slotCount;

        // ensure default state has a facing
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    // Getter for slots the drawer has
    public int getSlotCount() {
        return slotCount;
    }

    // Check if front face
    public boolean isFrontFace(BlockState state, Direction clickedFace) {
        return state.getValue(HorizontalDirectionalBlock.FACING) == clickedFace;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
            .setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

}
