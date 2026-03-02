package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Compiler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Storage;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class EffectorAdapter implements Adapter<Effector, String> {
    private static final Logger logger = LoggerFactory.getLogger(EffectorAdapter.class);
    private final Translator<Effector, String> translator;
    private final Compiler compiler;
    private final Storage storage;
    private final Serializer serializer;
    private final String effectorPackage;
    private final Path effectorSource;
    private final Path effectorTarget;

    @Inject
    public EffectorAdapter(Translator<Effector, String> translator,
                           Compiler compiler,
                           Storage storage, Serializer serializer) {
        this.translator = translator;
        this.compiler = compiler;
        this.storage = storage;
        this.serializer = serializer;
        var configuration = new Configuration();
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
        return this.storage.walk(this.effectorTarget)
            .filter(path -> path.endsWith(".class"))
            .map(x -> x.replaceAll(".*/|\\.class$", ""))
            .map(this::fetch)
            .toList();
    }

    @Override
    public void publish(String id, String json) {
        Map<String, Object> definition = this.serializer.deserialize(json);
        var tuning = (String) definition.get("tuning");
        var effector = new Effector() {
            /// @formatter:off
            @Override public String name()    { return id; }
            @Override public String tuning()  { return tuning; }
            /// @formatter:on
            @Override
            public Map<String, Object> fire(Map<String, Object> input) {
                return Map.of();
            }
        };
        this.storage.write(
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
