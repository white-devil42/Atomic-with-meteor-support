/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.util;

import me.zeroX150.atomic.Atomic;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class Packets {

    public static void sendServerSideLook(Vec3d target1) {
        double vec = 57.2957763671875;
        Vec3d target = target1.subtract(Objects.requireNonNull(Atomic.client.player).getEyePos());
        double square = Math.sqrt(target.x * target.x + target.z * target.z);
        float pitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(target.y, square) * vec)));
        float yaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(target.z, target.x) * vec) - 90.0F);
        PlayerMoveC2SPacket p = new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, Atomic.client.player.isOnGround());
        Objects.requireNonNull(Atomic.client.getNetworkHandler()).sendPacket(p);
    }
}
