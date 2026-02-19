package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ClassScanner;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ClasspathClassScanner implements ClassScanner {
    private static final Logger logger = LoggerFactory.getLogger(ClasspathClassScanner.class);
    private final String basePackage;

    public ClasspathClassScanner(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public List<Class<?>> scan() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String resourcePath = this.basePackage.replace('.', '/');
            return Collections.list(classLoader.getResources(resourcePath)).stream()
                .flatMap(this::toPath)
                .map(this::toClassName)
                .filter(name -> !name.isEmpty())
                .<Class<?>>map(this::toClass)
                .distinct()
                .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Unified scan failed", e);
        }
    }

    private Stream<Path> toPath(URL url) {
        try {
            URI uri = url.toURI();
            if ("jar".equals(uri.getScheme())) {
                String[] parts = uri.toString().split("!");
                try (FileSystem fileSystem = this.getOrCreateFileSystem(parts[0])) {
                    Path jarPath = fileSystem.getPath(parts[1]);
                    try (Stream<Path> walk = Files.walk(jarPath)) {
                        return walk.filter(p -> p.toString().endsWith(".class"))
                            .toList()
                            .stream();
                    }
                }
            }
            try (Stream<Path> walk = Files.walk(Paths.get(uri))) {
                return walk.filter(p -> p.toString().endsWith(".class"))
                    .toList()
                    .stream();
            }
        } catch (Exception e) {
            logger.warn("[SCAN_SKIP] Could not process URL: {}", url, e);
            return Stream.empty();
        }
    }

    private FileSystem getOrCreateFileSystem(String uriPart) throws IOException {
        URI uri = URI.create(uriPart);
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Collections.emptyMap());
        }
    }

    private String toClassName(Path path) {
        String pathStr = path.toString();
        int index = pathStr.replace('/', '.').indexOf(this.basePackage);
        if (index == -1) return "";
        String className = pathStr.substring(index)
            .replace(File.separatorChar, '.')
            .replace('/', '.');
        return className.endsWith(".class")
            ? className.substring(0, className.length() - 6)
            : className;
    }

    private Class<?> toClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + className, e);
        }
    }
}
