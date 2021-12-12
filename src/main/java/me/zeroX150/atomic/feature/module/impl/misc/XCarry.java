package me.zeroX150.atomic.feature.module.impl.misc;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class XCarry extends Module {
    private boolean isInventoryOpen = false;

    public XCarry() {
        super("XCarry", "Allows you to carry items in your crafting grid without having your inventory open", ModuleType.MISC);

        Events.registerEventHandler(EventType.PACKET_SEND, rawEvent -> {
            PacketEvent event = (PacketEvent) rawEvent;

            if (Atomic.client.player != null && event.getPacket() instanceof CloseHandledScreenC2SPacket packet && packet.getSyncId() == Atomic.client.player.playerScreenHandler.syncId && this.isEnabled()) {
                this.isInventoryOpen = true;
                rawEvent.setCancelled(true);
            }
        });
    }

    @Override public void tick() {

    }

    @Override public void enable() {
        this.isInventoryOpen = false;
    }

    @Override public void disable() {
        if (this.isInventoryOpen && Atomic.client.player != null) {
            Atomic.client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(Atomic.client.player.playerScreenHandler.syncId));
        }

        this.isInventoryOpen = false;
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}
