package dev.emythiel.createitemdrawers.client.renderer;

import com.simibubi.create.AllItems;
import com.simibubi.create.CreateClient;
import dev.emythiel.createitemdrawers.block.entity.DrawerStorageBlockEntity;
import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

import static dev.emythiel.createitemdrawers.block.base.BaseDrawerBlock.HORIZONTAL_FACING;

public class DrawerTooltip {

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;
        if (!player.getMainHandItem().is(AllItems.WRENCH.get())) return;
        if (player.isShiftKeyDown()) return;

        ClientLevel level = mc.level;
        if (level == null) return;

        HitResult target = mc.hitResult;
        if (!(target instanceof BlockHitResult result)) return;

        BlockPos pos = result.getBlockPos();
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof DrawerStorageBlockEntity drawer)) return;

        Direction front = drawer.getBlockState().getValue(HORIZONTAL_FACING);
        if (result.getDirection() != front) return;

        List<MutableComponent> tip = new ArrayList<>();
        tip.add(CreateItemDrawerLang.translate("interaction.settings").component());
        tip.add(CreateItemDrawerLang.translate("interaction.open_settings").component());
        CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
    }
}
