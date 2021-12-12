/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.screen;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.Themes;
import me.zeroX150.atomic.feature.module.impl.render.Waypoints;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class WaypointManagerScreen extends Screen implements FastTickable {

    double scroll = 0, renderScroll = 0;

    public WaypointManagerScreen() {
        super(Text.of(""));
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll += amount * 20;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override public void onFastTick() {
        renderScroll = Transitions.transition(renderScroll, scroll, 7);
    }

    @Override public boolean isPauseScreen() {
        return false;
    }

    @Override protected void init() {
        int yOff = 5;
        int xOff = 5;
        for (Waypoints.Waypoint waypoint : Waypoints.getWaypoints()) {
            ButtonWidget bw = new ButtonWidget(xOff, yOff, 100, 20, Text.of("Manage " + waypoint.name()), button -> Atomic.client.setScreen(new SingleManager(waypoint, this)));
            addDrawableChild(bw);
            yOff += 25;
            if (yOff + 50 > height) {
                yOff = 5;
                xOff += 105;
            }
        }
        ButtonWidget add = new ButtonWidget(xOff, yOff, 100, 20, Text.of("Add new"), button -> {
            Vec3d ppos = Objects.requireNonNull(Atomic.client.player).getPos();
            Atomic.client.setScreen(new SingleManager(new Waypoints.Waypoint(Math.round(ppos.x), Math.round(ppos.z), Utils.getCurrentRGB().getRGB(), ""), this));
        });
        addDrawableChild(add);
        super.init();
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

class SingleManager extends Screen {

    final Waypoints.Waypoint manage;
    final Screen             parent;

    public SingleManager(Waypoints.Waypoint w, Screen parent) {
        super(Text.of(""));
        this.manage = w;
        this.parent = parent;
    }

    @Override public void onClose() {
        Atomic.client.setScreen(parent);
    }

    @Override public boolean isPauseScreen() {
        return false;
    }

    @Override protected void init() {
        int buttonOffsets = 40;
        TextFieldWidget color = new TextFieldWidget(textRenderer, width / 2 - 70, height / 2 + buttonOffsets - 25 - 25 - 25 - 25 - 25, 140, 20, Text.of("SPECIAL:Color in HEX"));
        color.setMaxLength(6);
        color.setText(String.format("%06X", (0xFFFFFF & manage.color())));
        TextFieldWidget name = new TextFieldWidget(textRenderer, width / 2 - 70, height / 2 + buttonOffsets - 25 - 25 - 25 - 25, 140, 20, Text.of("SPECIAL:Name"));
        name.setMaxLength(64);
        name.setText(manage.name());
        TextFieldWidget coordX = new TextFieldWidget(textRenderer, width / 2 - 70, height / 2 + buttonOffsets - 25 - 25 - 25, 140, 20, Text.of("SPECIAL:Pos X"));
        coordX.setText(manage.posX() + "");
        TextFieldWidget coordZ = new TextFieldWidget(textRenderer, width / 2 - 70, height / 2 + buttonOffsets - 25 - 25, 140, 20, Text.of("SPECIAL:Pos Z"));
        coordZ.setText(manage.posZ() + "");
        ButtonWidget putHere = new ButtonWidget(width / 2 - 70, height / 2 + buttonOffsets - 25, 140, 20, Text.of("Set pos here"), button -> {
            Vec3d p = Objects.requireNonNull(Atomic.client.player).getPos();
            coordX.setText(Math.round(p.x) + ".0");
            coordZ.setText(Math.round(p.z) + ".0");
        });
        ButtonWidget save = new ButtonWidget(width / 2 - 70, height / 2 + buttonOffsets, 140, 20, Text.of("Save"), button -> {
            String c = color.getText();
            int newColor;
            String newName;
            double newCX, newCZ;
            try {
                if (c.length() != 6) {
                    throw new Exception();
                }
                newColor = Integer.parseInt(c, 16);
                color.setEditableColor(0xFFFFFF);
            } catch (Exception ignored) {
                color.setEditableColor(0xFF5555);
                return;
            }
            if (name.getText().isEmpty()) {
                name.setEditableColor(0xFF5555);
                return;
            }
            newName = name.getText();
            name.setEditableColor(0xFFFFFF);
            try {
                newCX = Double.parseDouble(coordX.getText());
                newCZ = Double.parseDouble(coordZ.getText());
                coordX.setEditableColor(0xFFFFFF);
                coordZ.setEditableColor(0xFFFFFF);
            } catch (Exception ignored) {
                coordX.setEditableColor(0xFF5555);
                coordZ.setEditableColor(0xFF5555);
                return;
            }
            Waypoints.getWaypoints().remove(manage);
            Waypoints.getWaypoints().add(new Waypoints.Waypoint(newCX, newCZ, newColor, newName));
            onClose();
        });
        ButtonWidget cancel = new ButtonWidget(width / 2 - 70, height / 2 + buttonOffsets + 25 + 25, 140, 20, Text.of("Cancel"), button -> onClose());
        ButtonWidget delete = new ButtonWidget(width / 2 - 70, height / 2 + buttonOffsets + 25, 140, 20, Text.of("Delete"), button -> {
            Waypoints.getWaypoints().remove(manage);
            onClose();
        });
        addDrawableChild(color);
        addDrawableChild(name);
        addDrawableChild(coordX);
        addDrawableChild(coordZ);
        addDrawableChild(putHere);
        addDrawableChild(save);
        addDrawableChild(cancel);
        addDrawableChild(delete);
        super.init();
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element child : children()) {
            child.mouseClicked(0, 0, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        parent.render(matrices, 0, 0, delta);
        Renderer.R2D.fill(matrices, Themes.Theme.ATOMIC.getPalette().left(), width / 2d - 75, height / 2d + 40 + 30 + 25 + 20, width / 2d + 75, height / 2d + 40 - 25 - 25 - 25 - 25 - 25 - 5);
        super.render(matrices, mouseX, mouseY, delta);
    }
}