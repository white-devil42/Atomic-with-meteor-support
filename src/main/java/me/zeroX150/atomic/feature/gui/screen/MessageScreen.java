/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.CustomColor;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class MessageScreen extends Screen implements FastTickable {

    final String          title;
    final String          description;
    final BooleanConsumer callback;
    final ScreenType      type;
    final double          width = 230;
    Screen  parent;
    double  height    = 0;
    double  animation = 0;
    boolean close     = false;

    public MessageScreen(Screen parent, String title, String description, BooleanConsumer callback, ScreenType type) {
        super(Text.of(""));
        this.parent = parent;
        if (this.parent == null) {
            this.parent = Atomic.client.currentScreen;
        }
        this.title = title;
        description = String.join("\n", Utils.splitLinesToWidth(description, width - 10, FontRenderers.mono));
        this.description = description;
        this.callback = callback;
        this.type = type;
        if (!title.isEmpty()) {
            height += 10; // title
        }
        if (!description.isEmpty()) {
            height += description.split("\n").length * 10; // description
        }
        height += 10; // offset
        height += 20; // buttons
        height += 4; // padding
    }

    @Override public void onFastTick() {
        double a = 0.02;
        if (close) {
            a *= -1;
        }
        animation += a;
        animation = MathHelper.clamp(animation, 0, 1);
        if (close && animation == 0) {
            Atomic.client.setScreen(parent);
        }
    }

    @Override public void onClose() {
        if (type == ScreenType.OK) {
            close = true;
        }
    }

    @Override protected void init() {
        close = false;
        animation = 0;
        double centerX = Atomic.client.getWindow().getScaledWidth() / 2d;
        double centerY = Atomic.client.getWindow().getScaledHeight() / 2d;
        if (type == ScreenType.OK) {
            ButtonWidget bw = new ButtonWidget((int) (centerX - (width / 2) + 5), (int) (centerY + (height / 2d - 4 - 20)), (int) width - 10, 20, Text.of("OK"), button -> {
                close = true;
                callback.accept(true);
            });
            addDrawableChild(bw);
        } else if (type == ScreenType.YESNO) {
            ButtonWidget yes = new ButtonWidget((int) (centerX - (width / 2) + 5), (int) (centerY + (height / 2d - 4 - 20)), (int) (width / 2d - 10), 20, Text.of("Yes"), button -> {
                close = true;
                callback.accept(true);
            });
            ButtonWidget no = new ButtonWidget((int) (centerX + 5), (int) (centerY + (height / 2d - 4 - 20)), (int) (width / 2d - 10), 20, Text.of("No"), button -> {
                close = true;
                callback.accept(false);
            });
            addDrawableChild(yes);
            addDrawableChild(no);
        }
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (parent != null) {
            parent.render(matrices, mouseX, mouseY, delta);
        }
        double centerX = Atomic.client.getWindow().getScaledWidth() / 2d;
        double centerY = Atomic.client.getWindow().getScaledHeight() / 2d;
        double ai = Transitions.easeOutExpo(animation);
        double ap1 = MathHelper.clamp(ai * 2, 0, 1);
        double ap2 = MathHelper.clamp(ai * 2, 1, 2) - 1;
        double rh = (height * ap1) / 2d;
        double rw = (width * ap2) / 2d;
        Renderer.R2D.scissor(centerX - width / 2d - 2, centerY - height / 2d, rw * 2 + 2, rh * 2);
        Renderer.R2D.fill(matrices, Utils.getCurrentRGB(), centerX - width / 2d - 2, centerY - height / 2d, centerX - width / 2d, centerY + height / 2d);
        Renderer.R2D.fill(matrices, new CustomColor(0, 0, 0, 200), centerX - width / 2d, centerY - height / 2d, centerX + width / 2d, centerY + height / 2d);
        if (!title.isEmpty()) {
            FontRenderers.normal.drawCenteredString(matrices, title, centerX, centerY - height / 2d + FontRenderers.normal.getFontHeight() / 2f, 0xFFFFFF);
        }
        if (!description.isEmpty()) {
            int yoff = 0;
            for (String s : description.split("\n")) {
                FontRenderers.mono.drawCenteredString(matrices, s, centerX, centerY - height / 2d + FontRenderers.normal.getFontHeight() / 2f + 10 + yoff, 0xCCCCCC);
                yoff += FontRenderers.mono.getFontHeight();
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
        Renderer.R2D.unscissor();
    }

    @Override public boolean isPauseScreen() {
        return false;
    }

    public enum ScreenType {
        YESNO, OK
    }
}
