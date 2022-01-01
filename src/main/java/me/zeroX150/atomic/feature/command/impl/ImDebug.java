package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.gui.screen.ImGuiProxyScreen;

public class ImDebug extends Command {
    public ImDebug() {
        super("ImDebug", "Opens the debug window for imgui", "imdebug", "imguidebug");
    }

    @Override public void onExecute(String[] args) {
        ImGuiProxyScreen.imguiDebugWindow = !ImGuiProxyScreen.imguiDebugWindow;
        if (ImGuiProxyScreen.imguiDebugWindow) {
            success("Showing debug window");
        } else {
            message("Hiding debug window");
        }
    }
}
