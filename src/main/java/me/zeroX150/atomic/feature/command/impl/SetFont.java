package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.font.GlyphPageFontRenderer;
import me.zeroX150.atomic.helper.util.Utils;

import java.io.File;
import java.io.FileInputStream;

public class SetFont extends Command {

    public SetFont() {
        super("SetFont", "Sets the font renderer's properties to some custom shit", "setfont");
    }

    @Override public void onExecute(String[] args) {
        if (args.length < 2) {
            Utils.Client.sendMessage("Need you to specify font path and size");
            return;
        }
        String path = args[0];
        String size = args[1];
        int sizeI = Utils.Math.tryParseInt(size, 0);
        if (sizeI < 1) {
            Utils.Client.sendMessage("Size needs to be above 1");
            return;
        }
        File f = new File(path);
        if (!f.exists() || !f.isFile()) {
            Utils.Client.sendMessage("File \"" + f.getAbsolutePath() + "\" doesn't exist or is not a file");
            return;
        }
        try {
            FileInputStream fip = new FileInputStream(f);
            FontRenderers.normal = GlyphPageFontRenderer.createFromStream(fip, sizeI, false, false, false);
            Utils.Client.sendMessage("Loaded font");
        } catch (Exception e) {
            Utils.Client.sendMessage("Couldn't read font file: " + e.getMessage());
        }
    }
}
