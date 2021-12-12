/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.clickgui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.FastTickable;
import me.zeroX150.atomic.feature.gui.screen.NonClearingInit;
import me.zeroX150.atomic.feature.gui.widget.SimpleCustomTextFieldWidget;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.ColorValue;
import me.zeroX150.atomic.feature.module.config.DynamicValue;
import me.zeroX150.atomic.feature.module.config.ModuleConfig;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.PropGroup;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.feature.module.impl.render.ClickGUI;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen.CATEGORY_HEIGHT;
import static me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen.CATEGORY_WIDTH;
import static me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen.CONFIG_HEIGHT;
import static me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen.CONFIG_WIDTH;
import static me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen.MAX_HEIGHT;
import static me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen.MAX_WIDTH;
import static me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen.MODULE_HEIGHT;
import static me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen.MODULE_WIDTH;
import static me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen.SOURCE_X;
import static me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen.SOURCE_Y;
import static me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen.getInstance;

public class ClickGUIScreen extends Screen implements FastTickable, NonClearingInit {

    public static final  Identifier     LOGO            = new Identifier("atomic", "logo.png");
    private static final ClickGUIScreen INSTANCE        = new ClickGUIScreen();
    static               double         CATEGORY_WIDTH  = 100;
    static               double         CATEGORY_HEIGHT = 30;
    static               double         MODULE_WIDTH    = 100;
    static               double         MODULE_HEIGHT   = 20;
    static               double         MAX_HEIGHT      = (int) (Arrays.stream(ModuleType.values()).filter(moduleType -> moduleType != ModuleType.HIDDEN).count() * CATEGORY_HEIGHT);
    static               double         CONFIG_HEIGHT   = MAX_HEIGHT;
    static               double         CONFIG_WIDTH    = 300;
    static               double         MAX_WIDTH       = CATEGORY_WIDTH + MODULE_WIDTH + CONFIG_WIDTH;
    static               int            SOURCE_X        = 10;
    static               int            SOURCE_Y        = 10;
    CategoryDisplay selected;
    String          desc       = "";
    String          searchTerm = "";
    double          animProg   = 0;
    boolean         closed     = false;

    public ClickGUIScreen() {
        super(Text.of("Atomic client"));
    }

    public static ClickGUIScreen getInstance() {
        return INSTANCE;
    }

    public void renderDescription(String desc) {
        this.desc = desc;
    }

    @Override public void resize(MinecraftClient client, int width, int height) {
        selected = null;
        clearChildren();
        super.resize(client, width, height);
        animProg = 1;
    }

    @Override public void onFastTick() {
        double a = 0.03;
        if (ClickGUI.instant.getValue()) {
            a = 1;
        }
        if (closed) {
            a *= -1;
        }
        animProg += a;
        animProg = MathHelper.clamp(animProg, 0, 1);
    }

    @Override public void onClose() {
        closed = true;
    }

