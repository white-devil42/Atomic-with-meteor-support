/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.HomeScreen;
import me.zeroX150.atomic.helper.math.Matrix4x4;
import me.zeroX150.atomic.helper.math.Vector3D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class Renderer {

    public static class R3D {

        static final   MatrixStack empty           = new MatrixStack();
        private static MatrixStack lastRenderStack = new MatrixStack();

        public static void renderOutlineIntern(Vec3d start, Vec3d dimensions, MatrixStack stack, BufferBuilder buffer) {
            Camera c = Atomic.client.gameRenderer.getCamera();
            Vec3d camPos = c.getPos();
            start = start.subtract(camPos);
            Vec3d end = start.add(dimensions);
            Matrix4f matrix = stack.peek().getPositionMatrix();
            float x1 = (float) start.x;
            float y1 = (float) start.y;
            float z1 = (float) start.z;
            float x2 = (float) end.x;
            float y2 = (float) end.y;
            float z2 = (float) end.z;

            buffer.vertex(matrix, x1, y1, z1).next();
            buffer.vertex(matrix, x1, y1, z2).next();
            buffer.vertex(matrix, x1, y1, z2).next();
            buffer.vertex(matrix, x2, y1, z2).next();
            buffer.vertex(matrix, x2, y1, z2).next();
            buffer.vertex(matrix, x2, y1, z1).next();
            buffer.vertex(matrix, x2, y1, z1).next();
            buffer.vertex(matrix, x1, y1, z1).next();

            buffer.vertex(matrix, x1, y2, z1).next();
            buffer.vertex(matrix, x1, y2, z2).next();
            buffer.vertex(matrix, x1, y2, z2).next();
            buffer.vertex(matrix, x2, y2, z2).next();
            buffer.vertex(matrix, x2, y2, z2).next();
            buffer.vertex(matrix, x2, y2, z1).next();
            buffer.vertex(matrix, x2, y2, z1).next();
            buffer.vertex(matrix, x1, y2, z1).next();

            buffer.vertex(matrix, x1, y1, z1).next();
            buffer.vertex(matrix, x1, y2, z1).next();

            buffer.vertex(matrix, x2, y1, z1).next();
            buffer.vertex(matrix, x2, y2, z1).next();

            buffer.vertex(matrix, x2, y1, z2).next();
            buffer.vertex(matrix, x2, y2, z2).next();

            buffer.vertex(matrix, x1, y1, z2).next();
            buffer.vertex(matrix, x1, y2, z2).next();
        }

        //you can call renderOutlineIntern multiple times to save performance
        public static void renderOutline(Vec3d start, Vec3d dimensions, Color color, MatrixStack stack) {
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            BufferBuilder buffer = renderPrepare(color);

            renderOutlineIntern(start, dimensions, stack, buffer);

            buffer.end();
            BufferRenderer.draw(buffer);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            RenderSystem.disableBlend();
        }

        public static BufferBuilder renderPrepare(Color color) {
            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            float alpha = color.getAlpha() / 255f;
            RenderSystem.setShader(GameRenderer::getPositionShader);
            GL11.glDepthFunc(GL11.GL_ALWAYS);
            RenderSystem.setShaderColor(red, green, blue, alpha);
            //RenderSystem.lineWidth(2f);
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
            return buffer;
        }

        public static void renderFilled(Vec3d start, Vec3d dimensions, Color color, MatrixStack stack) {
            renderFilled(start, dimensions, color, stack, GL11.GL_ALWAYS);
        }

        public static MatrixStack getLastRenderStack() {
            return lastRenderStack;
        }

        public static void setLastRenderStack(MatrixStack lastRenderStack) {
            R3D.lastRenderStack = lastRenderStack;
        }

        public static MatrixStack getEmptyMatrixStack() {
            empty.loadIdentity(); // essentially clear the stack
            return empty;
        }

        public static void renderFilled(Vec3d start, Vec3d dimensions, Color color, MatrixStack stack, int GLMODE) {
            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            float alpha = color.getAlpha() / 255f;
            Camera c = Atomic.client.gameRenderer.getCamera();
            Vec3d camPos = c.getPos();
            start = start.subtract(camPos);
            Vec3d end = start.add(dimensions);
            Matrix4f matrix = stack.peek().getPositionMatrix();
            float x1 = (float) start.x;
            float y1 = (float) start.y;
            float z1 = (float) start.z;
            float x2 = (float) end.x;
            float y2 = (float) end.y;
            float z2 = (float) end.z;
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            GL11.glDepthFunc(GLMODE);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();

            buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();

            buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();

            buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();

            buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();

            buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();

            buffer.end();

            BufferRenderer.draw(buffer);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
        }

        public static void line(Vec3d start, Vec3d end, Color color, MatrixStack matrices) {
            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            float alpha = color.getAlpha() / 255f;
            Camera c = Atomic.client.gameRenderer.getCamera();
            Vec3d camPos = c.getPos();
            start = start.subtract(camPos);
            end = end.subtract(camPos);
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            float x1 = (float) start.x;
            float y1 = (float) start.y;
            float z1 = (float) start.z;
            float x2 = (float) end.x;
            float y2 = (float) end.y;
            float z2 = (float) end.z;
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            GL11.glDepthFunc(GL11.GL_ALWAYS);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();

            buffer.end();

            BufferRenderer.draw(buffer);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            RenderSystem.disableBlend();
        }

        public static Vec3d getCrosshairVector() {

            Camera camera = Atomic.client.gameRenderer.getCamera();

            ClientPlayerEntity player = Atomic.client.player;

            float f = 0.017453292F;
            float pi = (float) Math.PI;

            assert player != null;
            float f1 = MathHelper.cos(-player.getYaw() * f - pi);
            float f2 = MathHelper.sin(-player.getYaw() * f - pi);
            float f3 = -MathHelper.cos(-player.getPitch() * f);
            float f4 = MathHelper.sin(-player.getPitch() * f);

            return new Vec3d(f2 * f3, f4, f1 * f3).add(camera.getPos());
        }

    }

    public static class R2D {

        public static final Identifier OPTIONS_BACKGROUND_TEXTURE = new Identifier("atomic", "background.jpg");

        public static void scissor(double x, double y, double width, double height) {
            float d = (float) Atomic.client.getWindow().getScaleFactor();
            int ay = (int) ((Atomic.client.getWindow().getScaledHeight() - (y + height)) * d);
            RenderSystem.enableScissor((int) (x * d), ay, (int) (width * d), (int) (height * d));
        }

        public static void unscissor() {
            RenderSystem.disableScissor();
        }

        public static void drawEntity(double x, double y, double size, float mouseX, float mouseY, LivingEntity entity, MatrixStack stack) {
            float f = (float) Math.atan(mouseX / 40.0F);
            float g = (float) Math.atan(mouseY / 40.0F);
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate(x, y, 1050.0D);
            matrixStack.scale(1.0F, 1.0F, -1.0F);
            RenderSystem.applyModelViewMatrix();
            stack.push();
            stack.translate(0.0D, 0.0D, 1000.0D);
            stack.scale((float) size, (float) size, (float) size);
            Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
            Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
            quaternion.hamiltonProduct(quaternion2);
            stack.multiply(quaternion);
            float h = entity.bodyYaw;
            float i = entity.getYaw();
            float j = entity.getPitch();
            float k = entity.prevHeadYaw;
            float l = entity.headYaw;
            entity.bodyYaw = 180.0F + f * 20.0F;
            entity.setYaw(180.0F + f * 40.0F);
            entity.setPitch(-g * 20.0F);
            entity.headYaw = entity.getYaw();
            entity.prevHeadYaw = entity.getYaw();
            DiffuseLighting.method_34742();
            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            quaternion2.conjugate();
            entityRenderDispatcher.setRotation(quaternion2);
            entityRenderDispatcher.setRenderShadows(false);
            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            //noinspection deprecation
            RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, stack, immediate, 15728880));
            immediate.draw();
            entityRenderDispatcher.setRenderShadows(true);
            entity.bodyYaw = h;
            entity.setYaw(i);
            entity.setPitch(j);
            entity.prevHeadYaw = k;
            entity.headYaw = l;
            matrixStack.pop();
            stack.pop();
            RenderSystem.applyModelViewMatrix();
            DiffuseLighting.enableGuiDepthLighting();
        }

        public static boolean isOnScreen(Vec3d pos) {
            return pos != null && (pos.z > -1 && pos.z < 1);
        }

        public static Vec3d getScreenSpaceCoordinate(Vec3d pos, MatrixStack stack) {
            Camera camera = Atomic.client.getEntityRenderDispatcher().camera;
            Matrix4f matrix = stack.peek().getPositionMatrix();
            double x = pos.x - camera.getPos().x;
            double y = pos.y - camera.getPos().y;
            double z = pos.z - camera.getPos().z;
            Vector4f vector4f = new Vector4f((float) x, (float) y, (float) z, 1.f);
            vector4f.transform(matrix);
            int displayHeight = Atomic.client.getWindow().getHeight();
            Vector3D screenCoords = new Vector3D();
            int[] viewport = new int[4];
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            Matrix4x4 matrix4x4Proj = Matrix4x4.copyFromColumnMajor(RenderSystem.getProjectionMatrix());//no more joml :)
            Matrix4x4 matrix4x4Model = Matrix4x4.copyFromColumnMajor(RenderSystem.getModelViewMatrix());//but I do the math myself now :( (heck math)
            matrix4x4Proj.mul(matrix4x4Model).project(vector4f.getX(), vector4f.getY(), vector4f.getZ(), viewport, screenCoords);
            return new Vec3d(screenCoords.x / Atomic.client.getWindow().getScaleFactor(), (displayHeight - screenCoords.y) / Atomic.client.getWindow().getScaleFactor(), screenCoords.z);
        }

        public static Vec3d getScreenSpaceCoordinate(Vec3d pos) {
            return getScreenSpaceCoordinate(pos, R3D.getLastRenderStack());
        }

        public static void gradientLineScreen(Color start, Color end, double x, double y, double x1, double y1) {
            float g = start.getRed() / 255f;
            float h = start.getGreen() / 255f;
            float k = start.getBlue() / 255f;
            float f = start.getAlpha() / 255f;
            float g1 = end.getRed() / 255f;
            float h1 = end.getGreen() / 255f;
            float k1 = end.getBlue() / 255f;
            float f1 = end.getAlpha() / 255f;
            Matrix4f m = R3D.getEmptyMatrixStack().peek().getPositionMatrix();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(m, (float) x, (float) y, 0f).color(g, h, k, f).next();
            bufferBuilder.vertex(m, (float) x1, (float) y1, 0f).color(g1, h1, k1, f1).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

        public static void fill(MatrixStack matrices, Color c, double x1, double y1, double x2, double y2) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            int color = c.getRGB();
            double j;
            if (x1 < x2) {
                j = x1;
                x1 = x2;
                x2 = j;
            }

            if (y1 < y2) {
                j = y1;
                y1 = y2;
                y2 = j;
            }
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            float f = (float) (color >> 24 & 255) / 255.0F;
            float g = (float) (color >> 16 & 255) / 255.0F;
            float h = (float) (color >> 8 & 255) / 255.0F;
            float k = (float) (color & 255) / 255.0F;
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
            bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
            bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
            bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

        public static void fillGradientH(MatrixStack matrices, Color c2, Color c1, double x1, double y1, double x2, double y2) {
            float r1 = c1.getRed() / 255f;
            float g1 = c1.getGreen() / 255f;
            float b1 = c1.getBlue() / 255f;
            float a1 = c1.getAlpha() / 255f;
            float r2 = c2.getRed() / 255f;
            float g2 = c2.getGreen() / 255f;
            float b2 = c2.getBlue() / 255f;
            float a2 = c2.getAlpha() / 255f;

            double j;

            if (x1 < x2) {
                j = x1;
                x1 = x2;
                x2 = j;
            }

            if (y1 < y2) {
                j = y1;
                y1 = y2;
                y2 = j;
            }
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(r1, g1, b1, a1).next();
            bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(r2, g2, b2, a2).next();
            bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(r2, g2, b2, a2).next();
            bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(r1, g1, b1, a1).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();

        }

        public static void fill(Color c, double x1, double y1, double x2, double y2) {
            fill(R3D.getEmptyMatrixStack(), c, x1, y1, x2, y2);
        }

        public static void lineScreenD(Color c, double x, double y, double x1, double y1) {
            float g = c.getRed() / 255f;
            float h = c.getGreen() / 255f;
            float k = c.getBlue() / 255f;
            float f = c.getAlpha() / 255f;
            Matrix4f m = R3D.getEmptyMatrixStack().peek().getPositionMatrix();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(m, (float) x, (float) y, 0f).color(g, h, k, f).next();
            bufferBuilder.vertex(m, (float) x1, (float) y1, 0f).color(g, h, k, f).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

        public static void renderBackgroundTexture() {
            if (Atomic.client.currentScreen instanceof SocialInteractionsScreen) {
                return;
            }
            int width = Atomic.client.getWindow().getScaledWidth();
            int height = Atomic.client.getWindow().getScaledHeight();
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
            Screen.drawTexture(R3D.getEmptyMatrixStack(), 0, 0, 0, 0, width, height, width, height);
            if (!(Atomic.client.currentScreen instanceof HomeScreen)) {
                DrawableHelper.fill(R3D.getEmptyMatrixStack(), 0, 0, width, height, new Color(0, 0, 0, 60).getRGB());
            }
        }
    }

    public static class Util {

        public static int lerp(int o, int i, double p) {
            return (int) Math.floor(i + (o - i) * MathHelper.clamp(p, 0, 1));
        }

        public static double lerp(double i, double o, double p) {
            return (i + (o - i) * MathHelper.clamp(p, 0, 1));
        }

        public static Color lerp(Color a, Color b, double c) {
            return new Color(lerp(a.getRed(), b.getRed(), c), lerp(a.getGreen(), b.getGreen(), c), lerp(a.getBlue(), b.getBlue(), c), lerp(a.getAlpha(), b.getAlpha(), c));
        }

        /**
         * @param original       the original color
         * @param redOverwrite   the new red (or -1 for original)
         * @param greenOverwrite the new green (or -1 for original)
         * @param blueOverwrite  the new blue (or -1 for original)
         * @param alphaOverwrite the new alpha (or -1 for original)
         * @return the modified color
         */
        public static Color modify(Color original, int redOverwrite, int greenOverwrite, int blueOverwrite, int alphaOverwrite) {
            return new Color(redOverwrite == -1 ? original.getRed() : redOverwrite, greenOverwrite == -1 ? original.getGreen() : greenOverwrite, blueOverwrite == -1 ? original.getBlue() : blueOverwrite, alphaOverwrite == -1 ? original.getAlpha() : alphaOverwrite);
        }
    }

}