package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

@ApplicationScoped
public class MnemonicWatcher {
    private static final Logger logger = LoggerFactory.getLogger(MnemonicWatcher.class);
    private final SseRegistry sseRegistry;
    private final Gson gson;
    private final Path episodePath;
    private final Path knowledgePath;
    private final WatchService watchService;
    private final ExecutorService executorService;

    @Inject
    public MnemonicWatcher(SseRegistry sseRegistry) {
        this.sseRegistry = sseRegistry;
        this.gson = new Gson();
        this.episodePath = Path.of("filesystem/hippocampal/episode", "");
        this.knowledgePath = Path.of("filesystem/neocortical/knowledge", "");
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    void activate() {
        this.register(this.episodePath);
        this.register(this.knowledgePath);
        this.executorService.submit(this::watch);
        logger.info("[MNEMONIC] watcher started: {}, {}",
            this.episodePath, this.knowledgePath);
    }

    @PreDestroy
    void deactivate() {
        this.executorService.shutdownNow();
        try {
            this.watchService.close();
        } catch (IOException e) {
            logger.error("[MNEMONIC] watcher close failed", e);
        }
    }

    private void register(Path path) {
        try {
            path.register(this.watchService,
                ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (IOException e) {
            logger.warn("[MNEMONIC] cannot watch {}: {}",
                path, e.getMessage());
        }
    }

    private void watch() {
        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key;
            try {
                key = this.watchService.poll(
                    30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (key == null) {
                continue;
            }
            key.pollEvents();
            key.reset();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            this.broadcast();
        }
    }

    public String mnemonicJson() {
        var root = new JsonObject();
        root.add("episodes", this.readEntries(this.episodePath));
        root.add("knowledge", this.readEntries(this.knowledgePath));
        return this.gson.toJson(root);
    }

    private JsonArray readEntries(Path dir) {
        var array = new JsonArray();
        try (var stream = Files.list(dir)) {
            stream.filter(p -> p.toString().endsWith(".json"))
                .sorted()
                .forEach(p -> {
                    try {
                        var content = Files.readString(p);
                        var parsed = this.gson.fromJson(
                            content, JsonArray.class);
                        if (parsed == null) {
                            return;
                        }
                        var timestamp = Files.getLastModifiedTime(p)
                            .toMillis();
                        var entry = new JsonObject();
                        entry.addProperty("file",
                            p.getFileName().toString());
                        entry.add("entries", parsed);
                        entry.addProperty("timestamp", timestamp);
                        array.add(entry);
                    } catch (Exception e) {
                        logger.debug("[MNEMONIC] skip {}: {}",
                            p, e.getMessage());
                    }
                });
        } catch (IOException e) {
            logger.warn("[MNEMONIC] cannot list {}: {}",
                dir, e.getMessage());
        }
        return array;
    }

    private void broadcast() {
        try {
            var json = "{\"type\":\"mnemonic\","
                + "\"content\":" + this.mnemonicJson() + "}";
            this.sseRegistry.broadcast(json);
            logger.debug("[MNEMONIC] broadcast");
        } catch (Exception e) {
            logger.error("[MNEMONIC] broadcast failed", e);
        }
    }
}
