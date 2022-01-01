package me.zeroX150.atomic.feature.gui.hud.element;

import com.mojang.blaze3d.systems.RenderSystem;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.font.FontRenderers;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class Taco extends HudElement {
    public Taco() {
        super("Taco", 0, Atomic.client.getWindow().getScaledHeight(), 100, 100);
    }

    @Override public void renderIntern(MatrixStack stack) {
        if (!me.zeroX150.atomic.feature.command.impl.Taco.config.enabled) {
            return;
        }
        me.zeroX150.atomic.feature.command.impl.Taco.Frame frame = me.zeroX150.atomic.feature.command.impl.Taco.getCurrentFrame();
        if (frame == null) {
            FontRenderers.getMono().drawString(stack, "Nothing to taco", 0, 0, 0xFFFFFF);
            return;
        }
        Identifier current = frame.getI();

        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, current);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        DrawableHelper.drawTexture(stack, 0, 0, 0, 0, 0, (int) width, (int) height, (int) width, (int) height);
    }
}
