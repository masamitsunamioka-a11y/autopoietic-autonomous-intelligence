package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Effector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FileSystemEffectorAdapter implements Adapter<Effector, String> {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemEffectorAdapter.class);
    private final Translator<Effector, String> translator;
    private final FileSystem fileSystem;
    private final JsonCodec jsonCodec;
    private final Compiler compiler;
    private final String effectorPackage;
    private final Path effectorSource;
    private final Path effectorTarget;

    @Inject
    public FileSystemEffectorAdapter(Translator<Effector, String> translator,
                                     @Localic FileSystem fileSystem,
                                     JsonCodec jsonCodec,
                                     Compiler compiler) {
        this.translator = translator;
        this.fileSystem = fileSystem;
        this.jsonCodec = jsonCodec;
        this.compiler = compiler;
        var configuration = new Configuration("anticorruption.yaml");
        this.effectorPackage = configuration.get("anticorruption.effectors.package");
        this.effectorSource = Path.of(configuration.get("anticorruption.effectors.source"), "");
        this.effectorTarget = Path.of(configuration.get("anticorruption.effectors.target"), "");
    }

    @Override
    public Effector fetch(String id) {
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

    @Override
    public List<Effector> fetchAll() {
        return this.fileSystem.walk(this.effectorTarget)
            .filter(path -> path.endsWith(".class"))
            .map(x -> x.replaceAll(".*/|\\.class$", ""))
            .map(this::fetch)
            .toList();
    }

    @Override
    public void publish(String id, String json) {
        Map<String, Object> definition = this.jsonCodec.unmarshal(json);
        var description = (String) definition.get("description");
        var effector = new Effector() {
            /// @formatter:off
            @Override public String name()        { return id; }
            @Override public String description() { return description; }
            /// @formatter:on
            @Override
            public Map<String, Object> fire(Map<String, Object> input) {
                return Map.of();
            }
        };
        this.fileSystem.write(
            this.effectorSource.resolve(Path.of(this.effectorPackage.replace(".", "/"), id + ".java")),
            this.translator.translateTo(id, effector),
            StandardCharsets.UTF_8);
        this.compiler.compile(this.effectorSource, this.effectorTarget);
    }

    @Override
    public void revoke(String id) {
        throw new UnsupportedOperationException();
    }
}
