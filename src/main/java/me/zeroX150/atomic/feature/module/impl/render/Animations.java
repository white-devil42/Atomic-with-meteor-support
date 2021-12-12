/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Quaternion;

public class Animations extends Module {

    final SliderValue scaleX  = (SliderValue) this.config.create("Scale X", 0.5, 0, 2, 3).description("How much to scale on the X axis");
    final SliderValue scaleY  = (SliderValue) this.config.create("Scale Y", 0.5, 0, 2, 3).description("How much to scale on the Y axis");
    final SliderValue scaleZ  = (SliderValue) this.config.create("Scale Z", 0.5, 0, 2, 3).description("How much to scale on the Z axis");
    final SliderValue rotateX = (SliderValue) this.config.create("Rotate X", 0, 0, 360, 0).description("How much to rotate on the X");
    final SliderValue rotateY = (SliderValue) this.config.create("Rotate Y", 0, 0, 360, 0).description("How much to rotate on the Y");
    final SliderValue rotateZ = (SliderValue) this.config.create("Rotate Z", 0, 0, 360, 0).description("How much to rotate on the Z");
    final SliderValue offsetX = (SliderValue) this.config.create("Offset X", 0, -5, 5, 3).description("How much to offset on the X");
    final SliderValue offsetY = (SliderValue) this.config.create("Offset Y", 0, -5, 5, 3).description("How much to offset on the Y");
    final SliderValue offsetZ = (SliderValue) this.config.create("Offset Z", 0, -5, 5, 3).description("How much to offset on the Z");

    public Animations() {
        super("Animations", "Does a funny when you use an item", ModuleType.RENDER);
        this.config.createPropGroup("Scale", scaleX, scaleY, scaleZ);
        this.config.createPropGroup("Rotation", rotateX, rotateY, rotateZ);
        this.config.createPropGroup("Offset", offsetX, offsetY, offsetZ);
    }

    @Override public void tick() {

    }

    public void applyRotations(LivingEntity le, MatrixStack stack) {
        if (!le.equals(Atomic.client.player) || Atomic.client.gameRenderer.getCamera().isThirdPerson()) {
            return;
        }
        //stack.multiply(new Quaternion(new Vec3f(0,1,0),,true));
        stack.translate(0, (1 - scaleY.getValue()) * .2, 0);
        stack.translate(offsetX.getValue(), offsetY.getValue(), offsetZ.getValue());
        stack.multiply(new Quaternion((float) (rotateX.getValue() + 0), (float) (rotateY.getValue() + 0), (float) (rotateZ.getValue() + 0), true));
        stack.scale((float) (0 + scaleX.getValue()), (float) (0 + scaleY.getValue()), (float) (0 + scaleZ.getValue()));
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