    @Override protected void init() {
        closed = false;
        animProg = 0;
        SOURCE_X = (int) (width / 2d - MAX_WIDTH / 2d);
        SOURCE_Y = (int) (height / 2d - MAX_HEIGHT / 2d);
        int yOffset = 0;
        //selected = null;
        if (children().isEmpty()) {
            clearChildren();
            for (ModuleType value : ModuleType.values()) {
                if (value == ModuleType.HIDDEN) {
                    continue;
                }
                CategoryDisplay c = new CategoryDisplay(SOURCE_X, SOURCE_Y + yOffset, value);
                if (selected == null) {
                    selected = c;
                }
                addDrawableChild(c);
                yOffset += c.getHeight();
            }
        }
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element child : children()) {
            if (child.mouseClicked(mouseX, mouseY, button) && child instanceof CategoryDisplay e) {
                selected = e;
            }
        }
        return true;
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (closed && animProg == 0) {
            Atomic.client.setScreen(null);
            return;
        }
        double animProgE = Transitions.easeOutExpo(animProg);

        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        Matrix4f matrices4 = matrices.peek().getPositionMatrix();
        int a = (int) Math.floor(Math.abs(animProgE) * 150);
        a *= Themes.currentActiveTheme.backgroundOpacity();
        float offset = (float) ((System.currentTimeMillis() % 3000) / 3000d);
        float hsv2p = 0.25f + offset;
        float hsv3p = 0.5f + offset;
        float hsv4p = 0.75f + offset;
        java.awt.Color hsv1 = java.awt.Color.getHSBColor(offset % 1, 0.6f, 0.8f);
        java.awt.Color hsv2 = java.awt.Color.getHSBColor(hsv2p % 1, 0.6f, 0.8f);
        java.awt.Color hsv3 = java.awt.Color.getHSBColor(hsv3p % 1, 0.6f, 0.8f);
        java.awt.Color hsv4 = java.awt.Color.getHSBColor(hsv4p % 1, 0.6f, 0.8f);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrices4, 0, 0, 0).color(hsv1.getRed(), hsv1.getGreen(), hsv1.getBlue(), a).next();
        bufferBuilder.vertex(matrices4, 0, height, 0).color(hsv2.getRed(), hsv2.getGreen(), hsv2.getBlue(), a).next();
        bufferBuilder.vertex(matrices4, width, height, 0).color(hsv3.getRed(), hsv3.getGreen(), hsv3.getBlue(), a).next();
        bufferBuilder.vertex(matrices4, width, 0, 0).color(hsv4.getRed(), hsv4.getGreen(), hsv4.getBlue(), a).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableBlend();

        matrices.push();
        matrices.translate(0, (1 - animProgE) * -(130 * 0.28 + 11), 0);
        RenderSystem.setShaderTexture(0, LOGO);
        RenderSystem.enableBlend();
        RenderSystem.blendEquation(32774);
        RenderSystem.blendFunc(770, 1);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        drawTexture(matrices, 10, 10, 0, 0, 0, (int) (504 * 0.28), (int) (130 * 0.28), (int) (504 * 0.28), (int) (130 * 0.28));
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();

        matrices.pop();

        for (Element child : children()) {
            if (child instanceof CategoryDisplay e) {
                e.isSelected = e.equals(selected);
            }
        }
        double addX = (1 - animProgE) * (MAX_WIDTH / 2d);
        double addY = (1 - animProgE) * (MAX_HEIGHT / 2d);
        if (!searchTerm.isEmpty()) {
            FontRenderers.mono.drawString(matrices, searchTerm + " (esc to clear)", (float) (SOURCE_X + addX), (float) (SOURCE_Y + addY - 10), new Color(255, 255, 255, 100).getRGB(), false);
        }
        Renderer.R2D.scissor(SOURCE_X + addX, SOURCE_Y + addY, animProgE * MAX_WIDTH, animProgE * MAX_HEIGHT);
        super.render(matrices, mouseX, mouseY, delta);
        Renderer.R2D.unscissor();

        FontRenderers.normal.drawCenteredString(matrices, desc, width / 2f, height - 20, Themes.currentActiveTheme.fontColor().getRGB());
        desc = "";
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (Element child : children()) {
            if (child instanceof CategoryDisplay e) {
                e.mouseScrolled(mouseX, mouseY, amount);
            }
        }
        return true;
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Element child : children()) {
            if (child instanceof CategoryDisplay e) {
                e.mouseReleased(mouseX, mouseY, button);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean charTyped(char chr, int modifiers) {
        boolean c = true;
        for (Element child : children()) {
            if (child instanceof CategoryDisplay e) {
                if (e.charTyped(chr, modifiers)) {
                    c = false;
                }
            }
        }
        if (c) {
            searchTerm += chr;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override public void mouseMoved(double mouseX, double mouseY) {
        for (Element child : children()) {
            if (child instanceof CategoryDisplay e) {
                e.mouseMoved(mouseX, mouseY);
            }
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element child : children()) {
            if (child instanceof CategoryDisplay e) {
                e.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && !searchTerm.isEmpty()) {
            searchTerm = "";
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (Element child : children()) {
            if (child instanceof CategoryDisplay e) {
                e.keyReleased(keyCode, scanCode, modifiers);
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Element child : children()) {
            if (child instanceof CategoryDisplay e) {
                e.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override public boolean isPauseScreen() {
        return false;
    }
}

class ClearButton extends ClickableWidget {

    String   text;
    Runnable onClick;
    Color    inactive = new Color(20, 20, 20, 230);
    Color    active   = new Color(40, 40, 40, 230);

    public ClearButton(int x, int y, int width, int height, String text, Runnable onClick) {
        super(x, y, width, height, Text.of(text));
        this.text = text;
        this.onClick = onClick;
    }

    boolean isOnButton(double x, double y) {
        return x > this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Color toUse = isOnButton(mouseX, mouseY) ? active : inactive;
        Renderer.R2D.fill(matrices, toUse, this.x, this.y, this.x + width, this.y + height);
        double centerX = this.x + this.width / 2d;
        double centerY = this.y + this.height / 2d;
        FontRenderers.normal.drawCenteredString(matrices, text, centerX, centerY - FontRenderers.normal.getFontHeight() / 2f, Themes.currentActiveTheme.fontColor().getRGB());
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean v = isOnButton(mouseX, mouseY);
        if (v) {
            onClick.run();
        }
        return v;
    }

    @Override public void appendNarrations(NarrationMessageBuilder builder) {

    }
}

class ConfigDisplay extends ClickableWidget implements FastTickable {

    Map<PropGroup, List<Map.Entry<DynamicValue<?>, ClickableWidget>>> widgets = new LinkedHashMap<>();
    ModuleConfig                                                      parent;
    Module                                                            p;
    int                                                               padding = 5;
    double                                                            scroll  = 0, trackedScroll = 0;
    int configEntryWidth = (int) ((CONFIG_WIDTH) / 2 - 5);

    public ConfigDisplay(int x, int y, Module parent) {
        super(x, y, (int) CONFIG_WIDTH, (int) CONFIG_HEIGHT, Text.of(""));

        this.parent = parent.config;
        this.p = parent;
        int yOffset = 0;
        PropGroup orphans = new PropGroup("");
        List<DynamicValue<?>> leftOver = new ArrayList<>(this.parent.getAll());
        List<PropGroup> groups = new ArrayList<>(this.parent.getGroups());
        for (PropGroup group : this.parent.getGroups()) {
            for (DynamicValue<?> child : group.getChildren()) {
                leftOver.remove(child);
            }
        }
        for (DynamicValue<?> dynamicValue : leftOver) {
            orphans.withChild(dynamicValue);
        }
        groups.add(0, orphans);
        for (PropGroup group : groups) {
            widgets.put(group, new ArrayList<>());
            for (DynamicValue<?> dynamicValue : group.getChildren()) {
                ClickableWidget t;
                if (dynamicValue.getKey().equalsIgnoreCase("Keybind")) {
                    t = new KeyListenerButton(0, this.y + yOffset, configEntryWidth, parent) {
                        @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                            if (isHovered()) {
                                ClickGUIScreen.getInstance().renderDescription(dynamicValue.getDescription());
                            }
                            super.render(matrices, mouseX, mouseY, delta);
                        }
                    };
                } else if (dynamicValue instanceof BooleanValue) {
                    t = new Toggleable(0, this.y + yOffset, configEntryWidth, (BooleanValue) dynamicValue) {
                        @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                            if (isHovered()) {
                                ClickGUIScreen.getInstance().renderDescription(dynamicValue.getDescription());
                            }
                            super.render(matrices, mouseX, mouseY, delta);
                        }
                    };
                } else if (dynamicValue instanceof SliderValue) {
                    t = new Slider(0, this.y + yOffset + 1, configEntryWidth - 1, (SliderValue) dynamicValue) {
                        @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                            if (isHovered()) {
                                ClickGUIScreen.getInstance().renderDescription(dynamicValue.getDescription());
                            }
                            super.render(matrices, mouseX, mouseY, delta);
                        }
                    };
                } else if (dynamicValue instanceof MultiValue mval) {
                    t = new ButtonMultiSelectable(0, this.y + yOffset, configEntryWidth, mval) {
                        @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                            if (isHovered()) {
                                ClickGUIScreen.getInstance().renderDescription(dynamicValue.getDescription());
                            }
                            super.render(matrices, mouseX, mouseY, delta);
                        }
                    };
                } else if (dynamicValue instanceof ColorValue orig) {
                    t = new ColorConfig(0, this.y + yOffset, configEntryWidth, orig) {
                        @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                            if (isHovered()) {
                                ClickGUIScreen.getInstance().renderDescription(orig.getDescription());
                            }
                            super.render(matrices, mouseX, mouseY, delta);
                        }
                    };
                } else {
                    SimpleCustomTextFieldWidget w = new SimpleCustomTextFieldWidget(0, this.y + yOffset, configEntryWidth, 12, Text.of(dynamicValue.getKey())) {
                        @Override public void event_onTextChange() {
                            dynamicValue.setValue(this.getText());
                        }

                        @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                            if (isHovered()) {
                                ClickGUIScreen.getInstance().renderDescription(dynamicValue.getDescription());
                            }
                            super.render(matrices, mouseX, mouseY, delta);
                        }
                    };
                    w.setText(dynamicValue.getValue().toString());
                    t = w;
                }
                widgets.get(group).add(new AbstractMap.SimpleEntry<>(dynamicValue, t));
                yOffset += t.getHeight() + 1;
            }
            if (!group.getName().isEmpty()) {
                yOffset += 10;
            }
        }
    }

    @Override public void onFastTick() {
        trackedScroll = Transitions.transition(trackedScroll, scroll, 7, 0);
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        mouseY += trackedScroll;
        Renderer.R2D.fill(matrices, Themes.currentActiveTheme.right(), this.x, this.y, this.x + width, this.y + height);
        int yOffset = 14;
        matrices.translate(0, -trackedScroll, 0);
        FontRenderers.normal.drawString(matrices, p.getName() + " config", this.x + 5, this.y + yOffset / 2f - FontRenderers.normal.getFontHeight() / 2f, Themes.currentActiveTheme.fontColor()
                .getRGB());
        for (PropGroup propGroup : widgets.keySet()) {
            boolean showsAnything = false;
            for (Map.Entry<DynamicValue<?>, ClickableWidget> dynamicValueClickableWidgetEntry : widgets.get(propGroup)) {
                if (dynamicValueClickableWidgetEntry.getKey().shouldShow()) {
                    showsAnything = true;
                }
            }
            if (!propGroup.getName().isEmpty() && showsAnything) {
                FontRenderers.normal.drawCenteredString(matrices, propGroup.getName(), this.x + this.width / 2f, this.y + yOffset, Themes.currentActiveTheme.fontColor().getRGB());
                yOffset += FontRenderers.normal.getFontHeight() + 2;
            }
            for (Map.Entry<DynamicValue<?>, ClickableWidget> dynamicValueClickableWidgetEntry : widgets.get(propGroup)) {
                dynamicValueClickableWidgetEntry.getValue().y = this.y + yOffset;
                dynamicValueClickableWidgetEntry.getValue().x = this.x + width / 2;
                if (!dynamicValueClickableWidgetEntry.getKey().shouldShow()) {
                    continue;
                }
                FontRenderers.normal.drawString(matrices, dynamicValueClickableWidgetEntry.getKey().getKey(), this.x + padding, this.y + yOffset, Themes.currentActiveTheme.fontColor().getRGB());
                dynamicValueClickableWidgetEntry.getValue().render(matrices, mouseX, mouseY, delta);
                yOffset += dynamicValueClickableWidgetEntry.getValue().getHeight();
            }
        }
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= SOURCE_Y && mouseY <= SOURCE_Y + MAX_HEIGHT) {
            scroll -= amount * 10;
        }
        int maxScroll = 0;
        for (PropGroup propGroup : widgets.keySet()) {
            boolean showsAnything = false;
            for (Map.Entry<DynamicValue<?>, ClickableWidget> dynamicValueClickableWidgetEntry : widgets.get(propGroup)) {
                if (dynamicValueClickableWidgetEntry.getKey().shouldShow()) {
                    showsAnything = true;
                }
            }
            if (!propGroup.getName().isEmpty() && showsAnything) {
                maxScroll += 10;
            }
            for (Map.Entry<DynamicValue<?>, ClickableWidget> dynamicValueClickableWidgetEntry : widgets.get(propGroup)) {
                if (!dynamicValueClickableWidgetEntry.getKey().shouldShow()) {
                    continue;
                }
                maxScroll += dynamicValueClickableWidgetEntry.getValue().getHeight();
            }
        }
        maxScroll -= (CONFIG_HEIGHT - 20);
        maxScroll = Math.max(0, maxScroll);
        scroll = MathHelper.clamp(scroll, 0, maxScroll);
        return true;
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseY += trackedScroll;
        for (PropGroup propGroup : this.widgets.keySet()) {
            for (Map.Entry<DynamicValue<?>, ClickableWidget> dynamicValueClickableWidgetEntry : this.widgets.get(propGroup)) {
                dynamicValueClickableWidgetEntry.getValue().mouseClicked(mouseX, mouseY, button);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseY += trackedScroll;
        for (PropGroup propGroup : this.widgets.keySet()) {
            for (Map.Entry<DynamicValue<?>, ClickableWidget> dynamicValueClickableWidgetEntry : this.widgets.get(propGroup)) {
                dynamicValueClickableWidgetEntry.getValue().mouseReleased(mouseX, mouseY, button);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean charTyped(char c, int mod) {
        boolean v = false;
        for (List<Map.Entry<DynamicValue<?>, ClickableWidget>> value : this.widgets.values()) {
            for (Map.Entry<DynamicValue<?>, ClickableWidget> dynamicValueListEntry : value) {
                ClickableWidget child = dynamicValueListEntry.getValue();
                if (child instanceof KeyListenerButton b && (System.currentTimeMillis() - b.stoppedScanning) < 50) {
                    v = true;
                }
                if (child instanceof SimpleCustomTextFieldWidget) {
                    if (child.charTyped(c, mod)) {
                        v = true;
                    }
                }
            }
        }
        return v;
    }

    @Override public void mouseMoved(double mouseX, double mouseY) {
        mouseY += trackedScroll;
        for (PropGroup propGroup : this.widgets.keySet()) {
            for (Map.Entry<DynamicValue<?>, ClickableWidget> dynamicValueClickableWidgetEntry : this.widgets.get(propGroup)) {
                dynamicValueClickableWidgetEntry.getValue().mouseMoved(mouseX, mouseY);
            }
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        mouseY += trackedScroll;
        for (PropGroup propGroup : this.widgets.keySet()) {
            for (Map.Entry<DynamicValue<?>, ClickableWidget> dynamicValueClickableWidgetEntry : this.widgets.get(propGroup)) {
                dynamicValueClickableWidgetEntry.getValue().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (PropGroup propGroup : this.widgets.keySet()) {
            for (Map.Entry<DynamicValue<?>, ClickableWidget> dynamicValueClickableWidgetEntry : this.widgets.get(propGroup)) {
                if (dynamicValueClickableWidgetEntry.getValue() instanceof SimpleCustomTextFieldWidget || dynamicValueClickableWidgetEntry.getValue() instanceof KeyListenerButton) {
                    dynamicValueClickableWidgetEntry.getValue().keyPressed(keyCode, scanCode, modifiers);
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (PropGroup propGroup : this.widgets.keySet()) {
            for (Map.Entry<DynamicValue<?>, ClickableWidget> dynamicValueClickableWidgetEntry : this.widgets.get(propGroup)) {
                if (dynamicValueClickableWidgetEntry.getValue() instanceof SimpleCustomTextFieldWidget) {
                    dynamicValueClickableWidgetEntry.getValue().keyReleased(keyCode, scanCode, modifiers);
                }
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override public void appendNarrations(NarrationMessageBuilder builder) {

    }
}

class ModuleDisplay extends ClearButton implements FastTickable {

    boolean       isSelected;
    Module        module;
    ConfigDisplay configDisplay;
    double        animProg = 0;

    public ModuleDisplay(int x, int y, Module module) {
        super(x, y, (int) MODULE_WIDTH, (int) MODULE_HEIGHT, module.getName(), () -> {
        });
        this.module = module;
        this.configDisplay = new ConfigDisplay((int) (this.x + MODULE_WIDTH), SOURCE_Y, module);
    }

    static boolean nameMatches(String name, String search) {
        boolean isGood = true;
        for (char c : search.toLowerCase().toCharArray()) {
            if (!name.toLowerCase().contains(c + "")) {
                isGood = false;
                break;
            }
        }
        return isGood;
    }

    @Override public void onFastTick() {
        configDisplay.onFastTick();
        double y = 0.04;
        if (!module.isEnabled()) {
            y *= -1;
        }
        animProg += y;
        animProg = MathHelper.clamp(animProg, 0, 1);
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.active = Themes.currentActiveTheme.center().brighter();
        this.inactive = new Color(0, 0, 0, 0);
        double animProg = Transitions.easeOutExpo(this.animProg);
        boolean onButton = isOnButton(mouseX, mouseY);
        double absX = Utils.Mouse.getMouseX();
        double absY = Utils.Mouse.getMouseY();
        if (!(absX > SOURCE_X && absX < SOURCE_X + MAX_WIDTH && absY > SOURCE_Y && absY < SOURCE_Y + MAX_HEIGHT)) {
            onButton = false;
        }
        if (onButton) {
            ClickGUIScreen.getInstance().renderDescription(module.getDescription());
        }
        Color toUse = onButton ? active : inactive;
        Renderer.R2D.fill(matrices, toUse, this.x, this.y, this.x + width, this.y + height);
        Renderer.R2D.fill(matrices, Utils.getCurrentRGB(), this.x, this.y, this.x + 1, this.y + (animProg * height));
        double centerY = this.y + this.height / 2d;
        int color = getInstance().searchTerm.isEmpty() || nameMatches(module.getName(), getInstance().searchTerm) ? Themes.currentActiveTheme.fontColor().getRGB() : 0x555555;
        FontRenderers.normal.drawString(matrices, text, this.x + 5, centerY - FontRenderers.normal.getFontHeight() / 2f, color);
        MatrixStack s = Renderer.R3D.getEmptyMatrixStack();
        if (isSelected) {
            configDisplay.render(s, (int) Utils.Mouse.getMouseX(), (int) Utils.Mouse.getMouseY(), delta);
        }
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isSelected) {
            configDisplay.mouseClicked(Utils.Mouse.getMouseX(), Utils.Mouse.getMouseY(), button); // using absolute values here
        }
        boolean v = super.mouseClicked(mouseX, mouseY, button);
        double absX = Utils.Mouse.getMouseX();
        double absY = Utils.Mouse.getMouseY();
        if (!(absX > SOURCE_X && absX < SOURCE_X + MAX_WIDTH && absY > SOURCE_Y && absY < SOURCE_Y + MAX_HEIGHT)) {
            return false;
        }
        if (v && button == 0) {
            module.toggle();
        } else {
            return v && button == 1;
        }
        return false;
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isSelected) {
            configDisplay.mouseReleased(Utils.Mouse.getMouseX(), Utils.Mouse.getMouseY(), button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean charTyped(char chr, int modifiers) {
        if (isSelected) {
            return configDisplay.charTyped(chr, modifiers);
        }
        return false;
    }

    @Override public void mouseMoved(double mouseX, double mouseY) {
        if (isSelected) {
            configDisplay.mouseMoved(Utils.Mouse.getMouseX(), Utils.Mouse.getMouseY());
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isSelected) {
            configDisplay.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (isSelected) {
            configDisplay.keyReleased(keyCode, scanCode, modifiers);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isSelected) {
            configDisplay.mouseDragged(Utils.Mouse.getMouseX(), Utils.Mouse.getMouseY(), button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isSelected) {
            configDisplay.mouseScrolled(Utils.Mouse.getMouseX(), Utils.Mouse.getMouseY(), amount);
        }
        return true;
    }
}

class CategoryDisplay extends ClearButton implements FastTickable {

    public double trackedScroll = 0;
    boolean             isSelected = false;
    List<ModuleDisplay> modules    = new ArrayList<>();
    ModuleDisplay       selectedModule;
    ModuleType          type;
    double              scroll     = 0;

    public CategoryDisplay(int x, int y, ModuleType category) {
        super(x, y, (int) CATEGORY_WIDTH, (int) CATEGORY_HEIGHT, category.getName(), () -> {
        });
        this.type = category;
        modules.clear();
        int yOffset = 0;
        for (Module module : ModuleRegistry.getModules()) {
            if (module.getModuleType() == category) {
                ModuleDisplay md = new ModuleDisplay(x + width, SOURCE_Y + yOffset, module);
                if (selectedModule == null) {
                    selectedModule = md;
                }
                modules.add(md);
                yOffset += md.getHeight();
            }
        }
    }

    List<ModuleDisplay> getModules() {
        return modules;
    }

    @Override public void onFastTick() {
        trackedScroll = Transitions.transition(trackedScroll, scroll, 7, 0);
        for (ModuleDisplay module : getModules()) {
            module.onFastTick();
        }
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!this.isSelected) {
            return false;
        }
        if (mouseX >= this.x + this.width && mouseX <= this.x + this.width + MODULE_WIDTH && mouseY >= SOURCE_Y && mouseY <= SOURCE_Y + MAX_HEIGHT) {
            scroll -= amount * 10;
        }
        ModuleDisplay mostBottom = getModules().stream().sorted(Comparator.comparingInt(value -> -value.y)).collect(Collectors.toList()).get(0);
        int maxScroll = mostBottom.y + mostBottom.getHeight();
        maxScroll -= (MAX_HEIGHT + SOURCE_Y);
        maxScroll = Math.max(0, maxScroll);
        scroll = MathHelper.clamp(scroll, 0, maxScroll);
        for (ModuleDisplay module : getModules()) {
            module.mouseScrolled(mouseX, mouseY, amount);
        }
        return true;
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isSelected) {
            for (ModuleDisplay module : getModules()) {
                if (module.mouseClicked(mouseX, mouseY + trackedScroll, button)) {
                    selectedModule = module;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isSelected) {
            for (ModuleDisplay module : getModules()) {
                module.mouseReleased(mouseX, mouseY, button);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean charTyped(char chr, int modifiers) {
        boolean any = false;
        if (isSelected) {
            for (ModuleDisplay module : getModules()) {
                if (module.charTyped(chr, modifiers)) {
                    any = true;
                }
            }
        }
        return any;
    }

    @Override public void mouseMoved(double mouseX, double mouseY) {
        if (isSelected) {
            for (ModuleDisplay module : getModules()) {
                module.mouseMoved(mouseX, mouseY);
            }
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isSelected) {
            for (ModuleDisplay module : getModules()) {
                module.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (isSelected) {
            for (ModuleDisplay module : getModules()) {
                module.keyReleased(keyCode, scanCode, modifiers);
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isSelected) {
            for (ModuleDisplay module : getModules()) {
                module.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.active = Themes.currentActiveTheme.left().brighter();
        this.inactive = Themes.currentActiveTheme.left();
        Color toUse = isOnButton(mouseX, mouseY) ? active : inactive;
        if (isSelected) {
            toUse = Themes.currentActiveTheme.center();
        }
        Renderer.R2D.fill(matrices, toUse, this.x, this.y, this.x + width, this.y + height);
        double centerY = this.y + this.height / 2d;
        int c = Themes.currentActiveTheme.fontColor().getRGB();
        if (!getInstance().searchTerm.isEmpty()) {
            int found = 0;
            for (ModuleDisplay module : getModules()) {
                if (ModuleDisplay.nameMatches(module.text, getInstance().searchTerm)) {
                    found++;
                }
            }
            if (found == 0) {
                c = 0x555555;
            }
            FontRenderers.normal.drawString(matrices, found + "", this.x + this.width - FontRenderers.normal.getStringWidth(found + "") - 5, centerY - FontRenderers.normal.getFontHeight() / 2f, found == 0 ? c : 0xAAFFAA);
        }
        FontRenderers.normal.drawString(matrices, text, this.x + 5, centerY - FontRenderers.normal.getFontHeight() / 2f, c);
        if (isSelected) {
            Renderer.R2D.fill(matrices, Themes.currentActiveTheme.center(), this.x + this.width, SOURCE_Y, this.x + this.width + MODULE_WIDTH, SOURCE_Y + MAX_HEIGHT);
        }
        matrices.push();
        matrices.translate(0, -trackedScroll, 0);
        for (ModuleDisplay module : getModules()) {
            module.isSelected = module.equals(selectedModule);
            if (isSelected) {
                module.render(matrices, mouseX, (int) (mouseY + trackedScroll), delta);
            }
        }
        matrices.pop();
    }
}