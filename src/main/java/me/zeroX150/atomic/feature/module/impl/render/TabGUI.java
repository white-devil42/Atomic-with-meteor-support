/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.KeyboardEvent;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabGUI extends Module {

    public static final double                    mheight  = 12;
    final               Map<ModuleType, Double[]> animProg = new HashMap<>();
    final               Map<Module, Double[]>     bruh     = new HashMap<>();
    boolean expanded              = false;
    int     selectedModule        = 0;
    double  trackedSelectedModule = 0;
    int     fixedSelected         = 0;
    double  aprog                 = 0;
    double  anim                  = 0;
    double  mwidth                = 60;
    int     selected              = 0;
    double  trackedSelected       = 0;

    public TabGUI() {
        super("TabGUI", "Renders a small module manager top left", ModuleType.RENDER);
        Events.registerEventHandler(EventType.KEYBOARD, event -> {
            if (!this.isEnabled()) {
                return;
            }
            KeyboardEvent ke = (KeyboardEvent) event;
            if (ke.getType() != 1 && ke.getType() != 2) {
                return;
            }
            int kc = ke.getKeycode();
            if (!expanded) {
                if (kc == GLFW.GLFW_KEY_DOWN) {
                    selected++;
                } else if (kc == GLFW.GLFW_KEY_UP) {
                    selected--;
                } else if (kc == GLFW.GLFW_KEY_RIGHT && aprog <= .5) {
                    fixedSelected = selected;
                    expanded = true;
                }
            } else {
                if (kc == GLFW.GLFW_KEY_DOWN) {
                    selectedModule++;
                } else if (kc == GLFW.GLFW_KEY_UP) {
                    selectedModule--;
                } else if (kc == GLFW.GLFW_KEY_LEFT) {
                    expanded = false;
                } else if (kc == GLFW.GLFW_KEY_RIGHT || kc == GLFW.GLFW_KEY_ENTER) {
                    ModuleType t = getModulesForDisplay()[fixedSelected];
                    List<Module> v = new ArrayList<>();
                    for (Module module : ModuleRegistry.getModules()) {
                        if (module.getModuleType() == t) {
                            v.add(module);
                        }
                    }
                    v.get(selectedModule).toggle();
                }
            }
            selected = clampRevert(selected, getModulesForDisplay().length);
            if (expanded) {
                int mcCurrentCategory = 0;
                for (Module module : ModuleRegistry.getModules()) {
                    if (module.getModuleType() == getModulesForDisplay()[selected]) {
                        mcCurrentCategory++;
                    }
                }
                selectedModule = clampRevert(selectedModule, mcCurrentCategory);
            } else {
                selectedModule = 0;
            }
        });
    }

    public static ModuleType[] getModulesForDisplay() {
        return Arrays.stream(ModuleType.values()).filter(moduleType -> moduleType != ModuleType.HIDDEN)
                .sorted(Comparator.comparingDouble(value -> -FontRenderers.getNormal().getStringWidth(value.getName()))).toArray(ModuleType[]::new);
    }

    int clampRevert(int n, int max) {
        if (n < 0) {
            n = max - 1;
        } else if (n >= max) {
            n = 0;
        }
        return n;
    }

    @Override public void tick() {

    }

    @Override public void enable() {
        for (ModuleType moduleType : getModulesForDisplay()) {
            if (!animProg.containsKey(moduleType)) {
                animProg.put(moduleType, new Double[]{0d, 0d});
            }
        }
        for (Module module : ModuleRegistry.getModules()) {
            if (!bruh.containsKey(module)) {
                bruh.put(module, new Double[]{0d, 0d});
            }
        }
    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onFastTick() {
        trackedSelected = Transitions.transition(trackedSelected, selected, 5, 0.0001);
        trackedSelectedModule = Transitions.transition(trackedSelectedModule, selectedModule, 5, 0.0001);
        aprog = Transitions.transition(aprog, anim, 8, 0.0001);

        for (Map.Entry<ModuleType, Double[]> moduleTypeEntry : this.animProg.entrySet()) {
            moduleTypeEntry.getValue()[1] = Transitions.transition(moduleTypeEntry.getValue()[1], moduleTypeEntry.getValue()[0], 5, 0);
        }
        for (Map.Entry<Module, Double[]> moduleTypeEntry : this.bruh.entrySet()) {
            moduleTypeEntry.getValue()[1] = Transitions.transition(moduleTypeEntry.getValue()[1], moduleTypeEntry.getValue()[0], 5, 0);
        }

    }

    @Override public void onHudRender() {
        render(Renderer.R3D.getEmptyMatrixStack(), 5, 5);
    }

    public void render(MatrixStack stack, double x, double y) {
        Color bg = new Color(0, 0, 0, 255);
        Color active = Utils.getCurrentRGB();
        mwidth = 6 + FontRenderers.getNormal().getStringWidth(getModulesForDisplay()[0].getName()) + 20; // types sorted, so 0 will be the longest
        double yOffset = 0;
        int index = 0;
        Renderer.R2D.fill(stack, bg, x, y, x + mwidth, y + (mheight * getModulesForDisplay().length));
        double selectedOffset = mheight * trackedSelected;
        Renderer.R2D.fill(stack, active, x, y + selectedOffset, x + MathHelper.lerp(MathHelper.clamp(aprog * 2, 0, 1), 1, mwidth), y + selectedOffset + mheight);
        for (ModuleType value : getModulesForDisplay()) {
            int c = 0xFFFFFF;
            if (aprog != 0 && index != selected) {
                c = Renderer.Util.lerp(new Color(0xAA, 0xAA, 0xAA), Color.WHITE, aprog).getRGB();
            }
            //Renderer.R2D.fill(bg, x, y + yOffset, x + mwidth, y + yOffset + mheight);
            if (value == getModulesForDisplay()[selected]) {
                animProg.getOrDefault(value, new Double[]{0d, 0d})[0] = 1d;
            } else {
                animProg.getOrDefault(value, new Double[]{0d, 0d})[0] = 0d;
            }
            FontRenderers.getNormal().drawString(stack, value.getName(), x + MathHelper.lerp(animProg.getOrDefault(value, new Double[]{0d, 0d})[1], 2, mwidth / 2f - FontRenderers.getNormal()
                    .getStringWidth(value.getName()) / 2f), y + yOffset + (mheight - FontRenderers.getNormal().getFontHeight()) / 2f, c);
            yOffset += mheight;
            index++;
        }

        if (expanded) {
            anim = 1;
        } else {
            anim = 0;
        }
        ModuleType t = getModulesForDisplay()[fixedSelected];
        List<Module> a = new ArrayList<>();
        for (Module module : ModuleRegistry.getModules()) {
            if (module.getModuleType() == t) {
                a.add(module);
            }
        }
        double rx = x + mwidth + 3;
        double ry = y + mheight * fixedSelected;
        int yoff = 0;
        double w = FontRenderers.getNormal().getStringWidth(a.get(0).getName()) + 4 + 20;
        Renderer.R2D.scissor(rx - 3, ry, w + 3, mheight * a.size());
        rx -= (w + 3) * (1 - (MathHelper.clamp(aprog * 2, 1, 2) - 1));
        for (Module module : a) {
            Renderer.R2D.fill(stack, bg, rx, ry + yoff, rx + w, ry + yoff + mheight);
            if (module.isEnabled()) {
                Renderer.R2D.fill(stack, Color.WHITE, rx + w, ry + yoff, rx + w - 1, ry + yoff + mheight);
            }
            if (module == a.get(selectedModule)) {
                bruh.getOrDefault(module, new Double[]{0d, 0d})[0] = 1d;
            } else {
                bruh.getOrDefault(module, new Double[]{0d, 0d})[0] = 0d;
            }
            FontRenderers.getNormal().drawString(stack, module.getName(), rx + MathHelper.lerp(bruh.getOrDefault(module, new Double[]{0d, 0d})[1], 2, w / 2f - FontRenderers.getNormal()
                    .getStringWidth(module.getName()) / 2f), ry + yoff + (mheight - FontRenderers.getNormal().getFontHeight()) / 2f, 0xFFFFFF);
            yoff += mheight;
        }
        double selectedOffset1 = mheight * trackedSelectedModule;
        Renderer.R2D.fill(stack, active, rx, ry + selectedOffset1, rx + 1, ry + selectedOffset1 + mheight);
        Renderer.R2D.unscissor();
    }
}
