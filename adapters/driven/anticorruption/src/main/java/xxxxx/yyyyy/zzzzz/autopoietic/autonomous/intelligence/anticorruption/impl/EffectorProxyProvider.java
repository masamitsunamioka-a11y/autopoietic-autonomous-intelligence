package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Storage;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;

import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Utility.actualTypeArguments;

@ApplicationScoped
public class EffectorProxyProvider implements ProxyProvider<Effector> {
    private static final Logger logger = LoggerFactory.getLogger(EffectorProxyProvider.class);
    private final Storage storage;
    private final Serializer serializer;
    private final String effectorPackage;
    private final Path effectorSource;
    private final Path effectorTarget;
    private final String classpath;

    @Inject
    public EffectorProxyProvider(Storage storage, Serializer serializer) {
        this.storage = storage;
        this.serializer = serializer;
        var configuration = new Configuration();
        this.effectorPackage = configuration.get("anticorruption.effectors.package");
        this.effectorSource = Path.of(configuration.get("anticorruption.effectors.source"), "");
        this.effectorTarget = Path.of(configuration.get("anticorruption.effectors.target"), "");
        this.classpath = "manual".equals(configuration.get("anticorruption.effectors.compiler.classpath.strategy"))
            ? configuration.get("anticorruption.effectors.compiler.classpath.value")
            : System.getProperty("java.class.path");
        this.compile();
    }

    @Override
    public Effector provide(String id) {
        this.compile();
        var classFile = this.effectorTarget.resolve(
            Path.of(this.effectorPackage.replace(".", "/"), id + ".class"));
        if (!this.storage.exists(classFile)) {
            return null;
        }
        var instance = this.load(id);
        return (Effector) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{actualTypeArguments(this.getClass())},
            (proxy, method, args) -> switch (method.getName()) {
                case "toString" -> this.serializer.serialize(
                    Map.of("name", instance.name(), "tuning", instance.tuning()));
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> this.equals(proxy, args);
                default -> method.invoke(instance, args);
            }
        );
    }

    private void compile() {
        try {
            var sources = this.sources();
            if (sources.isEmpty()) {
                return;
            }
            if (Files.notExists(this.effectorTarget)) {
                Files.createDirectories(this.effectorTarget);
            }
            var result = ToolProvider.getSystemJavaCompiler().run(
                null, null, null,
                this.arguments(this.options(), sources));
            if (result != 0) {
                throw new IllegalStateException();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Effector load(String id) {
        var name = this.effectorPackage + "." + id;
        try (var loader = this.urlClassLoader()) {
            return (Effector) loader.loadClass(name).getConstructor().newInstance();
        } catch (IOException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private URLClassLoader urlClassLoader() {
        try {
            return new URLClassLoader(
                new URL[]{this.effectorTarget.toUri().toURL()},
                Thread.currentThread().getContextClassLoader());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean equals(Object proxy, Object[] args) {
        return args != null && args.length == 1 && proxy == args[0];
    }

    private String[] arguments(List<String> options, List<String> sources) {
        return Stream.of(options, sources)
            .flatMap(List::stream)
            .toArray(String[]::new);
    }

    private List<String> options() {
        return List.of(
            "-d", this.effectorTarget.toString(),
            "-classpath", this.classpath,
            "-Xlint:none");
    }

    private List<String> sources() {
        try (var stream = Files.walk(this.effectorSource)) {
            return stream
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .filter(x -> x.endsWith(".java"))
                .peek(x -> logger.trace("java: {}", x))
                .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
