package me.zeroX150.atomic.feature.command.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.gui.screen.AtomicConsoleScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class FakeItem extends Command {
    public FakeItem() {
        super("FakeItem", "fakes a person holding a specific item", "fakeitem");
    }

    @Override public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Atomic.client.world.getPlayers().stream().map(abstractClientPlayerEntity -> abstractClientPlayerEntity.getGameProfile().getName()).collect(Collectors.toList())
                    .toArray(String[]::new);
        } else if (args.length == 2) {
            return new String[]{"hand", "custom:(item id) [item nbt]"};
        } else if (args.length == 3 && args[1].toLowerCase().startsWith("custom:")) {
            return new String[]{"[item nbt]"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) { // no uuid or item
            error("Specify player UUID or player username and item");
            return;
        } else if (args.length == 1) { // no item
            error("You have to specify which item to fake (hand or custom:id).");
            message("Tip: you can also provide additional nbt for the item with custom:id, fakeitem entity custom:minecraft:item {\"nbt\":\"goes here\"}");
            return;
        }
        UUID u = null;
        String nameTarget = null;
        try {
            u = UUID.fromString(args[0]);
        } catch (Exception ignored) {
            nameTarget = args[0];
        }
        PlayerEntity le = null;
        for (Entity entity : Atomic.client.world.getEntities()) {
            if (entity instanceof PlayerEntity le1) {
                if (u != null && entity.getUuid().equals(u)) {
                    le = le1;
                } else if (nameTarget != null && le1.getGameProfile().getName().equals(nameTarget)) {
                    le = le1;
                }
            }
        }
        if (le == null) {
            error("Player not found");
            return;
        }
        if (args[1].equalsIgnoreCase("hand")) {
            ItemStack main = Atomic.client.player.getMainHandStack().copy();
            if (main.isEmpty()) {
                error("You're not holding anything");
                return;
            }
            le.equipStack(EquipmentSlot.MAINHAND, main);
        } else if (args[1].toLowerCase().startsWith("custom:")) {
            String id = args[1].substring("custom:".length());
            Identifier idParsed = Identifier.tryParse(id);
            if (idParsed == null) {
                error("Invalid item");
                return;
            }

            if (!Registry.ITEM.containsId(idParsed)) {
                error("Item not found");
                return;
            }
            Item item = Registry.ITEM.get(idParsed);
            ItemStack stack = new ItemStack(item);
            if (args.length > 2) { // we got additional nbt
                try {
                    stack.setNbt(StringNbtReader.parse(String.join(" ", Arrays.copyOfRange(args, 2, args.length))));
                } catch (CommandSyntaxException e) {
                    error("Invalid NBT: " + e.getContext());
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    for (String s : sw.toString().split("\n")) {
                        AtomicConsoleScreen.instance().log(s, AtomicConsoleScreen.BACKGROUND);
                    }
                    return;
                }
            }
            le.equipStack(EquipmentSlot.MAINHAND, stack);
        }
        success("Faked item until manual update by the entity");
    }
}
