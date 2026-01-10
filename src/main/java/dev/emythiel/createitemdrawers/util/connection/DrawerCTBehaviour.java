package dev.emythiel.createitemdrawers.util.connection;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import dev.emythiel.createitemdrawers.block.base.BaseDrawerBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static dev.emythiel.createitemdrawers.block.base.BaseDrawerBlock.HORIZONTAL_FACING;

public class DrawerCTBehaviour extends ConnectedTextureBehaviour.Base {

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader,
                              BlockPos pos, BlockPos otherPos, Direction face) {
        if (!(state.getBlock() instanceof BaseDrawerBlock) || !(other.getBlock() instanceof BaseDrawerBlock))
            return false;
        if (state.getValue(HORIZONTAL_FACING) != other.getValue(HORIZONTAL_FACING))
            return false;

        return ConnectionHelper.areDrawersConnected(reader, pos, otherPos);
    }

    @Override
    protected boolean reverseUVs(BlockState state, Direction direction) {
        if (!direction.getAxis().isVertical())
            return false;
        Direction facing = state.getValue(HORIZONTAL_FACING);
        if (facing.getAxis() == direction.getAxis())
            return false;

        boolean isNegative = facing.getAxisDirection() == AxisDirection.NEGATIVE;
        if (direction == Direction.DOWN && facing.getAxis() == Axis.Z)
            return !isNegative;
        return isNegative;
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        Direction facing = state.getValue(HORIZONTAL_FACING);
        boolean isFront = facing.getAxis() == direction.getAxis();
        boolean isVertical = direction.getAxis().isVertical();
        boolean facingX = facing.getAxis() == Axis.X;
        return isFront ? DrawerSpriteShifts.DRAWER_BACK
            : isVertical && !facingX ? DrawerSpriteShifts.DRAWER_SIDE_HOR : DrawerSpriteShifts.DRAWER_SIDE_VER;
    }
}
