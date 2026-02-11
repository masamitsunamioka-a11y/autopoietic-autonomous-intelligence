package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ApplicationScoped
@Localic
public class LocalFileSystem implements FileSystem {
    @Override
    public String read(String path, Charset charset) {
        Path target = Paths.get(path).normalize();
        try {
            return Files.readString(target, charset);
        } catch (IOException e) {
            throw new UncheckedIOException("Read failed: " + target, e);
        }
    }

    @Override
    public void write(String path, String content, Charset charset) {
        Path target = Paths.get(path).normalize();
        try {
            Optional.ofNullable(target.getParent()).ifPresent(p -> {
                try {
                    Files.createDirectories(p);
                } catch (IOException e) {
                    throw new UncheckedIOException("Directory creation failed: " + p, e);
                }
            });
            Files.writeString(target, content, charset);
            for (int i = 0; i < 10; i++) {
                if (Files.exists(target)) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new UncheckedIOException(
                            "Interrupted while waiting for file: " + target, new IOException(e));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Write failed: " + target, e);
        }
    }

    @Override
    public boolean exists(String path) {
        return Files.exists(Paths.get(path).normalize());
    }

    @Override
    public Stream<String> list(String path) {
        Path target = Paths.get(path).normalize();
        try {
            try (Stream<Path> stream = Files.list(target)) {
                List<String> result = stream
                        .map(Path::toString)
                        .toList();
                return result.stream();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("List failed: " + target, e);
        }
    }

    @Override
    public Stream<String> walk(String path) {
        Path target = Paths.get(path).normalize();
        try {
            try (Stream<Path> stream = Files.walk(target)) {
                List<String> result = stream
                        .filter(Files::isRegularFile)
                        .map(Path::toString)
                        .toList();
                return result.stream();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Walk failed: " + target, e);
        }
    }
}
