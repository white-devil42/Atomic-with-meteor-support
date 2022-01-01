package me.zeroX150.atomic.mixin.game.screen;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.AtomicConsoleScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class) public class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin() {
        super(Text.of(""));
    }

    @Inject(method = "initWidgets", at = @At("HEAD")) void atomic_addAtomicButton(CallbackInfo ci) {
        ButtonWidget bw = new ButtonWidget(this.width / 2 - 102, this.height / 4 + 144 - 16, 204, 20, Text.of("Atomic console"), button -> Atomic.client.setScreen(AtomicConsoleScreen.instance()));
        this.addDrawableChild(bw);
    }
}
