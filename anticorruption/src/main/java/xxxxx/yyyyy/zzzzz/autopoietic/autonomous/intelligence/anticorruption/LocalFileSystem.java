package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

@ApplicationScoped
@Localic
public class LocalFileSystem implements FileSystem {
    private static final Logger logger = LoggerFactory.getLogger(LocalFileSystem.class);
    private final ReadWriteLock lock;

    public LocalFileSystem() {
        lock = new ReentrantReadWriteLock(true);
    }

    @Override
    public String read(Path path, Charset charset) {
        this.lock.readLock().lock();
        try {
            return Files.readString(path, charset);
        } catch (IOException e) {
            throw new UncheckedIOException("Read failed: " + path, e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void write(Path path, String content, Charset charset) {
        this.lock.writeLock().lock();
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, content, charset);
            new SpinLock().await(() -> Files.exists(path), 10, 100);
        } catch (IOException e) {
            throw new UncheckedIOException("Write failed: " + path, e);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public boolean exists(Path path) {
        this.lock.readLock().lock();
        try {
            return Files.exists(path);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public Stream<String> walk(Path path, boolean recursive) {
        this.lock.readLock().lock();
        try {
            try (var stream = recursive ? Files.walk(path) : Files.list(path)) {
                return stream
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .toList()
                    .stream();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("List failed: " + path, e);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void delete(Path path) {
        this.lock.writeLock().lock();
        try {
            Files.deleteIfExists(path);
            new SpinLock().await(() -> !Files.exists(path), 10, 100);
        } catch (IOException e) {
            throw new UncheckedIOException("Delete failed: " + path, e);
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
