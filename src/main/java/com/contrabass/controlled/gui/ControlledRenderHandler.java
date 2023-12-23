package com.contrabass.controlled.gui;

import com.contrabass.controlled.ControlledInputHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class ControlledRenderHandler implements IRenderer {

    @Override
    public void onRenderWorldLast(MatrixStack matrixStack, Matrix4f projMatrix) {
        if (ControlledInputHandler.pathfindingTarget != null) {
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            RenderUtils.setupBlend();
            Color4f colour = new Color4f(0, 0, 255, 128);
            MinecraftClient minecraft = MinecraftClient.getInstance();
            assert minecraft.cameraEntity != null;

            renderTopTarget(colour, minecraft, ControlledInputHandler.pathfindingTarget);

            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
        }
    }

    private static void renderTopTarget(Color4f colour, MinecraftClient minecraft, BlockPos target) {
        assert minecraft.cameraEntity != null;

        Direction playerFacing = minecraft.cameraEntity.getHorizontalFacing();

        Vec3d cameraPos = minecraft.gameRenderer.getCamera().getPos();

        double x = target.getX() + 0.5d - cameraPos.x;
        double y = target.getY() + 0.5d - cameraPos.y;
        double z = target.getZ() + 0.5d - cameraPos.z;

        MatrixStack globalStack = RenderSystem.getModelViewStack();
        globalStack.push();
        globalStack.translate(x, y, z);

        globalStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f - playerFacing.asRotation()));
        globalStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));

        globalStack.translate(-x, -y, -z + 0.510);
        RenderSystem.applyModelViewMatrix();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
//        RenderSystem.disableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        int r = (int) colour.r;
        int g = (int) colour.g;
        int b = (int) colour.b;
        int a = (int) colour.a;

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        buffer.vertex(x - 0.5, y - 0.5, z).color(r, g, b, a).next();
        buffer.vertex(x + 0.5, y - 0.5, z).color(r, g, b, a).next();
        buffer.vertex(x + 0.5, y + 0.5, z).color(r, g, b, a).next();
        buffer.vertex(x - 0.5, y + 0.5, z).color(r, g, b, a).next();

        tessellator.draw();

        globalStack.pop();
        RenderSystem.applyModelViewMatrix();
    }
}
