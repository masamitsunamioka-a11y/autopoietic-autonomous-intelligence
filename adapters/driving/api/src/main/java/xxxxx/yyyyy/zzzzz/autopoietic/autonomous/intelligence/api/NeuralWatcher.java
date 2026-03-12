package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

@ApplicationScoped
public class NeuralWatcher {
    private static final Logger logger = LoggerFactory.getLogger(NeuralWatcher.class);
    private final Repository<Area> areaRepository;
    private final Repository<Neuron> neuronRepository;
    private final Repository<Effector> effectorRepository;
    private final SseRegistry sseRegistry;
    private final Gson gson;
    private final Path areasPath;
    private final Path neuronsPath;
    private final Path effectorsPath;
    private final WatchService watchService;
    private final ExecutorService executorService;

    @Inject
    public NeuralWatcher(Repository<Area> areaRepository,
                         Repository<Neuron> neuronRepository,
                         Repository<Effector> effectorRepository,
                         SseRegistry sseRegistry) {
        this.areaRepository = areaRepository;
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
        this.sseRegistry = sseRegistry;
        this.gson = new Gson();
        this.areasPath = Path.of("filesystem/neural/areas", "");
        this.neuronsPath = Path.of("filesystem/neural/neurons", "");
        this.effectorsPath = Path.of(
            "adapters/driven/services/src/main/java/"
                + "xxxxx/yyyyy/zzzzz/autopoietic/autonomous/"
                + "intelligence/effectors/effector", "");
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    void activate() {
        this.register(this.areasPath);
        this.register(this.neuronsPath);
        this.register(this.effectorsPath);
        this.executorService.submit(this::watch);
        logger.info("[NEURAL] watcher started: {}, {}, {}",
            this.areasPath, this.neuronsPath, this.effectorsPath);
    }

    @PreDestroy
    void deactivate() {
        this.executorService.shutdownNow();
        try {
            this.watchService.close();
        } catch (IOException e) {
            logger.error("[NEURAL] watcher close failed", e);
        }
    }

    private void register(Path path) {
        try {
            path.register(this.watchService,
                ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (IOException e) {
            logger.warn("[NEURAL] cannot watch {}: {}",
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

    public String treeJson() {
        var timestamps = this.readTimestamps();
        var tree = new NeuralTree(
            this.areaRepository, this.neuronRepository,
            this.effectorRepository, timestamps);
        return this.gson.toJson(tree);
    }

    private Map<String, Long> readTimestamps() {
        var map = new HashMap<String, Long>();
        this.scanJsonDir(this.areasPath, map);
        this.scanJsonDir(this.neuronsPath, map);
        this.scanJavaDir(this.effectorsPath, map);
        return map;
    }

    @SuppressWarnings("unchecked")
    private void scanJsonDir(Path dir,
                             Map<String, Long> map) {
        try (var stream = Files.list(dir)) {
            stream.filter(p -> p.toString().endsWith(".json"))
                .forEach(p -> {
                    try {
                        var json = Files.readString(p);
                        var obj = (Map<String, Object>)
                            this.gson.fromJson(json, Map.class);
                        var id = (String) obj.get("id");
                        var ts = Files.getLastModifiedTime(p)
                            .toMillis();
                        map.put(id, ts);
                    } catch (IOException e) {
                    }
                });
        } catch (IOException e) {
            logger.warn("[NEURAL] cannot list {}: {}",
                dir, e.getMessage());
        }
    }

    private void scanJavaDir(Path dir,
                             Map<String, Long> map) {
        try (var stream = Files.list(dir)) {
            stream.filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> {
                    try {
                        var name = p.getFileName().toString();
                        var id = name.substring(
                            0, name.length() - 5);
                        var ts = Files.getLastModifiedTime(p)
                            .toMillis();
                        map.put(id, ts);
                    } catch (IOException e) {
                    }
                });
        } catch (IOException e) {
            logger.warn("[NEURAL] cannot list {}: {}",
                dir, e.getMessage());
        }
    }

    private void broadcast() {
        try {
            var json = "{\"type\":\"neural\","
                + "\"content\":" + this.treeJson() + "}";
            this.sseRegistry.broadcast(json);
            logger.debug("[NEURAL] tree broadcast");
        } catch (Exception e) {
            logger.error("[NEURAL] broadcast failed", e);
        }
    }
}
