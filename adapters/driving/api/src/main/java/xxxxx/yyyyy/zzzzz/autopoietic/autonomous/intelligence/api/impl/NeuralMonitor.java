package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Monitor;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

@Neural
@ApplicationScoped
public class NeuralMonitor implements Monitor {
    private static final Logger logger = LoggerFactory.getLogger(NeuralMonitor.class);
    private final Repository<Area> areaRepository;
    private final Repository<Neuron> neuronRepository;
    private final Repository<Effector> effectorRepository;
    private final Publisher publisher;
    private final Gson gson;
    private final Path areasPath;
    private final Path neuronsPath;
    private final Path effectorsPath;
    private final WatchService watchService;
    private final ExecutorService executorService;

    @Inject
    public NeuralMonitor(Repository<Area> areaRepository,
                         Repository<Neuron> neuronRepository,
                         Repository<Effector> effectorRepository,
                         Publisher publisher) {
        this.areaRepository = areaRepository;
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
        this.publisher = publisher;
        this.gson = new Gson();
        this.areasPath = Path.of("filesystem/neural/areas", "");
        this.neuronsPath = Path.of("filesystem/neural/neurons", "");
        this.effectorsPath = Path.of("adapters/driven/services/src/main/java/xxxxx/yyyyy/zzzzz/autopoietic/autonomous/intelligence/effectors/effector", "");
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    void activate() {
        this.register(this.areasPath);
        this.register(this.neuronsPath);
        this.register(this.effectorsPath);
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
            this.publisher.publish(
                new NeuralEvent(this.content()));
        }
    }

    @Override
    public Object content() {
        var timestamps = this.readTimestamps();
        return new NeuralTree(this.areaRepository, this.neuronRepository, this.effectorRepository, timestamps);
    }

    private Map<String, Long> readTimestamps() {
        var map = new HashMap<String, Long>();
        this.scanJsonDir(this.areasPath, map);
        this.scanJsonDir(this.neuronsPath, map);
        this.scanJavaDir(this.effectorsPath, map);
        return map;
    }

    @SuppressWarnings("unchecked")
    private void scanJsonDir(Path dir, Map<String, Long> map) {
        try (var stream = Files.list(dir)) {
            stream.filter(x -> x.toString().endsWith(".json"))
                .forEach(x -> {
                    try {
                        var json = Files.readString(x);
                        var obj = (Map<String, Object>) this.gson.fromJson(json, Map.class);
                        var id = (String) obj.get("id");
                        var ts = Files.getLastModifiedTime(x).toMillis();
                        map.put(id, ts);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void scanJavaDir(Path dir, Map<String, Long> map) {
        try (var stream = Files.list(dir)) {
            stream.filter(x -> x.toString().endsWith(".java"))
                .forEach(x -> {
                    try {
                        var name = x.getFileName().toString();
                        var id = name.substring(0, name.length() - 5);
                        var ts = Files.getLastModifiedTime(x).toMillis();
                        map.put(id, ts);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
