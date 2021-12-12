/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.render;

import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.render.ESP;
import me.zeroX150.atomic.feature.module.impl.render.NoRender;
import me.zeroX150.atomic.helper.util.Friends;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;
import java.util.Objects;

@Mixin(WorldRenderer.class) public class WorldRendererMixin {

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    public void atomic_cancelWeatherRender(LightmapTextureManager manager, float f, double d, double e, double g, CallbackInfo ci) {
        if (ModuleRegistry.getByClass(NoRender.class).isEnabled() && NoRender.weather.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderEntity", at = @At("HEAD"))
    void atomic_overwriteShaderOutlineColor(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        ESP e = ModuleRegistry.getByClass(ESP.class);
        if (Objects.requireNonNull(e).isEnabled() && vertexConsumers instanceof OutlineVertexConsumerProvider provider && e.shouldRenderEntity(entity) && e.outlineMode.getValue()
                .equalsIgnoreCase("shader")) {
            Color c = Utils.getCurrentRGB();
            if (entity instanceof PlayerEntity pe && Friends.isAFriend(pe)) {
                c = new Color(100, 255, 20);
            }
            provider.setColor(c.getRed(), c.getGreen(), c.getBlue(), 255);
        }
    }
}
