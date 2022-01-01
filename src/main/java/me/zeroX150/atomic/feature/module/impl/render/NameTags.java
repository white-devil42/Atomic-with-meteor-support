/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;

public class NameTags extends Module {

    public NameTags() {
        super("NameTags", "big nametag.mp4", ModuleType.RENDER);
    }

    public boolean renderTag(Text text, Entity entity, MatrixStack stack) {
        if (!(entity instanceof LivingEntity le)) {
            return false;
        }
        if (Atomic.client.player == null || entity.isInvisible() || !entity.shouldRenderName()) {
            return false;
        }
        if (!Utils.Players.isPlayerNameValid(le.getEntityName())) {
            return false;
        }
        if (entity.getUuid() == Atomic.client.player.getUuid()) {
            return false;
        }
        Vec3d eSource = new Vec3d(MathHelper.lerp(Atomic.client.getTickDelta(), entity.prevX, entity.getX()), MathHelper.lerp(Atomic.client.getTickDelta(), entity.prevY, entity.getY()), MathHelper.lerp(Atomic.client.getTickDelta(), entity.prevZ, entity.getZ()));
        Vec3d sourcePos = eSource.add(0, (entity.getHeight() + .5) * 2, 0);
        Vec3d screenSpace = Renderer.R2D.getScreenSpaceCoordinate(sourcePos, stack);
        if (Renderer.R2D.isOnScreen(screenSpace)) {
            Utils.TickManager.runOnNextRender(() -> {
                String name = text.getString();
                float health = le.getHealth();
                double healthRounded = Utils.Math.roundToDecimal(health, 1);
                String entireDisplay = name + healthRounded;
                float w = FontRenderers.getNormal().getStringWidth(entireDisplay) + 7 + 2;
                //w = Math.max(w, Atomic.fontRenderer.getStringWidth("a".repeat(16)) + 2);
                float wh = w / 2f;

                float maxHealth = le.getMaxHealth();
                float hPer = health / maxHealth;
                hPer = MathHelper.clamp(hPer, 0, 1);
                Color GREEN = new Color(100, 255, 20);
                Color RED = new Color(255, 50, 20);
                Color MID_END = Renderer.Util.lerp(GREEN, RED, hPer);
                MatrixStack empty = Renderer.R3D.getEmptyMatrixStack();
                Renderer.R2D.fill(new Color(20, 20, 20, 100), screenSpace.x - wh, screenSpace.y - FontRenderers.getNormal().getFontHeight() - 1, screenSpace.x + wh, screenSpace.y + 1.5);
                FontRenderers.getNormal().drawString(empty, name, screenSpace.x - wh + 2, screenSpace.y - FontRenderers.getNormal().getFontHeight(), 0xFFFFFF);
                FontRenderers.getNormal()
                        .drawString(empty, healthRounded + "", screenSpace.x + wh - 2 - FontRenderers.getNormal().getStringWidth(healthRounded + ""), screenSpace.y - FontRenderers.getNormal()
                                .getFontHeight(), MID_END.getRGB());
                Renderer.R2D.fillGradientH(empty, RED, MID_END, screenSpace.x - wh, screenSpace.y, screenSpace.x - wh + (wh * 2 * hPer), screenSpace.y + 1.5);
            });
        }
        return true;
    }

    @Override public void tick() {

    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

