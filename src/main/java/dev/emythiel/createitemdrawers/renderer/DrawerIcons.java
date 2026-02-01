package dev.emythiel.createitemdrawers.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.emythiel.createitemdrawers.CreateItemDrawers;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DrawerIcons {

    public static final Icon
        UPGRADE_TIER_1 = icon(0, 0),
        UPGRADE_TIER_2 = icon(16, 0),
        UPGRADE_TIER_3 = icon(32, 0),
        UPGRADE_TIER_4 = icon(48, 0),
        UPGRADE_TIER_5 = icon(64, 0),

        LOCK = icon(0, 16),
        VOID = icon(16, 16),

        ITEMS = icon(32, 16),
        COUNTS = icon(48, 16),
        MODES = icon(64, 16);

    private static Icon icon(int x, int y) {
        return new Icon(x, y);
    }

    public static final ResourceLocation ATLAS = CreateItemDrawers.asResource("textures/sprite/icons.png");

    public static final int ATLAS_SIZE = 256;
    public static final int ICON_SIZE = 16;

    public static class Icon implements ScreenElement {
        private final int pixelX;
        private final int pixelY;
        private final float u1, u2, v1, v2;

        private Icon(int pixelX, int pixelY) {
            this.pixelX = pixelX;
            this.pixelY = pixelY;

            this.u1 = pixelX / (float) ATLAS_SIZE;
            this.u2 = (pixelX + ICON_SIZE) / (float) ATLAS_SIZE;
            this.v1 = pixelY / (float) ATLAS_SIZE;
            this.v2 = (pixelY + ICON_SIZE) / (float) ATLAS_SIZE;
        }

        @OnlyIn(Dist.CLIENT)
        public void bind() {
            RenderSystem.setShaderTexture(0, ATLAS);
        }

        @Override @OnlyIn(Dist.CLIENT)
        public void render(GuiGraphics graphics, int x, int y) {
            graphics.blit(ATLAS, x, y, 0, pixelX, pixelY, ICON_SIZE, ICON_SIZE, ATLAS_SIZE, ATLAS_SIZE);
        }

        @OnlyIn(Dist.CLIENT)
        public void renderWorld(PoseStack ms, MultiBufferSource buffer, int light, int overlay, float size) {
            VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(ATLAS));
            Matrix4f matrix = ms.last().pose();
            Vector3f normal = new Vector3f(0, 0, 1);

            float h = size / 2f;

            addQuad(matrix, builder, light, overlay, -h, h, -h, h, 0, u1, u2, v1, v2, normal);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void addQuad(Matrix4f matrix, VertexConsumer builder, int light, int overlay,
                                float x1, float x2, float y1, float y2, float z,
                                float u1, float u2, float v1, float v2, Vector3f normal) {
        vertex(matrix, builder, light, overlay, x2, y1, z, u2, v2, normal);
        vertex(matrix, builder, light, overlay, x2, y2, z, u2, v1, normal);
        vertex(matrix, builder, light, overlay, x1, y2, z, u1, v1, normal);
        vertex(matrix, builder, light, overlay, x1, y1, z, u1, v2, normal);
    }

    @OnlyIn(Dist.CLIENT)
    private static void vertex(Matrix4f matrix, VertexConsumer builder, int light, int overlay,
                               float x, float y, float z, float u, float v, Vector3f normal) {
        builder.addVertex(matrix, x, y, z)
            .setColor(1f, 1f, 1f, 1f)
            .setUv(u, v)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal.x(), normal.y(), normal.z());
    }
}
