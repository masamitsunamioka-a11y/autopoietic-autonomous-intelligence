package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FileSystemActionAdapter implements Adapter<Action, String> {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemActionAdapter.class);
    private final Translator<Action, String> translator;
    private final FileSystem fileSystem;
    private final JsonCodec jsonCodec;
    private final Compiler compiler;
    private final String actionsPackage;
    private final Path actionsSource;
    private final Path actionsTarget;

    @Inject
    public FileSystemActionAdapter(Translator<Action, String> translator,
                                   @Localic FileSystem fileSystem,
                                   JsonCodec jsonCodec,
                                   Compiler compiler) {
        this.translator = translator;
        this.fileSystem = fileSystem;
        this.jsonCodec = jsonCodec;
        this.compiler = compiler;
        var configuration = new Configuration("anticorruption.yaml");
        this.actionsPackage = configuration.get("anticorruption.actions.package");
        this.actionsSource = Path.of(configuration.get("anticorruption.actions.source"), "");
        this.actionsTarget = Path.of(configuration.get("anticorruption.actions.target"), "");
    }

    @Override
    public Action fetch(String id) {
        var name = this.actionsPackage + "." + id;
        try (var loader = this.urlClassLoader()) {
            return (Action) loader.loadClass(name).getConstructor().newInstance();
        } catch (IOException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private URLClassLoader urlClassLoader() {
        try {
            return new URLClassLoader(
                new URL[]{this.actionsTarget.toUri().toURL()},
                Thread.currentThread().getContextClassLoader());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Action> fetchAll() {
        return this.fileSystem.walk(this.actionsTarget)
            .filter(path -> path.endsWith(".class"))
            .map(x -> x.replaceAll(".*/|\\.class$", ""))
            .map(this::fetch)
            .toList();
    }

    @Override
    public void publish(String id, String json) {
        Map<String, Object> definition = this.jsonCodec.unmarshal(json);
        var label = (String) definition.get("label");
        var description = (String) definition.get("description");
        var action = new Action() {
            /// @formatter:off
            @Override public String name() { return id; }
            @Override public String label() { return label; }
            @Override public String description() { return description; }
            /// @formatter:on
            @Override
            public Map<String, Object> execute(Map<String, Object> input) {
                return Map.of();
            }
        };
        this.fileSystem.write(
            this.actionsSource.resolve(Path.of(this.actionsPackage.replace(".", "/"), id + ".java")),
            this.translator.translateTo(id, action),
            StandardCharsets.UTF_8);
        this.compiler.compile(this.actionsSource, this.actionsTarget);
    }

    @Override
    public void revoke(String id) {
        throw new UnsupportedOperationException();
    }
}
