/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.windowed;

import me.zeroX150.atomic.feature.gui.screen.FastTickable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WindowScreen extends Screen implements FastTickable {

    protected final List<Window> windows = new ArrayList<>();

    public WindowScreen(String name) {
        super(Text.of(name));
    }

    public void clearWindows() {
        windows.clear();
    }

    public void addWindow(Window window) {
        windows.add(window);
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        windows.removeIf(window -> window.discarded);
        List<Window> c = new ArrayList<>(windows);
        Collections.reverse(c);
        for (int i = 0; i < c.size(); i++) {
            Window w = c.get(i);
            int mpy = mouseY;
            int mpx = mouseX;
            if (i != c.size() - 1) {
                mpy = mpx = -100;
            }
            w.render(matrices, mpx, mpy);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Window selected = null;
        for (Window window : windows) {
            if (window.mouseClicked(mouseX, mouseY, button)) {
                selected = window;
                break;
            }
        }
        if (selected != null) {
            windows.remove(selected);
            windows.add(0, selected); // push to front
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean charTyped(char chr, int modifiers) {
        if (!windows.isEmpty()) {
            windows.get(0).charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!windows.isEmpty()) {
            windows.get(0).keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!windows.isEmpty()) {
            windows.get(0).keyReleased(keyCode, scanCode, modifiers);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!windows.isEmpty()) {
            windows.get(0).mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!windows.isEmpty()) {
            windows.get(0).mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        Window selected = null;
        for (Window window : windows) {
            if (window.mouseScrolled(mouseX, mouseY, amount)) {
                selected = window;
                break;
            }
        }
        if (selected != null) {
            windows.remove(selected);
            windows.add(0, selected); // push to front
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override public void onFastTick() {
        for (Window window : windows) {
            window.onFastTick();
        }
    }

    @Override public boolean isPauseScreen() {
        return false;
    }
}
