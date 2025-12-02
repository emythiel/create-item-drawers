package dev.emythiel.createitemdrawers.util.connection;

import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.*;

public class ConnectedGroup {
    public boolean isController;
    public List<BlockPos> offsets = Collections.synchronizedList(new ArrayList<>());

    public ConnectedGroup() {
        isController = true;
        offsets.add(BlockPos.ZERO);
    }

    public void attachTo(BlockPos controllerPos, BlockPos myPos) {
        isController = false;
        offsets.clear();
        offsets.add(controllerPos.subtract(myPos));
    }

    public BlockPos getController(BlockPos myPos) {
        return myPos.offset(offsets.get(0));
    }

    public void write(CompoundTag tag) {
        tag.putBoolean("Controller", isController);
        ListTag list = new ListTag();
        offsets.forEach(pos -> {
            CompoundTag data = new CompoundTag();
            data.putInt("X", pos.getX());
            data.putInt("Y", pos.getY());
            data.putInt("Z", pos.getZ());
            list.add(data);
        });
        tag.put("Offsets", list);
    }

    public void read(CompoundTag tag) {
        isController = tag.getBoolean("Controller");
        offsets = NBTHelper.readCompoundList(tag.getList("Offsets", Tag.TAG_COMPOUND),
            c -> new BlockPos(c.getInt("X"), c.getInt("Y"), c.getInt("Z")));

        if (offsets.isEmpty()) {
            isController = true;
            offsets.add(BlockPos.ZERO);
        }
    }
}
