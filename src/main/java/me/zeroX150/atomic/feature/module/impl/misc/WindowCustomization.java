/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.misc;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.DynamicValue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WindowCustomization extends Module {

    public final DynamicValue<String> title       = this.config.create("Title", "Minecraft sus edition").description("The title of the window (leave blank for default)");
    final        DynamicValue<String> pathToImage = this.config.create("Image", "").description("The path to the window icon (leave blank for default, HAS TO BE 1:1 ASPECT)");

    public WindowCustomization() {
        super("BetterWindow", "Allows you to change aspects of the minecraft window", ModuleType.MISC);
        title.addChangeListener(() -> {
            if (Atomic.client.getWindow() != null) {
                Atomic.client.updateWindowTitle();
            }
        });
        pathToImage.addChangeListener(() -> {
            if (Atomic.client.getWindow() != null) {
                String v = pathToImage.getValue();
                if (v.isEmpty()) {
                    pathToImage.setInvalid(false);
                    setDefaultIcon();
                    return;
                }
                File f = new File(v);
                if (!f.exists() || !f.isFile()) {
                    pathToImage.setInvalid(true);
                } else {
                    pathToImage.setInvalid(false);
                    try {
                        InputStream is = FileUtils.openInputStream(f);
                        Atomic.client.getWindow().setIcon(is, is);
                    } catch (Exception e) {
                        pathToImage.setInvalid(true);
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override public void tick() {
    }

    @Override public void enable() {
        if (Atomic.client.getWindow() != null) {
            Atomic.client.updateWindowTitle();
            String v = pathToImage.getValue();
            if (v.isEmpty()) {
                pathToImage.setInvalid(false);
                setDefaultIcon();
                return;
            }
            File f = new File(v);
            if (!f.exists() || !f.isFile()) {
                pathToImage.setInvalid(true);
            } else {
                pathToImage.setInvalid(false);
                try {
                    FileInputStream is = FileUtils.openInputStream(f);
                    Atomic.client.getWindow().setIcon(is, is);
                    is.close();
                } catch (Exception e) {
                    pathToImage.setInvalid(true);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override public void disable() {
        Atomic.client.updateWindowTitle();
        setDefaultIcon();
    }

    void setDefaultIcon() {
        try {
            InputStream inputStream = Atomic.client.getResourcePackProvider().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("icons/icon_16x16.png"));
            InputStream inputStream2 = Atomic.client.getResourcePackProvider().getPack().open(ResourceType.CLIENT_RESOURCES, new Identifier("icons/icon_32x32.png"));
            Atomic.client.getWindow().setIcon(inputStream, inputStream2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

