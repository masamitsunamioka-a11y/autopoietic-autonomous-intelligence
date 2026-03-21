package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LocalFileSystem implements Extern {
    private static final Logger logger = LoggerFactory.getLogger(LocalFileSystem.class);
    private final Path parent;
    private final ReadWriteLock lock;

    public LocalFileSystem(Path parent) {
        this.parent = parent;
        this.lock = new ReentrantReadWriteLock(true);
    }

    @Override
    public URI resolve(String name) {
        return this.parent.resolve(name).toUri();
    }

    @Override
    public URI resolve(Path path) {
        return this.parent.resolve(path).toUri();
    }

    @Override
    public Resource get(URI uri) {
        var path = Path.of(uri);
        this.lock.readLock().lock();
        try {
            return Files.exists(path)
                ? new FileResource(Files.readString(path, UTF_8))
                : null;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void put(URI uri, Resource resource) {
        var path = Path.of(uri);
        this.lock.writeLock().lock();
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, resource.content(), UTF_8);
            new SpinLock().await(() -> Files.exists(path), 10, 100);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(URI uri) {
        var path = Path.of(uri);
        this.lock.writeLock().lock();
        try {
            Files.deleteIfExists(path);
            new SpinLock().await(() -> !Files.exists(path), 10, 100);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
            if (!Files.exists(this.parent)) {
                return Set.of();
            }
            try (var stream = Files.walk(this.parent)) {
                return stream.filter(Files::isRegularFile)
                    .map(Path::toUri)
                    .collect(Collectors.toSet());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
