/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.screen;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.Themes;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;

public class ProxyManagerScreen extends Screen implements FastTickable {

    public static Proxy   currentProxy = null;
    static        boolean isSocks4     = true;
    final         Screen  parent;
    TextFieldWidget actualProxy;
    TextFieldWidget username;
    TextFieldWidget password;
    double          animProg    = 0;
    boolean         shouldClose = false;

    public ProxyManagerScreen(Screen parent) {
        super(Text.of(""));
        this.parent = parent;
    }

    boolean isValid(String n) {
        String[] split = n.split(":");
        if (split.length != 2) {
            return false;
        }
        if (!StringUtils.isNumeric(split[1])) {
            return false;
        }
        int port = Integer.parseInt(split[1]);
        return port >= 1 && port <= 0xFFFF;
    }

    int getW() {
        return (int) Math.floor(width / 2d);
    }

    int getH() {
        return (int) Math.floor(height / 2d);
    }

    @Override public void onClose() {
        //Atomic.client.openScreen(parent);
        shouldClose = true;
    }

    @Override protected void init() {
        shouldClose = false;
        actualProxy = new TextFieldWidget(textRenderer, getW() - 90, getH() - 55, 180, 20, Text.of("SPECIAL:Proxy IP:PORT"));
        actualProxy.setMaxLength(100);
        addDrawableChild(actualProxy);
        username = new TextFieldWidget(textRenderer, getW() - 90, getH() - 30, 180, 20, Text.of("SPECIAL:Username"));
        username.setMaxLength(100);
        addDrawableChild(username);
        password = new TextFieldWidget(textRenderer, getW() - 90, getH() - 5, 180, 20, Text.of("SPECIAL:Password"));
        password.setMaxLength(100);
        addDrawableChild(password);
        if (currentProxy != null) {
            actualProxy.setText(currentProxy.ipPort);
            username.setText(currentProxy.username);
            password.setText(currentProxy.password);
        }
        ButtonWidget type = new ButtonWidget(getW() - 90, getH() + 20, 180, 20, Text.of("Type: " + (isSocks4 ? "SOCKS4" : "SOCKS5")), button -> {
            isSocks4 = !isSocks4;
            button.setMessage(Text.of("Type: " + (isSocks4 ? "SOCKS4" : "SOCKS5")));
            password.setEditable(!isSocks4);
        });
        password.setEditable(!isSocks4);
        addDrawableChild(type);
        ButtonWidget check = new ButtonWidget(getW() + 35, getH() + 45, 55, 20, Text.of("OK"), button -> {
            boolean validProxy = isValid(actualProxy.getText());
            if (!validProxy) {
                actualProxy.setEditableColor(new Color(255, 20, 20).getRGB());
                return;
            } else {
                actualProxy.setEditableColor(0xFFFFFF);
            }
            currentProxy = new Proxy(isSocks4, actualProxy.getText(), username.getText(), password.getText());
        });
        addDrawableChild(check);
        ButtonWidget back = new ButtonWidget(getW() - 27, getH() + 45, 55, 20, Text.of("Back"), button -> onClose());
        addDrawableChild(back);
        ButtonWidget cancel = new ButtonWidget(getW() - 90, getH() + 45, 55, 20, Text.of("Reset"), button -> currentProxy = null);
        addDrawableChild(cancel);

    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (shouldClose && animProg == 0) {
            Atomic.client.setScreen(parent);
            return;
        }

        parent.render(matrices, -1, -1, delta);

        double m = Transitions.easeOutExpo(animProg);
        double mr = 1 - m;
        double pw = getW() * mr;
        double ph = getH() * mr;
        matrices.translate(pw, ph, 0);
        matrices.scale((float) m, (float) m, 1);
        DrawableHelper.fill(Renderer.R3D.getEmptyMatrixStack(), 0, 0, width, height, new Color(0, 0, 0, (int) (animProg * 50)).getRGB());
        DrawableHelper.fill(matrices, getW() - 100, getH() - 75, getW() + 100, getH() + 75, Renderer.Util.modify(Themes.currentActiveTheme.center(), -1, -1, -1, 170).getRGB());

        FontRenderers.getNormal().drawCenteredString(matrices, "Proxy manager" + (currentProxy == null ? "" : " (Using proxy)"), getW(), getH() - 70, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(0, 0, 0);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public void onFastTick() {
        double a = 0.04;
        if (shouldClose) {
            a *= -1;
        }
        animProg += a;
        animProg = MathHelper.clamp(animProg, 0, 1);
    }

    public static class Proxy {

        public final String    ipPort;
        public final ProxyType type;
        public final String    username;
        public final String    password;

        public Proxy(boolean isSocks4, String ipPort, String username, String password) {
            this.type = isSocks4 ? ProxyType.SOCKS4 : ProxyType.SOCKS5;
            this.ipPort = ipPort;
            this.username = username.isEmpty() ? null : username;
            this.password = password.isEmpty() ? null : password;
        }

        public int getPort() {
            return Integer.parseInt(ipPort.split(":")[1]);
        }

        public String getIp() {
            return ipPort.split(":")[0];
        }

        public enum ProxyType {
            SOCKS4, SOCKS5
        }
    }
}
