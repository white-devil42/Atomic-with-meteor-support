/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.windowed;

import com.google.common.collect.Lists;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.FastTickable;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Window implements FastTickable {

    public final  String                title;
    final         double                width;
    final         double                height;
    final         double                titlePadding  = 2;
    final         double                inset         = 2;
    final         boolean               isCloseable;
    final         double                sourceHeight;
    private final List<ClickableWidget> drawables     = Lists.newArrayList();
    protected     double                trackedScroll = 0;
    double oPosX, oPosY;
    double posX, posY;
    boolean clicked   = false;
    boolean discarded = false;
    double  scroll    = 0;

    public Window(String title, double posX, double posY, double width, double height, boolean closeable) {
        this.posX = this.oPosX = posX;
        this.posY = this.oPosY = posY;
        this.width = width + inset * 2;
        this.height = height + inset + titlePadding * 2 + 8;
        this.title = title;
        this.isCloseable = closeable;
        this.sourceHeight = height;
    }

    public double getPosX() {
        return posX;
    }

    public double getWidth() {
        return width;
    }

    public void discard() {
        discarded = true;
    }

    public double getPosY() {
        return posY;
    }

    public boolean isDiscarded() {
        return discarded;
    }

    public <T extends ClickableWidget> void addChild(T element) {
        this.drawables.add(element);
    }

    public void clearChildren() {
        this.drawables.clear();
    }

    protected void renderContents(MatrixStack stack, double mouseX, double mouseY) {
        for (ClickableWidget child : drawables) {
            child.render(stack, (int) mouseX, (int) mouseY, 0);
        }
    }

    public final void render(MatrixStack stack, double mouseX, double mouseY) {
        Renderer.R2D.scissor(posX, posY, width, height);
        stack.push();
        stack.translate(posX, posY, 0);

        Renderer.R2D.fill(stack, new Color(20, 20, 20, 100), 0, 0, width, height); // frame
        FontRenderers.getNormal().drawCenteredString(stack, title, width / 2, titlePadding, 0xFFFFFF); // title
        if (isCloseable) {
            FontRenderers.getNormal().drawString(stack, "X", width - FontRenderers.getNormal().getStringWidth("X") - inset, titlePadding, 0xFFFFFF);
        }
        Renderer.R2D.fill(stack, new Color(20, 20, 20, 20), inset, titlePadding * 2 + 8, width - inset, height - inset);
        stack.translate(inset, titlePadding * 2 + 8, 0);

        mouseX -= posX + inset;
        mouseY -= posY + titlePadding * 2 + 8 - trackedScroll;

        stack.push();
        stack.translate(0, -trackedScroll, 0);
        Renderer.R2D.unscissor();
        Renderer.R2D.scissor(posX + inset, posY + titlePadding * 2 + 8, width - inset, sourceHeight);
        renderContents(stack, mouseX, mouseY);
        Renderer.R2D.unscissor();
        stack.pop();

        stack.pop();
    }

    @Override public void onFastTick() {
        this.trackedScroll = Transitions.transition(trackedScroll, scroll, 7, 0.0001);
        this.posX = Transitions.transition(posX, oPosX, 7, 0.0001);
        this.posY = Transitions.transition(posY, oPosY, 7, 0.0001);
        posX = Math.max(0, posX);
        posY = Math.max(0, posY);
        posX = Math.min(Atomic.client.getWindow().getScaledWidth() - width, posX);
        posY = Math.min(Atomic.client.getWindow().getScaledHeight() - height, posY);
        if (!clicked) {
            oPosX = Math.max(0, oPosX);
            oPosY = Math.max(0, oPosY);
            oPosX = Math.min(Atomic.client.getWindow().getScaledWidth() - width, oPosX);
            oPosY = Math.min(Atomic.client.getWindow().getScaledHeight() - height, oPosY);
        }
        for (ClickableWidget drawable : drawables) {
            if (drawable instanceof FastTickable ft) {
                ft.onFastTick();
            }
        }
        scroll(0); // make sure we're in bounds
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean isInWindow = mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height;
        boolean isInContent = mouseX >= posX + inset && mouseX <= posX + width - inset && mouseY >= posY + titlePadding * 2 + 8 && mouseY <= posY + height - inset;
        boolean isOnX = mouseX >= posX + width - FontRenderers.getNormal().getStringWidth("X") - inset && mouseX <= posX + width && mouseY >= posY + titlePadding && mouseY <= posY + titlePadding + 8;
        if (isOnX && isCloseable) {
            discarded = true;
            return true;
        }
        if (isInWindow && !isInContent) {
            clicked = true;
        }
        if (isInContent) {
            mouseX -= posX + inset;
            mouseY -= posY + titlePadding * 2 + 8 - trackedScroll;
            for (Element child : drawables) {
                if (child.mouseClicked(mouseX, mouseY, button)) {
                    break;
                }
            }
        }
        return isInWindow;
    }

    public void charTyped(char chr, int modifiers) {
        for (Element child : drawables) {
            if (child.charTyped(chr, modifiers)) {
                break;
            }
        }
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            scroll(-1);
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            scroll(1);
        }
        for (Element child : drawables) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) {
                break;
            }
        }
    }

    public void keyReleased(int keyCode, int scanCode, int modifiers) {
        for (Element child : drawables) {
            if (child.keyReleased(keyCode, scanCode, modifiers)) {
                break;
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        clicked = false;
        mouseX -= posX + inset;
        mouseY -= posY + titlePadding * 2 + 8 - trackedScroll;
        for (Element child : drawables) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                break;
            }
        }
    }

    void scroll(double amount) {
        Optional<ClickableWidget> c = drawables.stream().min(Comparator.comparingInt(value -> value.y));
        if (c.isPresent()) {
            double maxScroll = (c.get().y + c.get().getHeight()) - sourceHeight;
            maxScroll = Math.max(maxScroll, 0);
            scroll -= amount * 10;
            scroll = MathHelper.clamp(scroll, 0, maxScroll);
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        boolean isInContent = mouseX >= posX + inset && mouseX <= posX + width - inset && mouseY >= posY + titlePadding * 2 + 8 && mouseY <= posY + height - inset;
        if (isInContent) {
            scroll(amount);
        }
        mouseX -= posX + inset;
        mouseY -= posY + titlePadding * 2 + 8 - trackedScroll;
        for (Element child : drawables) {
            if (child.mouseScrolled(mouseX, mouseY, amount)) {
                break;
            }
        }
        return isInContent;
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (clicked) {
            oPosX += deltaX;
            oPosY += deltaY;
        }
        mouseX -= posX + inset;
        mouseY -= posY + titlePadding * 2 + 8 - trackedScroll;
        for (Element child : drawables) {
            if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                break;
            }
        }
    }
}
