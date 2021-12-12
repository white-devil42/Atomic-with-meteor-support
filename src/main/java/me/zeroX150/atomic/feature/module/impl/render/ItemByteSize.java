package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.ByteCounter;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.LoreQueryEvent;
import net.minecraft.client.util.math.MatrixStack;

import java.text.StringCharacterIterator;

public class ItemByteSize extends Module {
    public ItemByteSize() {
        super("ItemByteSize", "Shows the size of an item in bytes on the tooltip", ModuleType.RENDER);
        Events.registerEventHandler(EventType.LORE_QUERY, event -> {
            if (!this.isEnabled()) {
                return;
            }
            LoreQueryEvent e = (LoreQueryEvent) event;
            ByteCounter inst = ByteCounter.instance();
            inst.reset();
            boolean error = false;
            try {
                e.getSource().getOrCreateNbt().write(inst);
            } catch (Exception ignored) {
                error = true;
            }
            long count = inst.getSize();
            String fmt;
            if (error) {
                fmt = "§cError";
            } else {
                fmt = humanReadableByteCountBin(count);
            }
            e.addClientLore("Size: " + fmt);
        });
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        StringCharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
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

