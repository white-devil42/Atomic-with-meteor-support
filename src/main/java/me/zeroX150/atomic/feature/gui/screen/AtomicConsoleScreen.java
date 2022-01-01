package me.zeroX150.atomic.feature.gui.screen;

import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import imgui.type.ImString;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.command.CommandRegistry;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AtomicConsoleScreen extends ImGuiProxyScreen {
    public static final Color               ERROR      = new Color(214, 93, 62);
    public static final Color               SUCCESS    = new Color(65, 217, 101);
    public static final Color               DEFAULT    = Color.WHITE;
    public static final Color               CLIENT     = new Color(61, 173, 217);
    public static final Color               BACKGROUND = new Color(80, 99, 107);
    static              AtomicConsoleScreen inst       = null;
    final               List<LogEntry>      logs       = new ArrayList<>();
    ImString current     = new ImString();
    boolean  focusBefore = false;

    private AtomicConsoleScreen() {

    }

    public static AtomicConsoleScreen instance() {
        if (inst == null) {
            inst = new AtomicConsoleScreen();
        }
        return inst;
    }

    public void log(String t, Color c) {
        logs.add(new LogEntry(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, t));
    }

    @Override protected void renderInternal() {
        current.inputData.isResizable = true;
        ImGui.setNextWindowSizeConstraints(300, 200, 800, 500);
        ImGui.begin("Console");
        ImGui.pushItemWidth(-1);

        if (ImGui.beginChild("ScrollRegion##", 0, -(ImGui.getStyle().getItemSpacingY() + ImGui.getFrameHeightWithSpacing()), false)) {
            ImGui.pushTextWrapPos();
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4, 0);
            for (LogEntry log : logs) {
                ImGui.pushStyleColor(ImGuiCol.Text, log.r, log.g, log.b, 1f);
                ImGui.textUnformatted(" " + log.text);
                ImGui.popStyleColor();
            }
            ImGui.popStyleVar();
            ImGui.popTextWrapPos();
            if (ImGui.getScrollY() >= ImGui.getScrollMaxY()) {
                ImGui.setScrollHereY(1f);
            }
            ImGui.endChild();
        }
        boolean reclaimFocus = false;
        ImGui.inputText("", current);
        if (focusBefore && !ImGui.isItemActive() && ImGui.isKeyPressed(ImGui.getIO().getKeyMap(ImGuiKey.Enter))) {
            String command = current.get();
            if (!command.isEmpty()) {
                CommandRegistry.execute(command);
            }
            current.set("");
            reclaimFocus = true;
        }
        focusBefore = ImGui.isItemActive();

        ImGui.popItemWidth();
        if (reclaimFocus) {
            ImGui.setKeyboardFocusHere(-1);
        }
        String cmd = current.get();
        if (!cmd.isEmpty() && !getSuggestions(cmd).isEmpty()) {
            List<String> s = getSuggestions(cmd);
            ImVec2 c = ImGui.getWindowPos();
            c.y += ImGui.getWindowHeight() + ImGui.getStyle().getWindowPaddingY();
            ImGui.setNextWindowSizeConstraints(0, 0, 200, 170);
            ImGui.begin("cmdSuggestions", ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoFocusOnAppearing);

            ImGui.setWindowPos(c.x, c.y);
            for (String suggestion : s) {
                ImGui.text(suggestion);
            }
            ImGui.end();
        }
        ImGui.end();

    }

    List<String> getSuggestions(String command) {
        List<String> a = new ArrayList<>();
        String[] args = command.split(" +");
        String cmd = args[0].toLowerCase();
        args = Arrays.copyOfRange(args, 1, args.length);
        if (command.endsWith(" ")) { // append empty arg when we end with a space
            String[] args1 = new String[args.length + 1];
            System.arraycopy(args, 0, args1, 0, args.length);
            args1[args1.length - 1] = "";
            args = args1;
        }
        if (args.length > 0) {
            Command c = CommandRegistry.getByAlias(cmd);
            if (c != null) {
                a = List.of(c.getSuggestions(command, args));
            } else {
                return new ArrayList<>(); // we have no command to ask -> we have no suggestions
            }
        } else {
            for (Command command1 : CommandRegistry.getCommands()) {
                for (String alias : command1.getAliases()) {
                    if (alias.toLowerCase().startsWith(cmd.toLowerCase())) {
                        a.add(alias);
                    }
                }
            }
        }
        String[] finalArgs = args;
        return finalArgs.length > 0 ? a.stream().filter(s -> s.toLowerCase().startsWith(finalArgs[finalArgs.length - 1].toLowerCase())).collect(Collectors.toList()) : a;
    }


    @Override public boolean isPauseScreen() {
        return false;
    }

    record LogEntry(float r, float g, float b, String text) {
    }
}
