package me.zeroX150.atomic.helper;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.ImGuiProxyScreen;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImGuiManager {
    protected static final ImGuiImplGlfw implGlfw = new ImGuiImplGlfw();
    protected static final ImGuiImplGl3  implGl3  = new ImGuiImplGl3();
    private static         boolean       init     = false;

    public static ImGuiImplGl3 getImplGl3() {
        return implGl3;
    }

    public static ImGuiImplGlfw getImplGlfw() {
        return implGlfw;
    }

    private static byte[] getMainFont() {
        try {
            return Files.readAllBytes(Paths.get(ImGuiProxyScreen.class.getClassLoader().getResource("Font.ttf").toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void style() {
        //        ImGui.getStyle().setWindowRounding(6f);
        ImGui.getStyle().setWindowPadding(8, 8);
        ImGui.getStyle().setFramePadding(4, 4);
        ImGui.getStyle().setCellPadding(4, 2);
        ImGui.getStyle().setItemSpacing(8, 4);
        ImGui.getStyle().setItemInnerSpacing(4, 4);
        ImGui.getStyle().setTouchExtraPadding(0, 0);
        ImGui.getStyle().setIndentSpacing(21);
        ImGui.getStyle().setScrollbarSize(10);
        ImGui.getStyle().setGrabMinSize(4);

        ImGui.getStyle().setWindowRounding(6);
        ImGui.getStyle().setChildRounding(6);
        ImGui.getStyle().setFrameRounding(6);
        ImGui.getStyle().setPopupRounding(6);
        ImGui.getStyle().setScrollbarRounding(6);
        ImGui.getStyle().setGrabRounding(6);
        ImGui.getStyle().setLogSliderDeadzone(4);
        ImGui.getStyle().setTabRounding(4);

        ImGui.getStyle().setColor(ImGuiCol.Text, 0.95f, 0.96f, 0.98f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TextDisabled, 0.36f, 0.42f, 0.47f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.WindowBg, 0.11f, 0.15f, 0.17f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ChildBg, 0.15f, 0.18f, 0.22f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.PopupBg, 0.08f, 0.08f, 0.08f, 0.94f);
        ImGui.getStyle().setColor(ImGuiCol.Border, 0.08f, 0.10f, 0.12f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBg, 0.20f, 0.25f, 0.29f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBgHovered, 0.12f, 0.20f, 0.28f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBgActive, 0.09f, 0.12f, 0.14f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBg, 0.09f, 0.12f, 0.14f, 0.65f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBgActive, 0.08f, 0.10f, 0.12f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBgCollapsed, 0.00f, 0.00f, 0.00f, 0.51f);
        ImGui.getStyle().setColor(ImGuiCol.MenuBarBg, 0.15f, 0.18f, 0.22f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarBg, 0.02f, 0.02f, 0.02f, 0.39f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrab, 0.20f, 0.25f, 0.29f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrabHovered, 0.18f, 0.22f, 0.25f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrabActive, 0.09f, 0.21f, 0.31f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.CheckMark, 0.28f, 0.56f, 1.00f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.SliderGrab, 0.28f, 0.56f, 1.00f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.SliderGrabActive, 0.37f, 0.61f, 1.00f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.Button, 0.20f, 0.25f, 0.29f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ButtonHovered, 0.28f, 0.56f, 1.00f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ButtonActive, 0.06f, 0.53f, 0.98f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.Header, 0.20f, 0.25f, 0.29f, 0.55f);
        ImGui.getStyle().setColor(ImGuiCol.HeaderHovered, 0.26f, 0.59f, 0.98f, 0.80f);
        ImGui.getStyle().setColor(ImGuiCol.HeaderActive, 0.26f, 0.59f, 0.98f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.Separator, 0.20f, 0.25f, 0.29f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.SeparatorHovered, 0.10f, 0.40f, 0.75f, 0.78f);
        ImGui.getStyle().setColor(ImGuiCol.SeparatorActive, 0.10f, 0.40f, 0.75f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ResizeGrip, 0.26f, 0.59f, 0.98f, 0.25f);
        ImGui.getStyle().setColor(ImGuiCol.ResizeGripHovered, 0.26f, 0.59f, 0.98f, 0.67f);
        ImGui.getStyle().setColor(ImGuiCol.ResizeGripActive, 0.26f, 0.59f, 0.98f, 0.95f);
        ImGui.getStyle().setColor(ImGuiCol.Tab, 0.11f, 0.15f, 0.17f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TabHovered, 0.26f, 0.59f, 0.98f, 0.80f);
        ImGui.getStyle().setColor(ImGuiCol.TabActive, 0.20f, 0.25f, 0.29f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TabUnfocused, 0.11f, 0.15f, 0.17f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TabUnfocusedActive, 0.11f, 0.15f, 0.17f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.PlotLines, 0.61f, 0.61f, 0.61f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.PlotHistogram, 0.90f, 0.70f, 0.00f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.PlotHistogramHovered, 1.00f, 0.60f, 0.00f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TextSelectedBg, 0.26f, 0.59f, 0.98f, 0.35f);
        ImGui.getStyle().setColor(ImGuiCol.DragDropTarget, 1.00f, 1.00f, 0.00f, 0.90f);
        ImGui.getStyle().setColor(ImGuiCol.NavHighlight, 0.26f, 0.59f, 0.98f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.NavWindowingHighlight, 1.00f, 1.00f, 1.00f, 0.70f);
        ImGui.getStyle().setColor(ImGuiCol.NavWindowingDimBg, 0.80f, 0.80f, 0.80f, 0.20f);
        ImGui.getStyle().setColor(ImGuiCol.ModalWindowDimBg, 0.80f, 0.80f, 0.80f, 0.35f);
    }

    public static void init() {
        if (init) {
            return;
        }

        init = true;
        long win = Atomic.client.getWindow().getHandle();
        ImGui.createContext();
        initFonts();
        style();
        implGlfw.init(win, true);
        implGl3.init();
        ImGui.getIO().setConfigWindowsMoveFromTitleBarOnly(true);
        //        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        ImGui.getStyle().setWindowMenuButtonPosition(-1);
    }

    public static void initFonts() {
        ImGui.getIO().getFonts().addFontFromMemoryTTF(getMainFont(), 18);
    }
}
