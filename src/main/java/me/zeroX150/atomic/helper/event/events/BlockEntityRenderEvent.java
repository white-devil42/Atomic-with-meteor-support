/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.event.events;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.MatrixStack;

@SuppressWarnings("unused") public class BlockEntityRenderEvent extends RenderEvent {

    final BlockEntity entity;

    public BlockEntityRenderEvent(MatrixStack stack, BlockEntity entity) {
        super(stack);
        this.entity = entity;
    }

    public BlockEntity getBlockEntity() {
        return entity;
    }
}
