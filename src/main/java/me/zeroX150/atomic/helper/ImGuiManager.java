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

    public static boolean isInitialized() {
        return init;
    }

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

        ImGui.getStyle().setColor(ImGuiCol.Text, 1.00f, 1.00f, 1.00f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TextDisabled, 0.50f, 0.50f, 0.50f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.WindowBg, 0.07f, 0.10f, 0.10f, 0.94f);
        ImGui.getStyle().setColor(ImGuiCol.ChildBg, 0.09f, 0.11f, 0.12f, 0.94f);
        ImGui.getStyle().setColor(ImGuiCol.PopupBg, 0.05f, 0.07f, 0.08f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.Border, 0.20f, 0.50f, 0.35f, 0.50f);
        ImGui.getStyle().setColor(ImGuiCol.BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBg, 0.09f, 0.13f, 0.15f, 0.94f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBgHovered, 0.12f, 0.18f, 0.20f, 0.94f);
        ImGui.getStyle().setColor(ImGuiCol.FrameBgActive, 0.17f, 0.23f, 0.26f, 0.94f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBg, 0.06f, 0.10f, 0.12f, 0.94f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBgActive, 0.09f, 0.15f, 0.16f, 0.94f);
        ImGui.getStyle().setColor(ImGuiCol.TitleBgCollapsed, 0.03f, 0.05f, 0.06f, 0.94f);
        ImGui.getStyle().setColor(ImGuiCol.MenuBarBg, 0.14f, 0.14f, 0.14f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarBg, 0.07f, 0.12f, 0.13f, 0.94f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrab, 0.20f, 0.50f, 0.35f, 0.50f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrabHovered, 0.28f, 0.54f, 0.41f, 0.50f);
        ImGui.getStyle().setColor(ImGuiCol.ScrollbarGrabActive, 0.20f, 0.50f, 0.28f, 0.50f);
        ImGui.getStyle().setColor(ImGuiCol.CheckMark, 0.26f, 0.59f, 0.98f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.SliderGrab, 0.24f, 0.52f, 0.88f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.SliderGrabActive, 0.26f, 0.59f, 0.98f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.Button, 0.10f, 0.23f, 0.24f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ButtonHovered, 0.12f, 0.30f, 0.31f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ButtonActive, 0.13f, 0.27f, 0.24f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.Header, 0.15f, 0.22f, 0.31f, 0.31f);
        ImGui.getStyle().setColor(ImGuiCol.HeaderHovered, 0.16f, 0.28f, 0.41f, 0.31f);
        ImGui.getStyle().setColor(ImGuiCol.HeaderActive, 0.25f, 0.35f, 0.47f, 0.31f);
        ImGui.getStyle().setColor(ImGuiCol.Separator, 0.43f, 0.43f, 0.50f, 0.50f);
        ImGui.getStyle().setColor(ImGuiCol.SeparatorHovered, 0.10f, 0.40f, 0.75f, 0.78f);
        ImGui.getStyle().setColor(ImGuiCol.SeparatorActive, 0.10f, 0.40f, 0.75f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.ResizeGrip, 0.16f, 0.32f, 0.35f, 0.20f);
        ImGui.getStyle().setColor(ImGuiCol.ResizeGripHovered, 0.13f, 0.34f, 0.39f, 0.20f);
        ImGui.getStyle().setColor(ImGuiCol.ResizeGripActive, 0.16f, 0.35f, 0.33f, 0.20f);
        ImGui.getStyle().setColor(ImGuiCol.Tab, 0.10f, 0.19f, 0.20f, 0.86f);
        ImGui.getStyle().setColor(ImGuiCol.TabHovered, 0.12f, 0.25f, 0.26f, 0.86f);
        ImGui.getStyle().setColor(ImGuiCol.TabActive, 0.12f, 0.26f, 0.24f, 0.86f);
        ImGui.getStyle().setColor(ImGuiCol.TabUnfocused, 0.07f, 0.10f, 0.15f, 0.97f);
        ImGui.getStyle().setColor(ImGuiCol.TabUnfocusedActive, 0.14f, 0.26f, 0.42f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.DockingPreview, 0.26f, 0.59f, 0.98f, 0.70f);
        ImGui.getStyle().setColor(ImGuiCol.DockingEmptyBg, 0.15f, 0.18f, 0.19f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.PlotLines, 0.61f, 0.85f, 0.80f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.PlotHistogram, 0.90f, 0.70f, 0.00f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.PlotHistogramHovered, 1.00f, 0.60f, 0.00f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TableHeaderBg, 0.19f, 0.19f, 0.20f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TableBorderStrong, 0.31f, 0.31f, 0.35f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TableBorderLight, 0.23f, 0.23f, 0.25f, 1.00f);
        ImGui.getStyle().setColor(ImGuiCol.TableRowBg, 0.00f, 0.00f, 0.00f, 0.00f);
        ImGui.getStyle().setColor(ImGuiCol.TableRowBgAlt, 1.00f, 1.00f, 1.00f, 0.06f);
        ImGui.getStyle().setColor(ImGuiCol.TextSelectedBg, 0.26f, 0.98f, 0.61f, 0.35f);
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
