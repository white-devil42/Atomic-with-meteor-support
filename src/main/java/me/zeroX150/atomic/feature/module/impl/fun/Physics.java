package me.zeroX150.atomic.feature.module.impl.fun;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.MessageScreen;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.MouseEvent;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Physics extends Module {
    final MultiValue  mode  = this.config.create("Mode", "Area", "Area", "Click");
    final SliderValue range = this.config.create("Range", 4, 0, 10, 1);
    //    final int range = 4;
    boolean go = false;

    public Physics() {
        super("Physics", "Applies physics to nearby blocks, idea proudly stolen from saturn.", ModuleType.FUN);
        Events.registerEventHandler(EventType.MOUSE_EVENT, event -> {
            if (!this.isEnabled()) {
                return;
            }
            if (Atomic.client.currentScreen != null) {
                return;
            }
            if (!mode.getValue().equalsIgnoreCase("click")) {
                return;
            }
            if (Atomic.client.player == null || Atomic.client.world == null) {
                return;
            }
            MouseEvent me = (MouseEvent) event;
            if (me.getAction() == MouseEvent.MouseEventType.MOUSE_CLICKED && me.getButton() == 0) {
                Vec3d o = Atomic.client.player.raycast(200, 0, false).getPos();
                run(o);
            }
        });
    }

    void run(Vec3d origin) {
        for (Map.Entry<BlockPos, Block> block : getBlocks(origin)) {
            setAir(block.getKey());
            spawnPhysicsObject(block.getKey(), block.getValue());
        }
    }

    @Override public void tick() {
        if (!go) {
            return;
        }
        if (mode.getValue().equalsIgnoreCase("area")) {
            run(Atomic.client.player.getPos());
        }
    }

    void setAir(BlockPos bp) {
        Atomic.client.player.sendChatMessage("/setblock " + bp.getX() + " " + bp.getY() + " " + bp.getZ() + " air");
    }

    void spawnPhysicsObject(BlockPos bp, Block bs) {
        Atomic.client.player.sendChatMessage("/summon falling_block " + bp.getX() + " " + bp.getY() + " " + bp.getZ() + " {BlockState:{Name:\"" + Registry.BLOCK.getId(bs) + "\"},Time:1}");
    }

    @Override public void enable() {
        go = false;
        MessageScreen msg = new MessageScreen(null, "Warning", "You need op for this, and you'll probably get kicked if you dont have permissions. Continue?", t -> {
            if (!t) {
                setEnabled(false);
            } else {
                go = true;
            }
        }, MessageScreen.ScreenType.YESNO);
        Atomic.client.execute(() -> Atomic.client.setScreen(msg));
    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    List<Map.Entry<BlockPos, Block>> getBlocks(Vec3d origin) {
        List<Map.Entry<BlockPos, Block>> bsr = new ArrayList<>();
        for (double ox = -range.getValue(); ox < range.getValue() + 1; ox++) {
            for (double oy = -range.getValue(); oy < range.getValue() + 1; oy++) {
                for (double oz = -range.getValue(); oz < range.getValue() + 1; oz++) {
                    Vec3d actual = origin.add(ox, oy, oz);
                    BlockPos p = new BlockPos(actual);
                    if (Atomic.client.world.getBlockState(p.down()).getMaterial().blocksMovement()) {
                        continue;
                    }
                    BlockState bs = Atomic.client.world.getBlockState(p);
                    if (!bs.isAir() && bs.getFluidState().isEmpty()) {
                        bsr.add(new AbstractMap.SimpleEntry<>(p, bs.getBlock()));
                    }
                }
            }
        }
        return bsr;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        if (mode.getValue().equalsIgnoreCase("click")) {
            Vec3d origin = Atomic.client.player.raycast(200, 0, false).getPos();
            for (Map.Entry<BlockPos, Block> block : getBlocks(origin)) {
                Renderer.R3D.renderOutline(Vec3d.of(block.getKey()), new Vec3d(1, 1, 1), Color.CYAN, matrices);
            }
        }
    }

    @Override public void onHudRender() {

    }
}

