package me.zeroX150.atomic.helper.font;

import me.zeroX150.atomic.feature.module.impl.client.ClientConfig;
import me.zeroX150.atomic.helper.font.adapter.FontAdapter;
import me.zeroX150.atomic.helper.font.adapter.impl.ClientFontRenderer;
import me.zeroX150.atomic.helper.font.render.GlyphPageFontRenderer;

import java.util.ArrayList;
import java.util.List;

public class FontRenderers {
    private static final List<ClientFontRenderer> fontRenderers = new ArrayList<>();
    private static       FontAdapter              normal, title, mono, vanilla;

    public static FontAdapter getNormal() {
        if (ClientConfig.fontRenderer.getValue().equalsIgnoreCase("client set")) {
            return normal;
        } else {
            return vanilla;
        }
    }

    public static void setNormal(FontAdapter normal) {
        FontRenderers.normal = normal;
    }

    public static ClientFontRenderer getCustomNormal(int size) {
        for (ClientFontRenderer fontRenderer : fontRenderers) {
            if (fontRenderer.getSize() == size) {
                return fontRenderer;
            }
        }
        ClientFontRenderer cfr = new ClientFontRenderer(GlyphPageFontRenderer.createFromID("Font.ttf", size, false, false, false));
        fontRenderers.add(cfr);
        return cfr;
    }

    public static FontAdapter getMono() {
        if (ClientConfig.fontRenderer.getValue().equalsIgnoreCase("client set")) {
            return mono;
        } else {
            return vanilla;
        }
    }

    public static void setMono(FontAdapter mono) {
        FontRenderers.mono = mono;
    }

    public static FontAdapter getTitle() {
        if (ClientConfig.fontRenderer.getValue().equalsIgnoreCase("client set")) {
            return title;
        } else {
            return vanilla;
        }
    }

    public static void setTitle(FontAdapter title) {
        FontRenderers.title = title;
    }

    public static FontAdapter getVanilla() {
        return vanilla;
    }

    public static void setVanilla(FontAdapter vanilla) {
        FontRenderers.vanilla = vanilla;
    }
}
