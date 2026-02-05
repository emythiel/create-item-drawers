package dev.emythiel.createitemdrawers.storage;

import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public enum DrawerUnpackingHandler implements UnpackingHandler {
    INSTANCE;

    @Override
    public boolean unpack(Level level, BlockPos pos, BlockState state, Direction side,
                          List<ItemStack> items, PackageOrderWithCrafts orderContext, boolean simulate) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof DrawerStorageBlockEntity drawerBE))
            return false;

        DrawerStorage storage = drawerBE.getStorage();

        for (ItemStack stack : items) {
            if (stack.isEmpty())
                continue;

            ItemStack remaining = stack;

            // Try all drawer slots
            for (int slot = 0; slot < storage.getSlotCount(); slot++) {
                remaining = storage.insert(slot, remaining, simulate);
                if (remaining.isEmpty())
                    break;
            }

            // If anything couldn't be inserted, unpack fails
            if (!remaining.isEmpty())
                return false;
        }

        if (!simulate)
            drawerBE.setChangedAndSync();

        return true;
    }
}
