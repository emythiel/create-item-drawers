package dev.emythiel.createitemdrawers.block;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import dev.emythiel.createitemdrawers.block.base.BaseDrawerBlock;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.registry.ModBlockEntities;
import dev.emythiel.createitemdrawers.storage.DrawerSlot;
import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import dev.emythiel.createitemdrawers.util.DrawerInteractionHelper;
import dev.emythiel.createitemdrawers.util.connection.ConnectedGroupHandler;
import dev.emythiel.createitemdrawers.util.connection.ConnectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Collections;
import java.util.List;

public class DrawerStorageBlock extends BaseDrawerBlock implements IWrenchable, IBE<DrawerStorageBlockEntity> {

    private final int slotCount;

    public DrawerStorageBlock(Properties properties, int slotCount) {
        super(properties);
        this.slotCount = slotCount;
    }

    @Override
    public Class<DrawerStorageBlockEntity> getBlockEntityClass() {
        return DrawerStorageBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DrawerStorageBlockEntity> getBlockEntityType() {
        return ModBlockEntities.DRAWER_STORAGE_BLOCK_ENTITY.get();
    }

    // Getter for slots the drawer has
    public int getSlotCount() { return slotCount; }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof DrawerStorageBlockEntity be))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!isFrontFace(state, hit.getDirection()))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        boolean sneaking = player.isShiftKeyDown();

        // Open GUI if front face is right-clicked with Create Wrench
        if (held.is(AllItems.WRENCH.get()) && !sneaking) {
            if (!level.isClientSide) {
                player.openMenu(be, buf -> buf.writeBlockPos(pos));
            }
            return ItemInteractionResult.SUCCESS;
        } else if (held.is(AllItems.WRENCH.get()) && sneaking) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        int slot = DrawerInteractionHelper.getHitSlot(be, hit.getLocation());
        if (slot < 0)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        boolean anyInserted = false;
        DrawerSlot drawerSlot = be.getStorage().getSlot(slot);


        // Attempt to insert held item first
        if (!held.isEmpty()) {
            int before = held.getCount();
            ItemStack leftover = be.getStorage().insert(slot, held, false);
            anyInserted = leftover.getCount() < before;

            player.setItemInHand(hand, leftover);

            if (anyInserted) {
                be.setChangedAndSync();

                if (!sneaking) {
                    CreateItemDrawerLang.translate("interaction.insert_held_stack").sendStatus(player);
                    AllSoundEvents.ITEM_HATCH.playOnServer(level, pos);
                    return ItemInteractionResult.SUCCESS;
                }
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

                int before = inv.getCount();
                ItemStack leftover = be.getStorage().insert(slot, inv, false);
                int after = leftover.getCount();

                if (after < before) {
                    anyInserted = true;
                    player.getInventory().setItem(i, leftover);
                }
            }
            if (anyInserted) {
                be.setChangedAndSync();
                CreateItemDrawerLang.translate("interaction.insert_matching_stacks").sendStatus(player);
                AllSoundEvents.ITEM_HATCH.playOnServer(level, pos);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (be instanceof DrawerStorageBlockEntity drawer) {
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
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        // If sides are wrenched, just return (no rotation can happen)
        Direction hitFace = context.getClickedFace();
        if (hitFace != Direction.UP && hitFace != Direction.DOWN)
            return InteractionResult.PASS;

        ConnectedGroupHandler.connectionGroupCleanup(state, context.getLevel(), context.getClickedPos());
        return IWrenchable.super.onWrenched(state, context);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
            DrawerStorageBlockEntity drawer = (DrawerStorageBlockEntity) ConnectionHelper.getDrawer(level, pos);

            if (drawer != null && !level.isClientSide() && !isMoving) {
                ConnectedGroupHandler.connectionGroupCleanup(state, level, pos);

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

            IBE.onRemove(state, level, pos, newState);
            return;
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }
}
