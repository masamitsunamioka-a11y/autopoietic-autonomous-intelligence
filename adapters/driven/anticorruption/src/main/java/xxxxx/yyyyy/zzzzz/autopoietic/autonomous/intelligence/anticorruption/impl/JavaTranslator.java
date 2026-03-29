package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.JavaResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public class JavaTranslator<I extends Entity> implements Translator<I, JavaResource> {
    private static final Logger logger = LoggerFactory.getLogger(JavaTranslator.class);
    private final Serializer serializer;
    private final Class<I> type;
    private final String package_;
    private final String classpath;
    private final Path target;
    private final String template;

    public JavaTranslator(Serializer serializer, Configuration configuration, Class<I> type) {
        this.serializer = serializer;
        this.type = type;
        this.package_ = configuration.get("package");
        this.classpath = configuration.get("compiler.classpath");
        this.target = Path.of(configuration.get("compiler.output"), "");
        this.template = this.loadResource("effector.java.template");
    }

    @Override
    @SuppressWarnings("unchecked")
    public I internalize(JavaResource resource) {
        if (resource == null) return null;
        var clazz = this.compile(resource);
        var instance = this.instantiate(clazz);
        var map = this.accessors().collect(toMap(Method::getName, x -> this.invoke(x, instance)));
        var string = this.serializer.serialize(map);
        var node = new ObjectMapper().valueToTree(map);
        return (I) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{this.type, JsonSerializable.class},
            (proxy, method, args) -> {
                return switch (method.getName()) {
                    case "serialize", "serializeWithType" -> {
                        ((JsonGenerator) args[0]).writeObject(node);
                        yield null;
                    }
                    case "toString" -> string;
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> args != null && args.length == 1 && proxy == args[0];
                    default -> method.invoke(instance, args);
                };
            }
        );
    }

    @Override
    public JavaResource externalize(I object) {
        var data = this.accessors().reduce(this.template.replace("{{package}}", this.package_),
            (x, y) -> x.replace("{{" + y.getName() + "}}", String.valueOf(this.invoke(y, object))),
            (x, y) -> x);
        return new JavaResource(null, data);
    }

    private Class<?> compile(JavaResource resource) {
        try {
            var javaFile = Path.of(resource.uri());
            Files.createDirectories(this.target);
            var javaCompiler = ToolProvider.getSystemJavaCompiler();
            var task = javaCompiler.getTask(
                null, null, null,
                List.of(
                    "-classpath", this.fullClasspath(),
                    "-d", this.target.toAbsolutePath().toString(),
                    "-Xlint:none"),
                null,
                javaCompiler.getStandardFileManager(null, null, null).getJavaFileObjects(javaFile));
            if (!task.call()) {
                throw new IllegalStateException();
            }
            var name = this.package_ + "." + javaFile.getFileName().toString().replace(".java", "");
            try (var classLoader = new URLClassLoader(
                new URL[]{this.target.toAbsolutePath().toUri().toURL()},
                Thread.currentThread().getContextClassLoader())) {
                return classLoader.loadClass(name);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String fullClasspath() {
        var resolved = stream(this.classpath.split(File.pathSeparator))
            .map(x -> Path.of(x).toAbsolutePath().toString())
            .collect(joining(File.pathSeparator));
        return resolved + File.pathSeparator + System.getProperty("java.class.path");
    }

    private I instantiate(Class<?> clazz) {
        try {
            return this.type.cast(clazz.getConstructor().newInstance());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<Method> accessors() {
        return Stream.of(this.type.getMethods())
            .filter(x -> x.getParameterCount() == 0 && !x.getDeclaringClass().equals(Object.class));
    }

    private Object invoke(Method method, Object target) {
        try {
            return method.invoke(target);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private String loadResource(String name) {
        try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
