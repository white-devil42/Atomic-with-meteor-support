package me.zeroX150.atomic.feature.gui.screen;

import imgui.ImGui;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.ImGuiManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;


public abstract class ImGuiProxyScreen extends Screen {
    public static boolean imguiDebugWindow = false;
    boolean closed    = false;
    boolean closedAck = false;

    public ImGuiProxyScreen() {
        super(Text.of(""));
        ImGuiManager.init();
    }

    protected abstract void renderInternal();

    @Override protected void init() {
        closed = closedAck = false;
    }

    @Override public void onClose() {
        closed = true;
        //        super.onClose();
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (closed && closedAck) {
            super.onClose();
            return;
        }
        // sets the size of the window in case it got resized
        ImGui.getIO().setDisplaySize(Atomic.client.getWindow().getWidth(), Atomic.client.getWindow().getHeight());
        // new frame
        ImGuiManager.getImplGlfw().newFrame();
        ImGui.newFrame();

        if (!closed) { // render empty frame when closed
            if (imguiDebugWindow) {
                ImGui.showMetricsWindow(); // show debug window on all imgui screens if we wish to
            }

            renderInternal(); // pass it to homeboy
        } else {
            closedAck = true;
        }

        // end the frame
        ImGui.endFrame();
        // draw
        ImGui.render();
        ImGuiManager.getImplGl3().renderDrawData(ImGui.getDrawData());
    }
}
