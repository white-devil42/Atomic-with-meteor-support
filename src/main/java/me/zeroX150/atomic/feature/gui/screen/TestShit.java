package me.zeroX150.atomic.feature.gui.screen;

import imgui.ImGui;
import imgui.flag.ImGuiKey;
import imgui.type.ImString;

public class TestShit extends ImGuiProxyScreen {
    ImString a              = new ImString();
    boolean  previousActive = false;

    @Override protected void renderInternal() {
        ImGui.begin("shit");
        ImGui.inputText("a", a);
        if (previousActive && !ImGui.isItemActive() && ImGui.isKeyPressed(ImGui.getIO().getKeyMap(ImGuiKey.Enter))) {
            System.out.println(a.get());
            ImGui.setKeyboardFocusHere(-1);
        }
        previousActive = ImGui.isItemActive();
        ImGui.text(a.get());
        ImGui.end();
    }
}
