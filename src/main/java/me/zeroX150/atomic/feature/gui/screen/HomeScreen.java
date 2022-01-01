/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen;
import me.zeroX150.atomic.feature.gui.particles.FlowParticleManager;
import me.zeroX150.atomic.feature.module.impl.client.ClientConfig;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HomeScreen extends Screen {

    final FlowParticleManager pm = new FlowParticleManager(100);
    String t;

    public HomeScreen() {
        super(Text.of("a"));
        InputStream is = HomeScreen.class.getClassLoader().getResourceAsStream("VERSION");
        try {
            String version = IOUtils.toString(Objects.requireNonNull(is), StandardCharsets.UTF_8);
            t = "Atomic b" + version + ", made by ";
        } catch (Exception ignored) {
            t = "Atomic (build unknown), made by ";
        }
    }

    @Override protected void init() {
        addDrawableChild(createCentered("Singleplayer", height / 2 - 20 - 20 - 10, button -> Atomic.client.setScreen(new SelectWorldScreen(this))));
        addDrawableChild(createCentered("Multiplayer", height / 2 - 25, button -> Atomic.client.setScreen(new MultiplayerScreen(this))));
        addDrawableChild(createCentered("Realms", height / 2, button -> Atomic.client.setScreen(new RealmsMainScreen(this))));
        addDrawableChild(new ButtonWidget(width / 2 - 75, height / 2 + 25, 70, 20, Text.of("Options"), button -> Atomic.client.setScreen(new OptionsScreen(this, Atomic.client.options))));
        addDrawableChild(new ButtonWidget(width / 2 + 5, height / 2 + 25, 70, 20, Text.of("Quit"), button -> Atomic.client.stop()));
        addDrawableChild(new ButtonWidget(width / 2 - (150 / 2), height / 2 + 25 + 25, 150, 20, Text.of("Alts"), button -> Atomic.client.setScreen(NewAltManagerScreen.instance())));
        addDrawableChild(new ButtonWidget(1, 1, 130, 20, Text.of("Vanilla home screen"), button -> {
            ClientConfig.customMainMenu.setValue(false);
            Atomic.client.setScreen(null);

            //Test.real();
        }));
        super.init();
    }

    @Override public void tick() {
        pm.tick();
        super.tick();
    }

    @Override public void resize(MinecraftClient client, int width, int height) {
        pm.remake();
        super.resize(client, width, height);
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(0);
        pm.render();
        double logoSize = 0.3;
        RenderSystem.setShaderTexture(0, ClickGUIScreen.LOGO);
        RenderSystem.enableBlend();
        RenderSystem.blendEquation(32774);
        RenderSystem.blendFunc(770, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        drawTexture(matrices, (int) (width / 2 - (504 * logoSize / 2)), 10, 0, 0, 0, (int) (504 * logoSize), (int) (130 * logoSize), (int) (504 * logoSize), (int) (130 * logoSize));
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        FontRenderers.getNormal().drawString(matrices, t, 1, height - FontRenderers.getNormal().getFontHeight(), 0xFFFFFF);
        FontRenderers.getNormal().drawString(matrices, "0x150", 1 + FontRenderers.getNormal().getStringWidth(t), height - FontRenderers.getNormal().getFontHeight(), Utils.getCurrentRGB().getRGB());
        super.render(matrices, mouseX, mouseY, delta);
    }

    ButtonWidget createCentered(String t, int y, ButtonWidget.PressAction action) {
        return new ButtonWidget(width / 2 - (150 / 2), y, 150, 20, Text.of(t), action);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float width = FontRenderers.getNormal().getStringWidth(t);
        float mwidth = width + FontRenderers.getNormal().getStringWidth("0x150");
        float h = height - 10;
        float m = height - 1;
        if (mouseX >= width && mouseX <= mwidth && mouseY >= h && mouseY <= m) {
            Util.getOperatingSystem().open("https://0x150.cf");
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
