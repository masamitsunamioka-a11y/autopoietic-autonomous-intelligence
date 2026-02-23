package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ClassScanner;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.list;

public class ClassScannerImpl implements ClassScanner {
    private static final Logger logger = LoggerFactory.getLogger(ClassScannerImpl.class);
    private final String basePackage;

    public ClassScannerImpl(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public List<Class<?>> scan() {
        var classLoader = Thread.currentThread().getContextClassLoader();
        var name = this.basePackage.replace('.', '/');
        try {
            return list(classLoader.getResources(name)).stream()
                .flatMap(this::toPath)
                .map(this::toClassName)
                .filter(x -> x.startsWith(this.basePackage))
                .<Class<?>>map(this::toClass)
                .distinct()
                .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Stream<Path> toPath(URL url) {
        try {
            var uri = url.toURI();
            var root = "jar".equals(uri.getScheme())
                ? this.getOrNewFileSystem(uri).getPath(uri.toString().split("!")[1])
                : Path.of(uri);
            try (var stream = Files.walk(root)) {
                return stream
                    .filter(x -> x.toString().endsWith(".class"))
                    .toList()
                    .stream();
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FileSystem getOrNewFileSystem(URI uri) {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            try {
                return FileSystems.newFileSystem(uri, Map.of());
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    private String toClassName(Path path) {
        return path.toString()
            .replaceFirst(
                "^.*?" + this.basePackage.replace(".", "[/\\\\]"),
                this.basePackage)
            .replaceAll("[/\\\\]", ".")
            .replace(".class", "");
    }

    private Class<?> toClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
