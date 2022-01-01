package me.zeroX150.atomic.feature.command.impl;

import com.google.gson.Gson;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Taco extends Command {
    public static final File          storage      = new File(Atomic.client.runDirectory, "taco.atomic");
    static final        File          gifPath      = new File(Atomic.client.runDirectory, "tacoFrames");
    public static       TacoConfig    config       = new TacoConfig();
    public static       boolean       enabled      = false;
    public static       List<Frame>   frames       = new ArrayList<>();
    public static       long          currentFrame = 0;
    public static       AtomicBoolean init         = new AtomicBoolean(false);
    static              Thread        ticker       = new Thread(() -> {
        while (true) {
            long sleepTime = 1000 / config.fps;
            currentFrame++;
            if (currentFrame >= frames.size()) {
                currentFrame = 0;
            }
            Utils.sleep(sleepTime);
        }
    });

    public Taco() {
        super("Taco", "config for taco hud", "taco");
        Events.registerEventHandler(EventType.CONFIG_SAVE, event -> saveConfig());
        Events.registerEventHandler(EventType.POST_INIT, event -> { // we in game, context is made, we can make textures
            if (!init.get()) {
                initFramesAndConfig();
            }
            init.set(true);
        });
    }

    static void initFramesAndConfig() {
        if (init.get()) {
            throw new IllegalStateException();
        }
        try {
            ticker.start();
        } catch (Exception ignored) {

        }
        try {
            if (!storage.isFile()) {
                storage.delete();
            }
            if (!storage.exists()) {
                storage.createNewFile();
                Atomic.log(Level.INFO, "Skipping taco config file because it doesnt exist");
                return;
            }
            String a = FileUtils.readFileToString(storage, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            config = gson.fromJson(a, TacoConfig.class);
            if (config == null) {
                config = new TacoConfig();
            }
            initFrames();
        } catch (Exception e) {
            Atomic.log(Level.ERROR, "Failed to read taco config");
            e.printStackTrace();
            if (storage.exists()) {
                storage.delete();
            }
        }
    }

    static void initFrames() throws Exception {
        checkGifPath();
        for (Frame frame : frames) {
            Atomic.client.getTextureManager().destroyTexture(frame.getI());
        }
        frames.clear();
        Frame.frameCounter = 0;
        File[] a = Objects.requireNonNull(gifPath.listFiles()).clone();
        List<String> framesSorted = Arrays.stream(a).map(File::getName).sorted().collect(Collectors.toList());
        for (String file : framesSorted) {
            File f = Arrays.stream(a).filter(file1 -> file1.getName().equals(file)).findFirst().orElseThrow();
            BufferedImage bi = ImageIO.read(f);
            Frame now = new Frame(bi);
            frames.add(now);
        }
    }

    public static Frame getCurrentFrame() {
        if (currentFrame >= frames.size()) {
            currentFrame = 0;
        }
        if (frames.isEmpty()) {
            return null;
        }
        return frames.get((int) currentFrame);
    }

    static void saveConfig() {
        Gson gson = new Gson();
        String json = gson.toJson(config);
        try {
            FileUtils.writeStringToFile(storage, json, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Atomic.log(Level.ERROR, "Failed to write taco config");
            e.printStackTrace();
        }
    }

    static void copyGifFiles(File f) {
        for (File file : Objects.requireNonNull(gifPath.listFiles())) {
            file.delete();
        }
        for (File file : Objects.requireNonNull(f.listFiles())) {
            try {
                FileUtils.copyFile(file, new File(gifPath, file.getName()));
            } catch (Exception ignored) {

            }
        }
    }

    static void checkGifPath() {
        if (!gifPath.isDirectory()) {
            gifPath.delete();
        }
        if (!gifPath.exists()) {
            gifPath.mkdir();
        }
    }

    @Override public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"fps", "frames", "toggle"};
        } else if (args.length == 2) {
            return switch (args[0].toLowerCase()) {
                case "fps" -> new String[]{"(new fps)"};
                case "frames" -> new String[]{"(path to frames folder)"};
                default -> new String[0];
            };
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) {
            error("Please specify an action");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "fps" -> {
                if (args.length < 2) {
                    error("give me the fps lmao");
                    return;
                }
                int i = Utils.Math.tryParseInt(args[1], -1);
                if (i < 1 || i > 1000) {
                    error("fps cant be below 1 or above 1000 mf");
                    return;
                }
                config.fps = i;
                success("set fps to " + i);
            }
            case "frames" -> {
                if (args.length < 2) {
                    error("give me the folder location lmao");
                    return;
                }
                File f = new File(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                if (!f.exists()) {
                    error("that folder doesnt exist");
                    return;
                }
                if (!f.isDirectory()) {
                    error("has to be a folder with the frames stored in order of appearance, use https://ezgif.com/split to convert the gif");
                    return;
                }
                message("Loading gif files, this may take a bit");
                checkGifPath();
                message("Copying frames");
                copyGifFiles(f);
                try {
                    message("Initializing frames");
                    initFrames();
                    success("Initialized frames!");
                } catch (Exception e) {
                    error("Failed to init: " + e.getMessage());
                    error("Logs have more detail");
                    e.printStackTrace();
                }
            }
            case "toggle" -> {
                config.enabled = !config.enabled;
                if (config.enabled) {
                    success("Taco is now tacoing");
                } else {
                    message("Taco is no longer tacoing :(");
                }
            }
        }
    }

    public static class TacoConfig {
        public long    fps     = 30;
        public boolean enabled = false;
    }

    public static class Frame {
        static long frameCounter = 0;
        NativeImageBackedTexture tex;
        Identifier               i;

        public Frame(BufferedImage image) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                byte[] bytes = baos.toByteArray();

                ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
                data.flip();
                tex = new NativeImageBackedTexture(NativeImage.read(data));

                i = new Identifier("atomic", "tacoframe_" + frameCounter);
                frameCounter++;
                Atomic.client.execute(() -> Atomic.client.getTextureManager().registerTexture(i, tex));
            } catch (Exception e) {
                Utils.Logging.error("failed to register frame " + frameCounter);
                e.printStackTrace();
            }
        }

        public Identifier getI() {
            return i;
        }

        public NativeImageBackedTexture getTex() {
            return tex;
        }
    }
}
