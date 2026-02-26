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
    private final List<String> includes;
    private final List<String> excludes;

    public ClassScannerImpl(List<String> includes, List<String> excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    @Override
    public List<Class<?>> scan() {
        var classLoader = Thread.currentThread().getContextClassLoader();
        return this.includes.stream()
            .flatMap(x -> this.scanPackage(classLoader, x))
            .filter(x -> this.excludes.stream().noneMatch(x::startsWith))
            .<Class<?>>map(this::toClass)
            .distinct()
            .toList();
    }

    private Stream<String> scanPackage(ClassLoader loader, String target) {
        var name = target.replace('.', '/');
        try {
            return list(loader.getResources(name)).stream()
                .flatMap(this::toPath)
                .map(path -> this.toClassName(path, target))
                .filter(x -> x.startsWith(target));
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

    private String toClassName(Path path, String pkg) {
        return path.toString()
            .replaceFirst(
                "^.*?" + pkg.replace(".", "[/\\\\]"),
                pkg)
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
