package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.s;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Monitor;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Snapshot;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.e.FileSystemChanged;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

@ApplicationScoped
public class FileSystemMonitor implements Monitor {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemMonitor.class);
    private final Publisher publisher;
    private final List<Path> paths;
    private final WatchService watchService;
    private final ExecutorService executorService;

    @Inject
    public FileSystemMonitor(Publisher publisher) {
        this.publisher = publisher;
        this.paths = List.of(
            Path.of("filesystem/eventsourcing/data/neural/areas", ""),
            Path.of("filesystem/eventsourcing/data/neural/neurons", ""),
            Path.of("adapters/driven/services/src/main/java/xxxxx/yyyyy/zzzzz/autopoietic/autonomous/intelligence/effectors/effector", ""),
            Path.of("filesystem/eventsourcing/data/hippocampal/episode", ""),
            Path.of("filesystem/eventsourcing/data/neocortical/knowledge", ""));
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    void activate() {
        this.paths.forEach(x -> {
            try {
                x.register(this.watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        this.executorService.submit(this::monitor);
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

    @Override
    public void monitor() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                var key = this.watchService.poll(30, TimeUnit.SECONDS);
                if (key == null) {
                    continue;
                }
                Thread.sleep(500);
                var detected = this.detect(key);
                key.reset();
                if (!detected.isEmpty()) {
                    this.publisher.submit(new FileSystemChanged(detected));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public List<Snapshot> snapshots() {
        var list = new ArrayList<Snapshot>();
        for (var path : this.paths) {
            list.addAll(this.scan(path));
        }
        return list;
    }

    private List<Snapshot> detect(WatchKey key) {
        var watchable = (Path) key.watchable();
        return key.pollEvents().stream()
            .map(x -> watchable.resolve((Path) x.context()))
            .map(this::toFile)
            .filter(File::isMonitorable)
            .map(File::snapshot)
            .toList();
    }

    private List<Snapshot> scan(Path path) {
        try (var stream = Files.list(path)) {
            return stream
                .sorted()
                .map(this::toFile)
                .filter(File::isMonitorable)
                .map(File::snapshot)
                .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private File toFile(Path path) {
        try {
            var name = path.getParent().getFileName() + "/" + path.getFileName();
            return new File(name, Files.readString(path), Files.getLastModifiedTime(path).toMillis());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /// @formatter:off
    private record File(
        String name, String content, long timestamp) {
        private static final ObjectMapper objectMapper = new ObjectMapper();
        public String mimeType() {
            return this.isJson() ? "application/json" : "text/x-java-source";
        }
        public boolean isMonitorable() {
            return this.isJson() || this.isJava();
        }
        public Snapshot snapshot() {
            return new SnapshotImpl(
                this.name(),
                this.mimeType(),
                this.isJson() ? this.parseJson() : null,
                this.timestamp());
        }
        private Object parseJson() {
            try {
                return objectMapper.readValue(this.content(), Object.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        private boolean isJson() {
            return this.name.endsWith(".json");
        }
        private boolean isJava() {
            return this.name.endsWith(".java");
        }
    }
    /// @formatter:on
}
