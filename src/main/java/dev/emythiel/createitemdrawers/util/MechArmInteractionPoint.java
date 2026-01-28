package dev.emythiel.createitemdrawers.util;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import dev.emythiel.createitemdrawers.block.DrawerStorageBlock;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

public class MechArmInteractionPoint {
    static {
        register("drawer", new DrawerType());
    }

    private static <T extends ArmInteractionPointType> void register(String name, T type) {
        Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, CreateItemDrawers.asResource(name), type);
    }

    @Internal
    public static void init() {}

    public static class DrawerType extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return state.getBlock() instanceof DrawerStorageBlock;
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new DrawerPoint(this, level, pos, state);
        }
    }

    public static class DrawerPoint extends ArmInteractionPoint {
        public DrawerPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        protected Vec3 getInteractionPositionVector() {
            Direction facing = cachedState.getValue(DrawerStorageBlock.HORIZONTAL_FACING);

            return Vec3.atCenterOf(pos)
                .add(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.5));
        }

        // Get local handler so arms don't interact with the connected network
        @Override @Nullable
        protected IItemHandler getHandler(ArmBlockEntity armBlockEntity) {
            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof DrawerStorageBlockEntity drawerBE) {
                return drawerBE.getLocalHandler();
            }

            return null;
        }
    }
}
