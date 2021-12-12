package me.zeroX150.atomic.feature.gui.screen;

import imgui.ImGui;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.ImGuiManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;


public abstract class ImGuiProxyScreen extends Screen {
    public ImGuiProxyScreen() {
        super(Text.of(""));
        ImGuiManager.init();
    }

    protected abstract void renderInternal();

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // sets the size of the window in case it got resized
        ImGui.getIO().setDisplaySize(Atomic.client.getWindow().getWidth(), Atomic.client.getWindow().getHeight());
        // new frame
        ImGuiManager.getImplGlfw().newFrame();
        ImGui.newFrame();

        renderInternal(); // pass it to homeboy

        // end the frame
        ImGui.endFrame();
        // draw
        ImGui.render();
        ImGuiManager.getImplGl3().renderDrawData(ImGui.getDrawData());
    }
}
