package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api._under_modification;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Events;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Mnemonic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Monitor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

@Mnemonic
@ApplicationScoped
public class MnemonicMonitor implements Monitor {
    private static final Logger logger = LoggerFactory.getLogger(MnemonicMonitor.class);
    private final Events events;
    private final Gson gson;
    private final Path episodePath;
    private final Path knowledgePath;
    private final WatchService watchService;
    private final ExecutorService executorService;

    @Inject
    public MnemonicMonitor(Events events) {
        this.events = events;
        this.gson = new Gson();
        this.episodePath = Path.of("filesystem/hippocampal/episode", "");
        this.knowledgePath = Path.of("filesystem/neocortical/knowledge", "");
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    void activate() {
        this.register(this.episodePath);
        this.register(this.knowledgePath);
        this.executorService.submit(this::watch);
    }

    @PreDestroy
    void deactivate() {
        this.executorService.shutdownNow();
        try {
            this.watchService.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void register(Path path) {
        try {
            path.register(this.watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void watch() {
        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key;
            try {
                key = this.watchService.poll(30, TimeUnit.SECONDS);
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
            this.events.queue(
                new Event("mnemonic", null, this.content()));
        }
    }

    @Override
    public Object content() {
        var root = new JsonObject();
        root.add("episodes", this.readEntries(this.episodePath));
        root.add("knowledge", this.readEntries(this.knowledgePath));
        return root;
    }

    private JsonArray readEntries(Path dir) {
        var array = new JsonArray();
        try (var stream = Files.list(dir)) {
            stream.filter(x -> x.toString().endsWith(".json"))
                .sorted()
                .forEach(x -> {
                    try {
                        var content = Files.readString(x);
                        var parsed = this.gson.fromJson(content, JsonArray.class);
                        if (parsed == null) {
                            return;
                        }
                        var timestamp = Files.getLastModifiedTime(x).toMillis();
                        var entry = new JsonObject();
                        entry.addProperty("file", x.getFileName().toString());
                        entry.add("entries", parsed);
                        entry.addProperty("timestamp", timestamp);
                        array.add(entry);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return array;
    }
}
