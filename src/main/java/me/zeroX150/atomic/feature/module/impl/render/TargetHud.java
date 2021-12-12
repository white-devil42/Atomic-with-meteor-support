/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.Themes;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.manager.AttackManager;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TargetHud extends Module {

    public static int          modalWidth     = 160;
    public static int          modalHeight    = 70;
    final         BooleanValue renderPing     = (BooleanValue) this.config.create("Render ping", true).description("Shows the ping of the enemy");
    final         BooleanValue renderHP       = (BooleanValue) this.config.create("Render health", true).description("Shows the current HP");
    final         BooleanValue renderMaxHP    = (BooleanValue) this.config.create("Render max health", true).description("Shows the max HP");
    final         BooleanValue renderDistance = (BooleanValue) this.config.create("Render distance", true).description("Shows the distance to the player");
    final         BooleanValue renderLook     = (BooleanValue) this.config.create("Render look", false).description("Shows if the player is looking at you");
    final         BooleanValue renderLoseWin  = (BooleanValue) this.config.create("Render lose / win", true).description("Shows if you're losing or winning, if in battle");
    double wX           = 0;
    double renderWX1    = 0;
    Entity e            = null;
    Entity re           = null;
    double trackedHp    = 0;
    double trackedMaxHp = 0;

    public TargetHud() {
        super("TargetHud", "the bruh", ModuleType.RENDER);
    }

    boolean isApplicable(Entity check) {
        if (check == Atomic.client.player) {
            return false;
        }
        if (check.distanceTo(Atomic.client.player) > 64) {
            return false;
        }
        int l = check.getEntityName().length();
        if (l < 3 || l > 16) {
            return false;
        }
        boolean isValidEntityName = Utils.Players.isPlayerNameValid(check.getEntityName());
        if (!isValidEntityName) {
            return false;
        }
        if (check == Atomic.client.player) {
            return false;
        }
        return check.getType() == EntityType.PLAYER && check instanceof PlayerEntity;
    }

    @Override public void tick() {
        if (AttackManager.getLastAttackInTimeRange() != null) {
            e = AttackManager.getLastAttackInTimeRange();
            return;
        }
        List<Entity> entitiesQueue = StreamSupport.stream(Objects.requireNonNull(Atomic.client.world).getEntities().spliterator(), false).filter(this::isApplicable)
                .sorted(Comparator.comparingDouble(value -> value.getPos().distanceTo(Objects.requireNonNull(Atomic.client.player).getPos()))).collect(Collectors.toList());
        if (entitiesQueue.size() > 0) {
            e = entitiesQueue.get(0);
        } else {
            e = null;
        }
        if (e instanceof LivingEntity ev) {
            if (ev.isDead()) {
                e = null;
            }
        }
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public void onFastTick() {
        renderWX1 = Transitions.transition(renderWX1, wX, 10);
        if (re instanceof LivingEntity e) {
            trackedHp = Transitions.transition(trackedHp, e.getHealth(), 15, 0.002);
            trackedMaxHp = Transitions.transition(trackedMaxHp, e.getMaxHealth(), 15, 0.002);
        }
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
    }

    @Override public void onHudRender() {

    }

    public void draw(MatrixStack stack) {
        if (!this.isEnabled()) {
            return;
        }
        if (e != null) {
            wX = 100;
            re = e;
        } else {
            wX = 0;
        }
        if (re != null) {
            if (!(re instanceof PlayerEntity entity)) {
                return;
            }

            float yOffset = 5;
            double renderWX = renderWX1 / 100d;
            stack.push();
            double rwxI = Math.abs(1 - renderWX);
            double x = rwxI * (modalWidth / 2d);
            double y = rwxI * (modalHeight / 2d);
            stack.translate(x, y, 0);
            stack.scale((float) renderWX, (float) renderWX, 1);
            Renderer.R2D.fill(stack, Renderer.Util.modify(Themes.Theme.ATOMIC.getPalette().left(), -1, -1, -1, 200), 0, 0, modalWidth, modalHeight);
            FontRenderers.normal.drawString(stack, entity.getEntityName(), 40, yOffset, 0xFFFFFF);
            yOffset += FontRenderers.normal.getFontHeight();
            PlayerListEntry ple = Objects.requireNonNull(Atomic.client.getNetworkHandler()).getPlayerListEntry(entity.getUuid());
            if (ple != null && renderPing.getValue()) {
                int ping = ple.getLatency();
                String v = ping + " ms";
                float ww = FontRenderers.normal.getStringWidth(v);
                FontRenderers.normal.drawString(stack, v, modalWidth - ww - 5, 5, 0xFFFFFF);
            }
            float mhealth = (float) trackedMaxHp;
            float health = (float) trackedHp;
            float remainder = health - mhealth;
            if (remainder < 0) {
                remainder = 0;
            }
            float hPer = health / mhealth;
            //hPer = MathHelper.clamp(hPer,0,1);
            double renderToX = modalWidth * hPer;
            renderToX = MathHelper.clamp(renderToX, 0, modalWidth);
            Color GREEN = new Color(100, 255, 20);
            Color RED = new Color(255, 50, 20);
            Color MID_END = Renderer.Util.lerp(GREEN, RED, hPer);
            Renderer.R2D.fillGradientH(stack, RED, MID_END, 0, modalHeight - 2, renderToX, modalHeight);
            if (renderHP.getValue()) {
                FontRenderers.normal.drawString(stack, Utils.Math.roundToDecimal(trackedHp, 2) + " HP", 40, yOffset, MID_END.getRGB());
                yOffset += FontRenderers.normal.getFontHeight();
            }
            if (renderDistance.getValue()) {
                FontRenderers.normal.drawString(stack, Utils.Math.roundToDecimal(entity.getPos().distanceTo(Objects.requireNonNull(Atomic.client.player).getPos()), 1) + " D", 40, yOffset, 0xFFFFFF);
                yOffset += FontRenderers.normal.getFontHeight();
            }
            if (renderMaxHP.getValue()) {
                String t = Utils.Math.roundToDecimal(mhealth, 2) + "";
                if (remainder > 0) {
                    t += "§6 + " + Utils.Math.roundToDecimal(remainder, 1);
                }
                float mhP = FontRenderers.normal.getStringWidth(t);
                FontRenderers.normal.drawString(stack, t, (modalWidth - mhP - 3), (modalHeight - 3 - FontRenderers.normal.getFontHeight()), 0xFFFFFF);
            }

            HitResult bhr = entity.raycast(entity.getPos().distanceTo(Objects.requireNonNull(Atomic.client.player).getPos()), 0f, false);
            if (bhr.getPos().distanceTo(Atomic.client.player.getPos().add(0, 1, 0)) < 1.5 && renderLook.getValue()) {
                FontRenderers.normal.drawString(stack, "Looks at you", 40, yOffset, 0xFFFFFF);
                yOffset += FontRenderers.normal.getFontHeight();
            }

            if (AttackManager.getLastAttackInTimeRange() != null && renderLoseWin.getValue()) {
                String st = entity.getHealth() > Atomic.client.player.getHealth() ? "Losing" : entity.getHealth() == Atomic.client.player.getHealth() ? "Stalemate" : "Winning";
                FontRenderers.normal.drawString(stack, st, 40, yOffset, 0xFFFFFF);
            }

            Text cname = re.getCustomName();
            re.setCustomName(Text.of("DoNotRenderThisUsernamePlease"));
            stack.pop();
            Renderer.R2D.drawEntity((20 * renderWX) + x, (modalHeight - 11) * renderWX + y, renderWX * 27, -10, -10, entity, stack);
            re.setCustomName(cname);
        }
    }

}

