package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.FileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Localic;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@ApplicationScoped
@Localic
public class LocalFileSystem implements FileSystem {
    private static final Logger logger = LoggerFactory.getLogger(LocalFileSystem.class);

    @Override
    public String read(Path path, Charset charset) {
        try {
            return Files.readString(path, charset);
        } catch (IOException e) {
            throw new UncheckedIOException("Read failed: " + path, e);
        }
    }

    @Override
    public void write(Path path, String content, Charset charset) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, content, charset);
            new SpinLock().await(() -> Files.exists(path), 10, 100);
        } catch (IOException e) {
            throw new UncheckedIOException("Write failed: " + path, e);
        }
    }

    @Override
    public boolean exists(Path path) {
        return Files.exists(path);
    }

    @Override
    public Stream<String> list(Path path) {
        try (var stream = Files.list(path)) {
            return stream
                .map(Path::toString)
                .toList()
                .stream();
        } catch (IOException e) {
            throw new UncheckedIOException("List failed: " + path, e);
        }
    }

    @Override
    public Stream<String> walk(Path path) {
        try (var stream = Files.walk(path)) {
            return stream
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .toList()
                .stream();
        } catch (IOException e) {
            throw new UncheckedIOException("Walk failed: " + path, e);
        }
    }

    @Override
    public void delete(Path path) {
        try {
            Files.deleteIfExists(path);
            new SpinLock().await(() -> !Files.exists(path), 10, 100);
        } catch (IOException e) {
            throw new UncheckedIOException("Delete failed: " + path, e);
        }
    }
}
