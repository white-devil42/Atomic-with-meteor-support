/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

public class Effect extends Command {

    public Effect() {
        super("Effect", "gives you an effect client side", "effect", "eff");
    }

    @Override public void onExecute(String[] args) {
        if (Atomic.client.player == null) {
            return;
        }
        if (args.length == 0) {
            Utils.Client.sendMessage("action please");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "give" -> {
                if (args.length < 4) {
                    Utils.Client.sendMessage("effect id, duration and strength pls");
                    return;
                }
                int id = Utils.Math.tryParseInt(args[1], -1);
                if (id == -1) {
                    Utils.Client.sendMessage("idk about that status effect ngl");
                    return;
                }
                int duration = Utils.Math.tryParseInt(args[2], 30);
                int strength = Utils.Math.tryParseInt(args[3], 1);
                StatusEffect effect = StatusEffect.byRawId(id);
                if (effect == null) {
                    Utils.Client.sendMessage("idk about that status effect ngl");
                    return;
                }
                StatusEffectInstance inst = new StatusEffectInstance(effect, duration, strength);
                Atomic.client.player.addStatusEffect(inst);
            }
            case "clear" -> {
                for (StatusEffectInstance statusEffect : Atomic.client.player.getStatusEffects().toArray(new StatusEffectInstance[0])) {
                    Atomic.client.player.removeStatusEffect(statusEffect.getEffectType());
                }
            }
            default -> Utils.Client.sendMessage("\"give\" and \"clear\" only pls");
        }
    }
}
