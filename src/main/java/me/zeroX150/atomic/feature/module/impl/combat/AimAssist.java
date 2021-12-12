/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.combat;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.manager.AttackManager;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Friends;
import me.zeroX150.atomic.helper.util.Rotations;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AimAssist extends Module {

    final BooleanValue attackPlayers      = (BooleanValue) this.config.create("Aim at players", true).description("Whether or not to aim at players");
    final BooleanValue attackHostile      = (BooleanValue) this.config.create("Aim at hostile", true).description("Whether or not to aim at monsters");
    final BooleanValue attackNeutral      = (BooleanValue) this.config.create("Aim at neutral", true).description("Whether or not to aim at neutral mobs");
    final BooleanValue attackPassive      = (BooleanValue) this.config.create("Aim at passive", true).description("Whether or not to aim at animals");
    final BooleanValue attackEverything   = (BooleanValue) this.config.create("Aim at everything", true).description("Aim at everything that does not apply to previous filters");
    final BooleanValue aimAtCombatPartner = (BooleanValue) this.config.create("Aim at combat", true).description("Whether or not to only aim at the combat partner (if in combat)");
    final MultiValue   prio               = (MultiValue) this.config.create("Priority", "Distance", "Distance", "Health descending", "Health ascending")
            .description("What to prioritize when selecting an entity to aim at");
    final SliderValue  laziness           = (SliderValue) this.config.create("Laziness", 1, 0.1, 5, 1).description("How lazy to be when aiming (bigger = slower aim speed)");
    final BooleanValue aimInstant         = (BooleanValue) this.config.create("Aim instantly", false).description("Aims instantly, instead of smooth transitioning to the target");
    final BooleanValue ignoreFriends      = (BooleanValue) this.config.create("Ignore friends", true).description("Whether or not to ignore friends");
    Entity le;

    public AimAssist() {
        super("AimAssist", "Assists in pvp", ModuleType.COMBAT);
        attackPlayers.showOnlyIf(() -> !aimAtCombatPartner.getValue());
        attackHostile.showOnlyIf(() -> !aimAtCombatPartner.getValue());
        attackNeutral.showOnlyIf(() -> !aimAtCombatPartner.getValue());
        attackPassive.showOnlyIf(() -> !aimAtCombatPartner.getValue());
        attackEverything.showOnlyIf(() -> !aimAtCombatPartner.getValue());
        laziness.showOnlyIf(() -> !aimInstant.getValue());
        this.config.createPropGroup("Targets", attackEverything, attackHostile, attackPlayers, attackNeutral, attackPassive, aimAtCombatPartner, ignoreFriends);
        this.config.createPropGroup("Aim config", prio, laziness, aimInstant);
    }

    @Override public void tick() {
        List<Entity> attacks = new ArrayList<>();
        if (aimAtCombatPartner.getValue()) {
            if (AttackManager.getLastAttackInTimeRange() != null) {
                attacks.add(AttackManager.getLastAttackInTimeRange());
            }
        } else {
            for (Entity entity : Objects.requireNonNull(Atomic.client.world).getEntities()) {
                if (!entity.isAttackable()) {
                    continue;
                }
                if (entity.equals(Atomic.client.player)) {
                    continue;
                }
                if (!entity.isAlive()) {
                    continue;
                }
                if (entity.getPos().distanceTo(Atomic.client.player.getPos()) > Objects.requireNonNull(Atomic.client.interactionManager).getReachDistance()) {
                    continue;
                }
                boolean checked = false;
                if (entity instanceof Angerable) {
                    checked = true;
                    if (attackNeutral.getValue()) {
                        attacks.add(entity);
                    } else {
                        continue;
                    }
                }
                if (entity instanceof PlayerEntity) {
                    if (attackPlayers.getValue()) {
                        attacks.add(entity);
                    }
                } else if (entity instanceof HostileEntity) {
                    if (attackHostile.getValue()) {
                        attacks.add(entity);
                    }
                } else if (entity instanceof PassiveEntity) {
                    if (attackPassive.getValue()) {
                        attacks.add(entity);
                    }
                } else if (attackEverything.getValue() && !checked) {
                    attacks.add(entity);
                }
            }
        }
        if (ignoreFriends.getValue() && !aimAtCombatPartner.getValue()) {
            attacks = attacks.stream().filter(entity -> { // true = keep the entity, false = delete
                if (entity instanceof PlayerEntity entity1) {
                    return !Friends.isAFriend(entity1); // if the dude is a friend, return false
                }
                return true;
            }).collect(Collectors.toList());
        }
        if (attacks.isEmpty()) {
            le = null;
            return;
        }
        if (prio.getValue().equalsIgnoreCase("distance")) {
            le = attacks.stream().sorted(Comparator.comparingDouble(value -> value.getPos().distanceTo(Objects.requireNonNull(Atomic.client.player).getPos()))).collect(Collectors.toList()).get(0);
        } else {
            // get entity with the least health if mode is ascending, else get most health
            le = attacks.stream().sorted(Comparator.comparingDouble(value -> {
                if (value instanceof LivingEntity e) {
                    return e.getHealth() * (prio.getValue().equalsIgnoreCase("health ascending") ? -1 : 1);
                }
                return Integer.MAX_VALUE; // not a living entity, discard
            })).collect(Collectors.toList()).get(0);
        }

    }

    @Override public void onFastTick() {
        aimAtTarget();
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    void aimAtTarget() {
        if (!aimInstant.getValue()) {
            Rotations.lookAtPositionSmooth(le.getPos().add(0, le.getHeight() / 2d, 0), laziness.getValue());
        } else {
            Vec2f py = Rotations.getPitchYaw(le.getPos().add(0, le.getHeight() / 2d, 0));
            Objects.requireNonNull(Atomic.client.player).setPitch(py.x);
            Atomic.client.player.setYaw(py.y);
        }
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        if (le != null) {
            Vec3d origin = le.getPos();
            float h = le.getHeight();
            Renderer.R3D.line(origin, origin.add(0, h, 0), Utils.getCurrentRGB(), matrices);
        }
    }

    @Override public void onHudRender() {

    }
}

