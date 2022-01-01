package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class MassUse extends Module {
    List<Packet<?>> dontRepeat = new ArrayList<>();
    SliderValue     uses       = (SliderValue) this.config.create("Uses", 3, 1, 100, 0).description("How many times to use the item");

    public MassUse() {
        super("MassUse", "Uses an item or block several times", ModuleType.WORLD);
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (dontRepeat.contains(pe.getPacket())) {
                dontRepeat.remove(pe.getPacket());
                return;
            }
            if (pe.getPacket() instanceof PlayerInteractBlockC2SPacket p1) {
                PlayerInteractBlockC2SPacket pp = new PlayerInteractBlockC2SPacket(p1.getHand(), p1.getBlockHitResult());
                for (int i = 0; i < uses.getValue(); i++) {
                    dontRepeat.add(pp);
                    Atomic.client.getNetworkHandler().sendPacket(pp);
                }
            } else if (pe.getPacket() instanceof PlayerInteractItemC2SPacket p1) {
                PlayerInteractItemC2SPacket pp = new PlayerInteractItemC2SPacket(p1.getHand());
                for (int i = 0; i < uses.getValue(); i++) {
                    dontRepeat.add(pp);
                    Atomic.client.getNetworkHandler().sendPacket(pp);
                }
            }
        });
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

    }

    @Override public void onHudRender() {

    }
}

