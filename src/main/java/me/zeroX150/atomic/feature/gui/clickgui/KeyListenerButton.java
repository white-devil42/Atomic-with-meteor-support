/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.clickgui;

import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.keybind.KeybindManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeyListenerButton extends ButtonWidget {

    final  Module  parent;
    public boolean listening       = false;
    public long    stoppedScanning = 0;
    int kc;

    public KeyListenerButton(int x, int y, int width, Module parent) {
        super(x, y, width, 12, Text.of(String.valueOf((char) Integer.parseInt(parent.config.get("Keybind").getValue() + "")).toUpperCase()), button -> {
        });
        kc = (int) parent.config.get("Keybind").getValue();
        this.parent = parent;
    }

    @Override public void onClick(double mouseX, double mouseY) {
        listening = true;
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.listening) {
            return false;
        }
        stoppedScanning = System.currentTimeMillis();
        if (keyCode == 47 || "-".equals(GLFW.glfwGetKeyName(kc, GLFW.glfwGetKeyScancode(kc)))) {
            listening = false;
            kc = -1;
            parent.config.get("Keybind").setValue(kc);
            KeybindManager.reload();
            return true;
        }
        kc = keyCode;
        listening = false;
        parent.config.get("Keybind").setValue(kc);
        KeybindManager.reload();
        //this.setMessage(InputUtil.fromKeyCode(keyCode, scanCode).getLocalizedText());
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (listening) {
            this.setMessage(Text.of("... (- to clear)"));
        } else {
            String n = kc == -1 ? "None" : GLFW.glfwGetKeyName(kc, GLFW.glfwGetKeyScancode(kc));
            if (n == null) {
                n = "kc." + kc;
            }
            this.setMessage(Text.of(n));
        }
        fill(matrices, x, y, x + width, y + height, (this.isHovered() ? Themes.currentActiveTheme.left() : Themes.currentActiveTheme.center()).getRGB());
        FontRenderers.normal.drawCenteredString(matrices, this.getMessage()
                .getString(), x + width / 2f, y + height / 2f - FontRenderers.normal.getFontHeight() / 2f, Themes.currentActiveTheme.fontColor().getRGB());
        //DrawableHelper.drawCenteredText(matrices, Atomic.client.textRenderer, this.getMessage(), x + (width / 2), y + (height / 2 - 9 / 2), 0xFFFFFF);
    }
}
