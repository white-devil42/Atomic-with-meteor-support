/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.combat;

import com.google.common.util.concurrent.AtomicDouble;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.Timer;
import me.zeroX150.atomic.helper.manager.AttackManager;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Friends;
import me.zeroX150.atomic.helper.util.Packets;
import me.zeroX150.atomic.helper.util.Rotations;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Killaura extends Module {

    final BooleanValue capRangeAtMax           = (BooleanValue) this.config.create("Max range", true).description("Whether or not to set the range to the max");
    final SliderValue  range                   = (SliderValue) this.config.create("Range", 3.2, 0.1, 7, 2).description("The range to select entities by");
    final BooleanValue automaticDelay          = (BooleanValue) this.config.create("Auto delay", true).description("Whether or not to automatically pick a delay based on your weapon");
    final SliderValue  delay                   = (SliderValue) this.config.create("Delay", 0, 0, 20, 1).description("The delay before attacking");
    final MultiValue   mode                    = (MultiValue) this.config.create("Mode", "Single", "Single", "Multi").description("The mode");
    final SliderValue  multiLimit              = (SliderValue) this.config.create("Targets", 1, 1, 5, 0).description("How many enemies to attack");
    final MultiValue   prio                    = (MultiValue) this.config.create("Priority", "Distance", "Distance", "Health descending", "Health ascending", "Angle")
            .description("What to prioritize when selecting an entity to attack");
    final BooleanValue attackPlayers           = (BooleanValue) this.config.create("Attack players", true).description("Whether or not to attack players");
    final BooleanValue attackHostile           = (BooleanValue) this.config.create("Attack hostile", true).description("Whether or not to attack monsters");
    final BooleanValue attackNeutral           = (BooleanValue) this.config.create("Attack neutral", true).description("Whether or not to attack neutral mobs");
    final BooleanValue attackPassive           = (BooleanValue) this.config.create("Attack passive", true).description("Whether or not to attack animals");
    final BooleanValue attackEverything        = (BooleanValue) this.config.create("Attack everything", true).description("Attack everything that does not apply to previous filters");
    final BooleanValue enableConfuse           = (BooleanValue) this.config.create("Enable Confuse", false).description("Various settings");
    final MultiValue   confuseMode             = this.config.create("Confuse Mode", "TP", "Behind", "TP", "Circle");
    final BooleanValue confuseAllowClip        = (BooleanValue) this.config.create("Move into solid", false).description("Allow confuse to tp into block");
    final BooleanValue attackOnlyCombatPartner = (BooleanValue) this.config.create("Attack combat", true).description("Whether or not to only attack the combat partner (if in combat)");
    final BooleanValue ignoreFriends           = (BooleanValue) this.config.create("Ignore friends", true).description("Whether or not to ignore friends");
    final Timer        delayExec               = new Timer();
    Entity       combatPartner;
    double       circleProg = 0;
    List<Entity> attacks    = new ArrayList<>();

    public Killaura() {
        super("Killaura", "anime", ModuleType.COMBAT);
        range.showOnlyIf(() -> !capRangeAtMax.getValue());
        delay.showOnlyIf(() -> !automaticDelay.getValue());
        capRangeAtMax.showOnlyIf(() -> !attackOnlyCombatPartner.getValue());
        //automaticDelay.showOnlyIf(() -> !attackOnlyCombatPartner.getValue());
        ignoreFriends.showOnlyIf(() -> !attackOnlyCombatPartner.getValue());
        multiLimit.showOnlyIf(() -> mode.getValue().equalsIgnoreCase("multi") && !attackOnlyCombatPartner.getValue());
        mode.showOnlyIf(() -> !attackOnlyCombatPartner.getValue());
        attackPlayers.showOnlyIf(() -> !attackOnlyCombatPartner.getValue());
        attackHostile.showOnlyIf(() -> !attackOnlyCombatPartner.getValue());
        attackNeutral.showOnlyIf(() -> !attackOnlyCombatPartner.getValue());
        attackPassive.showOnlyIf(() -> !attackOnlyCombatPartner.getValue());
        attackEverything.showOnlyIf(() -> !attackOnlyCombatPartner.getValue());
        prio.showOnlyIf(() -> mode.getValue().equalsIgnoreCase("single") && !attackOnlyCombatPartner.getValue());
        enableConfuse.showOnlyIf(() -> mode.getValue().equalsIgnoreCase("single"));
        confuseMode.showOnlyIf(() -> enableConfuse.getValue() && mode.getValue().equalsIgnoreCase("single"));
        confuseAllowClip.showOnlyIf(() -> enableConfuse.getValue() && mode.getValue().equalsIgnoreCase("single"));

        this.config.createPropGroup("Confuse", enableConfuse, confuseMode, confuseAllowClip);
        this.config.createPropGroup("Entities", attackEverything, attackHostile, attackNeutral, attackPassive, attackPlayers, attackOnlyCombatPartner);
    }

    int getDelay() {
        if (Atomic.client.player == null) {
            return 0;
        }
        if (!automaticDelay.getValue()) {
            return (int) (delay.getValue() + 0);
        } else {
            ItemStack hand = Atomic.client.player.getMainHandStack();
            if (hand == null) {
                hand = Atomic.client.player.getOffHandStack();
            }
            if (hand == null) {
                return 10;
            }
            hand.getTooltip(Atomic.client.player, TooltipContext.Default.ADVANCED);
            AtomicDouble speed = new AtomicDouble(Atomic.client.player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED));
            hand.getAttributeModifiers(EquipmentSlot.MAINHAND).forEach((entityAttribute, entityAttributeModifier) -> {
                if (entityAttribute == EntityAttributes.GENERIC_ATTACK_SPEED) {
                    speed.addAndGet(entityAttributeModifier.getValue());
                }
            });
            return (int) (20d / speed.get());
        }
    }

    double getRange() {
        if (Atomic.client.interactionManager == null) {
            return 0;
        }
        if (capRangeAtMax.getValue()) {
            return Atomic.client.interactionManager.getReachDistance();
        } else {
            return range.getValue();
        }
    }

    void doConfuse(Entity e) { // This also contains a range check
        Vec3d updatePos = Atomic.client.player.getPos();
        switch (confuseMode.getValue()) {
            case "Behind" -> {
                Vec3d p = e.getRotationVecClient();
                p = new Vec3d(p.x, 0, p.z).normalize().multiply(1.5);
                updatePos = e.getPos().add(p.multiply(-1));
            }
            case "TP" -> updatePos = new Vec3d(e.getX() + (Math.random() * 4 - 2), e.getY(), e.getZ() + (Math.random() * 4 - 2));
            case "Circle" -> {
                circleProg += 20;
                circleProg %= 360;
                double radians = Math.toRadians(circleProg);
                double sin = Math.sin(radians) * 2;
                double cos = Math.cos(radians) * 2;
                updatePos = new Vec3d(e.getX() + sin, e.getY(), e.getZ() + cos);
            }
        }
        if (!confuseAllowClip.getValue() && Atomic.client.world.getBlockState(new BlockPos(updatePos)).getMaterial().blocksMovement()) {
            return;
        }
        if (e.getPos().distanceTo(updatePos) <= getRange()) {
            Atomic.client.player.updatePosition(updatePos.x, updatePos.y, updatePos.z);
        }
    }

    @Override public void tick() {
        if (Atomic.client.world == null || Atomic.client.player == null || Atomic.client.interactionManager == null) {
            return;
        }
        boolean delayHasPassed = this.delayExec.hasExpired(getDelay() * 50L);
        if (attackOnlyCombatPartner.getValue()) {
            if (AttackManager.getLastAttackInTimeRange() != null) {
                combatPartner = (AttackManager.getLastAttackInTimeRange());
            } else {
                combatPartner = null;
            }
            if (combatPartner == null) {
                return;
            }
            if (!combatPartner.isAttackable()) {
                return;
            }
            if (combatPartner.equals(Atomic.client.player)) {
                return;
            }
            if (!combatPartner.isAlive()) {
                return;
            }
            if (enableConfuse.getValue()) {
                doConfuse(combatPartner);
            }
            if (combatPartner.getPos().distanceTo(Atomic.client.player.getPos()) > getRange()) {
                return;
            }
            Packets.sendServerSideLook(combatPartner.getEyePos());
            Rotations.lookAtV3(combatPartner.getPos().add(0, combatPartner.getHeight() / 2, 0));
            if (delayHasPassed) {
                Atomic.client.interactionManager.attackEntity(Atomic.client.player, combatPartner);
                Atomic.client.player.swingHand(Hand.MAIN_HAND);
                delayExec.reset();
            }
            return;
        }
        attacks.clear();
        for (Entity entity : Objects.requireNonNull(Atomic.client.world).getEntities()) {
            if (attacks.size() > multiLimit.getValue()) {
                break;
            }
            if (!entity.isAttackable()) {
                continue;
            }
            if (entity.equals(Atomic.client.player)) {
                continue;
            }
            if (!entity.isAlive()) {
                continue;
            }
            if (entity.getPos().distanceTo(Atomic.client.player.getPos()) > getRange()) {
                continue;
            }

            if (attackEverything.getValue()) {
                attacks.add(entity);
            } else {
                if (entity instanceof Angerable) {
                    if (((Angerable) entity).getAngryAt() == Atomic.client.player.getUuid()) {
                        if (attackHostile.getValue()) {
                            attacks.add(entity);
                        } else if (attackNeutral.getValue()) {
                            attacks.add(entity);
                        }
                    }
                } else {
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
                    }
                }
            }
        }

        if (ignoreFriends.getValue()) {
            attacks = attacks.stream().filter(entity -> { // true = keep the entity, false = delete
                if (entity instanceof PlayerEntity entity1) {
                    return !Friends.isAFriend(entity1); // if the dude is a friend, return false
                }
                return true;
            }).collect(Collectors.toList());
        }
        if (attacks.isEmpty()) {
            return;
        }
        if (mode.getValue().equalsIgnoreCase("single")) {
            Entity tar = null;
            if (prio.getValue().equalsIgnoreCase("distance")) {
                tar = attacks.stream().sorted(Comparator.comparingDouble(value -> value.getPos().distanceTo(Objects.requireNonNull(Atomic.client.player).getPos()))).collect(Collectors.toList())
                        .get(0);
            } else if (prio.getValue().contains("Health")) { // almost missed this
                // get entity with the least health if mode is ascending, else get most health
                tar = attacks.stream().sorted(Comparator.comparingDouble(value -> {
                    if (value instanceof LivingEntity e) {
                        return e.getHealth() * (prio.getValue().equalsIgnoreCase("health ascending") ? -1 : 1);
                    }
                    return Integer.MAX_VALUE; // not a living entity, discard
                })).collect(Collectors.toList()).get(0);
            } else if (prio.getValue().equalsIgnoreCase("angle")) {
                // get entity in front of you (or closest to the front)
                tar = attacks.stream().sorted(Comparator.comparingDouble(value -> {
                    Vec3d center = value.getBoundingBox().getCenter();
                    double offX = center.x - Atomic.client.player.getX();
                    double offZ = center.z - Atomic.client.player.getZ();
                    float yaw = (float) Math.toDegrees(Math.atan2(offZ, offX)) - 90F;
                    float pitch = (float) -Math.toDegrees(Math.atan2(center.y - Atomic.client.player.getEyeY(), Math.sqrt(offX * offX + offZ * offZ)));
                    return Math.abs(MathHelper.wrapDegrees(yaw - Atomic.client.player.getYaw())) + Math.abs(MathHelper.wrapDegrees(pitch - Atomic.client.player.getPitch()));
                })).sorted(Comparator.comparingDouble(value -> value.getPos().distanceTo(Objects.requireNonNull(Atomic.client.player).getPos()))).collect(Collectors.toList()).get(0);
            }
            if (tar == null) {
                return;
            }
            if (enableConfuse.getValue()) {
                doConfuse(tar);
            }
            if (tar.getPos().distanceTo(Atomic.client.player.getPos()) > getRange()) {
                return;
            }
            Packets.sendServerSideLook(tar.getEyePos());
            Rotations.lookAtV3(tar.getPos().add(0, tar.getHeight() / 2, 0));
            if (delayHasPassed) {
                Atomic.client.interactionManager.attackEntity(Atomic.client.player, tar);
                Atomic.client.player.swingHand(Hand.MAIN_HAND);
                delayExec.reset();
            }
            return;
        }
        for (Entity attack : attacks) {
            Packets.sendServerSideLook(attack.getEyePos());
            Rotations.lookAtV3(attack.getPos().add(0, attack.getHeight() / 2, 0));
            if (delayHasPassed) {
                Atomic.client.interactionManager.attackEntity(Atomic.client.player, attack);
                Atomic.client.player.swingHand(Hand.MAIN_HAND);
                delayExec.reset();
            }
        }
    }


    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        List<String> t = new ArrayList<>();
        t.add("T" + attacks.size());
        t.add("D" + getDelay());
        t.add("R" + getRange());
        t.add(mode.getValue());
        return "[" + String.join(";", t) + "]";
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        if (!attackOnlyCombatPartner.getValue()) {
            return;
        }
        if (combatPartner != null) {
            Vec3d origin = combatPartner.getPos();
            float h = combatPartner.getHeight();
            Renderer.R3D.line(origin, origin.add(0, h, 0), Utils.getCurrentRGB(), matrices);
        }
    }

    @Override public void onHudRender() {

    }
}

