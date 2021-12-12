/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.notifications;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.render.Hud;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class NotificationRenderer {

    public static final List<Notification> notifications       = new ArrayList<>();
    public static final List<Notification> topBarNotifications = new ArrayList<>();

    public static void render() {
        renderSide();
        renderTop();
    }

    public static void onFastTick() {
        for (Notification notification : new ArrayList<>(notifications)) {
            notification.renderPosX = Transitions.transition(notification.renderPosX, notification.posX, 10);
            notification.renderPosY = Transitions.transition(notification.renderPosY, notification.posY, 10);
        }
        for (Notification notification : new ArrayList<>(topBarNotifications)) {
            notification.renderPosX = Transitions.transition(notification.renderPosX, notification.posX, 10);
            notification.renderPosY = Transitions.transition(notification.renderPosY, notification.posY, 10);
            if (notification.shouldDoAnimation) {
                notification.animationProgress = Transitions.transition(notification.animationProgress, notification.animationGoal, 10, 0.0001);
            }
        }
    }

    public static void renderTop() {
        MatrixStack ms = Renderer.R3D.getEmptyMatrixStack();
        if (!Objects.requireNonNull(ModuleRegistry.getByClass(Hud.class)).isEnabled()) {
            return;
        }
        int baseX = Atomic.client.getWindow().getScaledWidth() / 2;
        int height = 16;
        int baseY = -height - 5;
        int currentYOffset = 5;
        float minWidth = 50;
        long c = System.currentTimeMillis();
        ArrayList<Notification> nf = new ArrayList<>(topBarNotifications);
        nf.sort(Comparator.comparingDouble(value -> -FontRenderers.normal.getStringWidth(String.join(" ", value.contents))));
        for (Notification notification : nf) {
            double timeRemaining = Math.abs(c - notification.creationDate - notification.duration) / (double) notification.duration;
            timeRemaining = MathHelper.clamp(timeRemaining, 0, 1);
            boolean notificationExpired = notification.creationDate + notification.duration < c;
            if (notification.duration < 0) {
                timeRemaining = 0;
                notificationExpired = false;
            }
            notification.posX = notification.renderPosX = baseX;
            if (notification.renderPosY == -69 || notification.posY == -69) {
                notification.renderPosY = baseY;
            }
            if (!notificationExpired) {
                notification.posY = currentYOffset;
                if (Math.abs(notification.posY - notification.renderPosY) < 5) {
                    notification.animationGoal = 1;
                }
            } else {
                notification.animationGoal = 0;
                if (notification.animationProgress < 0.005) {
                    notification.posY = baseY - 5;
                    if (notification.renderPosY < baseY + 5) {
                        topBarNotifications.remove(notification);
                    }
                }
            }
            notification.shouldDoAnimation = notification.animationGoal != notification.animationProgress;
            String contents = String.join(" ", notification.contents);
            float width = FontRenderers.normal.getStringWidth(contents) + 5;
            width = width / 2f;
            width = Math.max(minWidth, width);
            float pad = 1;
            width += pad;
            Renderer.R2D.fill(ms, new Color(28, 28, 28, 200), notification.renderPosX - width, notification.renderPosY, notification.renderPosX - width + pad + (width * 2 * notification.animationProgress), notification.renderPosY + height);
            Renderer.R2D.scissor(notification.renderPosX - width + pad, notification.renderPosY, (width * 2 * notification.animationProgress), height + 1);
            FontRenderers.normal.drawCenteredString(ms, contents, notification.renderPosX, notification.renderPosY + height / 2f - FontRenderers.normal.getFontHeight() / 2f, 0xFFFFFF);
            Color GREEN = new Color(100, 255, 20);
            Color RED = new Color(255, 50, 20);
            double timeRemainingInv = 1 - timeRemaining;
            if (!notification.shouldDoAnimation && notification.animationProgress == 0 && notificationExpired) {
                timeRemainingInv = 1;
            }
            Color color = Renderer.Util.lerp(GREEN, RED, timeRemaining);
            double sin = Math.sin(Math.toRadians((c % 1000) / 1000d * 360));
            if (notification.duration == -1) {
                color = Renderer.Util.lerp(RED, RED.darker().darker(), sin);
            } else if (notification.duration == -2) {
                color = Renderer.Util.lerp(GREEN, GREEN.darker().darker(), sin);
            }
            Renderer.R2D.fill(ms, color, notification.renderPosX - width, notification.renderPosY + height - 2, notification.renderPosX - width + (width * 2 * timeRemainingInv), notification.renderPosY + height - 1);
            Renderer.R2D.unscissor();
            currentYOffset += height + 3;
        }
    }

    public static void renderSide() {
        MatrixStack ms = Renderer.R3D.getEmptyMatrixStack();
        if (!Objects.requireNonNull(ModuleRegistry.getByClass(Hud.class)).isEnabled()) {
            return;
        }
        int currentYOffset = -20;
        int baseX = Atomic.client.getWindow().getScaledWidth() - 160;
        int baseY = Atomic.client.getWindow().getScaledHeight() - 50;
        long c = System.currentTimeMillis();
        for (Notification notification : new ArrayList<>(notifications)) {
            double timeRemaining = Math.abs(c - notification.creationDate - notification.duration) / (double) notification.duration;
            timeRemaining = MathHelper.clamp(timeRemaining, 0, 1);
            boolean notificationExpired = notification.creationDate + notification.duration < c;
            int notifHeight = 2 + ((notification.contents.length + (notification.title.isEmpty() ? 0 : 1)) * FontRenderers.normal.getFontHeight());
            currentYOffset += notifHeight + 2;
            notification.posY = baseY - currentYOffset;
            if (!notificationExpired) {
                notification.posX = baseX;
            } else {
                notification.posX = baseX + 170;
                if (notification.renderPosX > baseX + 165) {
                    notifications.remove(notification);
                    continue;
                }
            }
            if (notification.renderPosY == 0) {
                notification.renderPosY = notification.posY;
            }
            if (notification.renderPosX == 0) {
                notification.renderPosX = baseX + 150;
            }
            Renderer.R2D.fill(new Color(28, 28, 28, 170), notification.renderPosX, notification.renderPosY, notification.renderPosX + 151, notification.renderPosY + notifHeight);
            Color GREEN = new Color(100, 255, 20);
            Color RED = new Color(255, 50, 20);
            Renderer.R2D.fill(Renderer.Util.lerp(GREEN, RED, timeRemaining), notification.renderPosX + 150, notification.renderPosY, notification.renderPosX + 150 + 1, notification.renderPosY + ((1 - timeRemaining) * notifHeight));
            int currentYOffsetText = 1 + FontRenderers.normal.getFontHeight();
            FontRenderers.normal.drawString(ms, notification.title, notification.renderPosX + 2, notification.renderPosY + 1, 0xFFFFFF);
            for (String content : notification.contents) {
                FontRenderers.normal.drawString(ms, content, notification.renderPosX + 2, notification.renderPosY + currentYOffsetText, 0xFFFFFF);
                currentYOffsetText += FontRenderers.normal.getFontHeight();
            }
        }
    }
}
