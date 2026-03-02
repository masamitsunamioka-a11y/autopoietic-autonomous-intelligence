package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Storage;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Translator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class EffectorAdapter implements Adapter<Effector, String> {
    private static final Logger logger = LoggerFactory.getLogger(EffectorAdapter.class);
    private final Translator<Effector, String> translator;
    private final Storage storage;
    private final Serializer serializer;
    private final String effectorPackage;
    private final Path effectorSource;

    @Inject
    public EffectorAdapter(Translator<Effector, String> translator,
                           Storage storage, Serializer serializer) {
        this.translator = translator;
        this.storage = storage;
        this.serializer = serializer;
        var configuration = new Configuration();
        this.effectorPackage = configuration.get("anticorruption.effectors.package");
        this.effectorSource = Path.of(configuration.get("anticorruption.effectors.source"), "");
    }

    @Override
    public Effector fetch(String id) {
        var javaFile = this.effectorSource.resolve(
            Path.of(this.effectorPackage.replace(".", "/"), id + ".java"));
        if (!this.storage.exists(javaFile)) {
            return null;
        }
        return this.translator.translateFrom(id, id);
    }

    @Override
    public List<Effector> fetchAll() {
        var packageDir = this.effectorSource.resolve(
            Path.of(this.effectorPackage.replace(".", "/"), ""));
        return this.storage.list(packageDir)
            .filter(path -> path.endsWith(".java"))
            .map(x -> x.replaceAll(".*/|\\.java$", ""))
            .map(this::fetch)
            .filter(Objects::nonNull)
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
    }

    @Override
    public void revoke(String id) {
        throw new UnsupportedOperationException();
    }
}
