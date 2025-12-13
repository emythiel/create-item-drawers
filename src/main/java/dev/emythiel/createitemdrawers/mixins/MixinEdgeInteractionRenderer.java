package dev.emythiel.createitemdrawers.mixins;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionHandler;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionRenderer;
import dev.emythiel.createitemdrawers.block.DrawerBlock;
import dev.emythiel.createitemdrawers.util.CreateItemDrawerLang;
import dev.emythiel.createitemdrawers.util.connection.DrawerHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(EdgeInteractionRenderer.class)
public class MixinEdgeInteractionRenderer {

    @Inject(
        method = "tick",
        at = @At(
            value = "NEW",
            target = "java/util/ArrayList"
        ),
        cancellable = true
    )

    private static void create_item_drawers$overrideTooltip(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.hitResult instanceof BlockHitResult result))
            return;

        BlockPos pos = result.getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        if (!(state.getBlock() instanceof DrawerBlock)) return;

        EdgeInteractionBehaviour behaviour = BlockEntityBehaviour.get(mc.level, pos, EdgeInteractionBehaviour.TYPE);
        if (behaviour == null) return;

        List<Direction> connectiveSides = EdgeInteractionHandler.getConnectiveSides(mc.level, pos, result.getDirection(), behaviour);
        if (connectiveSides.isEmpty())
            return;

        Direction closestEdge = create_item_drawers$findClosestEdge(pos, result.getLocation(), connectiveSides);
        boolean connected = DrawerHelper.areDrawersConnected(mc.level, pos, pos.relative(closestEdge));

        List<MutableComponent> tip = new ArrayList<>();
        tip.add(CreateItemDrawerLang.translate("interaction.drawer_connected").component());
        tip.add(CreateItemDrawerLang.translate(connected
                ? "interaction.click_to_separate"
                : "interaction.click_to_merge"
        ).component());

        CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);

        ci.cancel();
    }

    @Unique
    private static Direction create_item_drawers$findClosestEdge(BlockPos pos, Vec3 hitLocation, List<Direction> sides) {
        Vec3 center = VecHelper.getCenterOf(pos);
        Direction closest = sides.get(0);
        double bestDistance = Double.MAX_VALUE;

        for (Direction side : sides) {
            double distance = Vec3.atLowerCornerOf(side.getNormal())
                .subtract(hitLocation.subtract(center))
                .length();
            if (distance < bestDistance) {
                bestDistance = distance;
                closest = side;
            }
        }
        return closest;
    }
}
