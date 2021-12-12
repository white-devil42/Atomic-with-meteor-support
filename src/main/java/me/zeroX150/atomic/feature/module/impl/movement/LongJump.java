package me.zeroX150.atomic.feature.module.impl.movement;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LongJump extends Module {

    SliderValue  xz            = (SliderValue) this.config.create("Speed", 5, 0, 20, 2).description("How fast to yeet forwards");
    MultiValue   focus         = (MultiValue) this.config.create("Focus on", "Direction", "Direction", "Velocity").description("What to look at when applying longjump");
    BooleanValue glide         = (BooleanValue) this.config.create("Glide", true).description("Whether or not to glide when falling from a jump");
    SliderValue  glideVelocity = (SliderValue) this.config.create("Glide velocity", 0.05, -0.08, 0.07, 2).description("How much upwards velocity to apply while gliding");
    BooleanValue keepApplying  = (BooleanValue) this.config.create("Keep applying", true).description("Whether or not to keep applying the effect when falling from a jump");
    SliderValue  applyStrength = (SliderValue) this.config.create("Apply strength", 0.3, 0.01, 0.3, 3).description("How strong the effect should be when applying in post");
    boolean      jumped        = false;

    public LongJump() {
        super("LongJump", "Jumps a long distance", ModuleType.MOVEMENT);
        this.config.createPropGroup("Jump", xz, focus);
        this.config.createPropGroup("Post", glide, glideVelocity, keepApplying, applyStrength);
        glideVelocity.showOnlyIf(glide::getValue);
        applyStrength.showOnlyIf(keepApplying::getValue);
    }

    Vec3d getVel() {
        float f = Atomic.client.player.getYaw() * 0.017453292F;
        double scaled = xz.getValue() / 5;
        return switch (focus.getValue()) {
            case "Direction" -> new Vec3d(-MathHelper.sin(f) * scaled, 0.0D, MathHelper.cos(f) * scaled);
            case "Velocity" -> new Vec3d(Atomic.client.player.getVelocity().normalize().x * scaled, 0.0D, Atomic.client.player.getVelocity().normalize().z * scaled);
            default -> new Vec3d(0, 0, 0);
        };
    }

    public void applyLongJumpVelocity() {
        Vec3d v = getVel();
        Atomic.client.player.addVelocity(v.x, v.y, v.z);
        jumped = true;
    }

    @Override public void tick() {
        if (!Atomic.client.options.keyJump.isPressed()) {
            jumped = false;
        }
        if (Atomic.client.player.getVelocity().y < 0 && !Atomic.client.player.isOnGround() && Atomic.client.player.fallDistance > 0 && jumped) {
            if (glide.getValue()) {
                Atomic.client.player.addVelocity(0, glideVelocity.getValue(), 0);
            }
            if (keepApplying.getValue()) {
                Vec3d newVel = getVel();
                newVel = newVel.multiply(applyStrength.getValue());
                Vec3d playerVel = Atomic.client.player.getVelocity();
                Vec3d reformattedVel = new Vec3d(newVel.x, 0, newVel.z);
                reformattedVel = reformattedVel.normalize();
                reformattedVel = new Vec3d(reformattedVel.x, playerVel.y, reformattedVel.z);
                Atomic.client.player.setVelocity(reformattedVel);
                Atomic.client.player.velocityDirty = true;
            }
        } else if (Atomic.client.player.isOnGround()) {
            jumped = false;
        }
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

