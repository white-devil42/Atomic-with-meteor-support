/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.screen;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.EditServerInfoScreen;
import me.zeroX150.atomic.feature.gui.screen.ProxyManagerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(MultiplayerScreen.class) public abstract class AMultiplayerScreenMixin extends Screen {

    @Shadow protected MultiplayerServerListWidget serverListWidget;
    ButtonWidget editMotd;

    public AMultiplayerScreenMixin() {
        super(Text.of(""));
    }

    @Inject(method = "init", at = @At("HEAD")) public void atomic_preInit(CallbackInfo ci) {
        editMotd = new ButtonWidget(8, height - 28, 100, 20, Text.of("Edit Server"), button -> {
            MultiplayerServerListWidget.ServerEntry se = (MultiplayerServerListWidget.ServerEntry) this.serverListWidget.getSelectedOrNull();
            Atomic.client.setScreen(new EditServerInfoScreen(Objects.requireNonNull(se).getServer(), this));
        });
        addDrawableChild(editMotd);
        ButtonWidget a = new ButtonWidget(5, 5, 100, 20, Text.of("Proxies"), button -> Atomic.client.setScreen(new ProxyManagerScreen(this)));
        addDrawableChild(a);
    }

    @Inject(method = "render", at = @At("HEAD")) public void atomic_preRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MultiplayerServerListWidget.Entry e = this.serverListWidget.getSelectedOrNull();
        editMotd.active = (e instanceof MultiplayerServerListWidget.ServerEntry);
    }
}
