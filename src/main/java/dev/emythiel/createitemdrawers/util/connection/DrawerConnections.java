package dev.emythiel.createitemdrawers.util.connection;

import dev.emythiel.createitemdrawers.block.DrawerBlock;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static dev.emythiel.createitemdrawers.block.base.BaseBlock.HORIZONTAL_FACING;
import static dev.emythiel.createitemdrawers.util.connection.DrawerConnectionHelper.getDrawer;

public class DrawerConnections {

    public static boolean shouldConnect(Level world, BlockPos pos, Direction face, Direction direction) {
        BlockState refState = world.getBlockState(pos);
        if (!refState.hasProperty(HORIZONTAL_FACING))
            return false;
        Direction refDirection = refState.getValue(HORIZONTAL_FACING);
        if (direction.getAxis() == refDirection.getAxis())
            return false;
        if (face == refDirection)
            return false;
        BlockState neighbour = world.getBlockState(pos.relative(direction));
        if (!(neighbour.getBlock() instanceof DrawerBlock))
            return false;
        if (refDirection != neighbour.getValue(HORIZONTAL_FACING))
            return false;
        return true;
    }

    public static void toggleConnection(Level world, BlockPos pos, BlockPos pos2) {
        DrawerBlockEntity drawer1 = getDrawer(world, pos);
        DrawerBlockEntity drawer2 = getDrawer(world, pos2);

        if (drawer1 == null || drawer2 == null)
            return;

        // Get each drawer's controller world position
        BlockPos controllerPos1 = drawer1.getBlockPos()
            .offset(drawer1.group.offsets.get(0));
        BlockPos controllerPos2 = drawer2.getBlockPos()
            .offset(drawer2.group.offsets.get(0));

        if (controllerPos1.equals(controllerPos2)) {
            DrawerBlockEntity controller = getDrawer(world, controllerPos1);

            Set<BlockPos> positions = controller.group.offsets.stream()
                .map(controllerPos1::offset)
                .collect(Collectors.toSet());
            List<BlockPos> frontier = new LinkedList<>();
            List<BlockPos> splitGroup = new ArrayList<>();

            frontier.add(pos2);
            positions.remove(pos2);
            positions.remove(pos);
            while (!frontier.isEmpty()) {
                BlockPos current = frontier.remove(0);
                for (Direction direction : Iterate.directions) {
                    BlockPos next = current.relative(direction);
                    if (!positions.remove(next))
                        continue;
                    splitGroup.add(next);
                    frontier.add(next);
                }
            }

            initAndAddAll(world, drawer1, positions);
            initAndAddAll(world, drawer2, splitGroup);

            drawer1.setChangedAndSync();
            drawer1.connectivityChanged();
            drawer2.setChangedAndSync();
            drawer2.connectivityChanged();
            return;
        }

        if (!drawer1.group.isController)
            drawer1 = getDrawer(world, controllerPos1);
        if (!drawer2.group.isController)
            drawer2 = getDrawer(world, controllerPos2);
        if (drawer1 == null || drawer2 == null)
            return;

        connectControllers(world, drawer1, drawer2);

        world.setBlock(drawer1.getBlockPos(), drawer1.getBlockState(), HorizontalDirectionalBlock.UPDATE_ALL);

        drawer1.setChangedAndSync();
        drawer1.connectivityChanged();
        drawer2.setChangedAndSync();
        drawer2.connectivityChanged();
    }

    public static void initAndAddAll(Level world, DrawerBlockEntity drawer, Collection<BlockPos> positions) {
        drawer.group = new ConnectedGroup();
        positions.forEach(splitPos -> {
            modifyAndUpdate(world, splitPos, group -> {
                group.attachTo(drawer.getBlockPos(), splitPos);
                drawer.group.offsets.add(splitPos.subtract(drawer.getBlockPos()));
            });
        });
    }

    public static void connectControllers(Level world, DrawerBlockEntity drawer1, DrawerBlockEntity drawer2) {

        drawer1.group.offsets.forEach(offset -> {
            BlockPos connectedPos = drawer1.getBlockPos()
                .offset(offset);
            modifyAndUpdate(world, connectedPos, group -> {
            });
        });

        drawer2.group.offsets.forEach(offset -> {
            if (offset.equals(BlockPos.ZERO))
                return;
            BlockPos connectedPos = drawer2.getBlockPos()
                .offset(offset);
            modifyAndUpdate(world, connectedPos, group -> {
                group.attachTo(drawer1.getBlockPos(), connectedPos);
                drawer1.group.offsets.add(BlockPos.ZERO.subtract(group.offsets.get(0)));
            });
        });

        drawer2.group.attachTo(drawer1.getBlockPos(), drawer2.getBlockPos());
        drawer1.group.offsets.add(BlockPos.ZERO.subtract(drawer2.group.offsets.get(0)));
    }

    private static void modifyAndUpdate(Level world, BlockPos pos, Consumer<ConnectedGroup> callback) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof DrawerBlockEntity drawer))
            return;

        callback.accept(drawer.group);
        drawer.setChangedAndSync();
        drawer.connectivityChanged();
    }

    public static IItemHandler buildCombinedHandler(DrawerBlockEntity anyMember) {
        if (anyMember == null || anyMember.getLevel() == null)
            return null;

        DrawerBlockEntity controller = getController(anyMember);
        if (controller == null)
            return null;

        ConnectedGroup group = controller.getGroup();

        // single drawer, no network
        if (group.isController && group.offsets.size() == 1)
            return controller.getLocalHandler();

        List<IItemHandlerModifiable> handlers = new ArrayList<>();

        for (BlockPos offset : group.offsets) {
            BlockPos abs = controller.getBlockPos().offset(offset);
            DrawerBlockEntity be = getDrawer(controller.getLevel(), abs);
            if (be != null) {
                handlers.add(be.getLocalHandler());
            }
        }

        return new CombinedInvWrapper(handlers.toArray(IItemHandlerModifiable[]::new));
    }


    public static DrawerBlockEntity getController(DrawerBlockEntity be) {
        if (be == null || be.getLevel() == null)
            return null;

        ConnectedGroup group = be.getGroup();
        if (group.isController)
            return be;

        BlockPos controllerPos = group.getController(be.getBlockPos());
        return getDrawer(be.getLevel(), controllerPos);
    }
}
