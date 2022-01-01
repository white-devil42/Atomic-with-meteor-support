/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.screen;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NbtEditScreen extends Screen implements FastTickable {

    static final double LINE_HEIGHT = FontRenderers.getMono().getMarginHeight();
    List<String> lines            = new ArrayList<>();
    NbtCompound  source;
    int          cursorX          = 0;
    int          cursorY          = 0;
    double       lastCursorPosX   = 0;
    double       lastCursorPosY   = 0;
    double       renderCursorPosX = 0;
    double       renderCursorPosY = 0;
    String       errorMessage     = "";
    long         lastMsgDisplay   = System.currentTimeMillis();
    double       scroll           = 0;
    double       trackedScroll    = 0;

    public NbtEditScreen(NbtCompound sourceCompound) {
        super(Text.of(""));
        source = sourceCompound;
        //lines.addAll(Arrays.asList(v.split("\n")));
    }

    @Override public void onFastTick() {
        trackedScroll = Transitions.transition(trackedScroll, scroll, 5);
        renderCursorPosX = Transitions.transition(renderCursorPosX, lastCursorPosX, 7);
        renderCursorPosY = Transitions.transition(renderCursorPosY, lastCursorPosY, 7);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll -= amount * 30;
        double height = LINE_HEIGHT * lines.size() + 2;
        scroll = MathHelper.clamp(scroll, 0, Math.max(height - this.height + 20, 0));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override protected void init() {
        {
            String v = NbtHelper.toNbtProviderString(source);
            //while(v.contains("§")) v = v.replace("§", "&");
            lines.clear();
            int maxWidth = width - 115;
            for (String s : v.split("\n")) {
                List<String> splitContent = new ArrayList<>();
                StringBuilder line = new StringBuilder();
                for (char c : s.toCharArray()) {
                    if (FontRenderers.getMono().getStringWidth(line + " " + c) >= maxWidth) {
                        splitContent.add(line.toString());
                        line = new StringBuilder();
                    }
                    line.append(c);
                }
                splitContent.add(line.toString());
                lines.addAll(splitContent);
            }
        }
        ButtonWidget check = new ButtonWidget(width - 105, 11, 100, 20, Text.of("Check"), button -> {
            try {
                StringNbtReader.parse(String.join("", lines));
                showMessage("§aValid nbt!");
            } catch (CommandSyntaxException e) {
                showMessage("§cInvalid NBT. " + e.getContext());
            }
        });
        ButtonWidget format = new ButtonWidget(width - 105, 11 + 25, 100, 20, Text.of("Format"), button -> {
            try {
                NbtCompound compound = StringNbtReader.parse(String.join("", lines));

                String v = NbtHelper.toNbtProviderString(compound);
                lines.clear();
                int maxWidth = width - 115;
                for (String s : v.split("\n")) {
                    List<String> splitContent = new ArrayList<>();
                    StringBuilder line = new StringBuilder();
                    for (char c : s.toCharArray()) {
                        if (FontRenderers.getMono().getStringWidth(line + " " + c) >= maxWidth) {
                            splitContent.add(line.toString());
                            line = new StringBuilder();
                        }
                        line.append(c);
                    }
                    splitContent.add(line.toString());
                    lines.addAll(splitContent);
                }
            } catch (CommandSyntaxException e) {
                showMessage("§cInvalid NBT. " + e.getContext());
            }
        });
        ButtonWidget upload = new ButtonWidget(width - 105, 11 + 25 + 25, 100, 20, Text.of("Upload"), button -> {
            try {
                String t = String.join("", lines);
                //                while (t.contains("&")) t = t.replace("&", "§");*/
                NbtCompound compound = StringNbtReader.parse(t);
                {
                    String v = NbtHelper.toNbtProviderString(compound);
                    lines.clear();
                    int maxWidth = width - 115;
                    for (String s : v.split("\n")) {
                        List<String> splitContent = new ArrayList<>();
                        StringBuilder line = new StringBuilder();
                        for (char c : s.toCharArray()) {
                            if (FontRenderers.getMono().getStringWidth(line + " " + c) >= maxWidth) {
                                splitContent.add(line.toString());
                                line = new StringBuilder();
                            }
                            line.append(c);
                        }
                        splitContent.add(line.toString());
                        lines.addAll(splitContent);
                    }
                }
                NbtCompound currentNbt = Objects.requireNonNull(Atomic.client.player).getInventory().getMainHandStack().getNbt();
                if (Objects.requireNonNull(currentNbt).equals(compound)) {
                    showMessage(Formatting.YELLOW + "No difference. Nothing to update");
                    return;
                }
                Atomic.client.player.getInventory().getMainHandStack().setNbt(compound);
                showMessage("§aUpdated item. Open your inv for it to take effect.");
            } catch (CommandSyntaxException e) {
                showMessage("§cInvalid NBT. " + e.getContext());
            }
        });
        ButtonWidget loadFromClip = new ButtonWidget(width - 105, 11 + 25 + 25 + 25, 100, 20, Text.of("From clipboard"), button -> {
            String clip = Atomic.client.keyboard.getClipboard();
            try {
                NbtCompound comp = StringNbtReader.parse(clip);
                String v = NbtHelper.toNbtProviderString(comp);
                lines.clear();
                int maxWidth = width - 115;
                for (String s : v.split("\n")) {
                    List<String> splitContent = new ArrayList<>();
                    StringBuilder line = new StringBuilder();
                    for (char c : s.toCharArray()) {
                        if (FontRenderers.getMono().getStringWidth(line + " " + c) >= maxWidth) {
                            splitContent.add(line.toString());
                            line = new StringBuilder();
                        }
                        line.append(c);
                    }
                    splitContent.add(line.toString());
                    lines.addAll(splitContent);
                }
            } catch (CommandSyntaxException e) {
                showMessage("§cInvalid NBT in clipboard. " + e.getContext());
            }
        });
        ButtonWidget saveToClip = new ButtonWidget(width - 105, 11 + 25 + 25 + 25 + 25, 100, 20, Text.of("To clipboard"), button -> {
            try {
                NbtCompound compound = StringNbtReader.parse(String.join("", lines));
                Text v = NbtHelper.toPrettyPrintedText(compound);
                Atomic.client.keyboard.setClipboard(v.getString());
                showMessage("§aCopied!");
            } catch (CommandSyntaxException e) {
                showMessage("§cInvalid NBT. " + e.getContext());
            }
        });
        addDrawableChild(check);
        addDrawableChild(format);
        addDrawableChild(upload);
        addDrawableChild(loadFromClip);
        addDrawableChild(saveToClip);
        super.init();
    }

    void showMessage(String msg) {
        errorMessage = msg;
        lastMsgDisplay = System.currentTimeMillis();
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        if (System.currentTimeMillis() - lastMsgDisplay > 6000) {
            errorMessage = "";
        }
        FontRenderers.getNormal().drawString(matrices, errorMessage, 5, 1, 0xFFFFFF);
        List<String> lines = new ArrayList<>(this.lines); // to make a backup for rendering
        if (lines.size() == 0) {
            lines.add("{");
            lines.add("");
            lines.add("}");
            this.lines = lines;
            cursorY = 1;
        }
        double height = LINE_HEIGHT * lines.size() + 2;
        matrices.push();
        matrices.translate(0, -trackedScroll, 0);
        Renderer.R2D.fill(matrices, new Color(0, 0, 0, 100), 5, 11, width - 110, 11 + height);
        int yOffset = 12;
        for (String line : lines) {
            FontRenderers.getMono().drawString(matrices, line, 6, yOffset, 0xFFFFFF);
            yOffset += FontRenderers.getMono().getFontHeight();
        }
        double rCX;
        double rCY;
        if (lines.size() != 0) {
            cursorY = MathHelper.clamp(cursorY, 0, lines.size() - 1); // start from 0 here, gotta shift everything down
            cursorX = MathHelper.clamp(cursorX, 0, lines.get(cursorY).length()); // start from 1 here because fucking substring
            rCX = FontRenderers.getMono().getStringWidth(lines.get(cursorY).substring(0, cursorX));
            rCY = cursorY * LINE_HEIGHT;
        } else {
            cursorY = cursorX = 0;
            rCX = rCY = 0;
        }
        lastCursorPosX = rCX;
        lastCursorPosY = rCY;
        rCX = renderCursorPosX;
        rCY = renderCursorPosY;
        Renderer.R2D.fill(matrices, Color.WHITE, rCX + 6, rCY + 12, rCX + 7, rCY + 11 + LINE_HEIGHT);
        matrices.pop();
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Screen.isPaste(keyCode)) {
            String clip = Atomic.client.keyboard.getClipboard();
            for (char c : clip.toCharArray()) {
                charTyped(c, 0);
            }
        } else {
            switch (keyCode) {
                case 262 -> cursorX++;
                case 263 -> cursorX--;
                case 264 -> cursorY++;
                case 265 -> cursorY--;
                case 257 -> {
                    lines.add(cursorY + 1, "");
                    cursorY++;
                }
                case 259 -> {
                    if (lines.size() == 0) {
                        break;
                    }
                    if (cursorX != 0) {
                        String current = lines.get(cursorY);
                        StringBuilder reassembled = new StringBuilder(current);
                        reassembled.deleteCharAt(cursorX - 1);
                        lines.remove(cursorY);
                        lines.add(cursorY, reassembled.toString());
                        cursorX--;
                    } else {
                        if (cursorY > 0) {
                            String backup = lines.get(cursorY);
                            lines.remove(cursorY);
                            cursorY--;
                            String current = lines.get(cursorY);
                            lines.remove(cursorY);
                            lines.add(cursorY, current + backup);
                            cursorX = current.length();
                        }
                    }
                }
                case 261 -> {
                    if (lines.size() != 0 && cursorX < lines.get(cursorY).length()) {
                        String current = lines.get(cursorY);
                        StringBuilder reassembled = new StringBuilder(current);
                        reassembled.deleteCharAt(cursorX);
                        lines.remove(cursorY);
                        lines.add(cursorY, reassembled.toString());
                    }
                }
                case 256 -> onClose();
            }
        }
        if (lines.size() != 0) {
            cursorY = MathHelper.clamp(cursorY, 0, lines.size() - 1); // start from 0 here, gotta shift everything down
            cursorX = MathHelper.clamp(cursorX, 0, lines.get(cursorY).length()); // start from 1 here because fucking substring
        } else {
            cursorY = cursorX = 0;
        }
        return true;

    }

    @Override public boolean charTyped(char chr, int modifiers) {
        if (SharedConstants.isValidChar(chr)) {
            StringBuilder b = new StringBuilder(lines.get(cursorY));
            b.insert(cursorX, chr);
            lines.remove(cursorY);
            lines.add(cursorY, b.toString());
            cursorX++;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override public void tick() {
        if (!lines.isEmpty()) {
            String combined = String.join("", lines);
            try {
                source = StringNbtReader.parse(combined);
            } catch (Exception ignored) {
                return;
            }
        }
        super.tick();
    }

    @Override public boolean isPauseScreen() {
        return false;
    }
}
