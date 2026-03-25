package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.BinaryResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Resource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.TextResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JavaResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JsonResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.MarkdownResource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toSet;

public class LocalFileSystem implements Extern {
    private static final Logger logger = LoggerFactory.getLogger(LocalFileSystem.class);
    private final Path base;
    private final ReadWriteLock lock;

    public LocalFileSystem(Path base) {
        this.base = base;
        this.lock = new ReentrantReadWriteLock(true);
    }

    @Override
    public URI resolve(String name) {
        return this.base.resolve(name).toUri();
    }

    public URI resolve(Path path) {
        return this.base.resolve(path).toUri();
    }

    @Override
    public Resource get(URI uri) {
        this.lock.readLock().lock();
        try {
            return this.read(Path.of(uri));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void put(Resource resource) {
        this.lock.writeLock().lock();
        try {
            this.write(Path.of(resource.uri()), resource);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(URI uri) {
        this.lock.writeLock().lock();
        try {
            this.delete(Path.of(uri));
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public boolean contains(URI uri) {
        this.lock.readLock().lock();
        try {
            return Files.exists(Path.of(uri));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public Set<URI> set() {
        this.lock.readLock().lock();
        try {
            return this.list();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private Resource read(Path path) {
        try {
            var extension = this.extension(path);
            var uri = path.toUri();
            /// @formatter:off
            return switch (extension) {
                case ".json"    -> new JsonResource(uri, Files.readString(path, UTF_8));
                case ".java"    -> new JavaResource(uri, Files.readString(path, UTF_8));
                case ".md"      -> new MarkdownResource(uri, Files.readString(path, UTF_8));
                default -> throw new IllegalArgumentException(extension);
            };
            /// @formatter:on
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void write(Path path, Resource resource) {
        try {
            Files.createDirectories(path.getParent());
            /// @formatter:off
            switch (resource) {
                case TextResource x     -> Files.writeString(path, x.content(), UTF_8);
                case BinaryResource x   -> Files.write(path, x.content());
            }
            /// @formatter:on
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void delete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Set<URI> list() {
        try (var stream = Files.walk(this.base)) {
            return stream
                .filter(Files::isRegularFile)
                .filter(x -> !x.getFileName().toString().startsWith("."))
                .map(Path::toUri)
                .collect(toSet());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String extension(Path path) {
        return path.getFileName().toString().replaceAll(".*(?=\\.)", "");
    }
}
