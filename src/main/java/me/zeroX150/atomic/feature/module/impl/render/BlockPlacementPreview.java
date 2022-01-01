package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BlockPlacementPreview extends Module {
    public BlockPlacementPreview() {
        super("BlockPlacementPreview", "Shows you how a block will look before placing", ModuleType.RENDER);
    }

    @Override public void tick() {

    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        ItemStack s = Atomic.client.player.getInventory().getMainHandStack();
        if (s.isEmpty()) {
            return;
        }
        HitResult hr = Atomic.client.crosshairTarget;
        if (!(hr instanceof BlockHitResult bhr)) {
            return;
        }
        BlockPos where = bhr.getBlockPos();
        Direction side = bhr.getSide();
        // TODO: 18.12.21 finish this 
    }

    @Override public void onHudRender() {

    }
}

