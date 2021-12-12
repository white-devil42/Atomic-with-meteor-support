package me.zeroX150.atomic.helper.manager;

import me.zeroX150.atomic.Atomic;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class CapeManager {
    public static List<CapeEntry> capes = new ArrayList<>();

    public static void init() {
        Map<String, String> alreadyDownloadedWithFN = new HashMap<>();
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(10)).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://raw.githubusercontent.com/cornos/atomicFiles/master/capes.txt")).timeout(Duration.ofSeconds(10)).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(s -> {
            for (String s2 : s.split("\n")) { // all capes, dumped into here
                if (s2.startsWith("#")) {
                    continue; // ignore comments
                }
                StringBuilder sb1 = new StringBuilder();
                for (char c : s2.toCharArray()) { // ignore comments even after content, walk through the string
                    if (c == '#') {
                        break; // if the current character is a #, abort
                    }
                    sb1.append(c); // else add it to the shit
                }
                String s1 = sb1.toString();
                Atomic.log(Level.INFO, s1);
                String[] split = s1.split(" +"); // split everything at a space
                if (split.length != 2) {
                    continue; // we only want "uuid capeUrl" format
                }
                String uuid = split[0];
                String capeUrl = split[1];
                try {
                    UUID u = UUID.fromString(uuid);
                    alreadyDownloadedWithFN.put(capeUrl, u + ".png");
                    capes.add(new CapeEntry(u, capeUrl, File.createTempFile("atomicCape", u.toString()), new AtomicBoolean(false)));
                } catch (Exception ignored) {
                    Atomic.log(Level.ERROR, "Invalid UUID entry \"" + uuid + "\"");
                }
            }
            Atomic.log(Level.INFO, "-- Cape mappings --");
            for (CapeEntry cape : capes) {
                Atomic.log(Level.INFO, " " + cape.owner + " @ " + cape.downloadURL + " -> " + cape.location.getAbsolutePath());
                cape.location.deleteOnExit();
            }
        }).exceptionally(throwable -> {
            Atomic.log(Level.ERROR, "Failed to download capes!");
            return null;
        });
    }

    public static void download(CapeEntry entry) throws Exception {
        if (entry.downloaded.get()) {
            return;
        }
        Atomic.log(Level.INFO, "Downloading " + entry.owner + "'s cape @ " + entry.downloadURL + " -> " + entry.location.getAbsolutePath());
        URL url = new URL(entry.downloadURL);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(entry.location);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        entry.downloaded.set(true);
    }

    public record CapeEntry(UUID owner, String downloadURL, File location, AtomicBoolean downloaded) {

    }
}
