package dev.emythiel.createitemdrawers.block;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.block.IBE;
import dev.emythiel.createitemdrawers.block.base.BaseBlock;
import dev.emythiel.createitemdrawers.block.entity.DrawerBlockEntity;
import dev.emythiel.createitemdrawers.registry.ModBlockEntities;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import dev.emythiel.createitemdrawers.util.DrawerInteractionHelper;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler.ConnectedGroup;
import dev.emythiel.createitemdrawers.util.connection.DrawerHelper;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Collections;
import java.util.List;

public class DrawerBlock extends BaseBlock implements IBE<DrawerBlockEntity> {

    private final int slotCount;

    public DrawerBlock(Properties properties, int slotCount) {
        super(properties);
        this.slotCount = slotCount;
    }

    @Override
    public Class<DrawerBlockEntity> getBlockEntityClass() {
        return DrawerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DrawerBlockEntity> getBlockEntityType() {
        return ModBlockEntities.DRAWER_BLOCK_ENTITY.get();
    }

    // Getter for slots the drawer has
    public int getSlotCount() { return slotCount; }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof DrawerBlockEntity be))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!isFrontFace(state, hit.getDirection()))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        // Open GUI if front face is right-clicked with Create Wrench
        if (held.is(AllItems.WRENCH.get())) {
            if (!level.isClientSide) {
                player.openMenu(be, buf -> buf.writeBlockPos(pos));
            }
            return ItemInteractionResult.SUCCESS;
        }

        int slot = DrawerInteractionHelper.getHitSlot(be, hit.getLocation());
        if (slot < 0)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        boolean sneaking = player.isShiftKeyDown();
        DrawerSlot drawerSlot = be.getStorage().getSlot(slot);


        // Attempt to insert held item first
        if (!held.isEmpty()) {
            int before = held.getCount();
            ItemStack leftover = be.getStorage().insert(slot, held, false);
            boolean inserted = leftover.getCount() < before;

            player.setItemInHand(hand, leftover);

            if (inserted) {
                be.setChangedAndSync();

                if (!sneaking)
                    return ItemInteractionResult.SUCCESS;
            }
        }

        ItemStack stored = drawerSlot.getStoredItem();

        if (sneaking) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack inv = player.getInventory().getItem(i);
                if (inv.isEmpty())
                    continue;

                if (!ItemStack.isSameItemSameComponents(inv, stored))
                    continue;

                ItemStack leftover = be.getStorage().insert(slot, inv, false);
                player.getInventory().setItem(i, leftover);
            }
            be.setChangedAndSync();
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (be instanceof DrawerBlockEntity drawer) {
            boolean hasStoredItems = false;
            for (int i = 0; i < drawer.getStorage().getSlotCount(); i++) {
                if (drawer.getStorage().getSlot(i).getCount() > 0) {
                    hasStoredItems = true;
                    break;
                }
            }

            if (hasStoredItems) {
                return Collections.emptyList();
            }
        }

        return super.getDrops(state, builder);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
            DrawerBlockEntity drawer = DrawerHelper.getDrawer(level, pos);

            if (drawer != null && !level.isClientSide() && !isMoving) {
                boolean hasStoredItems = false;
                for (int i = 0; i < drawer.getStorage().getSlotCount(); i++) {
                    if (drawer.getStorage().getSlot(i).getCount() > 0) {
                        hasStoredItems = true;
                        break;
                    }
                }
                if (hasStoredItems) {
                    ItemStack drawerStack = new ItemStack(this);
                    drawer.saveToItem(drawerStack, level.registryAccess());
                    Block.popResource(level, pos, drawerStack);
                } else if (!drawer.getUpgrade().isEmpty()) {
                    ItemStack upgrade = drawer.getUpgrade().copy();
                    Block.popResource(level, pos, upgrade);
                }
            }

            connectionGroupCleanup(state, level, pos);
            IBE.onRemove(state, level, pos, newState);
            return;
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void connectionGroupCleanup(BlockState state, Level level, BlockPos pos) {
        for (Direction direction : Iterate.directions) {
            if (direction.getAxis() == state.getValue(HORIZONTAL_FACING)
                .getAxis())
                continue;

            BlockPos otherPos = pos.relative(direction);
            ConnectedGroup thisGroup = DrawerHelper.getInput(level, pos);
            ConnectedGroup otherGroup = DrawerHelper.getInput(level, otherPos);

            if (thisGroup == null || otherGroup == null)
                continue;
            if (!pos.offset(thisGroup.offsets.get(0))
                .equals(otherPos.offset(otherGroup.offsets.get(0))))
                continue;

            ConnectedGroupHandler.toggleConnection(level, pos, otherPos);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DrawerBlockEntity drawer) {
            connectionGroupCleanup(state, level, pos);
            //drawer.connectivityChanged();
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }
}
